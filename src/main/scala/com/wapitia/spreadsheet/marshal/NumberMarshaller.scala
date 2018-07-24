package com.wapitia
package spreadsheet
package marshal

import java.math.BigDecimal
import com.wapitia.common.marshal.MarshalIn
import java.math.BigInteger

class SimpleNumberMarshal extends MarshalIn[Any,BigDecimal] {
  
  override def unmarshal(v: Any): BigDecimal = v match {
    case n: java.math.BigDecimal => n
    case _  => throw new SpreadsheetMarshalException("unparsable number %s:%s".format(v, v.getClass))
  }
    
}

trait Nullable {
  
  def isNull(v: Any): Boolean = v match {
    case s: String if s.isEmpty() => true
    case _  => false
  }
}

// TODO
//class NullableNumberMarshal extends SimpleNumberMarshal with Nullable
class NullableNumberMarshal extends SimpleNumberMarshal {
  
  override def isNull(v: Any): Boolean = v match {
    case s: String if s.isEmpty() => true
    case _  => false
  }
} 

class IntMarshal extends MarshalIn[Any,Int] {
  
  override def unmarshal(v: Any): Int = v match {
    case s: String => s.toInt
    case i: java.lang.Integer => i 
    case l: java.lang.Long => l.intValue() 
    case n: java.math.BigDecimal => n.intValue()
    case _  => throw new SpreadsheetMarshalException("unparsable integer %s:%s".format(v, v.getClass))
  }

}

class BigIntegerMarshal extends MarshalIn[Any,BigInteger] {
  
  override def unmarshal(v: Any): BigInteger = v match {
    case s: String => new BigInteger(s)
    case n: java.math.BigInteger => n
    case n: java.math.BigDecimal => n.toBigInteger()
    case _  => throw new SpreadsheetMarshalException("unparsable BigInteger %s:%s".format(v, v.getClass))
  }

}

class BigDecimalMarshal extends MarshalIn[Any,BigDecimal] {
  
  override def unmarshal(v: Any): BigDecimal = v match {
    case s: String => new BigDecimal(s)
    case n: java.math.BigInteger => new BigDecimal(n)
    case d: java.math.BigDecimal => d
    case _  => throw new SpreadsheetMarshalException("unparsable BigDecimal %s:%s".format(v, v.getClass))
  }

}

class FloatMarshal extends MarshalIn[Any,Float] {
  
  override def unmarshal(v: Any): Float = v match {
    case s: String => s.toFloat
    case i: java.lang.Integer => i.floatValue() 
    case l: java.lang.Long => l.floatValue() 
    case n: java.math.BigDecimal => n.floatValue()
    case _  => throw new SpreadsheetMarshalException("unparsable float %s:%s".format(v, v.getClass))
  }

}

class DoubleMarshal extends MarshalIn[Any,Double] {
  
  override def unmarshal(v: Any): Double = v match {
    case s: String => s.toDouble
    case i: java.lang.Integer => i.doubleValue() 
    case l: java.lang.Long => l.doubleValue() 
    case n: java.math.BigDecimal => n.doubleValue()
    case _  => throw new SpreadsheetMarshalException("unparsable float %s:%s".format(v, v.getClass))
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
  
  val simpleIntMarshal: MarshalIn[Any,Int] = new IntMarshal
  
  val simpleFloatMarshal: MarshalIn[Any,Float] = new FloatMarshal
  
  val simpleDoubleMarshal: MarshalIn[Any,Double] = new DoubleMarshal
  
  val intMarshal: MarshalIn[Any,Int] = new IntMarshal
  
  val boolMarshal: MarshalIn[Any,Boolean] = new BoolMarshal
  
  val trues: List[String] = List("YES", "TRUE", "1", "Y", "T")
  def stringToBool(str: String): Boolean = {
    trues.find(_ == str).isDefined
  }
  
}