package ocs.framework.dsl.epic.internal.event

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.{ActorMaterializer, KillSwitches, Materializer, UniqueKillSwitch}
import io.lettuce.core.{RedisClient, RedisURI}
import play.api.libs.json.Format
import reactor.core.publisher.FluxSink.OverflowStrategy
import romaine.RomaineFactory
import romaine.async.RedisAsyncApi
import romaine.reactive.RedisSubscriptionApi

import scala.concurrent.{ExecutionContext, Future}

class RedisEventService(ecTmp: ExecutionContext) extends EpicsEventService {

  private val factory                                                   = new RomaineFactory(RedisClient.create())(ecTmp)
  private val redisUri: Future[RedisURI]                                = Future.successful(RedisURI.create("localhost", 6379))
  private def asyncApi[T: Format]: RedisAsyncApi[String, EpicsEvent[T]] = factory.redisAsyncApi[String, EpicsEvent[T]](redisUri)

  private def subscriptionApi[T: Format]: RedisSubscriptionApi[String, EpicsEvent[T]] =
    factory.redisSubscriptionApi[String, EpicsEvent[T]](redisUri)

  implicit val ec: ExecutionContext     = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  implicit val actorSystem: ActorSystem = ActorSystem("server")
  implicit val mat: Materializer        = ActorMaterializer()

  def get[T: Format](key: String): Future[Option[EpicsEvent[T]]] = asyncApi.get(key)

  def publish[T: Format](key: String, value: EpicsEvent[T]): Future[Unit] = {
    asyncApi.publish(key, value)
    asyncApi.set(key, value).map(_ => ())
  }

  def subscribe[T: Format](key: String): Source[EpicsEvent[T], UniqueKillSwitch] = {
    subscriptionApi
      .subscribe(List(key), OverflowStrategy.LATEST)
      .map(_.value)
      .viaMat(KillSwitches.single)(Keep.right)
  }

}
