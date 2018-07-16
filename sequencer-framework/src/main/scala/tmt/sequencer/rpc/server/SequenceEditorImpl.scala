package tmt.sequencer.rpc.server

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.{ActorSystem, Scheduler}
import akka.util.Timeout
import csw.messages.commands.SequenceCommand
import csw.messages.params.models.Id
import tmt.sequencer.api.SequenceEditor
import tmt.sequencer.dsl.Script
import tmt.sequencer.messages.SequencerMsg._
import tmt.sequencer.messages.SupervisorMsg
import tmt.sequencer.models.Sequence

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.Try
import scala.util.control.NonFatal

class SequenceEditorImpl(supervisor: ActorRef[SupervisorMsg], script: Script)(implicit system: ActorSystem)
    extends SequenceEditor {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler
  import system.dispatcher

  def unitResponseHelper(future: Future[Try[Unit]]): Future[String] = future.map {
    case util.Failure(NonFatal(ex)) => ex.getMessage
    case _                          => "Operation Successful"
  }

  def sequenceCommandsFrom(commands: List[SequenceCommand]): List[SequenceCommand] = commands.map(cmd => cmd)

  override def sequence: Future[Sequence] = {
    val future: Future[Try[Sequence]] = supervisor ? (x => GetSequence(x))
    future.map(_.get)
  }

  override def addAll(commands: List[SequenceCommand]): Future[String] =
    unitResponseHelper(supervisor ? (x => Add(sequenceCommandsFrom(commands), x)))

  override def delete(ids: List[Id]): Future[String] = unitResponseHelper(supervisor ? (x => Delete(ids, x)))

  override def insertAfter(id: Id, commands: List[SequenceCommand]): Future[String] =
    unitResponseHelper(supervisor ? (x => InsertAfter(id, sequenceCommandsFrom(commands), x)))

  override def prepend(commands: List[SequenceCommand]): Future[String] =
    unitResponseHelper(supervisor ? (x => Prepend(sequenceCommandsFrom(commands), x)))

  override def replace(id: Id, commands: List[SequenceCommand]): Future[String] =
    unitResponseHelper(supervisor ? (x => Replace(id, sequenceCommandsFrom(commands), x)))

  override def reset(): Future[String] = unitResponseHelper(supervisor ? (x => DiscardPending(x)))

  override def pause(): Future[String] = unitResponseHelper(supervisor ? (x => Pause(x)))

  override def resume(): Future[String] = unitResponseHelper(supervisor ? (x => Resume(x)))

  override def addBreakpoints(ids: List[Id]): Future[String] = unitResponseHelper(supervisor ? (x => AddBreakpoints(ids, x)))

  override def removeBreakpoints(ids: List[Id]): Future[String] =
    unitResponseHelper(supervisor ? (x => RemoveBreakpoints(ids, x)))

  override def shutdown(): Future[String] = {
    script.shutdown().map(_ => "Operation Successful").recover {
      case NonFatal(ex) => ex.getMessage
    }
  }

}
