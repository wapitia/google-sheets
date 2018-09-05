/* Copyright 2016-2018 wapitia.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of wapitia.com or the names of contributors may be used to
 * endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED.
 * WAPITIA.COM ("WAPITIA") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL WAPITIA OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * WAPITIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
/**
 * Wapitia's calendar package allows creation of repeatable schedule in the form of date streams.
 * As an example the client can build a schedule recurring weekly every Friday
 * starting on the week of Monday 2018-10-15, so that the schedule then provides
 * the stream of dates (2018-10-19, 2018-10-26, 2018-11-02, ...) ad infinitum.
 * <p>
 * The two primary entry points are {@link Schedule} and {@link ScheduleFactory}.
 * <p>
 * Object {@code Schedule} hosts an assortment of generic as well as commonly used
 * schedule building functions to easily form the schedule that the client wants.
 * <p>
 * Object {@code ScheduleFactory} provides the tools to build up a schedule
 * from say a configuration data set, giving more of a programmable interface.
 * <p>
 * Providing a schedule stream of dates is a two phase process. First a schedule
 * instance is made defining the repeatable schedule characteristics such as
 * the number of days in the repeating cycle and the schedule days within that cycle.
 * Second, the schedule instance is queried to return a stream of schedule dates
 * on or after a certain date, this is usually done through the schedule's 
 * {@link Schedule.starting starting} method.
 * <p> 
 * Some convenience methods perform both of these phases at once.
 * So for example a call to {@code WeeklySchedule.biweeklyStarting("2018-10-11")}
 * provides the date stream (2018-10-11, 2018-10-25, 2018-11-08, ...),
 * where the biweekly schedule defining this stream is created with
 * the proper size of 14 days and with the given date providing the offset and
 * starting point within the repeating schedule.  
 * The newly created schedule is then immediately discarded once the date stream
 * is returned.  
 */
package com.wapitia.calendar;
