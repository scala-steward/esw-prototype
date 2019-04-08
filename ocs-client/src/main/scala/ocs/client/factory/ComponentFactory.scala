package ocs.client.factory

import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.{typed, ActorSystem}
import csw.command.api.scaladsl.CommandService
import csw.command.client._
import csw.location.api.models.{AkkaLocation, ComponentType}
import ocs.api.client.{ScriptLoaderCommandServiceJvmClient, SequenceEditorJvmClient, SequencerCommandServiceJvmClient}
import ocs.api.messages.{ScriptCommand, SupervisorMsg}
import ocs.api.{SequenceEditor, SequencerCommandService, SequencerUtil}

import scala.concurrent.Future

class ComponentFactory(locationService: LocationServiceWrapper)(implicit system: ActorSystem) {

  implicit val typedSystem: typed.ActorSystem[Nothing] = system.toTyped

  def assemblyCommandService(assemblyName: String): Future[CommandService] = {
    locationService.resolve(assemblyName, ComponentType.Assembly)(akkaLocation => CommandServiceFactory.make(akkaLocation))
  }

  def hcdCommandService(hcdName: String): Future[CommandService] = {
    locationService.resolve(hcdName, ComponentType.HCD)(akkaLocation => CommandServiceFactory.make(akkaLocation))
  }

  def assemblyLocation(assemblyName: String): Future[AkkaLocation] = {
    locationService.resolve(assemblyName, ComponentType.Assembly)(identity)
  }

  def scriptLoaderCommandService(scriptLoaderName: String): Future[ScriptLoaderCommandServiceJvmClient] = {
    locationService.resolve(scriptLoaderName, ComponentType.Service) { akkaLocation =>
      val scriptLoaderRef = akkaLocation.actorRef.unsafeUpcast[ScriptCommand]
      new ScriptLoaderCommandServiceJvmClient(scriptLoaderRef, system)
    }
  }

  def sequenceCommandService(sequencerId: String, observingMode: String): Future[SequencerCommandService] = {
    val componentName = SequencerUtil.getComponentName(sequencerId, observingMode)
    locationService.resolve(componentName, ComponentType.Sequencer) { akkaLocation =>
      val supervisorRef = akkaLocation.actorRef.unsafeUpcast[SupervisorMsg]
      new SequencerCommandServiceJvmClient(supervisorRef)
    }
  }

  def sequenceEditor(subSystemSequencerId: String, observingMode: String): Future[SequenceEditor] = {
    val componentName = SequencerUtil.getComponentName(subSystemSequencerId, observingMode)
    locationService.resolve(componentName, ComponentType.Sequencer) { akkaLocation =>
      val supervisorRef = akkaLocation.actorRef.unsafeUpcast[SupervisorMsg]
      new SequenceEditorJvmClient(supervisorRef)
    }
  }
}
