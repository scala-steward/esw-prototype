package tmt.sequencer.ui.r4s.feeder

import com.github.ahnfelt.react4s._
import tmt.sequencer.SequenceFeederClient
import tmt.sequencer.models.{AggregateResponseWeb, CommandListWeb, WebRWSupport}
import tmt.sequencer.ui.r4s.IOOperationComponent
import tmt.sequencer.ui.r4s.IOOperationComponent.HandleClick

import scala.util.{Failure, Success}

case class FeederComponent(client: P[SequenceFeederClient]) extends Component[NoEmit] with WebRWSupport {
  val feedResponse = State("")

  import scala.concurrent.ExecutionContext.Implicits.global

  def loadingOutput(): Unit = feedResponse.set("Waiting for Command Response ....")

  def handleFeed(get: Get, msg: IOOperationComponent.Msg): Unit = msg match {
    case HandleClick(request) =>
      loadingOutput()
      get(client).feed(upickle.default.read[CommandListWeb](request)).onComplete {
        case Success(response) => feedResponse.set(upickle.default.write[AggregateResponseWeb](response, 2))
        case Failure(ex)       => feedResponse.set(ex.getMessage)
      }
  }

  override def render(get: Get): ElementOrComponent = {
    E.div(
      Component(IOOperationComponent, "Sequence Feeder - Feed", "Feed Sequence", get(feedResponse), get(client))
        .withHandler(x => handleFeed(get, x))
    )
  }
}
