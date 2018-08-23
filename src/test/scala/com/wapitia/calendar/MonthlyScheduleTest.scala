package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import org.junit.Test
import org.junit.Assert._

class MonthlyScheduleTest {

  val jan2 = LocalDate.of(2018, 1, 2)
  val feb2 = LocalDate.of(2018, 2, 2)
  val mar2 = LocalDate.of(2018, 3, 2)
  val apr2 = LocalDate.of(2018, 4, 2)
  val may2 = LocalDate.of(2018, 5, 2)

  @Test
  def testScheduleDayOfMonth() {
    assertEquals(1, (new FirstDayOfMonth()).dayOfMonthOf(LocalDate.of(2018, 3, 21)))
    assertEquals(31, (new LastDayOfMonth()).dayOfMonthOf(LocalDate.of(2018, 3, 21)))
    assertEquals(28, (new LastDayOfMonth()).dayOfMonthOf(LocalDate.of(2018, 2, 21)))
    assertEquals(29, (new LastDayOfMonth()).dayOfMonthOf(LocalDate.of(2020, 2, 21)))
    assertEquals(31, (new LastDayOfMonth()).dayOfMonthOf(LocalDate.of(2018, 12, 21)))
    assertEquals(30, (new LastDayOfMonth()).dayOfMonthOf(LocalDate.of(2018, 9, 21)))
  }

  @Test
  def testCycleMonth() {

    println("problem child...")
      // problem child
    assertEquals(-2, MonthlySchedule.cycleMonth0(jan2, 3, 1) )

    println("and others...")

    assertEquals(0, MonthlySchedule.cycleMonth0(jan2, 3, 0) )
    assertEquals(0, MonthlySchedule.cycleMonth0(feb2, 3, 0) )
    assertEquals(0, MonthlySchedule.cycleMonth0(mar2, 3, 0) )
    assertEquals(3, MonthlySchedule.cycleMonth0(apr2, 3, 0) )
    assertEquals(3, MonthlySchedule.cycleMonth0(may2, 3, 0) )

    assertEquals(-2, MonthlySchedule.cycleMonth0(jan2, 3, 1) )
    assertEquals(1, MonthlySchedule.cycleMonth0(feb2, 3, 1) )

    assertEquals(-1, MonthlySchedule.cycleMonth0(jan2, 3, 2) )
  }

  @Test
  def testFunCycleMonthOnOrBefore() {

    assertEquals(LocalDate.of(2017, 12, 1), MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 1, 2), monthCycle=2, monthOffset=1) )

//    assertEquals(LocalDate.of(2018, 3, 1), MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 3, 2), 1, 0) )
//
//    assertEquals(LocalDate.of(2018, 3, 1), MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 3, 2), 2, 0) )
//
//    assertEquals(LocalDate.of(2018, 4, 1), MonthlySchedule.cycleMonthOnOrBefore(LocalDate.of(2018, 3, 2), 2, 1) )

  }

  @Test
  def testMonthlySched1() {
    val ms1: Schedule = (MonthlySchedule.builder()).monthly(2).build
    val dateStrm: Stream[LocalDate] = ms1.onOrAfter(LocalDate.of(2018, 3, 1))
    dateStrm.take(19).foreach(println(_))
  }

  @Test
  def testMonthlySched2() {
    val ms1: Schedule = (MonthlySchedule.builder()).monthly(2).build
    val dateStrm: Stream[LocalDate] = ms1.onOrAfter(LocalDate.of(2018, 3, 1))
    dateStrm.take(19).foreach(println(_))
  }

}
