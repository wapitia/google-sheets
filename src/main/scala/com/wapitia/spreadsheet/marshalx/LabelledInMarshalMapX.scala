package com.wapitia
package spreadsheet
package marshalx

import com.wapitia.common.marshal.InMarshal

/** Holds a `Map` from `String` tags to corresponding `InMarshal`s.
 */
class LabelledInMarshalMapX {

  import LabelledInMarshalMapX._

  var cellMarshallers: Map[String,InMarshal[Any,Any]] = Map()

  def addCellMarshal[CM <: Any](key: String, marshalIn: InMarshal[Any,CM]) {
    cellMarshallers += (key -> marshalIn)
  }

  def getCellMarshal[CM <: Any](marshalName: String): InMarshal[Any,CM] =
    cellMarshallers.getOrElse(marshalName, MarshalIdentity).asInstanceOf[InMarshal[Any,CM]]
}

object LabelledInMarshalMapX {

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

}
