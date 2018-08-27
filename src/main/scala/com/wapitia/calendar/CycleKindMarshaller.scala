package com.wapitia
package calendar

/** Unmarshal cycle names ("Monthly", "Quarterly", etc) from its String into
 *  the corresponding `Cycle` enum entities.
 *
 *  @usage
 *  {{{
 *     addCellMarshaller("Payment Cycle", CycleKindMarshaller.Into)
 *  }}}
 */
object CycleKindMarshaller extends com.wapitia.common.marshal.EnumMarshallerTemplate {
  override type EnumType = CycleKind
  override def enumValueMap = CycleKind.enumValueNamed
}
