package scripts.mocks

import akka.Done
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.{ActorSystem, Cancellable}
import csw.event.api.scaladsl.EventSubscription
import csw.params.commands.{CommandResponse, ControlCommand, SequenceCommand}
import csw.params.core.models.Id
import csw.params.events.{Event, EventKey}
import org.tmt.macros.StrandEc
import tmt.ocs.api.SequenceFeeder
import tmt.ocs.dsl.CswServices
import tmt.ocs.messages.SequencerMsg
import tmt.ocs.models.{AggregateResponse, CommandList}
import tmt.ocs.{Sequencer, SequencerBehaviour}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object SequenceFeederMock extends SequenceFeeder {
  override def feed(commandList: CommandList): Future[Unit] = Future.successful(())
  override def submit(commandList: CommandList): Future[AggregateResponse] = Future.successful(
    AggregateResponse(CommandResponse.Completed(Id("dummy-id")))
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

object CswServicesMock {
  def createSequencer(system: ActorSystem): Sequencer = {
    lazy val sequencerRef: ActorRef[SequencerMsg] = system.spawn(SequencerBehaviour.behavior, "sequencer")
    new Sequencer(sequencerRef, system)
  }

  def create()(implicit system: ActorSystem): CswServices =
    new CswServices(null, null, createSequencer(system), null, null, null) {
      val commandResponseF: Future[CommandResponse] = Future.successful(CommandResponse.Completed(Id("dummy-id")))

      override def sequenceFeeder(subSystemSequencerId: String): SequenceFeeder                               = SequenceFeederMock
      override def setup(assemblyName: String, command: SequenceCommand): Future[CommandResponse]             = commandResponseF
      override def submit(assemblyName: String, command: ControlCommand): Future[CommandResponse]             = commandResponseF
      override def submitAndSubscribe(assemblyName: String, command: ControlCommand): Future[CommandResponse] = commandResponseF
      override def oneway(assemblyName: String, command: ControlCommand): Future[CommandResponse]             = commandResponseF
      override def subscribe(eventKeys: Set[EventKey])(callback: Event => Done)(implicit strandEc: StrandEc): EventSubscription =
        EventSubscriptionMock
      override def publish(every: FiniteDuration)(eventGeneratorBlock: => Event): Cancellable = CancellableMock
      override def publish(event: Event): Future[Done]                                        = Future.successful(Done)
      override def sendResult(msg: String): Future[Done]                                      = Future.successful(Done)
    }
}
