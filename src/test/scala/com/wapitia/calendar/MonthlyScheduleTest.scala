package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import org.junit.{Test, Ignore}
import org.junit.Assert._

class MonthlyScheduleTest {

  import MonthlySchedule._
  import MonthlyCycle._
  import ScheduleDayOfMonth._

  val quarterlyMonthsInCycle = 3

  @Test
  def testScheduleDayOfMonth() {
    assertEquals(1, FirstDay.dayOfMonthOf("2018-03-21"))
    assertEquals(31, LastDay.dayOfMonthOf("2018-03-21"))
    assertEquals(28, LastDay.dayOfMonthOf("2018-02-21"))
    assertEquals(29, LastDay.dayOfMonthOf("2020-02-21"))
    assertEquals(31, LastDay.dayOfMonthOf("2018-12-21"))
    assertEquals(30, LastDay.dayOfMonthOf("2018-09-21"))
  }

  @Test
  def testCycleMonth() {

    // Monthly Cycle (cycle = 1) can only use offset=0, and all results are expected to be 0
    assertEquals(0, cycleMonthIndex(1, 0, "2018-01-02") )
    assertEquals(0, cycleMonthIndex(1, 0, "2018-02-02") )
    assertEquals(0, cycleMonthIndex(1, 0, "2018-03-02") )
    assertEquals(0, cycleMonthIndex(1, 0, "2018-04-02") )
    assertEquals(0, cycleMonthIndex(1, 0, "2018-05-02") )

    // Pick quarterly cycles to test
    assertEquals(0, cycleMonthIndex(quarterlyMonthsInCycle, 0, "2018-01-02") )
    assertEquals(1, cycleMonthIndex(quarterlyMonthsInCycle, 0, "2018-02-02") )
    assertEquals(2, cycleMonthIndex(quarterlyMonthsInCycle, 0, "2018-03-02") )
    assertEquals(0, cycleMonthIndex(quarterlyMonthsInCycle, 0, "2018-04-02") )
    assertEquals(1, cycleMonthIndex(quarterlyMonthsInCycle, 0, "2018-05-02") )

    assertEquals(2, cycleMonthIndex(quarterlyMonthsInCycle, 1, "2018-01-02") )
    assertEquals(0, cycleMonthIndex(quarterlyMonthsInCycle, 1, "2018-02-02") )
    assertEquals(1, cycleMonthIndex(quarterlyMonthsInCycle, 1, "2018-03-02") )
    assertEquals(2, cycleMonthIndex(quarterlyMonthsInCycle, 1, "2018-04-02") )
    assertEquals(0, cycleMonthIndex(quarterlyMonthsInCycle, 1, "2018-05-02") )

    assertEquals(1, cycleMonthIndex(quarterlyMonthsInCycle, 2, "2018-01-02") )
    assertEquals(2, cycleMonthIndex(quarterlyMonthsInCycle, 2, "2018-02-02") )
    assertEquals(0, cycleMonthIndex(quarterlyMonthsInCycle, 2, "2018-03-02") )
    assertEquals(1, cycleMonthIndex(quarterlyMonthsInCycle, 2, "2018-04-02") )
    assertEquals(2, cycleMonthIndex(quarterlyMonthsInCycle, 2, "2018-05-02") )
  }

  // @Inline
  def assertDatesEqual(exp: LocalDate, act: LocalDate) {
    assertEquals(exp, act)
  }

  @Test
  def testCycleStartDate() {
    assertDatesEqual("2017-12-01", cycleStartDate(2, 1, "2018-01-02") )
    assertDatesEqual("2018-03-01", cycleStartDate(1, 0, "2018-03-02") )
    assertDatesEqual("2018-01-01", cycleStartDate(3, 0, "2018-03-02") )
    assertDatesEqual("2018-02-01", cycleStartDate(3, 1, "2018-03-02") )
    assertDatesEqual("2018-03-01", cycleStartDate(3, 2, "2018-03-02") )
    assertDatesEqual("2018-03-01", cycleStartDate(2, 0, "2018-03-02") )
    assertDatesEqual("2018-02-01", cycleStartDate(2, 1, "2018-03-02") )
  }

  @Test
  def testMonthlySched1() {
    val expected = List[LocalDate]("2018-06-02", "2018-07-02", "2018-08-02", "2018-09-02",
      "2018-10-02", "2018-11-02", "2018-12-02", "2019-01-02", "2019-02-02", "2019-03-02",
      "2019-04-02", "2019-05-02", "2019-06-02", "2019-07-02", "2019-08-02", "2019-09-02")

    val ms1: Schedule = MonthlySchedule.builder().dayOfMonth(2).monthsInCycle(1).build
    val resultStream: Stream[LocalDate] = ms1.starting(LocalDate.of(2018, 6, 1))
    val zippedCompare = expected zip resultStream
    zippedCompare.foreach {
      case (exp, res) => assertDatesEqual(exp, res)
    }
  }

}
