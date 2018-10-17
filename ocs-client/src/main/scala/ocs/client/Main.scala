package ocs.client
import ammonite.util.Res

object Main {

  def main(args: Array[String]): Unit = {
    val wiring = new Wiring()
    import wiring._

    val (result: Res[Any], paths) = ammonite
      .Main(
        predefCode = """
                |import scala.concurrent.duration.Duration
                |import scala.concurrent.{Await, Future}
                |import csw.params.core.generics.KeyType._
                |import csw.params.commands._
                |import csw.params.core.models._
                |import ocs.api.messages.SequencerMsg._
                |import ocs.api.messages.SupervisorMsg._
                |import ocs.api.models.Sequence
                |implicit class RichFuture[T](val f: Future[T]) {
                |  def get: T = Await.result(f, Duration.Inf)
                |}
                | """.stripMargin
      )
      .run(
        "componentFactory" -> componentFactory
      )

    println(paths)
    println(result.asInstanceOf[Res.Exception].t.printStackTrace())
  }
}
