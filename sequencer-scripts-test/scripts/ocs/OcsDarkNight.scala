package ocs

import ocs.framework.ScriptImports._
import ocs.framework.dsl

class OcsDarkNight(csw: CswServices) extends dsl.Script(csw) {

  private val iris = csw.sequencerCommandService("iris")
  private val tcs  = csw.sequencerCommandService("tcs")

  private var eventCount   = 0
  private var commandCount = 0

  handleSetupCommand("setup-iris") { commandA =>
    spawn {
      println(s"[Ocs] Received command: ${commandA.commandName}")
      csw.sendResult(s"[Ocs] Received command: ${commandA.commandName}")
      var topLevelCommandIds: Set[Id] = Set(commandA.runId)
      var subCommandIds: Set[Id]      = Set.empty

      val maybeCommandB = nextIf(c => c.commandName.name == "setup-iris").await
      val subCommandsB = if (maybeCommandB.isDefined) {
        val commandB = maybeCommandB.get
        topLevelCommandIds += commandB.runId

        csw.addOrUpdateCommand(commandB.runId, CommandResponse.Started(commandB.runId))
        val commandB1 = Setup(Prefix("test-commandB1"), CommandName("setup-iris"), Some(ObsId("test-obsId")))
        val commandB2 = Setup(Prefix("test-commandB2"), CommandName("setup-iris"), Some(ObsId("test-obsId")))

        subCommandIds = subCommandIds ++ Set(commandB1.runId, commandB2.runId)

        csw.addSubCommand(commandB.runId, commandB1.runId)
        csw.addSubCommand(commandB.runId, commandB2.runId)
        Sequence.from(commandB1, commandB2)
      } else Sequence.empty

      val commandList    = subCommandsB.add(commandA)
      val submitResponse = iris.await.submit(commandList).await

      csw.addSequenceResponse(topLevelCommandIds, subCommandIds, submitResponse)
      Done
    }
  }

  handleSetupCommand("setup-iris-tcs") { commandC =>
    spawn {
      val maybeCommandD = nextIf(c2 => c2.commandName.name == "setup-iris-tcs").await
      val tcsSequence = if (maybeCommandD.isDefined) {
        val commandD = maybeCommandD.get
        Sequence.from(commandD)
      } else {
        Sequence.empty
      }

      println(s"[Ocs] Received command: ${commandC.commandName}")
      val irisSequence = Sequence.from(commandC)

//      parAggregate(
      iris.await.submit(irisSequence).await
      tcs.await.submit(tcsSequence).await
//      ).await

      val commandCResponse = Completed(commandC.runId)

      val response = if (maybeCommandD.isDefined) {
        AggregateResponse(commandCResponse, Completed(maybeCommandD.get.runId))
      } else {
        AggregateResponse(commandCResponse)
      }

      println(s"[Ocs] Received response: $response")
      csw.sendResult(s"$response")

      Done
    }
  }

  handleSetupCommand("setup-tcs") { command =>
    spawn {
      println(s"[Ocs] Received command: ${command.commandName}")

      tcs.await.submit(Sequence.from(command)).await

      val response = AggregateResponse(Completed(command.runId))
      println(s"[Ocs] Received response: $response")
      csw.sendResult(s"$response")
      Done
    }
  }

  override def onShutdown(): Future[Done] = spawn {
    println("shutdown ocs")
    Done
  }
}
