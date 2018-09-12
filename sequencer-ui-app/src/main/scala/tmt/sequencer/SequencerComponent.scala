package tmt.sequencer
import com.github.ahnfelt.react4s._
import tmt.WebClients
import tmt.ocs.models.SequencerInfo
import tmt.sequencer.r4s.editor.EditorComponent
import tmt.sequencer.r4s.feeder.FeederComponent
import tmt.sequencer.r4s.resultevent.ResultEventComponent

case class SequencerComponent(sequencerInfo: P[SequencerInfo]) extends Component[NoEmit] {

  override def render(get: Get): ElementOrComponent = {
    E.div(
      A.className("row"),
      E.div(
        A.className("col s6"),
        Component(FeederComponent, WebClients.feeder(get(sequencerInfo))),
        Component(EditorComponent, WebClients.editor(get(sequencerInfo)))
      ),
      E.div(
        A.className("col s6"),
        Component(ResultEventComponent, WebClients.results(get(sequencerInfo)))
      )
    )
  }
}
