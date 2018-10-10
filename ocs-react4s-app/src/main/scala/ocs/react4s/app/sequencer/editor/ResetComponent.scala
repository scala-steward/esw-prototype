package ocs.react4s.app.sequencer.editor

import com.github.ahnfelt.react4s._
import ocs.api.client.SequenceEditorJsClient
import ocs.api.codecs.SequencerJsonSupport
import ocs.react4s.app.theme.ButtonCss

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class ResetComponent(client: P[SequenceEditorJsClient]) extends Component[NoEmit] with SequencerJsonSupport {

  val resetResponse = State("")

  def handleReset(client: SequenceEditorJsClient): Unit = client.reset().onComplete {
    case Success(_)  => resetResponse.set("Operation Successful")
    case Failure(ex) => resetResponse.set(ex.getMessage)
  }

  override def render(get: Get): ElementOrComponent = {
    E.div(
      A.className("card-panel", "hoverable"),
      E.h6(Text("Sequence Editor Reset")),
      E.button(
        ButtonCss,
        Text("Reset Sequence"),
        A.onClick { e =>
          e.preventDefault()
          handleReset(get(client))
        }
      ),
      Text(get(resetResponse))
    )
  }
}
