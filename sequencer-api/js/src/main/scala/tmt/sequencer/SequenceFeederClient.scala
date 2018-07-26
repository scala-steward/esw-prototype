package tmt.sequencer

import tmt.sequencer.api.SequenceFeederWeb
import tmt.sequencer.models.{AggregateResponseWeb, CommandListWeb, WebRWSupport}

import scala.concurrent.Future

class SequenceFeederClient(gateway: WebGateway) extends SequenceFeederWeb with WebRWSupport {

  override def feed(commandList: CommandListWeb): Future[Unit] = gateway.post(
    url = s"${SequenceFeederWeb.ApiName}/${SequenceFeederWeb.Feed}",
    data = upickle.default.write(commandList)
  )
}
