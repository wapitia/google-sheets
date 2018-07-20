package com.wapitia
package spreadsheet
package marshal

class SpreadsheetMarshalException[E <: Throwable](message: String, cause: E) 
extends RuntimeException(message, cause) 
{
  override def getCause: E = cause
  
	def this(message: String) {
		this(message, null.asInstanceOf[E])
	}

}
