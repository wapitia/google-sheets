package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal

/** Marshals a spreadsheet of data into rows of objects of some type `A`.
 *  This is configured with marshals and binders for each labeled column.
 *
 *  @tparam A finished row object type
 *  @tparam B Builder of `A` objects
 */
abstract class LabelledSheetMarshal[A,B] {

  type BinderFunc[C] = ((_ <: RowMarshal,C) => Unit)
  

  /** Accumulator and Builder for each row of data in the spreadsheet */
  trait RowMarshal {

    /** Make the instance of the resultant object for this row */
    def make(): A

    /** Set the value for a named slot in the accumulating result object */
    def set[C](name: String, rawvalue: Any): Unit = setValue[C](name, rawvalue, this)
  }

  /** Start a new row marshal to ingest incoming raw values to produce its object */
  def makeRow(): RowMarshal

  val cellMarshalMap = scala.collection.mutable.Map[String,InMarshal[Any,Any]]()
  val objMarshalMap = scala.collection.mutable.Map[String,BinderFunc[_]]()

  def addMarshal(name: String, marshal: InMarshal[Any,Any]) {
    cellMarshalMap.put(name, marshal)
  }

  def addBinder[RM <: RowMarshal](name: String, binder: BinderFunc[_]) {
    objMarshalMap.put(name, binder)
  }

  /** Convenience method to add both a marshal and a binder for one named cell */
  def marshalChain[RM <: RowMarshal](name: String, marshal: InMarshal[Any,Any], binder: BinderFunc[_])  {
    addMarshal(name, marshal)
    addBinder(name, binder)
  }

  def setValue[C](name: String, rawvalue: Any, rowMarshal: RowMarshal): Unit = {
    import LabelledSheetMarshal._

    val cellMarshal = cellMarshalMap.getOrElse(name, MarshalIdentity).asInstanceOf[InMarshal[Any,C]]

    if (!cellMarshal.isNull(rawvalue)) {
      val buildFunc = objMarshalMap.getOrElse(name, NOOP _).asInstanceOf[(RowMarshal,C) => Unit]
      val mval: C = cellMarshal.unmarshal(rawvalue)
      buildFunc(rowMarshal,mval)
    }
  }
}

/** LabelledSheetMarshal helper objects */
object LabelledSheetMarshal {

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

  def NOOP[MR,CT](msh: MR, v: CT) {}
}