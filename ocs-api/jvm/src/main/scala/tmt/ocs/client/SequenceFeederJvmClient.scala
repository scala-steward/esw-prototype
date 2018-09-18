package tmt.ocs.client

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.{ActorSystem, Scheduler}
import akka.util.Timeout
import tmt.ocs.api.SequenceFeeder
import tmt.ocs.models.{AggregateResponse, CommandList}
import tmt.ocs.messages.SequencerMsg.ProcessSequence
import tmt.ocs.messages.SupervisorMsg

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.Try

class SequenceFeederJvmClient(supervisor: ActorRef[SupervisorMsg])(implicit system: ActorSystem) extends SequenceFeeder {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  import system.dispatcher

  override def feed(commandList: CommandList): Future[Unit] = {
    submit(commandList)
    Future.successful(())
  }

  override def submit(commandList: CommandList): Future[AggregateResponse] = {
    val future: Future[Try[AggregateResponse]] = supervisor ? (x => ProcessSequence(commandList.commands.toList, x))
    future.map(_.get)
  }
}