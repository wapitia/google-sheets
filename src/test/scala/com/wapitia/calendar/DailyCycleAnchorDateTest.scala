package com.wapitia.calendar

import java.time.{DayOfWeek, LocalDate}
import java.time.format.DateTimeParseException
import org.junit.{Test, Ignore, Rule}
import org.junit.Assert._
import org.junit.rules.ExpectedException

class DailyCycleAnchorDateTest {

  @Test
  def testDailyScheduleWeeklySunday() {
    val sundaySched: DailySchedule = DailySchedule.weeklyStartingSunday()
      .withWeekDaysInCycle(DayOfWeek.FRIDAY)
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
    val mondaySched: DailySchedule = DailySchedule.weeklyStartingMonday()
      .withWeekDaysInCycle(DayOfWeek.FRIDAY)
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

    val biwSched: DailySchedule = DailySchedule
      .mutipleWeekly(2, DayOfWeek.SUNDAY, 0)
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

  @Test
  def testDatePattern() {
    val matcherOpt = LocalDatePattern.findFirstMatchIn("2016-03-31")
    assertTrue(matcherOpt.isDefined)
    assertTrue(matcherOpt.get.groupCount == 3)
    assertEquals("2016", matcherOpt.get.group("year"))
    assertEquals("03", matcherOpt.get.group("month"))
    assertEquals("31", matcherOpt.get.group("day"))
  }

  @Test
  def testStringToDate() {
    List[(LocalDate,String)](
      (LocalDate.of(2018,3,1), "2018-03-01"),
      (LocalDate.of(2018,12,31), "2018-12-31"),
      (LocalDate.of(2018,1,1), "2018-01-01")
    ).foreach { case (exp, input) =>
      val dateOf: LocalDate = input
      assertEquals(s"Dates do not match $exp vs input $input", exp, dateOf)

    }
  }

  val _expectedEx = ExpectedException.none()
  @Rule
  def expectedEx = _expectedEx

  @Test
  def testDateOutOfBounds1() {
    expectedEx.expect(classOf[java.time.format.DateTimeParseException])
    expectedEx.expectMessage("'2018-02-29' could not be parsed: Invalid date 'February 29' as '2018' is not a leap year")
    val dateOf: LocalDate = "2018-02-29"
  }

  @Test
  def testDateOutOfBounds2() {
    expectedEx.expect(classOf[IllegalArgumentException])
    expectedEx.expectMessage("requirement failed: Not a valid date string: '2018-13-02'")
    val dateOf: LocalDate = "2018-13-02"
  }

  @Test
  def testDateOutOfBounds3() {
    expectedEx.expect(classOf[IllegalArgumentException])
    expectedEx.expectMessage("requirement failed: Not a valid date string: '2018-00-02'")
    val dateOf: LocalDate = "2018-00-02"
  }

  @Test
  def testDateOutOfBounds4() {
    expectedEx.expect(classOf[IllegalArgumentException])
    expectedEx.expectMessage("requirement failed: Not a valid date string: '2018-09-32'")
    val dateOf: LocalDate = "2018-09-32"
  }

  def assertAnchorDatesEqual(expCandPair: (LocalDate, LocalDate), dailySched: DailySchedule) {
    expCandPair match {
      case (exp, candidate) =>
        val d1: LocalDate = dailySched.cycleAnchorDate(candidate)
        assertEquals(s"For candidate $candidate, expected $exp but got $d1", exp, d1)
    }
  }

}
