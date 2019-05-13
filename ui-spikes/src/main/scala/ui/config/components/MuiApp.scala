package ui.config.components

import typings.cswDashAasDashJsLib.cswDashAasDashJsMod.^.AuthContextProvider
import typings.cswDashAasDashJsLib.cswDashAasDashJsMod.{AuthConfig, AuthContextProviderProps}
import typings.reactDashDomLib.reactDashDomMod.{^ ⇒ ReactDom}
import typings.reactLib.dsl._
import typings.reactLib.reactMod.FC
import typings.stdLib.{^, Element}
import ui.config.context.contexts._
import ui.todo.lib.JsUnit

object MuiApp {

  def render(): Unit = {
    ReactDom.render(
      MuiApp.App.noprops(),
      ^.document.getElementById("todo").asInstanceOf[Element]
    )
  }

  private val AuthProvider: FC[JsUnit] = define.fc[JsUnit] { props =>
    AuthContextProvider.props(
      AuthContextProviderProps(props.children.getOrElse(null), AuthConfig("csw-config-app", "TMT")),
      props.children.getOrElse(null)
    )
  }

  private val App = define.fc[JsUnit] { _ =>
    AuthProvider.noprops(
      ConfigProvider.noprops(
        UiProvider.noprops(
          ConfigApp.Component.noprops()
        )
      )
    )
  }

}
