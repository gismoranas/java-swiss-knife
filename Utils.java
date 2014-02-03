package com.dilax.mobile.model.network.util;

import java.util.Collection;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.dilax.mobile.enumeration.WeekDay;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

// TODO separate date time utils in another class 

public class Utils {

    private static String DATE_FORMAT = "ddMMYY";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT);

    /**
     * The first and the second period must not be ordered. If they have at least one day in common, true is returned. 
     * @return
     */
    public static boolean doTimePeriodsOverlap(final LocalDate firstStartDate, final LocalDate firstEndDate,
        final LocalDate secondStartDate, final LocalDate secondEndDate) {
        return doTimePeriodsOverlap(firstStartDate.toDateTimeAtStartOfDay(), firstEndDate.toDateTimeAtStartOfDay(),
            secondStartDate.toDateTimeAtStartOfDay(), secondEndDate.toDateTimeAtStartOfDay());
    }

    public static boolean doTimePeriodsOverlap(final DateTime firstStartDate, final DateTime firstEndDate,
        final DateTime secondStartDate, final DateTime secondEndDate) {
        Interval firstInterval = new Interval(firstStartDate.minusMillis(1), firstEndDate.plusMillis(1));
        Interval secondInterval = new Interval(secondStartDate.minusMillis(1), secondEndDate.plusMillis(1));
        return firstInterval.overlaps(secondInterval);
    }

    public static boolean isDayInTimePeriod(final DateTime day, final Pair<DateTime, DateTime> timePeriod) {
        return day.isAfter(timePeriod.getFirst().minusDays(1)) && day.isBefore(timePeriod.getSecond().plusDays(1));
    }

    public static boolean isDayInTimePeriod(final LocalDate localDate, final Pair<LocalDate, LocalDate> timePeriod) {
        return isDayInTimePeriod(localDate.toDateTimeAtStartOfDay(),
            Pair.of(timePeriod.getFirst().toDateTimeAtStartOfDay(), timePeriod.getSecond().toDateTimeAtStartOfDay()));
    }

    public static WeekDay getWeekDayFromJodaIndex(final int i) {
        switch (i) {
            case 1:
                return WeekDay.MONDAY;
            case 2:
                return WeekDay.TUESDAY;
            case 3:
                return WeekDay.WEDNESDAY;
            case 4:
                return WeekDay.THURSDAY;
            case 5:
                return WeekDay.FRIDAY;
            case 6:
                return WeekDay.SATURDAY;
            case 7:
                return WeekDay.SUNDAY;
            default:
                return null;
        }
    }

    /**
     * Generates a code like A-B_C-D if there are consecutive dates between A and B and between C and D,
     * but B and C are not consecutive. For single separated dates the code is like A_B_C.
     */
    public static String generateCodeFromDates(final Collection<LocalDate> dates) {
        if (dates.isEmpty()) {
            throw new IllegalArgumentException("No dates found.");
        }
        LocalDate previousDate = Iterables.get(dates, 0);
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(previousDate.toString(DATE_FORMATTER));
        for (LocalDate date : Iterables.skip(dates, 1)) {
            if (!date.minusDays(1).equals(previousDate)) {
                appendPreviousDate(codeBuilder, previousDate);
                codeBuilder.append("_").append(date.toString(DATE_FORMATTER));
            }
            previousDate = date;
        }

        appendPreviousDate(codeBuilder, previousDate);

        return codeBuilder.toString();
    }

    public static int getSecondsBetween(final LocalTime from, final LocalTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Arguments are be null.");
        }
        if (from.isAfter(to)) {
            return Seconds.secondsBetween(from, new LocalTime(23, 59)).getSeconds() + 60
                + Seconds.secondsBetween(new LocalTime(0, 0), to).getSeconds();
        }
        return Seconds.secondsBetween(from, to).getSeconds();
    }

    public static int getMinutesBetween(final LocalTime from, final LocalTime to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Arguments are be null.");
        }
        return getSecondsBetween(from, to) / 60;
    }

    public static String hashAndCrop(final String string, final int maxLength) {
        if (string.length() > maxLength) {
            return Joiner.on("_H").join(string.substring(0, maxLength - 10), string.hashCode());
        }
        return string;
    }

    private static void appendPreviousDate(final StringBuilder codeBuilder, final LocalDate previousDate) {
        if (!getSubString(codeBuilder.toString(), -1 * DATE_FORMAT.length()).equals(
            previousDate.toString(DATE_FORMATTER))) {
            codeBuilder.append("-").append(previousDate.toString(DATE_FORMATTER));
        }
    }

    public static <T> Iterable<T> getSubIterable(final Collection<T> collection, final int startIndex,
        final int endIndex) {

        return Iterables.skip(Iterables.limit(collection, endIndex <= 0 ? collection.size() + endIndex : endIndex),
            startIndex);
    }

    public static String getSubString(final String string, final int startIndex) {
        if (startIndex >= 0) {
            return string.substring(Math.min(string.length(), startIndex));
        } else {
            return string.substring(Math.max(0, string.length() + startIndex), string.length());
        }
    }

    public static <C> C getElementAfter(final C element, final Iterable<C> iterable) {
        Iterator<C> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(element)) {
                if (iterator.hasNext()) {
                    return iterator.next();
                } else {
                    return null;
                }
            }
        }
        throw new IllegalArgumentException("Iterable does not contain element.");
    }

    public static <C> C getElementBefore(final C element, final Iterable<C> iterable) {
        return getElementAfter(element, Lists.reverse(Lists.newArrayList(iterable)));
    }

}
