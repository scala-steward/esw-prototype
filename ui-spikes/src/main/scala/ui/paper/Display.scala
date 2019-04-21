package ui.paper

import com.raquo.airstream.signal.{Signal, Var}
import typings.paperLib.paperMod.{Path, Point => PPoint}
import typings.paperLib.paperNs

import scala.scalajs.js.|

class Display(radius: Int, maxRows: Int) extends MyOwner {
  lazy val cells: List[Cell]                       = new HoneycombFactory(radius, maxRows).create().trimmedCells
  lazy val mirrors: List[Mirror]                   = cells.map(cell => new Mirror(cell))
  lazy val mirrorsByRow: Map[Int, List[Mirror]]    = mirrors.groupBy(_.cell.row)
  lazy val mirrorByPosition: Map[Position, Mirror] = mirrors.map(m => m.cell.position -> m).toMap

  private lazy val externalService = new ExternalService

  def render(): Unit = {
    mirrors
    println(cells.length)
    cells.foreach(println)

    externalService.positions.foreach { position =>
      mirrorByPosition.get(position).foreach { mirror =>
        mirror.click()
      }
    }
  }

  val Colors = List("#E7CFA0", "#7CC1D2", "#A97FFF")

  class Mirror(val cell: Cell) extends MyOwner {
    def click(): Unit = clicked.set(!clicked.now())

    private val clicked = Var(false)

    val color: Signal[String] = clicked.signal.map {
      case false => Colors(cell.sector % 3)
      case true  => "red"
    }

    private lazy val mirrorsWithSameRow = mirrorsByRow.getOrElse(cell.row, Nil)

    val path = new Path() {
      color.foreach(x => fillColor = x)
      strokeColor = "white"
      override def onClick(event: paperNs.MouseEvent): Unit | Boolean = mirrorsWithSameRow.foreach(_.click())
    }

    cell.hexagon.vertices.foreach(p => path.add(new PPoint(p.x, p.y)))
  }
}
