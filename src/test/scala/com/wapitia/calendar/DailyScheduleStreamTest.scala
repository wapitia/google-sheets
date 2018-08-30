package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.format.DateTimeParseException
import java.time.DayOfWeek._
import org.junit.{Test, Ignore, Rule}

import org.junit.Assert._
import org.junit.rules.ExpectedException

class DailyScheduleStreamTest {

  @Test
  def testWeeklySundayScheduleStream() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingSunday()
      .withWeekDaysInCycle(FRIDAY)
      .build()
    val dateStrm: Stream[LocalDate] = dsched.onOrAfter("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-02","2018-03-09","2018-03-16","2018-03-23")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testWeeklyQuickScheduleStream() {
    val dateStrm: Stream[LocalDate] = WeeklySchedule.from("2018-03-02")
    val expecteds = Stream[LocalDate]("2018-03-02","2018-03-09","2018-03-16","2018-03-23")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testWeeklyMondayScheduleStream1() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingMonday()
      .withWeekDaysInCycle(SATURDAY)
      .build()
    val dateStrm: Stream[LocalDate] = dsched.onOrAfter("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-03","2018-03-10","2018-03-17","2018-03-24")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testWeeklyMondayScheduleStream2() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingMonday()
      .withWeekDaysInCycle(DayOfWeek.WEDNESDAY)
      .build()
    val dateStrm: Stream[LocalDate] = dsched.onOrAfter("2018-08-24")
    val expecteds = Stream[LocalDate]("2018-08-29","2018-09-05","2018-09-12","2018-09-19")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testBiWeeklySchedule1() {

    val biwSched: DailySchedule = DailySchedule
      .multipleWeekly(2, DayOfWeek.SUNDAY, 0)
      .withWeekDayOffsetsInCycle((0,DayOfWeek.FRIDAY), (1,DayOfWeek.THURSDAY))
      .build()

    val dateStrm: Stream[LocalDate] = biwSched.onOrAfter("2018-08-29")
    val expecteds = Stream[LocalDate]("2018-08-30","2018-09-07","2018-09-13","2018-09-21")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

}
