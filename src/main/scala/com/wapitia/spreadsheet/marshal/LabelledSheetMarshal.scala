package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal

/** Marshals a spreadsheet of data into rows of objects of some type `A`.
 *  This is configured with marshals and binders for each labeled column.
 *
 *  @tparam A finished row object type
 */
abstract class LabelledSheetMarshal[A] {

  /** Marshals a cell value from its original value into the internal type before
   *  binding via the binder function.
   */
  type CellMarshal = InMarshal[Any,Any]

  type BinderFunc[C] = (_ <: RowMarshal[C],String,C) => Unit

  /** Accumulator and Builder for each row of data in the spreadsheet */
  trait RowMarshal[-C] {

    /** Make the instance of the resultant object for this row */
    def make(): A

    /** Set the value for a named slot in the accumulating result object */
    def set(name: String, rawvalue: Any): Unit = setValue[C](name, rawvalue, this)
  }

  /** Start a new row marshal to ingest incoming raw values to produce its object */
  def makeRow[C](): RowMarshal[C]

  val cellMarshalMap = scala.collection.mutable.Map[String,CellMarshal]()
  val objMarshalMap = scala.collection.mutable.Map[String,BinderFunc[_]]()

  def addMarshal(name: String, marshal: CellMarshal) {
    cellMarshalMap.put(name, marshal)
  }

  def addBinder[C,RM <: RowMarshal[C]](name: String, binder: BinderFunc[C]) {
    objMarshalMap.put(name, binder)
  }

  /** Convenience method to add both a marshal and a binder for one named cell */
  def marshalChain[C,RM <: RowMarshal[C]](name: String, marshal: CellMarshal, binder: BinderFunc[C])  {
    addMarshal(name, marshal)
    addBinder(name, binder)
  }

  def setValue[C](name: String, rawvalue: Any, rowMarshal: RowMarshal[C]): Unit = {
    import LabelledSheetMarshal._

    val cellMarshal = cellMarshalMap.getOrElse(name, MarshalIdentity).asInstanceOf[InMarshal[Any,C]]

    if (!cellMarshal.isNull(rawvalue)) {
      val buildFunc = objMarshalMap.getOrElse(name, NOOP[RowMarshal[C],C] _).asInstanceOf[(RowMarshal[C],String,C) => Unit]
      val mval: C = cellMarshal.unmarshal(rawvalue)
      buildFunc(rowMarshal,name,mval)
    }
  }
}

/** LabelledSheetMarshal helper objects */
object LabelledSheetMarshal {

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

  def NOOP[MR,CT](msh: MR, n: String, v: CT) {}
}
