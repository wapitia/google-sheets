package com.wapitia
package spreadsheet
package marshalx

import com.wapitia.common.marshal.InMarshal

class MarshalKit {
  val marshalMap = new LabelledInMarshalMapX
  val rowObjectMarshalsX = scala.collection.mutable.Map[String, (_,_) => Unit]()

  def marshalChain(name: String, marshal: InMarshal[Any,Any], binder: (_,_) => Unit)  {
    marshalMap.addCellMarshal(name, marshal)
    rowObjectMarshalsX.put(name, binder)
  }

  def rowObjectMarshals: Map[String, (_,_) => Unit] = rowObjectMarshalsX.toMap
}
