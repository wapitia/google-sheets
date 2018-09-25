package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.marshal.InMarshal

trait SheetAccum[A] {

  import LabelledSheetMarshal._
  
  def cellMarshal[C](name: String): CellMarshal[C]
  def buildFunc[C](name: String): (RowMarshal[A],String,C) => Unit
}

/** Accumulator and Builder for each row of data in the spreadsheet */
abstract class RowMarshal[A](sheetA: SheetAccum[A]) {
  
  import LabelledSheetMarshal._


  /** Make the instance of the resultant object for this row */
  def make(): A

  /** Set the value for a named slot in the accumulating result object */
  def set[C](name: String, rawvalue: C): Unit = {

//    val cm = cellMarshalMap.getOrElse(name, MarshalIdentity).asInstanceOf[InMarshal[Any,C]]
    val cm = sheetA.cellMarshal[C](name)

    if (!cm.isNull(rawvalue)) {
//      val deffunc = sheetA.defBuild[C,RowMarshal[A]] _
//      val buildFunc = objMarshalMap.getOrElse(name, deffunc )
//      val bf = buildFunc.asInstanceOf[(RowMarshal,String,C) => Unit]
      val bf = sheetA.buildFunc[C](name)
      val mval: C = cm.unmarshal(rawvalue)
      bf(this,name,mval)
    }
  }

}
/** Marshals a spreadsheet of data into rows of objects of some type `A`.
 *  This is configured with marshals and binders for each labeled column.
 *
 *  @tparam A finished row object type
 */
abstract class LabelledSheetMarshal[A]() extends SheetAccum[A] {

  import LabelledSheetMarshal._


  /** Function taking a name and value and setting a value via a RowMarshal */
  type BinderFunc[C] = (_ <: RowMarshal[A],String,C) => Unit

  /** Start a new row marshal to ingest incoming raw values to produce its object */
  def makeRowMarshaller[C](): RowMarshal[A]

  val cellMarshalMap = scala.collection.mutable.Map[String,CellMarshal[_]]()
  val objMarshalMap = scala.collection.mutable.Map[String,BinderFunc[_]]()

  def buildFunc[C](name: String) = {
    val deffunc = defBuild[C,RowMarshal[A]] _
    val buildFunc = objMarshalMap.getOrElse(name, deffunc )
    val bf = buildFunc.asInstanceOf[(RowMarshal[A],String,C) => Unit]
    bf
  }
  
  def cellMarshal[C](name: String): CellMarshal[C] = {
    cellMarshalMap.getOrElse(name, MarshalIdentity).asInstanceOf[InMarshal[Any,C]]
  }
  
  def addMarshal[C](name: String, marshal: CellMarshal[C]) {
    cellMarshalMap.put(name, marshal)
  }

  def addBinder[C,RM <: RowMarshal[A]](name: String, binder: BinderFunc[C]) {
    objMarshalMap.put(name, binder)
  }

  def defBuild[C,M <: RowMarshal[A]](m: M, name: String, value: C) {
    NOOP[M,C](m,name,value)
  }

  /** Convenience method to add both a marshal and a binder for one named cell */
  def marshalChain[C,RM <: RowMarshal[A]](name: String, marshal: CellMarshal[C], binder: BinderFunc[C])  {
    addMarshal(name, marshal)
    addBinder(name, binder)
  }
}

/** LabelledSheetMarshal helper objects */
object LabelledSheetMarshal {

  /** Marshals a cell value from its original value into the internal type before
   *  binding via the binder function.
   */
  type CellMarshal[C] = InMarshal[Any,C]

  val MarshalIdentity = new InMarshal[Any,Any] {
    override def unmarshal(v: Any) = v
  }

  def NOOP[MR,CT](msh: MR, n: String, v: CT) {}
}
