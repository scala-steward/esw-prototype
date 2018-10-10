package ocs.react4s.app

import com.github.ahnfelt.react4s._
import ocs.api.WebClients
import ocs.api.models.SequencerInfo
import ocs.react4s.app.assembly.{AssemblyCommandComponent, AssemblySetupComponent}

case class PageComponent(page: P[Page]) extends Component[NoEmit] {
  override def render(get: Get): Node = {
    get(page) match {
      case Home                                      => Component(ListComponent, WebClients.listSequencers)
      case SequencerWithMode(mode, Sequencer(id, _)) => Component(SequencerComponent, SequencerInfo(id, mode))
      case Assembly(assemblyName, _)                 => Component(AssemblyCommandComponent, WebClients.assemblyCommandClient(assemblyName))
      case FilterAssembly(filterName, Assembly(assemblyName, _)) =>
        Component(AssemblySetupComponent, filterName, WebClients.assemblyCommandClient(assemblyName))
      case _ => E.div(Text("invalid route"))
    }
  }
}
