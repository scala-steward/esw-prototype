package tmt.ocs

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.{ActorSystem, Scheduler}
import akka.util.Timeout
import tmt.ocs.messages.SequencerMsg
import tmt.ocs.messages.SequencerMsg._
import tmt.ocs.models._

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class Sequencer(sequencer: ActorRef[SequencerMsg], system: ActorSystem) {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  def next: Future[Step]                        = sequencer ? GetNext
  def maybeNext: Future[Option[Step]]           = sequencer ? MaybeNext
  def update(response: AggregateResponse): Unit = sequencer ! Update(response)
}