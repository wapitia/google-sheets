package com.wapitia
package gsheets

class GSheetsException[E <: Throwable](message: String, cause: E)
extends RuntimeException(message, cause)
{
  override def getCause: E = cause

  def this(message: String) {
    this(message, null.asInstanceOf[E])
  }

}
