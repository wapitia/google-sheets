package com.wapitia.spreadsheet.marshal

import com.wapitia.common.marshal.InMarshal

class LabelledCellMarshallers {

  import LabelledRowMarshaller._

  var cellMarshallers: Map[String,InMarshal[Any,Any]] = Map()

  def addCellMarshaller[MT <: Any](key: String, marshalIn: InMarshal[Any,MT]): Unit =
    cellMarshallers += (key -> marshalIn)

  def getMarshal[C <: Any](marshalName: String): InMarshal[Any,C] =
     cellMarshallers.getOrElse(marshalName, MarshalIdentity).asInstanceOf[InMarshal[Any,C]]

}

/**
 *  @tparam A finished row object or builder/marshaller
 *            of such row objects
 */
trait LabelledRowMarshaller[A] {

  def cellMarshaller: LabelledCellMarshallers

  def build(): A
  var rowObjectMarshallers: Map[String, _ => _] = Map()

  import LabelledRowMarshaller._

  def addRowItemMarshaller[MT <: Any](key: String, t: MT => _): Unit = {
    rowObjectMarshallers += (key -> t)
  }

  def set[C](key: String, rawvalue: Any): Unit = {
    val marshaler = cellMarshaller.getMarshal[C](key)

    if (!marshaler.isNull(rawvalue)) {
      val mval: C = marshaler.unmarshal(rawvalue)

      val f: (C => Unit) = rowObjectMarshallers.getOrElse(key, NOOP _).asInstanceOf[C => Unit]
      f(mval)
    }
  }

}

abstract class EmptyLabelledRowMarshaller[A] extends LabelledRowMarshaller[A] {
  override val cellMarshaller: LabelledCellMarshallers = new LabelledCellMarshallers

  override def build(): A

  def addCellMarshaller[MT <: Any](key: String, marshalIn: InMarshal[Any,MT]): Unit = {
    cellMarshaller.addCellMarshaller(key, marshalIn)
  }

  def addLookup[MT <: Any](key: String, marshalIn: InMarshal[Any,MT], t: MT => _): Unit = {
    addCellMarshaller(key, marshalIn)
    addRowItemMarshaller(key, t)
  }

}

object LabelledRowMarshaller {

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

  def NOOP[CT](v: CT) {}

}
