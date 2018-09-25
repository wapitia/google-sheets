package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal

/** Marshals a spreadsheet of data into rows of objects of some type `A`.
 *  This is configured with marshals and binders for each labeled column.
 *
 *  @tparam A finished row object type
 */
abstract class LabelledSheetMarshal[A]() {

  import LabelledSheetMarshal._

  /** Marshals a cell value from its original value into the internal type before
   *  binding via the binder function.
   */
  type CellMarshal[c] = InMarshal[Any,c]

  /** Function taking a name and value and setting a value via a RowMarshal */
  type BinderFunc[C] = (_ <: RowMarshal,String,C) => Unit

  /** Accumulator and Builder for each row of data in the spreadsheet */
  trait RowMarshal {

    /** Make the instance of the resultant object for this row */
    def make(): A

    /** Set the value for a named slot in the accumulating result object */
    def set[C](name: String, rawvalue: C): Unit = {

      val cellMarshal = cellMarshalMap.getOrElse(name, MarshalIdentity).asInstanceOf[InMarshal[Any,C]]

      if (!cellMarshal.isNull(rawvalue)) {
  //      val buildFunc = objMarshalMap.getOrElse(name, NOOP[RowMarshal[C],C] _).asInstanceOf[(RowMarshal[C],String,C) => Unit]
        val deffunc = defBuild[C,RowMarshal] _
        val buildFunc = objMarshalMap.getOrElse(name, deffunc )
        val bf = buildFunc.asInstanceOf[(RowMarshal,String,C) => Unit]
        val mval: C = cellMarshal.unmarshal(rawvalue)
        bf(this,name,mval)
      }
    }

  }

  /** Start a new row marshal to ingest incoming raw values to produce its object */
  def makeRowMarshaller[C](): RowMarshal

  val cellMarshalMap = scala.collection.mutable.Map[String,CellMarshal[_]]()
  val objMarshalMap = scala.collection.mutable.Map[String,BinderFunc[_]]()

  def addMarshal[C](name: String, marshal: CellMarshal[C]) {
    cellMarshalMap.put(name, marshal)
  }

  def addBinder[C,RM <: RowMarshal](name: String, binder: BinderFunc[C]) {
    objMarshalMap.put(name, binder)
  }

//  def defBuildFunc[C,M <: RowMarshal[C]]: (M,String,C) => Unit = NOOP[M,C] _
  def defBuild[C,M <: RowMarshal](m: M, name: String, value: C): Unit = NOOP[M,C](m,name,value)

  /** Convenience method to add both a marshal and a binder for one named cell */
  def marshalChain[C,RM <: RowMarshal](name: String, marshal: CellMarshal[C], binder: BinderFunc[C])  {
    addMarshal(name, marshal)
    addBinder(name, binder)
  }
}

/** LabelledSheetMarshal helper objects */
object LabelledSheetMarshal {

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

  def NOOP[MR,CT](msh: MR, n: String, v: CT) {}
}
