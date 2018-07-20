package com.wapitia
package common
package marshal

/** As may be thrown by MarshalIn.unmarshal */
class UnmarshalException[E <: Throwable](message: String, cause: E)
extends RuntimeException(message, cause)
{
  override def getCause: E = cause

  /** null root cause when this UnmarshalException is itself the root cause */
  def this(message: String) {
    this(message, null.asInstanceOf[E])
  }

}
