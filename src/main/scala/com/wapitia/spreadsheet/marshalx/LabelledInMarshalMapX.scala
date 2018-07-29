package com.wapitia
package spreadsheet
package marshalx

import com.wapitia.common.marshal.InMarshal

/** Holds a `Map` from `String` tags to corresponding `InMarshal`s.
 */
class LabelledInMarshalMapX {

  import LabelledInMarshalMapX._

  var cellMarshallers: Map[String,InMarshal[Any,Any]] = Map()

  def addCellMarshal[T <: Any](key: String, marshalIn: InMarshal[Any,T]) {
    cellMarshallers += (key -> marshalIn)
  }

  def getCellMarshal[T <: Any](marshalName: String): InMarshal[Any,T] =
    cellMarshallers.getOrElse(marshalName, MarshalIdentity).asInstanceOf[InMarshal[Any,T]]
}

object LabelledInMarshalMapX {

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

}
