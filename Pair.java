package com.dilax.mobile.model.network.util;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Pair<A, B> implements Comparable<Pair<A, B>> {

    private final A first;

    private final B second;

    protected Pair(final A first, final B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(final A first, final B second) {
        return new Pair<A, B>(first, second);
    }

    @Override
    public int compareTo(final Pair<A, B> other) {
        return new CompareToBuilder().append(first, other.first).append(second, other.second).toComparison();
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(first).append(second).toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof Pair) {
            @SuppressWarnings("rawtypes")
            Pair pair = (Pair) object;
            return new EqualsBuilder().append(first, pair.first).append(second, pair.second).isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(first).append(second).toHashCode();
    }

}
