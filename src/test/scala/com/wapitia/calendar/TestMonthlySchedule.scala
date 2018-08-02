package com.wapitia.calendar

import java.time.DayOfWeek
import java.time.LocalDate

object TestMonthlySchedule extends App {

  def testCycleMonth() {
    val jan2 = LocalDate.of(2018, 1, 2)
    val feb2 = LocalDate.of(2018, 2, 2)
    val mar2 = LocalDate.of(2018, 3, 2)
    val apr2 = LocalDate.of(2018, 4, 2)
    val may2 = LocalDate.of(2018, 5, 2)

    println("problem child...")
      // problem child
    assert(-2 == MonthlySchedule.cycleMonth0(jan2, 3, 1) )

    println("and others...")

    assert(0 == MonthlySchedule.cycleMonth0(jan2, 3, 0) )
    assert(0 == MonthlySchedule.cycleMonth0(feb2, 3, 0) )
    assert(0 == MonthlySchedule.cycleMonth0(mar2, 3, 0) )
    assert(3 == MonthlySchedule.cycleMonth0(apr2, 3, 0) )
    assert(3 == MonthlySchedule.cycleMonth0(may2, 3, 0) )

    assert(-2 == MonthlySchedule.cycleMonth0(jan2, 3, 1) )
    assert(1 == MonthlySchedule.cycleMonth0(feb2, 3, 1) )

    assert(-1 == MonthlySchedule.cycleMonth0(jan2, 3, 2) )
  }

  def testFunCycleMonthOnOrBefore() {

    assert(LocalDate.of(2017, 12, 1) == MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 1, 2), 2, 1) )

//    assert(LocalDate.of(2018, 3, 1) == MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 3, 2), 1, 0) )
//
//    assert(LocalDate.of(2018, 3, 1) == MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 3, 2), 2, 0) )
//
//    assert(LocalDate.of(2018, 4, 1) == MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 3, 2), 2, 1) )

  }

  def testMonthlySched1() {
    val ms1: Schedule = (MonthlySchedule.builder()).monthly(2).build
    val dateStrm: Stream[LocalDate] = ms1.onOrAfter(LocalDate.of(2018, 3, 1))
    dateStrm.take(19).foreach(println(_))
  }

  def testMonthlySched2() {
    val ms1: Schedule = (MonthlySchedule.builder()).monthly(2).build
    val dateStrm: Stream[LocalDate] = ms1.onOrAfter(LocalDate.of(2018, 3, 1))
    dateStrm.take(19).foreach(println(_))
  }

//  testCycleMonth()
  testMonthlySched1()
//  testFunCycleMonthOnOrBefore()

}
