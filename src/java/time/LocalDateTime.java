/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Objects;

import static java.time.LocalTime.HOURS_PER_DAY;
import static java.time.LocalTime.MICROS_PER_DAY;
import static java.time.LocalTime.MILLIS_PER_DAY;
import static java.time.LocalTime.MINUTES_PER_DAY;
import static java.time.LocalTime.NANOS_PER_DAY;
import static java.time.LocalTime.NANOS_PER_HOUR;
import static java.time.LocalTime.NANOS_PER_MINUTE;
import static java.time.LocalTime.NANOS_PER_SECOND;
import static java.time.LocalTime.SECONDS_PER_DAY;

/**
 * A date-time without a time-zone in the ISO-8601 calendar system,
 * such as {@code 2007-12-03T10:15:30}.
 * <p>
 * {@code LocalDateTime} is an immutable date-time object that represents a date-time,
 * often viewed as year-month-day-hour-minute-second. Other date and time fields,
 * such as day-of-year, day-of-week and week-of-year, can also be accessed.
 * Time is represented to nanosecond precision.
 * For example, the value "2nd October 2007 at 13:45.30.123456789" can be
 * stored in a {@code LocalDateTime}.
 * <p>
 * This class does not store or represent a time-zone.
 * Instead, it is a description of the date, as used for birthdays, combined with
 * the local time as seen on a wall clock.
 * It cannot represent an instant on the time-line without additional information
 * such as an offset or time-zone.
 * <p>
 * The ISO-8601 calendar system is the modern civil calendar system used today
 * in most of the world. It is equivalent to the proleptic Gregorian calendar
 * system, in which today's rules for leap years are applied for all time.
 * For most applications written today, the ISO-8601 rules are entirely suitable.
 * However, any application that makes use of historical dates, and requires them
 * to be accurate will find the ISO-8601 approach unsuitable.
 *
 * <p>
 * This is a <a href="{@docRoot}/java.base/java/lang/doc-files/ValueBased.html">value-based</a>
 * class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of
 * {@code LocalDateTime} may have unpredictable results and should be avoided.
 * The {@code equals} method should be used for comparisons.
 *
 * @implSpec This class is immutable and thread-safe.
 * @since 1.8
 */
/*
 * "本地日期-时间"，"时间"[未关联]所属时区ID，"日期"基于[ISO]历法系统。
 *
 * 可以通过"now"之类的方法获取到某个时区的"本地日期-时间"，不过一旦得到LocalDateTime对象，它将失去时区信息。
 * 换句话说，可以从"时区"计算得到"本地日期-时间"信息，但是却不能由"本地日期-时间"反推该时间点位于哪个时区，
 * 这本质上是因为LocalDateTime对象并不记录这个时间点来自于哪个时区。
 * 当然，也可以通过"of"之类的方法，直接构造一个"本地日期-时间"时间点来使用。
 *
 * 注：
 * 1.LocalDateTime突出了本地性，反映的是某个时区的一个时间点。
 *   Instant是与UTC/GMT"零时区"关联的一个时间点，与时区无关。
 * 2.LocalDateTime对象一旦被构造，它就成为一个无时区约束的自由时间点，它可以在后续与任何时区关联而成为某个时区的本地时间。
 * 3.如果需要一个附带时区信息的"本地日期-时间"，应当使用OffsetDateTime或ZonedDateTime。
 */
public final class LocalDateTime implements Temporal, TemporalAdjuster, ChronoLocalDateTime<LocalDate>, Serializable {
    
    /**
     * The minimum supported {@code LocalDateTime}, '-999999999-01-01T00:00:00'.
     * This is the local date-time of midnight at the start of the minimum date.
     * This combines {@link LocalDate#MIN} and {@link LocalTime#MIN}.
     * This could be used by an application as a "far past" date-time.
     */
    public static final LocalDateTime MIN = LocalDateTime.of(LocalDate.MIN, LocalTime.MIN);
    /**
     * The maximum supported {@code LocalDateTime}, '+999999999-12-31T23:59:59.999999999'.
     * This is the local date-time just before midnight at the end of the maximum date.
     * This combines {@link LocalDate#MAX} and {@link LocalTime#MAX}.
     * This could be used by an application as a "far future" date-time.
     */
    public static final LocalDateTime MAX = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);
    
    /**
     * The date part.
     */
    private final LocalDate date; // "本地日期"部件
    
    /**
     * The time part.
     */
    private final LocalTime time; // "本地时间"部件
    
    
    
    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Constructor.
     *
     * @param date the date part of the date-time, validated not null
     * @param time the time part of the date-time, validated not null
     */
    private LocalDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 工厂方法 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Obtains the current date-time from the system clock in the default time-zone.
     * <p>
     * This will query the {@link Clock#systemDefaultZone() system clock} in the default
     * time-zone to obtain the current date-time.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @return the current date-time using the system clock and default time-zone, not null
     */
    // 基于此刻的UTC时间，构造属于系统默认时区的"本地日期-时间"对象
    public static LocalDateTime now() {
        // 获取一个系统时钟，其预设的时区ID为系统默认的时区ID
        Clock clock = Clock.systemDefaultZone();
        return now(clock);
    }
    
    /**
     * Obtains the current date-time from the system clock in the specified time-zone.
     * <p>
     * This will query the {@link Clock#system(ZoneId) system clock} to obtain the current date-time.
     * Specifying the time-zone avoids dependence on the default time-zone.
     * <p>
     * Using this method will prevent the ability to use an alternate clock for testing
     * because the clock is hard-coded.
     *
     * @param zone the zone ID to use, not null
     *
     * @return the current date-time using the system clock, not null
     */
    // 基于此刻的UTC时间，构造属于zone时区的"本地日期-时间"对象
    public static LocalDateTime now(ZoneId zone) {
        // 获取一个系统时钟，其预设的时区ID为zone
        Clock clock = Clock.system(zone);
        return now(clock);
    }
    
    /**
     * Obtains the current date-time from the specified clock.
     * <p>
     * This will query the specified clock to obtain the current date-time.
     * Using this method allows the use of an alternate clock for testing.
     * The alternate clock may be introduced using {@link Clock dependency injection}.
     *
     * @param clock the clock to use, not null
     *
     * @return the current date-time, not null
     */
    // 基于clock提供的时间戳和时区ID构造"本地日期-时间"对象
    public static LocalDateTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
    
        // 获取clock时钟提供的时间戳
        final Instant instant = clock.instant();
        // 获取clock时钟提供的时区ID
        ZoneId zoneId = clock.getZone();
    
        // 获取与zoneId对应的"时区规则集"
        ZoneRules rules = zoneId.getRules();
        /*
         * 获取zoneId时区在instant时刻的"实际偏移"。
         * 这里可以返回一个准确的"实际偏移"。
         */
        ZoneOffset offset = rules.getOffset(instant);
    
        return ofEpochSecond(instant.getEpochSecond(), instant.getNano(), offset);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * This returns a {@code LocalDateTime} with the specified year, month,
     * day-of-month, hour and minute.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The second and nanosecond fields will be set to zero.
     *
     * @param year       the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month      the month-of-year to represent, not null
     * @param dayOfMonth the day-of-month to represent, from 1 to 31
     * @param hour       the hour-of-day to represent, from 0 to 23
     * @param minute     the minute-of-hour to represent, from 0 to 59
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    // 使用给定的日期部件和时间部件构造一个无时区归属的"本地日期-时间"对象
    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute);
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * This returns a {@code LocalDateTime} with the specified year, month,
     * day-of-month, hour, minute and second.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The nanosecond field will be set to zero.
     *
     * @param year       the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month      the month-of-year to represent, not null
     * @param dayOfMonth the day-of-month to represent, from 1 to 31
     * @param hour       the hour-of-day to represent, from 0 to 23
     * @param minute     the minute-of-hour to represent, from 0 to 59
     * @param second     the second-of-minute to represent, from 0 to 59
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    // 使用给定的日期部件和时间部件构造一个无时区归属的"本地日期-时间"对象
    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * This returns a {@code LocalDateTime} with the specified year, month,
     * day-of-month, hour, minute, second and nanosecond.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year         the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month        the month-of-year to represent, not null
     * @param dayOfMonth   the day-of-month to represent, from 1 to 31
     * @param hour         the hour-of-day to represent, from 0 to 23
     * @param minute       the minute-of-hour to represent, from 0 to 59
     * @param second       the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond the nano-of-second to represent, from 0 to 999,999,999
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    // 使用给定的日期部件和时间部件构造一个无时区归属的"本地日期-时间"对象
    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour and minute, setting the second and nanosecond to zero.
     * <p>
     * This returns a {@code LocalDateTime} with the specified year, month,
     * day-of-month, hour and minute.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The second and nanosecond fields will be set to zero.
     *
     * @param year       the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month      the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth the day-of-month to represent, from 1 to 31
     * @param hour       the hour-of-day to represent, from 0 to 23
     * @param minute     the minute-of-hour to represent, from 0 to 59
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    // 使用给定的日期部件和时间部件构造一个无时区归属的"本地日期-时间"对象
    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute);
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute and second, setting the nanosecond to zero.
     * <p>
     * This returns a {@code LocalDateTime} with the specified year, month,
     * day-of-month, hour, minute and second.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     * The nanosecond field will be set to zero.
     *
     * @param year       the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month      the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth the day-of-month to represent, from 1 to 31
     * @param hour       the hour-of-day to represent, from 0 to 23
     * @param minute     the minute-of-hour to represent, from 0 to 59
     * @param second     the second-of-minute to represent, from 0 to 59
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    // 使用给定的日期部件和时间部件构造一个无时区归属的"本地日期-时间"对象
    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from year, month,
     * day, hour, minute, second and nanosecond.
     * <p>
     * This returns a {@code LocalDateTime} with the specified year, month,
     * day-of-month, hour, minute, second and nanosecond.
     * The day must be valid for the year and month, otherwise an exception will be thrown.
     *
     * @param year         the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month        the month-of-year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth   the day-of-month to represent, from 1 to 31
     * @param hour         the hour-of-day to represent, from 0 to 23
     * @param minute       the minute-of-hour to represent, from 0 to 59
     * @param second       the second-of-minute to represent, from 0 to 59
     * @param nanoOfSecond the nano-of-second to represent, from 0 to 999,999,999
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the value of any field is out of range,
     *                           or if the day-of-month is invalid for the month-year
     */
    // 使用给定的日期部件和时间部件构造一个无时区归属的"本地日期-时间"对象
    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from a date and time.
     *
     * @param date the local date, not null
     * @param time the local time, not null
     *
     * @return the local date-time, not null
     */
    // 使用指定的"本地日期"部件和"本地时间"部件构造一个"本地日期-时间"对象
    public static LocalDateTime of(LocalDate date, LocalTime time) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from an {@code Instant} and zone ID.
     * <p>
     * This creates a local date-time based on the specified instant.
     * First, the offset from UTC/Greenwich is obtained using the zone ID and instant,
     * which is simple as there is only one valid offset for each instant.
     * Then, the instant and offset are used to calculate the local date-time.
     *
     * @param instant the instant to create the date-time from, not null
     * @param zone    the time-zone, which may be an offset, not null
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the result exceeds the supported range
     */
    // 使用时间戳和时区ID构造一个属于zone时区的"本地日期-时间"对象
    public static LocalDateTime ofInstant(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
    
        // 获取与zone对应的"时区规则集"
        ZoneRules rules = zone.getRules();
        /*
         * 获取zone时区在instant时刻的"实际偏移"。
         * 这里可以返回一个准确的"实际偏移"。
         */
        ZoneOffset offset = rules.getOffset(instant);
    
        // 使用UTC时区的纪元秒、纳秒偏移以及时区ID构造一个属于offset时区的"本地日期-时间"
        return ofEpochSecond(instant.getEpochSecond(), instant.getNano(), offset);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} using seconds from the
     * epoch of 1970-01-01T00:00:00Z.
     * <p>
     * This allows the {@link ChronoField#INSTANT_SECONDS epoch-second} field
     * to be converted to a local date-time. This is primarily intended for
     * low-level conversions rather than general application usage.
     *
     * @param epochSecond  the number of seconds from the epoch of 1970-01-01T00:00:00Z
     * @param nanoOfSecond the nanosecond within the second, from 0 to 999,999,999
     * @param offset       the zone offset, not null
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if the result exceeds the supported range,
     *                           or if the nano-of-second is invalid
     */
    // 使用UTC时区的纪元秒、纳秒偏移以及时区ID构造一个属于offset时区的"本地日期-时间"对象
    public static LocalDateTime ofEpochSecond(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
        Objects.requireNonNull(offset, "offset");
    
        ChronoField.NANO_OF_SECOND.checkValidValue(nanoOfSecond);
    
        // 计算offset时区处的纪元秒
        long localSecond = epochSecond + offset.getTotalSeconds();
    
        // 纪元天部件
        long localEpochDay = Math.floorDiv(localSecond, SECONDS_PER_DAY);
        // 秒偏移部件
        int secsOfDay = Math.floorMod(localSecond, SECONDS_PER_DAY);
    
        // 根据给定的纪元天构造"本地日期'
        LocalDate date = LocalDate.ofEpochDay(localEpochDay);
        // 使用指定的纳秒数(不超过一天)构造"本地时间"
        LocalTime time = LocalTime.ofNanoOfDay(secsOfDay * NANOS_PER_SECOND + nanoOfSecond);
    
        return new LocalDateTime(date, time);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from a temporal object.
     * <p>
     * This obtains a local date-time based on the specified temporal.
     * A {@code TemporalAccessor} represents an arbitrary set of date and time information,
     * which this factory converts to an instance of {@code LocalDateTime}.
     * <p>
     * The conversion extracts and combines the {@code LocalDate} and the
     * {@code LocalTime} from the temporal object.
     * Implementations are permitted to perform optimizations such as accessing
     * those fields that are equivalent to the relevant objects.
     * <p>
     * This method matches the signature of the functional interface {@link TemporalQuery}
     * allowing it to be used as a query via method reference, {@code LocalDateTime::from}.
     *
     * @param temporal the temporal object to convert, not null
     *
     * @return the local date-time, not null
     *
     * @throws DateTimeException if unable to convert to a {@code LocalDateTime}
     */
    // 从temporal中获取/构造LocalDateTime对象
    public static LocalDateTime from(TemporalAccessor temporal) {
        if(temporal instanceof LocalDateTime) {
            return (LocalDateTime) temporal;
        }
    
        if(temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toLocalDateTime();
        }
    
        if(temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toLocalDateTime();
        }
    
        try {
            // 从temporal中查询LocalDate部件的信息
            LocalDate date = LocalDate.from(temporal);
            // 从temporal中查询LocalTime部件
            LocalTime time = LocalTime.from(temporal);
        
            return new LocalDateTime(date, time);
        } catch(DateTimeException ex) {
            throw new DateTimeException("Unable to obtain LocalDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from a text string such as {@code 2007-12-03T10:15:30}.
     * <p>
     * The string must represent a valid date-time and is parsed using
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param text the text to parse such as "2007-12-03T10:15:30", not null
     *
     * @return the parsed local date-time, not null
     *
     * @throws DateTimeParseException if the text cannot be parsed
     */
    // 从指定的文本中解析出LocalDateTime信息，要求该文本符合ISO规范，即类似：2020-01-15T08:20:53
    public static LocalDateTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    /**
     * Obtains an instance of {@code LocalDateTime} from a text string using a specific formatter.
     * <p>
     * The text is parsed using the formatter, returning a date-time.
     *
     * @param text      the text to parse, not null
     * @param formatter the formatter to use, not null
     *
     * @return the parsed local date-time, not null
     *
     * @throws DateTimeParseException if the text cannot be parsed
     */
    // 从指定的文本中解析出LocalDateTime信息，要求该文本符合指定的格式规范
    public static LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.parse(text, LocalDateTime::from);
    }
    
    /*▲ 工厂方法 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 转换 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Combines this date-time with an offset to create an {@code OffsetDateTime}.
     * <p>
     * This returns an {@code OffsetDateTime} formed from this date-time at the specified offset.
     * All possible combinations of date-time and offset are valid.
     *
     * @param offset the offset to combine with, not null
     *
     * @return the offset date-time formed from this date-time and the specified offset, not null
     */
    // 拿当前时间量与指定的时区偏移构造一个属于offset的"本地日期-时间"对象
    public OffsetDateTime atOffset(ZoneOffset offset) {
        return OffsetDateTime.of(this, offset);
    }
    
    /**
     * Combines this date-time with a time-zone to create a {@code ZonedDateTime}.
     * <p>
     * This returns a {@code ZonedDateTime} formed from this date-time at the
     * specified time-zone. The result will match this date-time as closely as possible.
     * Time-zone rules, such as daylight savings, mean that not every local date-time
     * is valid for the specified zone, thus the local date-time may be adjusted.
     * <p>
     * The local date-time is resolved to a single instant on the time-line.
     * This is achieved by finding a valid offset from UTC/Greenwich for the local
     * date-time as defined by the {@link ZoneRules rules} of the zone ID.
     * <p>
     * In most cases, there is only one valid offset for a local date-time.
     * In the case of an overlap, where clocks are set back, there are two valid offsets.
     * This method uses the earlier offset typically corresponding to "summer".
     * <p>
     * In the case of a gap, where clocks jump forward, there is no valid offset.
     * Instead, the local date-time is adjusted to be later by the length of the gap.
     * For a typical one hour daylight savings change, the local date-time will be
     * moved one hour later into the offset typically corresponding to "summer".
     * <p>
     * To obtain the later offset during an overlap, call
     * {@link ZonedDateTime#withLaterOffsetAtOverlap()} on the result of this method.
     * To throw an exception when there is a gap or overlap, use
     * {@link ZonedDateTime#ofStrict(LocalDateTime, ZoneOffset, ZoneId)}.
     *
     * @param zone the time-zone to use, not null
     *
     * @return the zoned date-time formed from this date-time, not null
     */
    /*
     * 拿当前时间量与指定的时区ID构造一个属于zone时区的"本地日期-时间"对象
     * 如果zone不是ZoneOffset类型，则时区偏移时间可能会不准确。
     */
    @Override
    public ZonedDateTime atZone(ZoneId zone) {
        return ZonedDateTime.of(this, zone);
    }
    
    /*▲ 转换 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 部件 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Gets the {@code LocalDate} part of this date-time.
     * <p>
     * This returns a {@code LocalDate} with the same year, month and day
     * as this date-time.
     *
     * @return the date part of this date-time, not null
     */
    // 返回"本地日期"部件
    @Override
    public LocalDate toLocalDate() {
        return date;
    }
    
    /**
     * Gets the {@code LocalTime} part of this date-time.
     * <p>
     * This returns a {@code LocalTime} with the same hour, minute, second and
     * nanosecond as this date-time.
     *
     * @return the time part of this date-time, not null
     */
    // 返回"本地时间"部件
    @Override
    public LocalTime toLocalTime() {
        return time;
    }
    
    /**
     * Gets the year field.
     * <p>
     * This method returns the primitive {@code int} value for the year.
     * <p>
     * The year returned by this method is proleptic as per {@code get(YEAR)}.
     * To obtain the year-of-era, use {@code get(YEAR_OF_ERA)}.
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    // (哪年)返回"年份"部件[-999999999, 999999999]，由"本地日期"部件计算而来
    public int getYear() {
        return date.getYear();
    }
    
    /**
     * Gets the month-of-year field from 1 to 12.
     * <p>
     * This method returns the month as an {@code int} from 1 to 12.
     * Application code is frequently clearer if the enum {@link Month}
     * is used by calling {@link #getMonth()}.
     *
     * @return the month-of-year, from 1 to 12
     *
     * @see #getMonth()
     */
    // (哪月)返回"月份"部件[1, 12]，由"本地日期"部件计算而来
    public int getMonthValue() {
        return date.getMonthValue();
    }
    
    /**
     * Gets the month-of-year field using the {@code Month} enum.
     * <p>
     * This method returns the enum {@link Month} for the month.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link Month#getValue() int value}.
     *
     * @return the month-of-year, not null
     *
     * @see #getMonthValue()
     */
    // (哪月)以Month形式返回"月份"部件，由"本地日期"部件计算而来
    public Month getMonth() {
        return date.getMonth();
    }
    
    /**
     * Gets the day-of-month field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 1 to 31
     */
    // (哪日)返回"天"部件[1, 28/31]
    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }
    
    /**
     * Gets the day-of-week field, which is an enum {@code DayOfWeek}.
     * <p>
     * This method returns the enum {@link DayOfWeek} for the day-of-week.
     * This avoids confusion as to what {@code int} values mean.
     * If you need access to the primitive {@code int} value then the enum
     * provides the {@link DayOfWeek#getValue() int value}.
     * <p>
     * Additional information can be obtained from the {@code DayOfWeek}.
     * This includes textual names of the values.
     *
     * @return the day-of-week, not null
     */
    // (周几)返回当前"本地日期-时间"是所在周的第几天
    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }
    
    /**
     * Gets the day-of-year field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-year.
     *
     * @return the day-of-year, from 1 to 365, or 366 in a leap year
     */
    // 返回当前"本地日期-时间"是所在年份的第几天
    public int getDayOfYear() {
        return date.getDayOfYear();
    }
    
    /**
     * Gets the hour-of-day field.
     *
     * @return the hour-of-day, from 0 to 23
     */
    // (几时)返回"小时"部件[0, 23]，由"本地时间"部件计算而来
    public int getHour() {
        return time.getHour();
    }
    
    /**
     * Gets the minute-of-hour field.
     *
     * @return the minute-of-hour, from 0 to 59
     */
    // (几分)返回"分钟"部件[0, 59]，由"本地时间"部件计算而来
    public int getMinute() {
        return time.getMinute();
    }
    
    /**
     * Gets the second-of-minute field.
     *
     * @return the second-of-minute, from 0 to 59
     */
    // (几秒)返回"秒"部件[0, 59]，由"本地时间"部件计算而来
    public int getSecond() {
        return time.getSecond();
    }
    
    /**
     * Gets the nano-of-second field.
     *
     * @return the nano-of-second, from 0 to 999,999,999
     */
    // (几纳秒)返回"纳秒"部件[0, 999999999]，由"本地时间"部件计算而来
    public int getNano() {
        return time.getNano();
    }
    
    /*▲ 部件 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 增加 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Returns a copy of this date-time with the specified amount added.
     * <p>
     * This returns a {@code LocalDateTime}, based on this one, with the specified amount added.
     * The amount is typically {@link Period} or {@link Duration} but may be
     * any other type implementing the {@link TemporalAmount} interface.
     * <p>
     * The calculation is delegated to the amount object by calling
     * {@link TemporalAmount#addTo(Temporal)}. The amount implementation is free
     * to implement the addition in any way it wishes, however it typically
     * calls back to {@link #plus(long, TemporalUnit)}. Consult the documentation
     * of the amount implementation to determine if it can be successfully added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd the amount to add, not null
     *
     * @return a {@code LocalDateTime} based on this date-time with the addition made, not null
     *
     * @throws DateTimeException   if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    /*
     * 对当前时间量的值与参数中的"时间段"求和
     *
     * 如果求和后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"求和"后的新对象再返回。
     */
    @Override
    public LocalDateTime plus(TemporalAmount amountToAdd) {
        Objects.requireNonNull(amountToAdd, "amountToAdd");
        
        if(amountToAdd instanceof Period) {
            Period periodToAdd = (Period) amountToAdd;
            return with(date.plus(periodToAdd), time);
        }
        
        return (LocalDateTime) amountToAdd.addTo(this);
    }
    
    /**
     * Returns a copy of this date-time with the specified amount added.
     * <p>
     * This returns a {@code LocalDateTime}, based on this one, with the amount
     * in terms of the unit added. If it is not possible to add the amount, because the
     * unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoUnit} then the addition is implemented here.
     * Date units are added as per {@link LocalDate#plus(long, TemporalUnit)}.
     * Time units are added as per {@link LocalTime#plus(long, TemporalUnit)} with
     * any overflow in days added equivalent to using {@link #plusDays(long)}.
     * <p>
     * If the field is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.addTo(Temporal, long)}
     * passing {@code this} as the argument. In this case, the unit determines
     * whether and how to perform the addition.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd the amount of the unit to add to the result, may be negative
     * @param unit        the unit of the amount to add, not null
     *
     * @return a {@code LocalDateTime} based on this date-time with the specified amount added, not null
     *
     * @throws DateTimeException                if the addition cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException              if numeric overflow occurs
     */
    /*
     * 对当前时间量的值累加amountToAdd个unit单位的时间量
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    @Override
    public LocalDateTime plus(long amountToAdd, TemporalUnit unit) {
        if(unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch(f) {
                case NANOS:
                    return plusNanos(amountToAdd);
                case MICROS:
                    return plusDays(amountToAdd / MICROS_PER_DAY).plusNanos((amountToAdd % MICROS_PER_DAY) * 1000);
                case MILLIS:
                    return plusDays(amountToAdd / MILLIS_PER_DAY).plusNanos((amountToAdd % MILLIS_PER_DAY) * 1000_000);
                case SECONDS:
                    return plusSeconds(amountToAdd);
                case MINUTES:
                    return plusMinutes(amountToAdd);
                case HOURS:
                    return plusHours(amountToAdd);
                case HALF_DAYS:
                    return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);  // no overflow (256 is multiple of 2)
            }
            
            return with(date.plus(amountToAdd, unit), time);
        }
        
        return unit.addTo(this, amountToAdd);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of years added.
     * <p>
     * This method adds the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years the years to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the years added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加years年
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusYears(long years) {
        LocalDate newDate = date.plusYears(years);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of months added.
     * <p>
     * This method adds the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months the months to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the months added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加months月
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusMonths(long months) {
        LocalDate newDate = date.plusMonths(months);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of weeks added.
     * <p>
     * This method adds the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks the weeks to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the weeks added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加weeks周
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusWeeks(long weeks) {
        LocalDate newDate = date.plusWeeks(weeks);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of days added.
     * <p>
     * This method adds the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days the days to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the days added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加days天
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusDays(long days) {
        LocalDate newDate = date.plusDays(days);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours the hours to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the hours added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加hours小时
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusHours(long hours) {
        return plusWithOverflow(date, hours, 0, 0, 0, 1);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes the minutes to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the minutes added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加minutes分钟
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusMinutes(long minutes) {
        return plusWithOverflow(date, 0, minutes, 0, 0, 1);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds the seconds to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the seconds added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加seconds秒
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusSeconds(long seconds) {
        return plusWithOverflow(date, 0, 0, seconds, 0, 1);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos the nanos to add, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the nanoseconds added, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上累加nanos纳秒
     *
     * 如果累加后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"累加"操作后的新对象再返回。
     */
    public LocalDateTime plusNanos(long nanos) {
        return plusWithOverflow(date, 0, 0, 0, nanos, 1);
    }
    
    /*▲ 增加 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 减少 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Returns a copy of this date-time with the specified amount subtracted.
     * <p>
     * This returns a {@code LocalDateTime}, based on this one, with the specified amount subtracted.
     * The amount is typically {@link Period} or {@link Duration} but may be
     * any other type implementing the {@link TemporalAmount} interface.
     * <p>
     * The calculation is delegated to the amount object by calling
     * {@link TemporalAmount#subtractFrom(Temporal)}. The amount implementation is free
     * to implement the subtraction in any way it wishes, however it typically
     * calls back to {@link #minus(long, TemporalUnit)}. Consult the documentation
     * of the amount implementation to determine if it can be successfully subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract the amount to subtract, not null
     *
     * @return a {@code LocalDateTime} based on this date-time with the subtraction made, not null
     *
     * @throws DateTimeException   if the subtraction cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    /*
     * 对当前时间量的值与参数中的"时间段"求差
     *
     * 如果求差后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"求差"后的新对象再返回。
     */
    @Override
    public LocalDateTime minus(TemporalAmount amountToSubtract) {
        Objects.requireNonNull(amountToSubtract, "amountToSubtract");
        
        if(amountToSubtract instanceof Period) {
            Period periodToSubtract = (Period) amountToSubtract;
            return with(date.minus(periodToSubtract), time);
        }
        
        return (LocalDateTime) amountToSubtract.subtractFrom(this);
    }
    
    /**
     * Returns a copy of this date-time with the specified amount subtracted.
     * <p>
     * This returns a {@code LocalDateTime}, based on this one, with the amount
     * in terms of the unit subtracted. If it is not possible to subtract the amount,
     * because the unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * This method is equivalent to {@link #plus(long, TemporalUnit)} with the amount negated.
     * See that method for a full description of how addition, and thus subtraction, works.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToSubtract the amount of the unit to subtract from the result, may be negative
     * @param unit             the unit of the amount to subtract, not null
     *
     * @return a {@code LocalDateTime} based on this date-time with the specified amount subtracted, not null
     *
     * @throws DateTimeException                if the subtraction cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException              if numeric overflow occurs
     */
    /*
     * 对当前时间量的值减去amountToSubtract个unit单位的时间量
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    @Override
    public LocalDateTime minus(long amountToSubtract, TemporalUnit unit) {
        if(amountToSubtract == Long.MIN_VALUE) {
            return plus(Long.MAX_VALUE, unit).plus(1, unit);
        }
        
        return plus(-amountToSubtract, unit);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of years subtracted.
     * <p>
     * This method subtracts the specified amount from the years field in three steps:
     * <ol>
     * <li>Subtract the input years from the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) minus one year would result in the
     * invalid date 2007-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2007-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years the years to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the years subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去years年
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusYears(long years) {
        if(years == Long.MIN_VALUE) {
            return plusYears(Long.MAX_VALUE).plusYears(1);
        }
        
        return plusYears(-years);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of months subtracted.
     * <p>
     * This method subtracts the specified amount from the months field in three steps:
     * <ol>
     * <li>Subtract the input months from the month-of-year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day-of-month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 minus one month would result in the invalid date
     * 2007-02-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months the months to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the months subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去months月
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusMonths(long months) {
        if(months == Long.MIN_VALUE) {
            return plusMonths(Long.MAX_VALUE).plusMonths(1);
        }
        
        return plusMonths(-months);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of weeks subtracted.
     * <p>
     * This method subtracts the specified amount in weeks from the days field decrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-07 minus one week would result in 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks the weeks to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the weeks subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去weeks周
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusWeeks(long weeks) {
        if(weeks == Long.MIN_VALUE) {
            return plusWeeks(Long.MAX_VALUE).plusWeeks(1);
        }
        
        return plusWeeks(-weeks);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of days subtracted.
     * <p>
     * This method subtracts the specified amount from the days field decrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2009-01-01 minus one day would result in 2008-12-31.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days the days to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the days subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去days天
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusDays(long days) {
        if(days == Long.MIN_VALUE) {
            return plusDays(Long.MAX_VALUE).plusDays(1);
        }
        
        return plusDays(-days);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of hours subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours the hours to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the hours subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去hours小时
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusHours(long hours) {
        return plusWithOverflow(date, hours, 0, 0, 0, -1);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of minutes subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes the minutes to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the minutes subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去minutes分钟
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusMinutes(long minutes) {
        return plusWithOverflow(date, 0, minutes, 0, 0, -1);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of seconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds the seconds to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the seconds subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去seconds秒
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusSeconds(long seconds) {
        return plusWithOverflow(date, 0, 0, seconds, 0, -1);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified number of nanoseconds subtracted.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos the nanos to subtract, may be negative
     *
     * @return a {@code LocalDateTime} based on this date-time with the nanoseconds subtracted, not null
     *
     * @throws DateTimeException if the result exceeds the supported date range
     */
    /*
     * 在当前时间量的值上减去nanos纳秒
     *
     * 如果减去后的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"减去"操作后的新对象再返回。
     */
    public LocalDateTime minusNanos(long nanos) {
        return plusWithOverflow(date, 0, 0, 0, nanos, -1);
    }
    
    /*▲ 减少 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 时间量单位 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Checks if the specified unit is supported.
     * <p>
     * This checks if the specified unit can be added to, or subtracted from, this date-time.
     * If false, then calling the {@link #plus(long, TemporalUnit)} and
     * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
     * <p>
     * If the unit is a {@link ChronoUnit} then the query is implemented here.
     * The supported units are:
     * <ul>
     * <li>{@code NANOS}
     * <li>{@code MICROS}
     * <li>{@code MILLIS}
     * <li>{@code SECONDS}
     * <li>{@code MINUTES}
     * <li>{@code HOURS}
     * <li>{@code HALF_DAYS}
     * <li>{@code DAYS}
     * <li>{@code WEEKS}
     * <li>{@code MONTHS}
     * <li>{@code YEARS}
     * <li>{@code DECADES}
     * <li>{@code CENTURIES}
     * <li>{@code MILLENNIA}
     * <li>{@code ERAS}
     * </ul>
     * All other {@code ChronoUnit} instances will return false.
     * <p>
     * If the unit is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.isSupportedBy(Temporal)}
     * passing {@code this} as the argument.
     * Whether the unit is supported is determined by the unit.
     *
     * @param unit the unit to check, null returns false
     *
     * @return true if the unit can be added/subtracted, false if not
     */
    // 判断当前时间量是否支持指定的时间量单位
    @Override
    public boolean isSupported(TemporalUnit unit) {
        return ChronoLocalDateTime.super.isSupported(unit);
    }
    
    /*▲ 时间量单位 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 时间量字段操作(TemporalAccessor) ███████████████████████████████████████████████████████┓ */
    
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this date-time can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range},
     * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
     * methods will throw an exception.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The supported fields are:
     * <ul>
     * <li>{@code NANO_OF_SECOND}
     * <li>{@code NANO_OF_DAY}
     * <li>{@code MICRO_OF_SECOND}
     * <li>{@code MICRO_OF_DAY}
     * <li>{@code MILLI_OF_SECOND}
     * <li>{@code MILLI_OF_DAY}
     * <li>{@code SECOND_OF_MINUTE}
     * <li>{@code SECOND_OF_DAY}
     * <li>{@code MINUTE_OF_HOUR}
     * <li>{@code MINUTE_OF_DAY}
     * <li>{@code HOUR_OF_AMPM}
     * <li>{@code CLOCK_HOUR_OF_AMPM}
     * <li>{@code HOUR_OF_DAY}
     * <li>{@code CLOCK_HOUR_OF_DAY}
     * <li>{@code AMPM_OF_DAY}
     * <li>{@code DAY_OF_WEEK}
     * <li>{@code ALIGNED_DAY_OF_WEEK_IN_MONTH}
     * <li>{@code ALIGNED_DAY_OF_WEEK_IN_YEAR}
     * <li>{@code DAY_OF_MONTH}
     * <li>{@code DAY_OF_YEAR}
     * <li>{@code EPOCH_DAY}
     * <li>{@code ALIGNED_WEEK_OF_MONTH}
     * <li>{@code ALIGNED_WEEK_OF_YEAR}
     * <li>{@code MONTH_OF_YEAR}
     * <li>{@code PROLEPTIC_MONTH}
     * <li>{@code YEAR_OF_ERA}
     * <li>{@code YEAR}
     * <li>{@code ERA}
     * </ul>
     * All other {@code ChronoField} instances will return false.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the field is supported is determined by the field.
     *
     * @param field the field to check, null returns false
     *
     * @return true if the field is supported on this date-time, false if not
     */
    // 判断当前时间量是否支持指定的时间量字段
    @Override
    public boolean isSupported(TemporalField field) {
        if(field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isDateBased() || f.isTimeBased();
        }
        
        return field != null && field.isSupportedBy(this);
    }
    
    /**
     * Gets the range of valid values for the specified field.
     * <p>
     * The range object expresses the minimum and maximum valid values for a field.
     * This date-time is used to enhance the accuracy of the returned range.
     * If it is not possible to return the range, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return
     * appropriate range instances.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.rangeRefinedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the range can be obtained is determined by the field.
     *
     * @param field the field to query the range for, not null
     *
     * @return the range of valid values for the field, not null
     *
     * @throws DateTimeException                if the range for the field cannot be obtained
     * @throws UnsupportedTemporalTypeException if the field is not supported
     */
    // 返回时间量字段field的取值区间，通常要求当前时间量支持该时间量字段
    @Override
    public ValueRange range(TemporalField field) {
        if(field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return (f.isTimeBased() ? time.range(field) : date.range(field));
        }
        
        return field.rangeRefinedBy(this);
    }
    
    /**
     * Gets the value of the specified field from this date-time as an {@code int}.
     * <p>
     * This queries this date-time for the value of the specified field.
     * The returned value will always be within the valid range of values for the field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this date-time, except {@code NANO_OF_DAY}, {@code MICRO_OF_DAY},
     * {@code EPOCH_DAY} and {@code PROLEPTIC_MONTH} which are too large to fit in
     * an {@code int} and throw an {@code UnsupportedTemporalTypeException}.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
     * passing {@code this} as the argument. Whether the value can be obtained,
     * and what the value represents, is determined by the field.
     *
     * @param field the field to get, not null
     *
     * @return the value for the field
     *
     * @throws DateTimeException                if a value for the field cannot be obtained or
     *                                          the value is outside the range of valid values for the field
     * @throws UnsupportedTemporalTypeException if the field is not supported or
     *                                          the range of values exceeds an {@code int}
     * @throws ArithmeticException              if numeric overflow occurs
     */
    /*
     * 以int形式返回时间量字段field的值
     *
     * 目前支持的字段包括：
     *
     * ChronoField.YEAR                         - 返回"Proleptic年"部件(哪年)
     * ChronoField.MONTH_OF_YEAR                - 返回"月份"部件(哪月)
     * ChronoField.DAY_OF_MONTH                 - 返回"天"部件(哪日)
     * ....................................................................................................
     * ChronoField.ERA                          - 计算"Proleptic年"年所在的纪元；在公历系统中，0是公元前，1是公元(后)
     * ChronoField.YEAR_OF_ERA                  - 将"Proleptic年"转换为位于纪元中的[年]后返回
     * ChronoField.DAY_OF_WEEK                  - 计算当前"本地日期"是一周的第几天(周几)
     * ChronoField.DAY_OF_YEAR                  - 计算当前"本地日期"是所在年份的第几天
     * ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH - 如果当前"本地日期"的当月第一天是周一，依次推算当前"本地日期"当月其它天是周几
     * ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR  - 如果当前"本地日期"的当年第一天是周一，依次推算当前"本地日期"当年其它天是周几
     * ChronoField.ALIGNED_WEEK_OF_MONTH        - 如果当前"本地日期"的当月第一天是周一，依次推算当前"本地日期"位于第几周
     * ChronoField.ALIGNED_WEEK_OF_YEAR         - 如果当前"本地日期"的当年第一天是周一，依次推算当前"本地日期"位于第几周
     * ChronoField.PROLEPTIC_MONTH              ×
     * ChronoField.EPOCH_DAY                    ×
     * =========================================================================================
     * ChronoField.HOUR_OF_DAY        - 返回小时部件(几时)
     * ChronoField.MINUTE_OF_HOUR     - 返回分钟部件(几分)
     * ChronoField.SECOND_OF_MINUTE   - 返回秒部件(几秒)
     * ChronoField.NANO_OF_SECOND     - 返回纳秒部件(几纳秒)
     * ..........................................................................................
     * ChronoField.MICRO_OF_SECOND    - 从纳秒部件中计算出包含的微秒数（不要与纳秒部件同时展示）
     * ChronoField.MILLI_OF_SECOND    - 从纳秒部件中计算出包含的毫秒数（不要与纳秒部件同时展示）
     * ChronoField.NANO_OF_DAY        ×
     * ChronoField.MICRO_OF_DAY       ×
     * ChronoField.MILLI_OF_DAY       - 计算当前"本地时间"包含的毫秒数
     * ChronoField.SECOND_OF_DAY      - 计算当前"本地时间"包含的秒数
     * ChronoField.MINUTE_OF_DAY      - 计算当前"本地时间"包含的分钟数
     * ChronoField.HOUR_OF_AMPM       - 计算当前"本地时间"包含的小时是12小时制中的哪个[小时](计数从0~11)
     * ChronoField.CLOCK_HOUR_OF_AMPM - 计算当前"本地时间"包含的小时是12小时制中的哪个[钟点](计数从1~12)
     * ChronoField.CLOCK_HOUR_OF_DAY  - 计算当前"本地时间"包含的小时是24小时制中的哪个[钟点](计数从1~24)
     * ChronoField.AMPM_OF_DAY        - 计算当前"本地时间"位于上午(0)还是下午(1)
     */
    @Override
    public int get(TemporalField field) {
        if(field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return (f.isTimeBased() ? time.get(field) : date.get(field));
        }
        
        return ChronoLocalDateTime.super.get(field);
    }
    
    /**
     * Gets the value of the specified field from this date-time as a {@code long}.
     * <p>
     * This queries this date-time for the value of the specified field.
     * If it is not possible to return the value, because the field is not supported
     * or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will return valid
     * values based on this date-time.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
     * passing {@code this} as the argument. Whether the value can be obtained,
     * and what the value represents, is determined by the field.
     *
     * @param field the field to get, not null
     *
     * @return the value for the field
     *
     * @throws DateTimeException                if a value for the field cannot be obtained
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException              if numeric overflow occurs
     */
    /*
     * 以long形式返回时间量字段field的值
     *
     * 目前支持的字段包括：
     *
     * ChronoField.YEAR                         - 返回"Proleptic年"部件(哪年)
     * ChronoField.MONTH_OF_YEAR                - 返回"月份"部件(哪月)
     * ChronoField.DAY_OF_MONTH                 - 返回"天"部件(哪日)
     * ....................................................................................................
     * ChronoField.ERA                          - 计算"Proleptic年"年所在的纪元；在公历系统中，0是公元前，1是公元(后)
     * ChronoField.YEAR_OF_ERA                  - 将"Proleptic年"转换为位于纪元中的[年]后返回
     * ChronoField.DAY_OF_WEEK                  - 计算当前"本地日期"是一周的第几天(周几)
     * ChronoField.DAY_OF_YEAR                  - 计算当前"本地日期"是所在年份的第几天
     * ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH - 如果当前"本地日期"的当月第一天是周一，依次推算当前"本地日期"当月其它天是周几
     * ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR  - 如果当前"本地日期"的当年第一天是周一，依次推算当前"本地日期"当年其它天是周几
     * ChronoField.ALIGNED_WEEK_OF_MONTH        - 如果当前"本地日期"的当月第一天是周一，依次推算当前"本地日期"位于第几周
     * ChronoField.ALIGNED_WEEK_OF_YEAR         - 如果当前"本地日期"的当年第一天是周一，依次推算当前"本地日期"位于第几周
     * ChronoField.PROLEPTIC_MONTH              = 计算当前时间量包含的"Proleptic月"
     * ChronoField.EPOCH_DAY                    = 计算当前时间量包含的"纪元天"
     * =====================================================================================================
     * ChronoField.HOUR_OF_DAY        - 返回小时部件(几时)
     * ChronoField.MINUTE_OF_HOUR     - 返回分钟部件(几分)
     * ChronoField.SECOND_OF_MINUTE   - 返回秒部件(几秒)
     * ChronoField.NANO_OF_SECOND     - 返回纳秒部件(几纳秒)
     * ..........................................................................................
     * ChronoField.MICRO_OF_SECOND    - 从纳秒部件中计算出包含的微秒数（不要与纳秒部件同时展示）
     * ChronoField.MILLI_OF_SECOND    - 从纳秒部件中计算出包含的毫秒数（不要与纳秒部件同时展示）
     * ChronoField.NANO_OF_DAY        = 计算当前"本地时间"包含的纳秒数
     * ChronoField.MICRO_OF_DAY       = 计算当前"本地时间"包含的微秒数
     * ChronoField.MILLI_OF_DAY       - 计算当前"本地时间"包含的毫秒数
     * ChronoField.SECOND_OF_DAY      - 计算当前"本地时间"包含的秒数
     * ChronoField.MINUTE_OF_DAY      - 计算当前"本地时间"包含的分钟数
     * ChronoField.HOUR_OF_AMPM       - 计算当前"本地时间"包含的小时是12小时制中的哪个[小时](计数从0~11)
     * ChronoField.CLOCK_HOUR_OF_AMPM - 计算当前"本地时间"包含的小时是12小时制中的哪个[钟点](计数从1~12)
     * ChronoField.CLOCK_HOUR_OF_DAY  - 计算当前"本地时间"包含的小时是24小时制中的哪个[钟点](计数从1~24)
     * ChronoField.AMPM_OF_DAY        - 计算当前"本地时间"位于上午(0)还是下午(1)
     */
    @Override
    public long getLong(TemporalField field) {
        if(field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return (f.isTimeBased() ? time.getLong(field) : date.getLong(field));
        }
        
        return field.getFrom(this);
    }
    
    /**
     * Queries this date-time using the specified query.
     * <p>
     * This queries this date-time using the specified query strategy object.
     * The {@code TemporalQuery} object defines the logic to be used to
     * obtain the result. Read the documentation of the query to understand
     * what the result of this method will be.
     * <p>
     * The result of this method is obtained by invoking the
     * {@link TemporalQuery#queryFrom(TemporalAccessor)} method on the
     * specified query passing {@code this} as the argument.
     *
     * @param <R>   the type of the result
     * @param query the query to invoke, not null
     *
     * @return the query result, null may be returned (defined by the query)
     *
     * @throws DateTimeException   if unable to query (defined by the query)
     * @throws ArithmeticException if numeric overflow occurs (defined by the query)
     */
    // 使用指定的时间量查询器，从当前时间量中查询目标信息
    @SuppressWarnings("unchecked")
    @Override
    public <R> R query(TemporalQuery<R> query) {
        if(query == TemporalQueries.localDate()) {
            return (R) date;
        }
        
        return ChronoLocalDateTime.super.query(query);
    }
    
    /*▲ 时间量字段操作(TemporalAccessor) ███████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 整合 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Returns an adjusted copy of this date-time.
     * <p>
     * This returns a {@code LocalDateTime}, based on this one, with the date-time adjusted.
     * The adjustment takes place using the specified adjuster strategy object.
     * Read the documentation of the adjuster to understand what adjustment will be made.
     * <p>
     * A simple adjuster might simply set the one of the fields, such as the year field.
     * A more complex adjuster might set the date to the last day of the month.
     * <p>
     * A selection of common adjustments is provided in
     * {@link java.time.temporal.TemporalAdjusters TemporalAdjusters}.
     * These include finding the "last day of the month" and "next Wednesday".
     * Key date-time classes also implement the {@code TemporalAdjuster} interface,
     * such as {@link Month} and {@link java.time.MonthDay MonthDay}.
     * The adjuster is responsible for handling special cases, such as the varying
     * lengths of month and leap years.
     * <p>
     * For example this code returns a date on the last day of July:
     * <pre>
     *  import static java.time.Month.*;
     *  import static java.time.temporal.TemporalAdjusters.*;
     *
     *  result = localDateTime.with(JULY).with(lastDayOfMonth());
     * </pre>
     * <p>
     * The classes {@link LocalDate} and {@link LocalTime} implement {@code TemporalAdjuster},
     * thus this method can be used to change the date, time or offset:
     * <pre>
     *  result = localDateTime.with(date);
     *  result = localDateTime.with(time);
     * </pre>
     * <p>
     * The result of this method is obtained by invoking the
     * {@link TemporalAdjuster#adjustInto(Temporal)} method on the
     * specified adjuster passing {@code this} as the argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param adjuster the adjuster to use, not null
     *
     * @return a {@code LocalDateTime} based on {@code this} with the adjustment made, not null
     *
     * @throws DateTimeException   if the adjustment cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    /*
     * 使用指定的时间量整合器adjuster来构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     */
    @Override
    public LocalDateTime with(TemporalAdjuster adjuster) {
        
        if(adjuster instanceof LocalDate) {
            return with((LocalDate) adjuster, time);
        }
        
        if(adjuster instanceof LocalTime) {
            return with(date, (LocalTime) adjuster);
        }
        
        if(adjuster instanceof LocalDateTime) {
            return (LocalDateTime) adjuster;
        }
        
        return (LocalDateTime) adjuster.adjustInto(this);
    }
    
    /**
     * Returns a copy of this date-time with the specified field set to a new value.
     * <p>
     * This returns a {@code LocalDateTime}, based on this one, with the value
     * for the specified field changed.
     * This can be used to change any supported field, such as the year, month or day-of-month.
     * If it is not possible to set the value, because the field is not supported or for
     * some other reason, an exception is thrown.
     * <p>
     * In some cases, changing the specified field can cause the resulting date-time to become invalid,
     * such as changing the month from 31st January to February would make the day-of-month invalid.
     * In cases like this, the field is responsible for resolving the date. Typically it will choose
     * the previous valid date, which would be the last valid day of February in this example.
     * <p>
     * If the field is a {@link ChronoField} then the adjustment is implemented here.
     * The {@link #isSupported(TemporalField) supported fields} will behave as per
     * the matching method on {@link LocalDate#with(TemporalField, long) LocalDate}
     * or {@link LocalTime#with(TemporalField, long) LocalTime}.
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.adjustInto(Temporal, long)}
     * passing {@code this} as the argument. In this case, the field determines
     * whether and how to adjust the instant.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field    the field to set in the result, not null
     * @param newValue the new value of the field in the result
     *
     * @return a {@code LocalDateTime} based on {@code this} with the specified field set, not null
     *
     * @throws DateTimeException                if the field cannot be set
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException              if numeric overflow occurs
     */
    /*
     * 通过整合指定类型的字段和当前时间量中的其他类型的字段来构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * field   : 待整合的字段(类型)
     * newValue: field的原始值，需要根据filed的类型进行放缩
     *
     * 目前支持传入的字段值包括：
     *
     * ChronoField.YEAR                         - 与[Proleptic-年]整合，只会覆盖当前时间量的"Proleptic年"部件
     * ChronoField.MONTH_OF_YEAR                - 与一年中的[月](1, 12)整合，只会覆盖当前时间量的"月份"组件
     * ChronoField.DAY_OF_MONTH                 - 与一月中的[天](1, 28/31)整合，只会覆盖当前时间量的"天"组件
     * ..............................................................................................................................
     * ChronoField.ERA                          - 与[纪元]整合，即切换公元前与公元(后)
     * ChronoField.YEAR_OF_ERA                  - 与位于纪元中的[年]整合，这会将该年份进行转换后覆盖"Proleptic年"部件
     * ChronoField.DAY_OF_WEEK                  - 与一周中的[天](1, 7)整合，这会在当前时间量的基础上增/减一定的天数，以达到给定的字段值表示的"周几"
     * ChronoField.DAY_OF_YEAR                  - 与一年中的[天](1, 365/366)整合，这会覆盖当前时间量的"月份"组件和"天"组件
     * ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH - 与位于月周对齐中的[天](1, 7)整合，这会在当前时间量的基础上增/减一定的天数，以达到给定的字段值表示的"周几"
     * ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR  - 与位于年周对齐中的[天](1, 7)整合，这会在当前时间量的基础上增/减一定的天数，以达到给定的字段值表示的"周几"
     * ChronoField.ALIGNED_WEEK_OF_MONTH        - 与位于月周对齐中的[第几周](1, 4/5)整合，这会在当前时间量的基础上增/减一定的周数，以达到给定的字段值表示的"第几周"
     * ChronoField.ALIGNED_WEEK_OF_YEAR         - 与位于年周对齐中的[第几周](1, 53)整合，这会在当前时间量的基础上增/减一定的周数，以达到给定的字段值表示的"第几周"
     * ChronoField.PROLEPTIC_MONTH              - 与[Proleptic-月]整合，这会在当前时间量的基础上增/减一定的月数，以达到给定的字段值表示的目标月
     * ChronoField.EPOCH_DAY                    - 与纪元中的[天](-365243219162L, 365241780471L)整合，这会完全地构造一个新的"本地日期"
     * =============================================================================================================
     * ChronoField.HOUR_OF_DAY        - 与24小时制中的[小时](0, 23)整合，只会覆盖当前时间量的"小时"组件
     * ChronoField.MINUTE_OF_HOUR     - 与一小时内的[分钟](0, 59)整合，只会覆盖当前时间量的"分钟"组件
     * ChronoField.SECOND_OF_MINUTE   - 与一分内的[秒](0, 59)整合，只会覆盖当前时间量的"秒"组件
     * ChronoField.NANO_OF_SECOND     - 与一秒内的[纳秒](0, 999_999_999)整合，只会覆盖当前时间量的"纳秒"组件
     * .............................................................................................................
     * ChronoField.MICRO_OF_SECOND    - 与一秒内的[微秒](0, 999_999)整合，会将其转换为纳秒，然后去覆盖当前时间量的"纳秒"组件
     * ChronoField.MILLI_OF_SECOND    - 与一秒内的[毫秒](0, 999)整合，会将其转换为纳秒，然后去覆盖当前时间量的"纳秒"组件
     * ChronoField.NANO_OF_DAY        - 与一天内的[纳秒](0, 86400L * 1000_000_000L - 1)整合，这会完全地构造一个新的"本地时间"
     * ChronoField.MICRO_OF_DAY       - 与一天内的[微秒](0, 86400L * 1000_000L - 1)整合，这会完全地构造一个新的"本地时间"
     * ChronoField.MILLI_OF_DAY       - 与一天内的[毫秒](0, 86400L * 1000L - 1)整合，这会完全地构造一个新的"本地时间"
     * ChronoField.SECOND_OF_DAY      - 与一天内的[秒](0, 86400L - 1)整合，这会在当前时间量的基础上增/减一定的秒数，以达到给定的字段值表示的时间
     * ChronoField.MINUTE_OF_DAY      - 与一天内的[分钟](0, (24 * 60) - 1)整合，这会在当前时间量的基础上增/减一定的分钟数，以达到给定的字段值表示的时间
     * ChronoField.HOUR_OF_AMPM       - 与12小时制中的[小时](0, 11)整合，这会在当前时间量的基础上增/减一定的小时数，以达到给定的字段值表示的时间
     * ChronoField.CLOCK_HOUR_OF_AMPM - 与12小时制中的[钟点](1, 12)整合，这会在当前时间量的基础上增/减一定的小时数，以达到给定的字段值表示的时间
     * ChronoField.CLOCK_HOUR_OF_DAY  - 与24小时制中的[钟点](1, 24)整合，会将其转换为24小时制的[小时]，然后覆盖当前时间量的"小时"组件
     * ChronoField.AMPM_OF_DAY        - 与一天中的[上午/下午](0, 1)整合，即在当前时间量的基础上增/减12个小时，进行上下午的切换
     */
    @Override
    public LocalDateTime with(TemporalField field, long newValue) {
        if(field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            
            // 更新"本地时间"部件
            if(f.isTimeBased()) {
                LocalTime newTime = time.with(field, newValue);
                return with(date, newTime);
                
                // 更新"本地日期"部件
            } else {
                LocalDate newDate = date.with(field, newValue);
                return with(newDate, time);
            }
        }
        
        return field.adjustInto(this, newValue);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the year altered.
     * <p>
     * The time does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year the year to set in the result, from MIN_YEAR to MAX_YEAR
     *
     * @return a {@code LocalDateTime} based on this date-time with the requested year, not null
     *
     * @throws DateTimeException if the year value is invalid
     */
    /*
     * 将指定的"年"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：年份
     */
    public LocalDateTime withYear(int year) {
        LocalDate newDate = date.withYear(year);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the month-of-year altered.
     * <p>
     * The time does not affect the calculation and will be the same in the result.
     * If the day-of-month is invalid for the year, it will be changed to the last valid day of the month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param month the month-of-year to set in the result, from 1 (January) to 12 (December)
     *
     * @return a {@code LocalDateTime} based on this date-time with the requested month, not null
     *
     * @throws DateTimeException if the month-of-year value is invalid
     */
    /*
     * 将指定的"月"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：月份
     */
    public LocalDateTime withMonth(int month) {
        LocalDate newDate = date.withMonth(month);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the day-of-month altered.
     * <p>
     * If the resulting date-time is invalid, an exception is thrown.
     * The time does not affect the calculation and will be the same in the result.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth the day-of-month to set in the result, from 1 to 28-31
     *
     * @return a {@code LocalDateTime} based on this date-time with the requested day, not null
     *
     * @throws DateTimeException if the day-of-month value is invalid,
     *                           or if the day-of-month is invalid for the month-year
     */
    /*
     * 将"一月中的天"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：天
     */
    public LocalDateTime withDayOfMonth(int dayOfMonth) {
        LocalDate newDate = date.withDayOfMonth(dayOfMonth);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the day-of-year altered.
     * <p>
     * If the resulting date-time is invalid, an exception is thrown.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear the day-of-year to set in the result, from 1 to 365-366
     *
     * @return a {@code LocalDateTime} based on this date with the requested day, not null
     *
     * @throws DateTimeException if the day-of-year value is invalid,
     *                           or if the day-of-year is invalid for the year
     */
    /*
     * 将"一年中的天"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：月份、天
     */
    public LocalDateTime withDayOfYear(int dayOfYear) {
        LocalDate newDate = date.withDayOfYear(dayOfYear);
        return with(newDate, time);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the hour-of-day altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hour the hour-of-day to set in the result, from 0 to 23
     *
     * @return a {@code LocalDateTime} based on this date-time with the requested hour, not null
     *
     * @throws DateTimeException if the hour value is invalid
     */
    /*
     * 将指定的"小时"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：小时
     */
    public LocalDateTime withHour(int hour) {
        LocalTime newTime = time.withHour(hour);
        return with(date, newTime);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the minute-of-hour altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minute the minute-of-hour to set in the result, from 0 to 59
     *
     * @return a {@code LocalDateTime} based on this date-time with the requested minute, not null
     *
     * @throws DateTimeException if the minute value is invalid
     */
    /*
     * 将指定的"分钟"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：分钟
     */
    public LocalDateTime withMinute(int minute) {
        LocalTime newTime = time.withMinute(minute);
        return with(date, newTime);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the second-of-minute altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param second the second-of-minute to set in the result, from 0 to 59
     *
     * @return a {@code LocalDateTime} based on this date-time with the requested second, not null
     *
     * @throws DateTimeException if the second value is invalid
     */
    /*
     * 将指定的"秒"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：秒
     */
    public LocalDateTime withSecond(int second) {
        LocalTime newTime = time.withSecond(second);
        return with(date, newTime);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the nano-of-second altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond the nano-of-second to set in the result, from 0 to 999,999,999
     *
     * @return a {@code LocalDateTime} based on this date-time with the requested nanosecond, not null
     *
     * @throws DateTimeException if the nano value is invalid
     */
    /*
     * 将指定的"纳秒"整合到当前时间量中以构造时间量对象。
     *
     * 如果整合后的值与当前时间量中的值相等，则直接返回当前时间量对象。
     * 否则，需要构造"整合"后的新对象再返回。
     *
     * 注：整合过程，通常是时间量部件的替换/覆盖过程。
     * 　　至于是替换/覆盖一个部件还是多个部件，则需要根据参数的意义而定。
     *
     * 影响部件：纳秒
     */
    public LocalDateTime withNano(int nanoOfSecond) {
        LocalTime newTime = time.withNano(nanoOfSecond);
        return with(date, newTime);
    }
    
    /**
     * Adjusts the specified temporal object to have the same date and time as this object.
     * <p>
     * This returns a temporal object of the same observable type as the input
     * with the date and time changed to be the same as this.
     * <p>
     * The adjustment is equivalent to using {@link Temporal#with(TemporalField, long)}
     * twice, passing {@link ChronoField#EPOCH_DAY} and
     * {@link ChronoField#NANO_OF_DAY} as the fields.
     * <p>
     * In most cases, it is clearer to reverse the calling pattern by using
     * {@link Temporal#with(TemporalAdjuster)}:
     * <pre>
     *   // these two lines are equivalent, but the second approach is recommended
     *   temporal = thisLocalDateTime.adjustInto(temporal);
     *   temporal = temporal.with(thisLocalDateTime);
     * </pre>
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param temporal the target object to be adjusted, not null
     *
     * @return the adjusted object, not null
     *
     * @throws DateTimeException   if unable to make the adjustment
     * @throws ArithmeticException if numeric overflow occurs
     */
    /*
     * 拿当前时间量中的特定字段与时间量temporal中的其他字段进行整合。
     *
     * 如果整合后的值与temporal中原有的值相等，则可以直接使用temporal本身；否则，会返回新构造的时间量对象。
     *
     * 注：通常，这会用到当前时间量的所有部件信息
     *
     *
     * 当前时间量参与整合字段包括：
     * ChronoField.EPOCH_DAY   - 当前时间量的日期部件中包含的纪元天
     * ChronoField.NANO_OF_DAY - 当前时间量的时间部件中包含的纳秒数
     *
     * 目标时间量temporal的取值可以是：
     * LocalDateTime
     * OffsetDateTime
     * ZonedDateTime
     * ChronoLocalDateTimeImpl
     * ChronoZonedDateTimeImpl
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        return ChronoLocalDateTime.super.adjustInto(temporal);
    }
    
    /*▲ 整合 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 杂项 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Calculates the amount of time until another date-time in terms of the specified unit.
     * <p>
     * This calculates the amount of time between two {@code LocalDateTime}
     * objects in terms of a single {@code TemporalUnit}.
     * The start and end points are {@code this} and the specified date-time.
     * The result will be negative if the end is before the start.
     * The {@code Temporal} passed to this method is converted to a
     * {@code LocalDateTime} using {@link #from(TemporalAccessor)}.
     * For example, the amount in days between two date-times can be calculated
     * using {@code startDateTime.until(endDateTime, DAYS)}.
     * <p>
     * The calculation returns a whole number, representing the number of
     * complete units between the two date-times.
     * For example, the amount in months between 2012-06-15T00:00 and 2012-08-14T23:59
     * will only be one month as it is one minute short of two months.
     * <p>
     * There are two equivalent ways of using this method.
     * The first is to invoke this method.
     * The second is to use {@link TemporalUnit#between(Temporal, Temporal)}:
     * <pre>
     *   // these two lines are equivalent
     *   amount = start.until(end, MONTHS);
     *   amount = MONTHS.between(start, end);
     * </pre>
     * The choice should be made based on which makes the code more readable.
     * <p>
     * The calculation is implemented in this method for {@link ChronoUnit}.
     * The units {@code NANOS}, {@code MICROS}, {@code MILLIS}, {@code SECONDS},
     * {@code MINUTES}, {@code HOURS} and {@code HALF_DAYS}, {@code DAYS},
     * {@code WEEKS}, {@code MONTHS}, {@code YEARS}, {@code DECADES},
     * {@code CENTURIES}, {@code MILLENNIA} and {@code ERAS} are supported.
     * Other {@code ChronoUnit} values will throw an exception.
     * <p>
     * If the unit is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
     * passing {@code this} as the first argument and the converted input temporal
     * as the second argument.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param endExclusive the end date, exclusive, which is converted to a {@code LocalDateTime}, not null
     * @param unit         the unit to measure the amount in, not null
     *
     * @return the amount of time between this date-time and the end date-time
     *
     * @throws DateTimeException                if the amount cannot be calculated, or the end
     *                                          temporal cannot be converted to a {@code LocalDateTime}
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException              if numeric overflow occurs
     */
    // 计算当前时间量到目标时间量endExclusive之间相差多少个unit单位的时间值
    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        LocalDateTime end = LocalDateTime.from(endExclusive);
    
        if(unit instanceof ChronoUnit) {
            if(unit.isTimeBased()) {
                long amount = date.daysUntil(end.date);
                if(amount == 0) {
                    return time.until(end.time, unit);
                }
                long timePart = end.time.toNanoOfDay() - time.toNanoOfDay();
                if(amount>0) {
                    amount--;  // safe
                    timePart += NANOS_PER_DAY;  // safe
                } else {
                    amount++;  // safe
                    timePart -= NANOS_PER_DAY;  // safe
                }
            
                switch((ChronoUnit) unit) {
                    case NANOS:
                        amount = Math.multiplyExact(amount, NANOS_PER_DAY);
                        break;
                    case MICROS:
                        amount = Math.multiplyExact(amount, MICROS_PER_DAY);
                        timePart = timePart / 1000;
                        break;
                    case MILLIS:
                        amount = Math.multiplyExact(amount, MILLIS_PER_DAY);
                        timePart = timePart / 1_000_000;
                        break;
                    case SECONDS:
                        amount = Math.multiplyExact(amount, SECONDS_PER_DAY);
                        timePart = timePart / NANOS_PER_SECOND;
                        break;
                    case MINUTES:
                        amount = Math.multiplyExact(amount, MINUTES_PER_DAY);
                        timePart = timePart / NANOS_PER_MINUTE;
                        break;
                    case HOURS:
                        amount = Math.multiplyExact(amount, HOURS_PER_DAY);
                        timePart = timePart / NANOS_PER_HOUR;
                        break;
                    case HALF_DAYS:
                        amount = Math.multiplyExact(amount, 2);
                        timePart = timePart / (NANOS_PER_HOUR * 12);
                        break;
                }
            
                return Math.addExact(amount, timePart);
            } else {
                LocalDate endDate = end.date;
                if(endDate.isAfter(date) && end.time.isBefore(time)) {
                    endDate = endDate.minusDays(1);
                } else if(endDate.isBefore(date) && end.time.isAfter(time)) {
                    endDate = endDate.plusDays(1);
                }
            
                return date.until(endDate, unit);
            }
        }
    
        return unit.between(this, end);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the time truncated.
     * <p>
     * Truncation returns a copy of the original date-time with fields
     * smaller than the specified unit set to zero.
     * For example, truncating with the {@link ChronoUnit#MINUTES minutes} unit
     * will set the second-of-minute and nano-of-second field to zero.
     * <p>
     * The unit must have a {@linkplain TemporalUnit#getDuration() duration}
     * that divides into the length of a standard day without remainder.
     * This includes all supplied time units on {@link ChronoUnit} and
     * {@link ChronoUnit#DAYS DAYS}. Other units throw an exception.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param unit the unit to truncate to, not null
     *
     * @return a {@code LocalDateTime} based on this date-time with the time truncated, not null
     *
     * @throws DateTimeException                if unable to truncate
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     */
    /*
     * 截断(对齐)
     *
     * 将当前时间量按照unit单位进行截断(对齐)，返回截断(对齐)后的新对象。
     *
     * 注：要求unit为"时间"单位
     */
    public LocalDateTime truncatedTo(TemporalUnit unit) {
        return with(date, time.truncatedTo(unit));
    }
    
    /**
     * Checks if this date-time is after the specified date-time.
     * <p>
     * This checks to see if this date-time represents a point on the
     * local time-line after the other date-time.
     * <pre>
     *   LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
     *   LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
     *   a.isAfter(b) == false
     *   a.isAfter(a) == false
     *   b.isAfter(a) == true
     * </pre>
     * <p>
     * This method only considers the position of the two date-times on the local time-line.
     * It does not take into account the chronology, or calendar system.
     * This is different from the comparison in {@link #compareTo(ChronoLocalDateTime)},
     * but is the same approach as {@link ChronoLocalDateTime#timeLineOrder()}.
     *
     * @param other the other date-time to compare to, not null
     *
     * @return true if this date-time is after the specified date-time
     */
    // 判断当前日期-时间是否晚于参数中指定的日期-时间
    @Override
    public boolean isAfter(ChronoLocalDateTime<?> other) {
        if(other instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) other)>0;
        }
        
        return ChronoLocalDateTime.super.isAfter(other);
    }
    
    /**
     * Checks if this date-time is before the specified date-time.
     * <p>
     * This checks to see if this date-time represents a point on the
     * local time-line before the other date-time.
     * <pre>
     *   LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
     *   LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
     *   a.isBefore(b) == true
     *   a.isBefore(a) == false
     *   b.isBefore(a) == false
     * </pre>
     * <p>
     * This method only considers the position of the two date-times on the local time-line.
     * It does not take into account the chronology, or calendar system.
     * This is different from the comparison in {@link #compareTo(ChronoLocalDateTime)},
     * but is the same approach as {@link ChronoLocalDateTime#timeLineOrder()}.
     *
     * @param other the other date-time to compare to, not null
     *
     * @return true if this date-time is before the specified date-time
     */
    // 判断当前日期-时间是否早于参数中指定的日期-时间
    @Override
    public boolean isBefore(ChronoLocalDateTime<?> other) {
        if(other instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) other)<0;
        }
        return ChronoLocalDateTime.super.isBefore(other);
    }
    
    /**
     * Checks if this date-time is equal to the specified date-time.
     * <p>
     * This checks to see if this date-time represents the same point on the
     * local time-line as the other date-time.
     * <pre>
     *   LocalDate a = LocalDateTime.of(2012, 6, 30, 12, 00);
     *   LocalDate b = LocalDateTime.of(2012, 7, 1, 12, 00);
     *   a.isEqual(b) == false
     *   a.isEqual(a) == true
     *   b.isEqual(a) == false
     * </pre>
     * <p>
     * This method only considers the position of the two date-times on the local time-line.
     * It does not take into account the chronology, or calendar system.
     * This is different from the comparison in {@link #compareTo(ChronoLocalDateTime)},
     * but is the same approach as {@link ChronoLocalDateTime#timeLineOrder()}.
     *
     * @param other the other date-time to compare to, not null
     *
     * @return true if this date-time is equal to the specified date-time
     */
    // 判断当前日期-时间与参数中指定的日期-时间是否相等
    @Override
    public boolean isEqual(ChronoLocalDateTime<?> other) {
        if(other instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) other) == 0;
        }
        
        return ChronoLocalDateTime.super.isEqual(other);
    }
    
    /**
     * Formats this date-time using the specified formatter.
     * <p>
     * This date-time will be passed to the formatter to produce a string.
     *
     * @param formatter the formatter to use, not null
     *
     * @return the formatted date-time string, not null
     *
     * @throws DateTimeException if an error occurs during printing
     */
    // 将当前日期-时间转换为一个指定格式的字符串后返回
    @Override
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }
    
    /*▲ 杂项 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    /**
     * Returns a copy of this date-time with the new date and time, checking
     * to see if a new object is in fact required.
     *
     * @param newDate the date of the new date-time, not null
     * @param newTime the time of the new date-time, not null
     *
     * @return the date-time, not null
     */
    /*
     * 使用指定的"本地日期"部件和"本地时间"部件构造"本地日期-时间"对象
     *
     * 如果给定部件的值与当前时间量的值相等，则直接返回当前时间量对象。
     * 否则，需要构造新的对象后再返回。
     */
    private LocalDateTime with(LocalDate newDate, LocalTime newTime) {
        if(date == newDate && time == newTime) {
            return this;
        }
        
        return new LocalDateTime(newDate, newTime);
    }
    
    /**
     * Returns a copy of this {@code LocalDateTime} with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param newDate the new date to base the calculation on, not null
     * @param hours   the hours to add, may be negative
     * @param minutes the minutes to add, may be negative
     * @param seconds the seconds to add, may be negative
     * @param nanos   the nanos to add, may be negative
     * @param sign    the sign to determine add or subtract
     *
     * @return the combined result, not null
     */
    /*
     * 将newDate和当前时间量的"本地时间"部件捆绑为一个时间点，
     * 然后在该时间点上累加或减去hours小时minutes分钟seconds秒nanos纳秒，以便构造新的时间点。
     *
     * 这里的hours/minutes/seconds/nanos理论上可以使用任意数值，sign用来标记是累加(1)还是减去(-1)
     */
    private LocalDateTime plusWithOverflow(LocalDate newDate, long hours, long minutes, long seconds, long nanos, int sign) {
        // 9223372036854775808 long, 2147483648 int
        if((hours | minutes | seconds | nanos) == 0) {
            return with(newDate, time);
        }
        
        // 获取"时间"单位中包含的天数
        long totDays = nanos / NANOS_PER_DAY +      // max/24*60*60*1000_000_000
            seconds / SECONDS_PER_DAY +  // max/24*60*60
            minutes / MINUTES_PER_DAY +  // max/24*60
            hours / HOURS_PER_DAY;       // max/24
        
        totDays *= sign;    // total max*0.4237...
        
        // "时间"单位中剩余的纳秒
        long totNanos = nanos % NANOS_PER_DAY +                            // max 86400000000000
            (seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   // max 86400000000000
            (minutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   // max 86400000000000
            (hours % HOURS_PER_DAY) * NANOS_PER_HOUR;          // max 86400000000000
        
        // 计算"本地时间"time包含的纳秒数
        long curNoD = time.toNanoOfDay();                      // max 86400000000000
        
        // 计算所有纳秒的和
        totNanos = totNanos * sign + curNoD;                   // total 432000000000000
        
        // 计算所有的天数
        totDays += Math.floorDiv(totNanos, NANOS_PER_DAY);
        
        // 计算残留的纳秒数
        long newNoD = Math.floorMod(totNanos, NANOS_PER_DAY);
        
        // 构造新的"本地时间"
        LocalTime newTime = (newNoD == curNoD ? time // 重用time部件
            : LocalTime.ofNanoOfDay(newNoD));   // 使用指定的纳秒数(不超过一天)构造"本地时间"
        
        // 在newDate的值上累加daysToAdd天，构造新的"本地日期"
        LocalDate plusDays = newDate.plusDays(totDays);
        
        return with(plusDays, newTime);
    }
    
    private int compareTo0(LocalDateTime other) {
        int cmp = date.compareTo0(other.toLocalDate());
        if(cmp == 0) {
            cmp = time.compareTo(other.toLocalTime());
        }
        return cmp;
    }
    
    
    /**
     * Compares this date-time to another date-time.
     * <p>
     * The comparison is primarily based on the date-time, from earliest to latest.
     * It is "consistent with equals", as defined by {@link Comparable}.
     * <p>
     * If all the date-times being compared are instances of {@code LocalDateTime},
     * then the comparison will be entirely based on the date-time.
     * If some dates being compared are in different chronologies, then the
     * chronology is also considered, see {@link ChronoLocalDateTime#compareTo}.
     *
     * @param other the other date-time to compare to, not null
     *
     * @return the comparator value, negative if less, positive if greater
     */
    @Override
    public int compareTo(ChronoLocalDateTime<?> other) {
        if(other instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) other);
        }
        return ChronoLocalDateTime.super.compareTo(other);
    }
    
    /**
     * Outputs this date-time as a {@code String}, such as {@code 2007-12-03T10:15:30}.
     * <p>
     * The output will be one of the following ISO-8601 formats:
     * <ul>
     * <li>{@code uuuu-MM-dd'T'HH:mm}</li>
     * <li>{@code uuuu-MM-dd'T'HH:mm:ss}</li>
     * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSS}</li>
     * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSSSSS}</li>
     * <li>{@code uuuu-MM-dd'T'HH:mm:ss.SSSSSSSSS}</li>
     * </ul>
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return a string representation of this date-time, not null
     */
    @Override
    public String toString() {
        return date.toString() + 'T' + time.toString();
    }
    
    /**
     * Checks if this date-time is equal to another date-time.
     * <p>
     * Compares this {@code LocalDateTime} with another ensuring that the date-time is the same.
     * Only objects of type {@code LocalDateTime} are compared, other types return false.
     *
     * @param obj the object to check, null returns false
     *
     * @return true if this is equal to the other date-time
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj instanceof LocalDateTime) {
            LocalDateTime other = (LocalDateTime) obj;
            return date.equals(other.date) && time.equals(other.time);
        }
        return false;
    }
    
    /**
     * A hash code for this date-time.
     *
     * @return a suitable hash code
     */
    @Override
    public int hashCode() {
        return date.hashCode() ^ time.hashCode();
    }
    
    
    
    /*▼ 序列化 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 6207766400415563566L;
    
    /**
     * Writes the object using a
     * <a href="../../serialized-form.html#java.time.Ser">dedicated serialized form</a>.
     *
     * @return the instance of {@code Ser}, not null
     *
     * @serialData <pre>
     *  out.writeByte(5);  // identifies a LocalDateTime
     *  // the <a href="../../serialized-form.html#java.time.LocalDate">date</a> excluding the one byte header
     *  // the <a href="../../serialized-form.html#java.time.LocalTime">time</a> excluding the one byte header
     * </pre>
     */
    private Object writeReplace() {
        return new Ser(Ser.LOCAL_DATE_TIME_TYPE, this);
    }
    
    /**
     * Defend against malicious streams.
     *
     * @param s the stream to read
     *
     * @throws InvalidObjectException always
     */
    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
    
    static LocalDateTime readExternal(DataInput in) throws IOException {
        LocalDate date = LocalDate.readExternal(in);
        LocalTime time = LocalTime.readExternal(in);
        return LocalDateTime.of(date, time);
    }
    
    void writeExternal(DataOutput out) throws IOException {
        date.writeExternal(out);
        time.writeExternal(out);
    }
    
    /*▲ 序列化 ████████████████████████████████████████████████████████████████████████████████┛ */
    
}