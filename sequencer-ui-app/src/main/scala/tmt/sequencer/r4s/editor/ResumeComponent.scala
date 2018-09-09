package tmt.sequencer.r4s.editor

import com.github.ahnfelt.react4s._
import tmt.sequencer.client.SequenceEditorClient
import tmt.sequencer.codecs.SequencerJsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class ResumeComponent(client: P[SequenceEditorClient]) extends Component[NoEmit] with SequencerJsonSupport {

  val resumeResponse = State("")

  def handleResume(client: SequenceEditorClient): Unit = client.resume().onComplete {
    case Success(_)  => resumeResponse.set("Operation Successful")
    case Failure(ex) => resumeResponse.set(ex.getMessage)
  }

  override def render(get: Get): ElementOrComponent = {
    E.div(
      A.className("card-panel", "hoverable"),
      E.h6(Text("Sequence Editor Resume")),
      E.div(
        E.div(
          E.a(
            A.className("btn-large"),
            E.i(
              A.className("material-icons"),
              Text("play_arrow"),
              A.onClick { e =>
                e.preventDefault()
                handleResume(get(client))
              }
            )
          ),
          E.span(Text(get(resumeResponse)))
        )
      )
    )
  }
}
