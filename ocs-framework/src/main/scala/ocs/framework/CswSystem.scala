package ocs.framework

import akka.{actor, Done}
import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter.TypedActorSystemOps
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Props}
import akka.stream.typed.scaladsl.ActorMaterializer
import akka.util.Timeout
import csw.location.api.scaladsl.LocationService
import csw.location.client.ActorSystemFactory
import csw.location.client.scaladsl.HttpLocationServiceFactory
import ocs.client.factory.LocationServiceWrapper
import ocs.framework.GuardianActor.GuardianMsg

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{Await, ExecutionContext}

class CswSystem(name: String) {

  implicit lazy val typedSystem: ActorSystem[GuardianMsg] = ActorSystemFactory.remote[GuardianMsg](GuardianActor.behavior, name)
  implicit lazy val system: actor.ActorSystem             = typedSystem.toUntyped

  implicit lazy val materializer: ActorMaterializer    = ActorMaterializer()
  implicit lazy val executionContext: ExecutionContext = typedSystem.executionContext

  implicit lazy val scheduler: Scheduler = typedSystem.scheduler
  implicit lazy val timeout: Timeout     = Timeout(5.seconds)

  lazy val locationService: LocationService               = HttpLocationServiceFactory.makeLocalClient
  lazy val locationServiceWrapper: LocationServiceWrapper = new LocationServiceWrapper(locationService, system)

  def spawn[T](behavior: Behavior[T], name: String, props: Props = Props.empty): ActorRef[T] = {
    Await.result(typedSystem ? GuardianActor.Spawn(behavior, name, props), 5.seconds)
  }

  def shutdownChildren[T](): Done = {
    Await.result(typedSystem ? GuardianActor.ShutdownChildren, 10.seconds)
  }
}
