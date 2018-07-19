package tmt.sequencer.ui.r4s.editor

import com.github.ahnfelt.react4s._
import tmt.sequencer.SequenceEditorClient
import tmt.sequencer.models.WebRWSupport
import tmt.sequencer.ui.r4s.theme.{ButtonCss, OperationTitleCss}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class ResumeComponent(client: P[SequenceEditorClient]) extends Component[NoEmit] with WebRWSupport {

  val resumeResponse = State("")

  def handleResume(client: SequenceEditorClient): Unit = client.resume().onComplete {
    case Success(_)  => resumeResponse.set("Operation Successful")
    case Failure(ex) => resumeResponse.set(ex.getMessage)
  }

  override def render(get: Get): ElementOrComponent = {
    E.div(
      OperationTitleCss,
      E.button(
        ButtonCss,
        Text("Resume Sequence"),
        A.onClick { e =>
          e.preventDefault()
          handleResume(get(client))
        }
      ),
      Text(get(resumeResponse))
    )
  }
}
