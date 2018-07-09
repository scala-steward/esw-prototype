package tmt.sequencer.models

import csw.messages.commands.CommandIssue._
import csw.messages.commands.CommandResponse._
import csw.messages.commands._
import csw.messages.params.models.{Id, ObsId, Prefix}
import play.api.libs.json._
import upickle.default.{macroRW, ReadWriter => RW, _}

trait UpickleRWSupport {
  import csw.messages.params.formats.JsonSupport._

  implicit lazy val idRW: RW[Id]         = UpickleFormatAdapter.playJsonToUpickle
  implicit lazy val resultRW: RW[Result] = UpickleFormatAdapter.playJsonToUpickle

  implicit lazy val aggregateResponseRW: RW[AggregateResponse] = macroRW
  implicit lazy val commandListRW: RW[CommandList]             = macroRW
  implicit lazy val stepRW: RW[Step]                           = macroRW
  implicit lazy val sequenceRW: RW[Sequence]                   = macroRW

  implicit lazy val commandResponseWebRW: RW[CommandResponse] = readwriter[CommandResponseWeb].bimap(
    x => CommandResponseWeb(x.runId.id, x.resultType.entryName, upickle.default.write(x)),
    x => commandResponseRW(x.payload)
  )

  implicit lazy val sequenceCommandRW: RW[SequenceCommand] = readwriter[SequenceCommandWeb].bimap(
    command =>
      SequenceCommandWeb(
        command.getClass.getSimpleName,
        command.runId.toString,
        command.source.prefix,
        command.commandName.name,
        command.maybeObsId.map(_.obsId),
        ujson.read((Json.toJson(command) \ "paramSet").toString).arr
    ),
    command =>
      command.kind match {
        case "Setup" =>
          Setup(
            Prefix(command.source),
            CommandName(command.commandName),
            command.maybeObsId.map(v => ObsId(v)),
            paramSetFormat.reads(Json.parse(command.paramSet.toString())).getOrElse(Set.empty)
          )
        case "Observe" =>
          Observe(
            Prefix(command.source),
            CommandName(command.commandName),
            command.maybeObsId.map(v => ObsId(v)),
            paramSetFormat.reads(Json.parse(command.paramSet.toString())).getOrElse(Set.empty)
          )
        case "Wait" => ???
        case _      => ???
    }
  )

  implicit lazy val commandIssueRW: RW[CommandIssue] = RW.merge(
    macroRW[MissingKeyIssue],
    macroRW[WrongPrefixIssue],
    macroRW[WrongUnitsIssue],
    macroRW[WrongNumberOfParametersIssue],
    macroRW[WrongParameterTypeIssue],
    macroRW[AssemblyBusyIssue],
    macroRW[UnresolvedLocationsIssue],
    macroRW[ParameterValueOutOfRangeIssue],
    macroRW[WrongInternalStateIssue],
    macroRW[UnsupportedCommandInStateIssue],
    macroRW[UnsupportedCommandIssue],
    macroRW[RequiredServiceUnavailableIssue],
    macroRW[RequiredHCDUnavailableIssue],
    macroRW[RequiredAssemblyUnavailableIssue],
    macroRW[RequiredSequencerUnavailableIssue],
    macroRW[ComponentLockedIssue],
    macroRW[OtherIssue],
  )

  lazy val commandResponseRW: RW[CommandResponse] = RW.merge(
    macroRW[Accepted],
    macroRW[Invalid],
    macroRW[CompletedWithResult],
    macroRW[Completed],
    macroRW[NoLongerValid],
    macroRW[Error],
    macroRW[Cancelled],
    macroRW[CommandNotAvailable],
    macroRW[NotAllowed]
  )
}

object UpickleFormatAdapter {
  def playJsonToUpickle[T](implicit format: Format[T]): RW[T] = {
    upickle.default
      .readwriter[String]
      .bimap[T](
        result => format.writes(result).toString(),
        str => format.reads(Json.parse(str)).get
      )
  }

  def upickleToPlayJson[T](implicit rw: RW[T]): Format[T] = {
    new Format[T] {
      override def reads(json: JsValue): JsResult[T] = JsSuccess(read[T](json.toString()))
      override def writes(o: T): JsValue             = Json.parse(write(o))
    }
  }
}
