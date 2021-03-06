package ui.todo.components

import typings.reactDashDomLib.reactDashDomMod.{^ => ReactDom}
import typings.reactLib.dsl._
import typings.reactLib.reactMod.FC
import typings.stdLib.{^, Element}
import ui.todo.context.Context
import ui.todo.lib.JsUnit

object ReactApp {

  def render(): Unit = {
    ReactDom.render(
      ReactApp.Component.noprops(),
      ^.document.getElementById("todo").asInstanceOf[Element]
    )
  }

  val Component: FC[JsUnit] = define.fc[JsUnit] { _ =>
    println("**** rendering App")

    Context.Provider.noprops(
      AddTodo.Component.noprops(),
      TodoListComp.Component.noprops(),
      Footer.Component.noprops()
    )
  }

}
