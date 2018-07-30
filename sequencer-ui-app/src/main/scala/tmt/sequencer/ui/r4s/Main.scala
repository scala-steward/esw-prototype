package tmt.sequencer.ui.r4s

import com.github.ahnfelt.react4s._
import tmt.sequencer._
import tmt.sequencer.ui.r4s.editor.EditorComponent
import tmt.sequencer.ui.r4s.facade.NpmReactBridge
import tmt.sequencer.ui.r4s.feeder.FeederComponent
import tmt.sequencer.ui.r4s.resultevent.{ImageComponent, ResultEventComponent}

object Main {
  def main(arguments: Array[String]): Unit = {
    val component = E.div(
      Component(HeaderComponent),
      Component(FeederComponent, SequencerClient.feeder),
      Component(EditorComponent, SequencerClient.editor),
      Component(ResultEventComponent, SequencerClient.logger),
      Component(ImageComponent, SequencerClient.imageEventSource)
    )
    NpmReactBridge.renderToDomById(component, "main")
  }
}
