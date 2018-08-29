package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.format.DateTimeParseException
import org.junit.{Test, Ignore, Rule}
import org.junit.Assert._
import org.junit.rules.ExpectedException

class DailyScheduleStreamTest {

  @Test
  def testWeeklySundayScheduleStream() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingSunday()
      .withWeekDaysInCycle(DayOfWeek.FRIDAY)
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
  def testWeeklyMondayScheduleStream() {
    val dsched: DailySchedule = DailySchedule.weeklyStartingMonday()
      .withWeekDaysInCycle(DayOfWeek.SATURDAY)
      .build()
    val dateStrm: Stream[LocalDate] = dsched.onOrAfter("2018-03-01")
    val expecteds = Stream[LocalDate]("2018-03-03","2018-03-10","2018-03-17","2018-03-24")
    (expecteds zip dateStrm).foreach {
      case (exp, act) => assertEquals(exp, act)
    }
  }

}
