package com.wapitia
package calendar.marshal

import com.wapitia.common.marshal.MarshalIn
import com.wapitia.calendar.Cycle

class CycleMarshaller(caseSensitive: Boolean) extends MarshalIn[AnyRef,Cycle] {

  override def unmarshal(cycleName: AnyRef): Cycle =  Cycle.valueOf(nameForLookup(cycleName.toString()))

  def nameForLookup(cycleName: String): String = {
    if (caseSensitive) cycleName.trim()
    else cycleName.trim().toUpperCase()
  }

}

object CycleMarshaller {

  def apply() = new CycleMarshaller(false)

  def strict() = new CycleMarshaller(true)

  def relaxed() = new CycleMarshaller(false)
}
