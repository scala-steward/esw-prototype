package tmt.sequencer.ui.apps

import tmt.sequencer.WebGateway
import tmt.sequencer.client.SequenceFeederClient
import tmt.sequencer.models.{CommandListWeb, SequenceCommandWeb, WebRWSupport}
import ujson.Js

object JsDemoApp extends WebRWSupport {
  def main2(args: Array[String]): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global

    val gateway = new WebGateway()
    val client  = new SequenceFeederClient(gateway)

    val commandListWeb = CommandListWeb(
      Seq(
        SequenceCommandWeb(
          "Setup",
          "test1",
          "setup-iris",
          Some("test-obsId1"),
          Js.Arr()
        )
      )
    )

    println(upickle.default.write(commandListWeb))

    val res = client.feed(commandListWeb)
    res.onComplete(println)
  }
}
