package com.wapitia
package calendar

/** Unmarshal cycle names ("Monthly", "Quarterly", etc) from its String into
 *  the corresponding `Cycle` enum entities.
 */
object CycleMarshaller extends com.wapitia.common.marshal.EnumMarshallerTemplate {
  override type EnumType = Cycle
  override def enumValueMap = Cycle.enumValueNamed
}
