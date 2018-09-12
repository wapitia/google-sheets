package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.format.DateTimeParseException
import java.time.DayOfWeek._
import org.junit.{Test, Ignore, Rule}

import org.junit.Assert._
import org.junit.rules.ExpectedException

class MultiWeeklyScheduleStreamTest {

  @Test
  def testBiWeeklySchedule1() {

    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
      .withWeekDayOffsetsInSchedule((0,FRIDAY), (1,THURSDAY))
      .build()

    val dateStrm: Stream[LocalDate] = biwSched.starting("2018-08-29")
    val expecteds = Stream[LocalDate](
      "2018-08-30",    // Thursday
      "2018-09-07",    // Friday
      "2018-09-13",    // Thursday
      "2018-09-21")    // Friday
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testBiWeeklyScheduleMw1() {

    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
      .withWeekDayOffsetsInSchedule((1,THURSDAY))
      .build()

    val dateStrm: Stream[LocalDate] = biwSched.starting("2018-08-29")
    val expecteds = Stream[LocalDate](
      "2018-08-30",
      "2018-09-13",
      "2018-09-27",
      "2018-10-11",
      "2018-10-25",
      "2018-11-08")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testBiWeeklyScheduleMw1a() {

    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
      .withWeekDayOffsetsInSchedule((1,THURSDAY))
      .build()

    val dateStrm: Stream[LocalDate] = biwSched.starting("2018-08-30")
    val expecteds = Stream[LocalDate](
      "2018-08-30",
      "2018-09-13",
      "2018-09-27",
      "2018-10-11",
      "2018-10-25",
      "2018-11-08")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testBiWeeklyScheduleMw2() {

    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
      .withWeekDayOffsetsInSchedule((0,THURSDAY))
      .build()

    val dateStrm: Stream[LocalDate] = biwSched.starting("2018-08-29")
    val expecteds = Stream[LocalDate](
      "2018-09-06",
      "2018-09-20",
      "2018-10-04",
      "2018-10-18",
      "2018-11-01",
      "2018-11-15")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

  @Test
  def testBiWeeklyScheduleMw2a() {

    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
      .withWeekDayOffsetsInSchedule((0,THURSDAY))
      .build()

    val dateStrm: Stream[LocalDate] = biwSched.starting("2018-09-06")
    val expecteds = Stream[LocalDate](
      "2018-09-06",
      "2018-09-20",
      "2018-10-04",
      "2018-10-18",
      "2018-11-01",
      "2018-11-15")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

//  @Test
//  def testBiWeeklyScheduleMw2b() {
//
////    val biwSched: Schedule = WeeklySchedule.multipleWeekly(2, SUNDAY, 0)
////      .withWeekDayOffsetsInSchedule((0,THURSDAY))
////      .build()
//
//    val dateStrm: Stream[LocalDate] = WeeklySchedule.biweeklyStarting("2018-09-06")
//    val expecteds = Stream[LocalDate](
//      "2018-09-06",
//      "2018-09-20",
//      "2018-10-04",
//      "2018-10-18",
//      "2018-11-01",
//      "2018-11-15")
//    (expecteds zip dateStrm).foreach {
//      case (exp, act) => assertEquals(exp, act)
//    }
//  }

}
