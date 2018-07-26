package com.wapitia.calendar

object TestCycles extends App {
  
  val monthsToDaysD: Double  = (12.0).asInstanceOf[Double] / (365.2425).asInstanceOf[Double]
  
  val monthsToDaysF: Float = ((12.0).asInstanceOf[Double] / (365.2425).asInstanceOf[Double]).asInstanceOf[Float]
  
  val diff: Double = monthsToDaysD - monthsToDaysF
  
  println(s"difference monthToDays Float vs Double: $diff")
  
  Cycle.enumValues.foreach { cycle =>
    val nDays = cycle.asDays
    val nMonths = cycle.asMonths
    val cName = cycle.name
    println(s"$cName #days: $nDays, #months: $nMonths")
  }
  
  println(s"days per year: $DaysPerYear")
  
  println(s"days per month: $DaysPerMonth")
  println(s"epoch day of week: $EpochDayOfWeek")
}