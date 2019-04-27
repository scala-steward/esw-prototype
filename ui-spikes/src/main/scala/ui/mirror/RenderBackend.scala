package ui.mirror

import com.raquo.airstream.signal.Signal

trait RenderBackend {
  def center: Point
  def setup(width: Int, height: Int, store: Store): Unit
  def draw(cell: Cell, color: Signal[String], store: Store): Unit
  def postDraw(): Unit
}
