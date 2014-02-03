package com.dilax.mobile.model.network.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.LocalDate;
import org.joda.time.base.AbstractInstant;
import org.joda.time.base.AbstractPartial;

public class Matchers {

    /**
     * It can be used with LocalDate or LocalTime, for example.
     */
    public static Matcher<AbstractPartial> isAfter(final AbstractPartial partial) {
        return new BaseMatcher<AbstractPartial>(){

            @Override
            public void describeTo(final Description description) {
                description.appendText("after partial: ").appendValue(partial);
            }

            @Override
            public boolean matches(final Object object) {
                if (object instanceof AbstractPartial) {
                    return ((AbstractPartial) object).isAfter(partial);
                }
                return false;
            }
        };
    }

    /**
     * It can be used with DateTime, for example.
     */
    public static Matcher<AbstractInstant> isAfter(final AbstractInstant instant) {
        return new BaseMatcher<AbstractInstant>(){

            @Override
            public void describeTo(final Description description) {
                description.appendText("after instant: ").appendValue(instant);
            }

            @Override
            public boolean matches(final Object object) {
                if (object instanceof AbstractInstant) {
                    return ((AbstractInstant) object).isAfter(instant);
                }
                return false;
            }
        };
    }

    /**
     * It can be used with DateTime, for example.
     */
    public static Matcher<AbstractInstant> isBefore(final AbstractInstant instant) {
        return new BaseMatcher<AbstractInstant>(){

            @Override
            public void describeTo(final Description description) {
                description.appendText("before instant: ").appendValue(instant);
            }

            @Override
            public boolean matches(final Object object) {
                if (object instanceof AbstractInstant) {
                    return ((AbstractInstant) object).isBefore(instant);
                }
                return false;
            }
        };
    }

    /**
     * It can be used with LocalDate, for example.
     */
    public static Matcher<AbstractPartial> isAfterOrEquals(final LocalDate date) {
        return isAfter(date.minusDays(1));
    }

    /**
     * It can be used with LocalDate or LocalTime, for example.
     */
    public static Matcher<AbstractPartial> isBefore(final AbstractPartial partial) {
        return new BaseMatcher<AbstractPartial>(){

            @Override
            public void describeTo(final Description description) {
                description.appendText("before partial: ").appendValue(partial);

            }

            @Override
            public boolean matches(final Object object) {
                if (object instanceof AbstractPartial) {
                    return ((AbstractPartial) object).isBefore(partial);
                }
                return false;
            }
        };
    }

    /**
     * It can be used with LocalDate, for example.
     */
    public static Matcher<AbstractPartial> isBeforeOrEquals(final LocalDate date) {
        return isBefore(date.plusDays(1));
    }
}
