package com.wapitia
package calendar

import com.wapitia.common.marshal.MarshalIn
import com.wapitia.common.marshal.MarshalInException
import com.wapitia.common.marshal.EnumMarshaller
import com.wapitia.common.EValue

/**
 * Marshals a name of a cycle ("Daily", "BiDaily", etc) into its corresponding
 * Cycle instance.
 * 
 * @param defaultCycle cycle to use if input is blank
 */
class CycleMarshaller(defaultCycle: => Cycle, whenBad: => Cycle, lookup: Map[String,Cycle], caseSensitive: Boolean) 
   extends EnumMarshaller[Cycle](defaultCycle, whenBad, lookup, caseSensitive)

object CycleMarshaller {
  
//  import EnumMarshaller._
  
  val MissingCycle = "Missing Cycle"
  val UnkCycle = "Unrecognized Cycle"
  
  // default Cycle marshaller singleton, immutable reusable and thread-safe
  val intoCycle = apply()
  
  def apply(): CycleMarshaller = apply(throw new MarshalInException(MissingCycle,null))
  
  def apply(defaultCycle: => Cycle): CycleMarshaller = new CycleMarshaller(
    defaultCycle = defaultCycle,
    whenBad = throw new MarshalInException(UnkCycle, null),
    lookup = Cycle.enumValueNamed,
    caseSensitive = false)
}

