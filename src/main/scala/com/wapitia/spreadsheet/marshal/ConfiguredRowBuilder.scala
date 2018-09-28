package com.wapitia
package spreadsheet
package marshal

import com.wapitia.common.BLDR

/** A row marshal that exposes and wraps an item BLDR instance. */
class ConfiguredRowBuilder[A,M <: ConfiguredRowMarshal[A,M],B <: BLDR[A]](mcRepo: MarshalSetRepo[A,M], newBuilder: B, marshalColumnName: String => String)
extends ConfiguredRowMarshal[A,M](mcRepo, marshalColumnName) {
  var rb: B = newBuilder
  override def make(): A = rb.build()
}
