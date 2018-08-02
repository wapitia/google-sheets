package com.wapitia
package calendar

/** Monthly cycle within a year.
 *  @param monthsInCycle: 1 = every month, 2= semi-monthly, 3=quarterly.
 *  @param offsetInCycle: day of offset in month, 1 means 1st day of month, etc.
 *  @TODO offsetInCycle should be a function
 */
case class MonthlyCycle(monthsInCycle: Int, offsetInCycle: Int)
