package tmt.sequencer.api

import csw.messages.params.models.Id
import tmt.sequencer.models.{Sequence, SequenceCommandWeb}

import scala.concurrent.Future

trait SequenceEditor {
  def addAll(commands: List[SequenceCommandWeb]): Future[Unit]
  def pause(): Future[Unit]
  def resume(): Future[Unit]
  def reset(): Future[Unit]
  def sequence: Future[Sequence]
  def delete(ids: List[Id]): Future[Unit]
  def addBreakpoints(ids: List[Id]): Future[Unit]
  def removeBreakpoints(ids: List[Id]): Future[Unit]
  def insertAfter(id: Id, commands: List[SequenceCommandWeb]): Future[Unit]
  def prepend(commands: List[SequenceCommandWeb]): Future[Unit]
  def replace(id: Id, commands: List[SequenceCommandWeb]): Future[Unit]
  def shutdown(): Future[Unit]
}

object SequenceEditor {
  val ApiName           = "editor"
  val AddAll            = "addAll"
  val Pause             = "pause"
  val Resume            = "resume"
  val Reset             = "reset"
  val Sequence          = "sequence"
  val Delete            = "delete"
  val AddBreakpoints    = "addBreakpoints"
  val RemoveBreakpoints = "removeBreakpoints"
  val InsertAfter       = "insertAfter"
  val Prepend           = "prepend"
  val Replace           = "replace"
  val Shutdown          = "shutdown"
}
