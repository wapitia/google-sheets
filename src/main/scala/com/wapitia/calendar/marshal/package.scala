package com.wapitia
package calendar

import com.wapitia.common.marshal.MarshalIn

/** calendar package-wide common constants and functions. */
package object marshal {

  val cycleMarshal: MarshalIn[AnyRef,Cycle] = CycleMarshaller.relaxed()
}
