package com.wapitia
package spreadsheet
package marshal

import java.math.BigDecimal
import com.wapitia.common.marshal.MarshalIn

class SimpleNumberMarshal extends MarshalIn[Any,BigDecimal] {
  
  override def unmarshal(v: Any): BigDecimal = v match {
    case n: java.math.BigDecimal => n
    case _  => throw new SpreadsheetMarshalException("unparsable number %s:%s".format(v, v.getClass))
  }
    
}

class NullableNumberMarshal extends SimpleNumberMarshal {
  
  override def isNull(v: Any): Boolean = v match {
    case s: String if s.isEmpty() => true
    case _  => false
  }

}

class IntMarshal extends MarshalIn[Any,Int] {
  
  override def unmarshal(v: Any): Int = v match {
    case n: java.math.BigDecimal => n.intValue()
    case _  => throw new SpreadsheetMarshalException("unparsable number %s:%s".format(v, v.getClass))
  }

}

class BoolMarshal extends MarshalIn[Any,Boolean] {
  
  import NumberMarshaller.stringToBool
  
  override def unmarshal(v: Any): Boolean = v match {
    case s: String => stringToBool(s)
    case _  => throw new SpreadsheetMarshalException("unparsable boolean value %s:%s".format(v, v.getClass))
  }

}

object NumberMarshaller {
  
  val simpleMarshal: MarshalIn[Any,BigDecimal] = new SimpleNumberMarshal  
  
  val nullableMarshal: MarshalIn[Any,BigDecimal] = new NullableNumberMarshal
  
  val simpleCurrencyMarshal: MarshalIn[Any,BigDecimal] = new SimpleNumberMarshal  
  
  val nullableCurrencyMarshal: MarshalIn[Any,BigDecimal] = new NullableNumberMarshal
  
  val intMarshal: MarshalIn[Any,Int] = new IntMarshal
  
  val boolMarshal: MarshalIn[Any,Boolean] = new BoolMarshal
  
  val trues: List[String] = List("YES", "TRUE", "1", "Y", "T")
  def stringToBool(str: String): Boolean = {
    trues.find(_ == str).isDefined
  }
  
}