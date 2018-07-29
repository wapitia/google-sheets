package com.wapitia.spreadsheet.marshalx

import com.wapitia.common.marshal.InMarshal

/** Holds a collection of heterogeneous `LabelledRowMarshaller`s indexed
 *  by some tag `String`.
 *
 *  @tparam A finished row object or builder/marshaller
 *            of such row objects
 */
trait LabelledRowMarshalX[O,+A] {

  def cellMarshallMap: LabelledInMarshalMapX
  def build(): O
  def rowObjectMarshals: Map[String, (_,_) => Unit]

  def set[MR <: LabelledRowMarshalX[O,_],C](marshalContainer: MR, key: String, rawvalue: Any): Unit = {
    import LabelledRowMarshalX._
    val cellMarshal = cellMarshallMap.getCellMarshal[C](key)

    if (!cellMarshal.isNull(rawvalue)) {
      val mval: C = cellMarshal.unmarshal(rawvalue)

      val f: ((MR,C) => Unit) = rowObjectMarshals.getOrElse(key, NOOP _).asInstanceOf[(MR,C) => Unit]
      f(marshalContainer,mval)
    }
  }

}

object LabelledRowMarshalX {

  def NOOP[MR,CT](msh: MR, v: CT) {}

}
