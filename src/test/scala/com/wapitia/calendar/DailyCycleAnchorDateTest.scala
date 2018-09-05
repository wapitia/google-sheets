package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.format.DateTimeParseException
import org.junit.{Test, Ignore, Rule}
import org.junit.Assert._
import org.junit.rules.ExpectedException

class DailyCycleAnchorDateTest {

  @Test
  def testDailyScheduleWeeklySunday() {
    val sundaySched: DailySchedule = WeeklySchedule.weeklyStartingSunday()
      .withWeekDaysInSchedule(DayOfWeek.FRIDAY)
      .build()

    List[(LocalDate, LocalDate)](
      ("2018-02-25", "2018-03-01"),
      ("2018-02-25", "2018-02-28"),
      ("2018-02-25", "2018-02-27"),
      ("2018-02-25", "2018-02-26"),
      ("2018-02-25", "2018-02-25"),
      ("2018-02-18", "2018-02-24"),
      ("2018-02-18", "2018-02-23"),
      ("2018-02-18", "2018-02-22"),
      ("2018-02-18", "2018-02-21"),
      ("2018-02-18", "2018-02-20"),
      ("2018-02-11", "2018-02-17")
    ).foreach {
      assertAnchorDatesEqual(_, sundaySched)
    }

  }

  @Test
  def testDailyScheduleWeeklyMonday() {
    val mondaySched: Schedule = WeeklySchedule.weeklyStartingMonday()
      .withWeekDaysInSchedule(DayOfWeek.FRIDAY)
      .build()

    List[(LocalDate, LocalDate)](
      ("2018-02-26", "2018-03-01"),
      ("2018-02-26", "2018-02-28"),
      ("2018-02-26", "2018-02-27"),
      ("2018-02-26", "2018-02-26"),
      ("2018-02-19", "2018-02-25"),
      ("2018-02-19", "2018-02-24"),
      ("2018-02-19", "2018-02-23"),
      ("2018-02-19", "2018-02-22"),
      ("2018-02-19", "2018-02-21"),
      ("2018-02-19", "2018-02-20"),
      ("2018-02-19", "2018-02-19"),
      ("2018-02-12", "2018-02-18")
    ).foreach {
      assertAnchorDatesEqual(_, mondaySched)
    }

  }

  @Test
  def testBiWeeklySchedule() {

    val biwSched: DailySchedule = WeeklySchedule
      .multipleWeekly(2, DayOfWeek.SUNDAY, 0)
      // .withWeekDayOffsetsInCycle((0,DayOfWeek.FRIDAY), (1,DayOfWeek.THURSDAY))
      .build()
    List[(LocalDate, LocalDate)](
      ("2018-02-18", "2018-02-25"),
      ("2018-02-18", "2018-02-18"),
      ("2018-02-04", "2018-02-17"),
      ("2018-02-18", "2018-03-03"),
      ("2018-02-18", "2018-02-28"),
      ("2018-02-18", "2018-03-01"),
      ("2018-03-04", "2018-03-04")
    ).foreach {
      assertAnchorDatesEqual(_, biwSched)
    }

  }

  def assertAnchorDatesEqual(expCandPair: (LocalDate, LocalDate), dailySched: Schedule) {
    expCandPair match {
      case (exp, candidate) =>
        val d1: LocalDate = dailySched.cycleAnchorDate(candidate)
        assertEquals(s"For candidate $candidate, expected $exp but got $d1", exp, d1)
    }
  }

}
