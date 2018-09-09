package tmt.sequencer.models

import enumeratum.{Enum, EnumEntry}
import play.api.libs.json.{JsArray, JsObject}

import scala.collection.immutable

case class AggregateResponseWeb(childResponses: Set[CommandResponseWeb])
case class CommandListWeb(commands: Seq[SequenceCommandWeb])
case class CommandResponseWeb(response: JsObject) {
  def runId: String = response("runId").as[String]
}

case class SequenceCommandWeb(
    kind: String,
    source: String,
    commandName: String,
    maybeObsId: Option[String],
    paramSet: JsArray,
    runId: Option[String] = None,
)

case class SequenceWeb(steps: List[StepWeb])

sealed trait StepStatus extends EnumEntry

object StepStatus extends Enum[StepStatus] {
  override def values: immutable.IndexedSeq[StepStatus] = findValues
  case object Pending  extends StepStatus
  case object InFlight extends StepStatus
  case object Finished extends StepStatus
}

case class StepWeb(command: SequenceCommandWeb, status: StepStatus, hasBreakpoint: Boolean)

case class SequencerInfo(id: String, mode: String)
