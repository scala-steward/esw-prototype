package ui.events

import com.raquo.airstream.signal.{StrictSignal, Var}

class Counter {
  private val value             = Var(0)
  def signal: StrictSignal[Int] = value.signal
  def increment(): Unit         = value.update(_ + 1)
  def decrement(): Unit         = value.update(_ - 1)
}
