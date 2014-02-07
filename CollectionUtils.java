package com.dilax.mobile.model.network.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.helpers.MessageFormatter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class CollectionUtils {

    public static <C> C coalesce(final C... objects) {
        for (C object : objects) {
            if (object != null) {
                return object;
            }
        }
        throw new IllegalArgumentException("Array is empty or contains just null.");
    }

    public static <C> C coalesce(final Iterable<C> iterable) {
        for (C object : iterable) {
            if (object != null) {
                return object;
            }
        }
        throw new IllegalArgumentException("Array is empty or contains just null.");

    }

    /**
     * Returns false if both are null, otherwise iterates comparing element pairs.
     * @param first
     * @param second
     * @return
     */
    public static <C> boolean areEquallyOrdered(final Collection<C> first, final Collection<C> second) {
        if (first == null || second == null) {
            return false;
        }
        if (first.size() == second.size()) {
            Iterator<Pair<C, C>> iterator = getPairedIterator(first, second);
            while (iterator.hasNext()) {
                Pair<C, C> pair = iterator.next();
                if (!pair.getFirst().equals(pair.getSecond())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // TODO this can be called from other methods with option to remove just first or second element.
    public static <C, A> Iterator<Pair<C, A>> getPairedIterator(final Collection<C> first, final Collection<A> second) {
        if (isNullOrEmpty(first, second)) {
            throw new IllegalArgumentException("Collections must be not null or empty.");
        }
        if (first.size() != second.size()) {
            throw new IllegalArgumentException(MessageFormatter.format("Collections have different sizes: {}, {}",
                first.size(), second.size()).getMessage());
        }
        final Iterator<C> firstIterator = first.iterator();
        final Iterator<A> secondIterator = second.iterator();
        return new Iterator<Pair<C, A>>(){

            @Override
            public boolean hasNext() {
                return firstIterator.hasNext();
            }

            @Override
            public Pair<C, A> next() {
                return Pair.of(firstIterator.next(), secondIterator.next());
            }

            @Override
            public void remove() {
                firstIterator.remove();
                secondIterator.remove();
            }

        };
    }

    /**
     * Iterates on contiguous couples of elements. For example, on the list [a, b, c, d] the first iteration
     * would give the pair [a, b], the second [b, c] and the third and last iteration the pair [c, d].
     * @param collection
     * @return
     */
    public static <C> Iterator<TwinPair<C>> getWindowIterator(final Collection<C> collection) {
        return getWindowIterator(collection, false);
    }

    /**
     * Does the same as getWindowIterator(), but makes an iteration more where the second item of the TwinPair
     * is null. So the last iteration on the list [a, b, c] would give the element [c, null].
     * @param collection
     * @return
     */
    public static <C> Iterator<TwinPair<C>> getPaddedWindowIterator(final Collection<C> collection) {
        return getWindowIterator(collection, true);
    }

    // TODO implementation could be faster or take less resources
    private static <C> Iterator<TwinPair<C>> getWindowIterator(final Collection<C> collection, final boolean isPadded) {
        if (isNullOrEmpty(collection)) {
            throw new IllegalArgumentException("Collection cannot be null or empty.");
        }
        if (collection.size() < 2) {
            throw new IllegalArgumentException("Collection must have size at least two.");
        }
        List<C> firstList = isPadded ? Lists.newArrayList(collection) : Lists.newArrayList(Iterables.limit(collection,
            collection.size() - 1));
        List<C> secondList = Lists.newArrayList(Iterables.skip(collection, 1));
        if (isPadded) {
            secondList.add(null);
        }
        final Iterator<Pair<C, C>> iterator = getPairedIterator(firstList, secondList);
        return new Iterator<TwinPair<C>>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public TwinPair<C> next() {
                return TwinPair.from(iterator.next());
            }

            @Override
            public void remove() {
                throw new NotImplementedException();

            }

        };
    }

    public static boolean isNullOrEmpty(final Object object) {
        if (object != null) {
            if (object instanceof Collection) {
                Collection<?> collection = (Collection<?>) object;
                if (collection.isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isNullOrEmpty(final Object first, final Object second, Object... objects) {
        objects = ArrayUtils.addAll(objects, new Object[ ] { first, second });
        for (Object object : objects) {
            if (isNullOrEmpty(object)) {
                return true;
            }
        }
        return false;
    }

    // TODO this is not lambda stuff
    public static boolean isNullOrEmptyRecursive(final Object object) {
        if (object != null) {
            if (object instanceof Collection) {
                Collection<?> collection = (Collection<?>) object;
                if (!collection.isEmpty()) {
                    for (Object collectionObject : collection) {
                        if (isNullOrEmptyRecursive(collectionObject)) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static <C> Set<C> newTreeSet(final C... elements) {
        Set<C> result = new TreeSet<C>();
        for (C element : elements) {
            result.add(element);
        }
        return result;
    }

    // TODO this is not lambda stuff
    public static <C> Multimap<Integer, C> getFrequencyMultimap(final Collection<C> collection,
        final Ordering<Integer> ordering) {
        if (CollectionUtils.isNullOrEmpty(collection) || ordering == null) {
            throw new IllegalArgumentException(MessageFormatter.format("Null or empty arguments: {}, {} ", collection,
                ordering).getMessage());
        }
        @SuppressWarnings("unchecked")
        Multimap<Integer, C> result = TreeMultimap.create(ordering, (Comparator<C>) Ordering.natural());
        for (C element : collection) {
            result.put(Collections.frequency(collection, element), element);
        }
        return result;
    }

    // TODO this is not lambda stuff
    public static <C> Collection<C> getMostFrequentElements(final Collection<C> collection) {
        if (CollectionUtils.isNullOrEmpty(collection)) {
            throw new IllegalArgumentException(MessageFormatter.format("Null or empty arguments: {}, {} ", collection)
                .getMessage());
        }
        Ordering<Integer> reverseIntegerOrdering = Ordering.natural().reverse();
        Multimap<Integer, C> frequencyMap = getFrequencyMultimap(collection, reverseIntegerOrdering);
        return frequencyMap.get(Iterables.getFirst(frequencyMap.keySet(), null));
    }

}
