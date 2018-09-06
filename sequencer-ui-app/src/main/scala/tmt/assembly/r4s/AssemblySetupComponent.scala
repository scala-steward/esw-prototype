package tmt.assembly.r4s

import com.github.ahnfelt.react4s._
import tmt.assembly.client.AssemblyCommandWebClient
import tmt.sequencer.codecs.SequencerRWSupport
import tmt.sequencer.r4s.theme.{ButtonCss, TextAreaCss}
import tmt.util.FilterWheelUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class AssemblySetupComponent(filterName: P[String], client: P[AssemblyCommandWebClient])
    extends Component[NoEmit]
    with SequencerRWSupport {
  val submitResponse           = State("")
  val commandType              = State("")
  val movePosition: State[Int] = State(0)

  def handleSubmit(client: AssemblyCommandWebClient, commandType: String, movePosition: Int): Unit = {
    submitResponse.set("Waiting for Response ....")
    val command = FilterWheelUtil.createMoveCommand(commandType, movePosition)
    client.submit(command).onComplete {
      case Success(response) => submitResponse.set(upickle.default.write(response, 2))
      case Failure(ex)       => submitResponse.set(ex.getMessage)
    }
  }

  override def render(get: Get): ElementOrComponent = {
    E.div(
      A.className("card-panel", "hoverable"),
      E.h6(Text("Assembly Setup command")),
      E.div(
        Text("Command Type"),
        E.span(
          E.input(
            A.onChangeText(commandType.set),
            A.value(get(commandType))
          )
        )
      ),
      E.div(
        Text("Move position coordinate"),
        E.span(
          E.input(
            A.onChangeText(x => movePosition.set(x.toInt)),
            A.value(get(movePosition).toString)
          )
        )
      ),
      E.button(
        ButtonCss,
        Text("Submit"),
        A.onClick { e =>
          e.preventDefault()
          handleSubmit(get(client), get(commandType), get(movePosition))
        }
      ),
      E.div(
        Text("Submit Response"),
        E.div(
          TextAreaCss,
          E.span(E.pre(Text(get(submitResponse))))
        )
      )
    )
  }
}
