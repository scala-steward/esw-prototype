package ocs.testkit.mocks

import akka.Done
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.{ActorSystem, Cancellable}
import csw.event.api.scaladsl.EventSubscription
import csw.params.commands.CommandResponse.SubmitResponse
import csw.params.commands.{CommandResponse, ControlCommand}
import csw.params.core.models.Id
import csw.params.events.{Event, EventKey}
import ocs.api.SequencerCommandService
import ocs.api.messages.SequencerMsg
import ocs.api.models.Sequence
import ocs.framework.core.{SequenceOperator, SequencerBehaviour}
import ocs.framework.dsl.CswServices
import sequencer.macros.StrandEc

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object SequencerCommandServiceMock$ extends SequencerCommandService {
  override def submit(commandList: Sequence): Future[SubmitResponse] = Future.successful(
    CommandResponse.Completed(Id("dummy-id"))
  )
}

object EventSubscriptionMock extends EventSubscription {
  override def unsubscribe(): Future[Done] = Future.successful(Done)
  override def ready(): Future[Done]       = Future.successful(Done)
}

object CancellableMock extends Cancellable {
  override def cancel(): Boolean    = true
  override def isCancelled: Boolean = true
}

class CswServicesMock(sequencerId: String, observingMode: String, sequencer: SequenceOperator)(implicit system: ActorSystem)
    extends CswServices(sequencerId, observingMode, sequencer, null, null, null, null, null) {
  val commandResponseF: Future[CommandResponse] = Future.successful(CommandResponse.Completed(Id("dummy-id")))
  val submitResponseF: Future[SubmitResponse]   = Future.successful(CommandResponse.Completed(Id("dummy-id")))

  override def sequencerCommandService(subSystemSequencerId: String): Future[SequencerCommandService] =
    Future.successful(SequencerCommandServiceMock$)
  override def submit(assemblyName: String, command: ControlCommand): Future[SubmitResponse]             = submitResponseF
  override def submitAndSubscribe(assemblyName: String, command: ControlCommand): Future[SubmitResponse] = submitResponseF
  override def oneway(assemblyName: String, command: ControlCommand): Future[CommandResponse]            = commandResponseF
  override def subscribe(eventKeys: Set[EventKey])(callback: Event => Done)(implicit strandEc: StrandEc): EventSubscription =
    EventSubscriptionMock
  override def publish(every: FiniteDuration)(eventGeneratorBlock: => Event)(implicit strandEc: StrandEc): Cancellable =
    CancellableMock
  override def publish(event: Event): Future[Done]   = Future.successful(Done)
  override def sendResult(msg: String): Future[Done] = Future.successful(Done)
}

object CswServicesMock {
  def create(sequencer: SequenceOperator)(implicit system: ActorSystem): CswServices =
    new CswServicesMock("sequencer1", "mode1", sequencer)
}

object SequencerFactory {
  def create()(implicit system: ActorSystem): SequenceOperator = {
    lazy val sequencerRef: ActorRef[SequencerMsg] = system.spawn(SequencerBehaviour.behavior(null), "sequencer")
    new SequenceOperator(sequencerRef, system)
  }
}
