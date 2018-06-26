package scripts.iris

import tmt.sequencer.ScriptImports._

class IrisDarkNight(cs: CswServices) extends Script(cs) {

  var eventCount   = 0
  var commandCount = 0

  val cancellable = cs.publish(5.seconds) {
    SystemEvent(Prefix("iris-test"), EventName("system"))
  }

  val subscription = cs.subscribe(Set(EventKey("iris-test.system"))) { eventKey =>
    eventCount = eventCount + 1
    println(s"------------------> received-event on key: $eventKey")
    Done
  }

  cs.handleCommand("setup-iris") { command =>
    spawn {
      var firstAssemblyResponse: CommandResponse = null
      var counter                                = 0
      loop {
        spawn {
          counter += 1
          firstAssemblyResponse = cs.setup("Sample1Assembly", command).await
          println(counter)
          stopWhen(counter > 2)
        }
      }.await
      println(s"[Iris] Received command: ${command.commandName}")
      val response = AggregateResponse
        .add(firstAssemblyResponse)
        .markSuccessful(command)

      println(s"[Iris] Received response: $response")
      response
    }
  }

  override def onShutdown(): Future[Done] = spawn {
    subscription.cancel().await
    cancellable.cancel().await
    println("shutdown iris")
    Done
  }
}
