package tmt.sequencer.ui.r4s

import com.github.ahnfelt.react4s._
import tmt.sequencer.client.SequencerWebClients
import tmt.sequencer.ui.r4s.editor.EditorComponent
import tmt.sequencer.ui.r4s.facade.NpmReactBridge
import tmt.sequencer.ui.r4s.feeder.FeederComponent
import tmt.sequencer.ui.r4s.resultevent.ResultEventComponent

object Main {
  def main(arguments: Array[String]): Unit = {
    val component = E.div(
      Component(HeaderComponent),
      Component(FeederComponent, SequencerWebClients.feeder),
      Component(EditorComponent, SequencerWebClients.editor),
      Component(ResultEventComponent, SequencerWebClients.logger)
    )
    NpmReactBridge.renderToDomById(component, "main")
  }
}
