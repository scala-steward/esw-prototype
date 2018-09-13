package tmt.ocs.util

import csw.messages.commands._

//discuss wait or delte
object CswCommandAdapter {
  def setupCommandFrom(setup: SequenceCommand): Setup = {
    import setup._
    Setup(source, commandName, maybeObsId)
  }

  def observeCommandFrom(observe: SequenceCommand): Observe = {
    import observe._
    Observe(source, commandName, maybeObsId)
  }
}
