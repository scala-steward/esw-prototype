package ocs.framework.dsl

import akka.actor.typed.scaladsl.adapter._
import akka.actor.{typed, ActorSystem, Cancellable}
import akka.util.Timeout
import akka.{util, Done}
import com.typesafe.config.ConfigFactory
import csw.command.scaladsl.CommandService
import csw.event.api.scaladsl.{EventService, EventSubscription}
import csw.location.api.models.ComponentType
import csw.params.commands.{CommandResponse, ControlCommand}
import csw.params.events.{Event, EventKey}
import ocs.api.client.{SequenceEditorJvmClient, SequenceFeederJvmClient}
import ocs.api.messages.SupervisorMsg
import ocs.api.{SequenceEditor, SequenceFeeder, SequencerUtil}
import ocs.framework.Sequencer
import ocs.framework.util.LocationServiceGateway
import romaine.RomaineFactory
import romaine.async.RedisAsyncApi
import sequencer.macros.StrandEc

import scala.async.Async._
import scala.concurrent.duration.{DurationDouble, FiniteDuration}
import scala.concurrent.{Await, Future}

class CswServices(
    val sequencerId: String,
    val observingMode: String,
    val sequencer: Sequencer, //this param is carried only to be passed to the Script
    locationService: LocationServiceGateway,
    eventService: EventService,
    romaineFactory: RomaineFactory
)(implicit system: ActorSystem) {

  implicit val typedSystem: typed.ActorSystem[Nothing] = system.toTyped

  private lazy val masterId: String = ConfigFactory.load().getString("csw-event.redis.masterId")

  private lazy val redisAsyncScalaApi: RedisAsyncApi[String, String] = {
    romaineFactory.redisAsyncApi(locationService.redisUrI(masterId))
  }

  def sequenceFeeder(subSystemSequencerId: String): SequenceFeeder = {
    val componentName = SequencerUtil.getComponentName(subSystemSequencerId, observingMode)
    val eventualFeederImpl = locationService.resolve(componentName, ComponentType.Sequencer) { akkaLocation =>
      async {
        val supervisorRef = akkaLocation.actorRef.upcast[SupervisorMsg]
        new SequenceFeederJvmClient(supervisorRef)
      }(system.dispatcher)
    }
    Await.result(eventualFeederImpl, 5.seconds)
  }

  def sequenceEditor(subSystemSequencerId: String): SequenceEditor = {
    val componentName = SequencerUtil.getComponentName(subSystemSequencerId, observingMode)
    val eventualEditorImpl = locationService.resolve(componentName, ComponentType.Sequencer) { akkaLocation =>
      async {
        val supervisorRef = akkaLocation.actorRef.upcast[SupervisorMsg]
        new SequenceEditorJvmClient(supervisorRef)
      }(system.dispatcher)
    }
    Await.result(eventualEditorImpl, 5.seconds)
  }

  def submit(assemblyName: String, command: ControlCommand): Future[CommandResponse] = {
    locationService.resolve(assemblyName, ComponentType.Assembly) { akkaLocation =>
      async {
        implicit val timeout: Timeout = util.Timeout(10.seconds)
        val response                  = await(new CommandService(akkaLocation).submit(command))
        println(s"Response - $response")
        response
      }(system.dispatcher)
    }
  }

  def submitAndSubscribe(assemblyName: String, command: ControlCommand): Future[CommandResponse] = {
    locationService.resolve(assemblyName, ComponentType.Assembly) { akkaLocation =>
      async {
        implicit val timeout: Timeout = util.Timeout(10.seconds)
        val response                  = await(new CommandService(akkaLocation).submitAndSubscribe(command))
        println(s"Response - $response")
        response
      }(system.dispatcher)
    }
  }

  def oneway(assemblyName: String, command: ControlCommand): Future[CommandResponse] = {
    locationService.resolve(assemblyName, ComponentType.Assembly) { akkaLocation =>
      async {
        implicit val timeout: Timeout = util.Timeout(10.seconds)
        val response                  = await(new CommandService(akkaLocation).oneway(command))
        println(s"Response - $response")
        response
      }(system.dispatcher)
    }
  }

  def subscribe(eventKeys: Set[EventKey])(callback: Event => Done)(implicit strandEc: StrandEc): EventSubscription = {
    println(s"==========================> Subscribing event $eventKeys")
    eventService.defaultSubscriber.subscribeAsync(eventKeys, e => Future(callback(e))(strandEc.ec))
  }

  def publish(every: FiniteDuration)(eventGeneratorBlock: => Event): Cancellable = {
    println(s"=========================> Publishing event $eventGeneratorBlock every $every")
    eventService.defaultPublisher.publish(eventGeneratorBlock, every)
  }

  def publish(event: Event): Future[Done] = {
    println(s"=========================> Publishing event $event")
    eventService.defaultPublisher.publish(event)
  }

  def sendResult(msg: String): Future[Done] = {
    redisAsyncScalaApi.publish(s"$sequencerId-$observingMode", msg).map(_ => Done)(system.dispatcher)
  }
}
