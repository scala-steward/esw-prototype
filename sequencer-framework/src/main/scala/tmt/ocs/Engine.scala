package tmt.ocs

import akka.Done
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import tmt.ocs.dsl.Script

import scala.async.Async._
import scala.concurrent.Future

class Engine(implicit mat: Materializer) {
  import mat.executionContext

  def start(sequencer: Sequencer, script: Script): Future[Done] = {
    Source.repeat(()).mapAsync(1)(_ => processStep(sequencer, script)).runForeach(_ => ())
  }

  def processStep(sequencer: Sequencer, script: Script): Future[Done] = async {
    val step              = await(sequencer.next)
    val aggregateResponse = await(script.execute(step.command))
    sequencer.update(aggregateResponse)
    Done
  }
}
