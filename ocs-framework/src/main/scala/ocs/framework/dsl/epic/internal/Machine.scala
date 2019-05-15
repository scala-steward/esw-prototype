package ocs.framework.dsl.epic.internal

import akka.Done
import ocs.framework.dsl.FutureUtils
import ocs.framework.dsl.epic.{ProgramContext, Refreshable}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

abstract class Machine[State](name: String, init: State)(implicit programContext: ProgramContext) extends Refreshable {
  type Logic = State => Unit

  def logic: Logic

  private var currentState: State  = _
  private var previousState: State = _

  import programContext._

  implicit lazy val Refreshable: Refreshable = this

  protected def become(state: State): Unit = {
    currentState = state
  }

  def refresh(source: String): Future[Done] = {
    Future {
      println(
        f"machine = $name%-8s    previousState = $previousState%-8s     currentState = $currentState%-8s    action = $source%-8s     $debugString%8s"
      )
      logic(currentState)
      Done
    }
  }

  def when(condition: => Boolean = true)(body: => Unit): Unit = {
    previousState = currentState
    if (condition) {
      body
      refresh("when")
    }
  }

  def when(delay: FiniteDuration)(body: => Unit): Unit = {
    FutureUtils.timeout(delay, strandEc.executorService).onComplete { _ =>
      when() {
        body
      }
    }
  }

  def entry(body: => Unit): Unit = {
    if (currentState != previousState) {
      body
    }
  }

  def debugString: String = ""

  implicit def varToT[T](reactive: Var[T]): T = reactive.get
}

object Machine {
  type Logic[T] = T => Unit
}
