package com.wapitia.spreadsheet.marshalx

import com.wapitia.common.marshal.MarshalIn

class LabelledCellMarshallersX {
  
  import LabelledRowMarshallerX._
  
  var cellMarshallers: Map[String,MarshalIn[Any,_]] = Map()
  
  def addCellMarshaller[MT <: Any](key: String, marshalIn: MarshalIn[Any,MT]): Unit =
    cellMarshallers += (key -> marshalIn)
  
  def getMarshal[C <: Any](marshalName: String): MarshalIn[Any,C] =
     cellMarshallers.getOrElse(marshalName, MarshalInIdentity).asInstanceOf[MarshalIn[Any,C]]
  
}

/** 
 *  @tparam A finished row object or builder/marshaller 
 *            of such row objects
 */
trait LabelledRowMarshallerX[A] {

  def cellMarshaller: LabelledCellMarshallersX
  
  def build(): A
  var rowObjectMarshallers: Map[String, _ => _] = Map()
  
  import LabelledRowMarshallerX._
  
  def addRowItemMarshaller[MT <: Any](key: String, t: MT => _): Unit = {
    rowObjectMarshallers += (key -> t)
  }
  
  def set[C](key: String, rawvalue: AnyRef): Unit = {
    val marshaler = cellMarshaller.getMarshal[C](key)
    
    if (!marshaler.isNull(rawvalue)) {
      val mval: C = marshaler.unmarshal(rawvalue)
  
      val f: (C => Unit) = rowObjectMarshallers.getOrElse(key, NOOP _).asInstanceOf[C => Unit]
      f(mval)
    }
  }
  
}


abstract class EmptyLabelledRowMarshallerX[A] extends LabelledRowMarshallerX[A] {
  val cellMarshaller: LabelledCellMarshallersX = new LabelledCellMarshallersX
  
  def build(): A
  
  def addCellMarshaller[MT <: Any](key: String, marshalIn: MarshalIn[Any,MT]): Unit = {
    cellMarshaller.addCellMarshaller(key, marshalIn)
  }

  def addLookup[MT <: Any](key: String, marshalIn: MarshalIn[Any,MT], t: MT => _): Unit = {
    addCellMarshaller(key, marshalIn)
    addRowItemMarshaller(key, t)
  }
  
}

object LabelledRowMarshallerX {
  
  val MarshalInIdentity = new MarshalIn[Any,Any] {
    override def unmarshal(v: Any) = v
	}
  
  def NOOP[CT](v: CT) {} 

}