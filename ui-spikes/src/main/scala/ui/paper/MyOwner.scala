package ui.paper

import com.raquo.airstream.ownership.Owner

trait MyOwner {
  implicit val owner: Owner = new Owner {}
}
