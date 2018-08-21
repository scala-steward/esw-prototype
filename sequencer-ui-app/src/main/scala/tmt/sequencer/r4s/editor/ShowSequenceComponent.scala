package tmt.sequencer.r4s.editor

import com.github.ahnfelt.react4s._
import tmt.sequencer.client.SequenceEditorWebClient
import tmt.sequencer.codecs.SequencerWebRWSupport
import tmt.sequencer.r4s.theme.{ButtonCss, TextAreaCss}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class ShowSequenceComponent(client: P[SequenceEditorWebClient]) extends Component[NoEmit] with SequencerWebRWSupport {

  val sequenceResponse = State("")

  def handleShowSequence(client: SequenceEditorWebClient): Unit = client.sequenceWeb.onComplete {
    case Success(value) => sequenceResponse.set(upickle.default.write(value, indent = 2))
    case Failure(ex)    => sequenceResponse.set(ex.getMessage)
  }

  override def render(get: Get): ElementOrComponent = {
    E.div(
      A.className("card-panel", "hoverable"),
      E.h6(Text("Sequence Editor Show Sequence")),
      E.div(
        E.button(
          ButtonCss,
          Text("Show Sequence"),
          A.onClick { e =>
            e.preventDefault()
            handleShowSequence(get(client))
          }
        ),
        E.div(
          Text("Sequence Editor - Show Sequence Response"),
          E.div(
            TextAreaCss,
            E.span(E.pre(Text(get(sequenceResponse))))
          )
        )
      )
    )
  }
}
