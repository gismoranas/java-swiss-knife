package com.dilax.mobile.model.network.util;

public class TwinPair<C> extends Pair<C, C> {

    protected TwinPair(final C first, final C second) {
        super(first, second);
    }

    public static <C> TwinPair<C> from(final C first, final C second) {
        return new TwinPair<C>(first, second);
    }

    public static <C> TwinPair<C> from(final Pair<C, C> pair) {
        return new TwinPair<C>(pair.getFirst(), pair.getSecond());
    }

}
