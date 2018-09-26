package com.wapitia
package common.marshal

import com.wapitia.spreadsheet.marshal.SpreadsheetMarshalException
import scala.BigDecimal
import scala.BigInt
import scala.math.BigDecimal.javaBigDecimal2bigDecimal
import scala.math.BigInt.javaBigInteger2bigInt

class SimpleNumberMarshal extends InMarshal[Any,BigDecimal] {

  override def unmarshal(v: Any): BigDecimal = v match {
    case n: java.math.BigDecimal => n
    case n: scala.math.BigDecimal => n
    case _  => throw new SpreadsheetMarshalException("unparsable number %s:%s".format(v, v.getClass))
  }

}

trait Nullable {

  def isNull(v: Any): Boolean = v match {
    case null => true
    case s: String if s.isEmpty() => true
    case _  => false
  }
}

// TODO
//class NullableNumberMarshal extends SimpleNumberMarshal with Nullable
class NullableNumberMarshal extends SimpleNumberMarshal {

  override def isNull(v: Any): Boolean = v match {
    case null => true
    case s: String if s.isEmpty() => true
    case _  => false
  }
}

class IntMarshal extends InMarshal[Any,Int] {

  override def unmarshal(v: Any): Int = v match {
    case s: String => s.toInt
    case i: java.lang.Integer => i
    case l: java.lang.Long => l.intValue()
    case n: java.math.BigDecimal => n.intValue()
    case d: scala.math.BigDecimal => d.intValue()
    case _  => throw new SpreadsheetMarshalException("unparsable integer %s:%s".format(v, v.getClass))
  }

}

class BigIntegerMarshal extends InMarshal[Any,BigInt] {

  override def unmarshal(v: Any): BigInt = v match {
    case s: String => BigInt(s)
    case n: java.math.BigInteger => n
    case n: java.math.BigDecimal => BigInt(n.toBigInteger())
    case d: scala.math.BigDecimal => d.toBigInt()
    case _  => throw new SpreadsheetMarshalException("unparsable BigInteger %s:%s".format(v, v.getClass))
  }

}

class BigDecimalMarshal extends InMarshal[Any,BigDecimal] {

  override def unmarshal(v: Any): BigDecimal = v match {
    case s: String => BigDecimal(s)
    case n: java.math.BigInteger => BigDecimal(n)
    case d: java.math.BigDecimal => BigDecimal(d)
    case d: scala.math.BigDecimal => d
    case _  => throw new SpreadsheetMarshalException("unparsable BigDecimal %s:%s".format(v, v.getClass))
  }

}

class FloatMarshal extends InMarshal[Any,Float] {

  override def unmarshal(v: Any): Float = v match {
    case s: String => s.toFloat
    case i: java.lang.Integer => i.floatValue()
    case l: java.lang.Long => l.floatValue()
    case n: java.math.BigDecimal => n.floatValue()
    case n: scala.math.BigDecimal => n.floatValue()
    case _  => throw new SpreadsheetMarshalException("unparsable float %s:%s".format(v, v.getClass))
  }

}

class DoubleMarshal extends InMarshal[Any,Double] {

  override def unmarshal(v: Any): Double = v match {
    case s: String => s.toDouble
    case i: java.lang.Integer => i.doubleValue()
    case l: java.lang.Long => l.doubleValue()
    case n: java.math.BigDecimal => n.doubleValue()
    case n: scala.math.BigDecimal => n.doubleValue()
    case _  => throw new SpreadsheetMarshalException("unparsable float %s:%s".format(v, v.getClass))
  }

}

class BoolMarshal extends InMarshal[Any,Boolean] {

  import NumberMarshals.stringToBool

  override def unmarshal(v: Any): Boolean = v match {
    case s: String => stringToBool(s)
    case _  => throw new SpreadsheetMarshalException("unparsable boolean value %s:%s".format(v, v.getClass))
  }

}

object NumberMarshals {

  val simpleMarshal: InMarshal[Any,BigDecimal] = new SimpleNumberMarshal

  val nullableMarshal: InMarshal[Any,BigDecimal] = new NullableNumberMarshal

  val simpleCurrencyMarshal: InMarshal[Any,BigDecimal] = new SimpleNumberMarshal

  val nullableCurrencyMarshal: InMarshal[Any,BigDecimal] = new NullableNumberMarshal

  val simpleIntMarshal: InMarshal[Any,Int] = new IntMarshal

  val simpleFloatMarshal: InMarshal[Any,Float] = new FloatMarshal

  val simpleDoubleMarshal: InMarshal[Any,Double] = new DoubleMarshal

  val intMarshal: InMarshal[Any,Int] = new IntMarshal

  val boolMarshal: InMarshal[Any,Boolean] = new BoolMarshal

  val trues: List[String] = List("YES", "TRUE", "1", "Y", "T")
  def stringToBool(str: String): Boolean = {
    trues.find(_ == str).isDefined
  }

}
