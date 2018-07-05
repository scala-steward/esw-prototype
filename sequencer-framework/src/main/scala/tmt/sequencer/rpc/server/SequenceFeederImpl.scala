package tmt.sequencer.rpc.server

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.{ActorSystem, Scheduler}
import akka.util.Timeout
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.messages.SequencerMsg.ProcessSequence
import tmt.sequencer.messages.SupervisorMsg
import tmt.sequencer.models.{AggregateResponse, CommandList, InputCommand}

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.Try

class SequenceFeederImpl(supervisor: ActorRef[SupervisorMsg])(implicit system: ActorSystem) extends SequenceFeeder {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  import system.dispatcher

  override def feed(commandList: CommandList): Future[AggregateResponse] = {
    val sequenceCommands                       = commandList.commands.map(cmd => InputCommand.asSequenceCommand(cmd)).toList
    val future: Future[Try[AggregateResponse]] = supervisor ? (x => ProcessSequence(sequenceCommands, x))
    future.map(_.get)
  }
}
