package com.dilax.mobile.model.network.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matchers;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.slf4j.helpers.MessageFormatter;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.ArgumentConverter;

import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

//TODO move to utils non lambda stuff 
//TODO move to commons when out of beta
//TODO is needed to handle special cases? avoid null pointers?

/**
 * @author manigrasso 
 */
public class LambdaUtils {

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

    // TODO this is not lambda stuff
    public static <C> C coalesce(final C... objects) {
        for (C object : objects) {
            if (object != null) {
                return object;
            }
        }
        throw new IllegalArgumentException("Array is empty or contains just null.");
    }

    // TODO this is not lambda stuff
    public static <C> C coalesce(final Iterable<C> iterable) {
        for (C object : iterable) {
            if (object != null) {
                return object;
            }
        }
        throw new IllegalArgumentException("Array is empty or contains just null.");

    }

    public static <C, A> A coalesce(final Iterable<C> iterable, final A field) {
        List<A> list = Lambda.extract(iterable, field);
        return coalesce(list);
    }

    public static String crop(final String string, final int newLength) {
        return string.substring(0, Math.min(newLength, string.length()));

    }

    public static <C, A> Set<A> extractDistinct(final Collection<C> collection, final A field) {
        return Sets.newLinkedHashSet(Lambda.extract(collection, field));
    }

    /**
     * Extracts a constant field from a collection.
     * @param collection
     * @param field
     * @return
     * @throws NoSuchElementException if the collection is empty
     * @throws IllegalArgumentException if the result is not unique
     */
    public static <C, A> A extractUnique(final Collection<C> collection, final A field) {
        return Iterables.getOnlyElement(extractDistinct(collection, field));
    }

    public static <C, A> Collection<A> getAllValues(final Map<C, A> map, final Collection<C> keys) {
        return Collections2.transform(Collections2.filter(keys, Predicates.in(map.keySet())), Functions.forMap(map));
    }

    public static <C, A> Collection<A> getAllValues(final Multimap<C, A> map, final Collection<C> keys) {
        return Lambda.flatten(getAllValues(map.asMap(), keys));
    }

    // TODO this is not lambda stuff
    public static <C> Multimap<Integer, C> getFrequencyMultimap(final Collection<C> collection,
        final Ordering<Integer> ordering) {
        if (isNullOrEmpty(collection) || ordering == null) {
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
        if (isNullOrEmpty(collection)) {
            throw new IllegalArgumentException(MessageFormatter.format("Null or empty arguments: {}, {} ", collection)
                .getMessage());
        }
        Ordering<Integer> reverseIntegerOrdering = Ordering.natural().reverse();
        Multimap<Integer, C> frequencyMap = getFrequencyMultimap(collection, reverseIntegerOrdering);
        return frequencyMap.get(Iterables.getFirst(frequencyMap.keySet(), null));
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

    // TODO this is not lambda stuff
    public static int getSecondsOfDay(final LocalDateTime localDateTime) {
        return Seconds.secondsBetween(localDateTime.toLocalDate(), localDateTime).getSeconds();
    }

    public static <C, A, T> Map<C, A> indexAndConvert(final Collection<T> collection, final C keyField,
        final A valueField) {
        Map<C, T> map = Lambda.index(collection, keyField);
        return Lambda.convertMap(map, valueField);
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

    public static <C, A> Multimap<C, A> multiIndex(final Collection<A> collection, final C keyField) {
        Multimap<C, A> multimap = ArrayListMultimap.create();
        for (A element : collection) {
            multimap.put(new ArgumentConverter<A, C>(keyField).convert(element), element);
        }
        return multimap;
    }

    public static <C, A, T> Multimap<C, A> multiIndexAndConvert(final Collection<T> collection, final C keyField,
        final A valueField) {
        Multimap<C, T> multimap = ArrayListMultimap.create();
        for (T element : collection) {
            multimap.put(new ArgumentConverter<T, C>(keyField).convert(element), element);
        }
        Multimap<C, A> result = ArrayListMultimap.create();
        for (C key : multimap.keySet()) {
            result.putAll(key, Lambda.extract(multimap.get(key), valueField));
        }
        return result;
    }

    public static <C, A, T> Multimap<C, A> multiIndexAndConvertOrdered(final Collection<T> collection,
        final C keyField, final A valueField) {
        Multimap<C, T> multimap = LinkedListMultimap.create();
        for (T element : collection) {
            multimap.put(new ArgumentConverter<T, C>(keyField).convert(element), element);
        }
        Multimap<C, A> result = LinkedListMultimap.create();
        for (C key : multimap.keySet()) {
            result.putAll(key, Lambda.extract(multimap.get(key), valueField));
        }
        return result;
    }

    // TODO is it necessary to make a copy?
    public static <C, A> Map<C, List<A>> multiIndexPreservingOrder(final List<A> list, final C keyField) {

        Map<C, List<A>> map = Maps.newLinkedHashMap();

        List<A> copyOfList = Lists.newArrayList(list);
        while (!copyOfList.isEmpty()) {
            A element = copyOfList.get(0);
            C elementKey = Lambda.argument(keyField).evaluate(element);
            List<A> similarElements = Lambda.select(copyOfList, Lambda.having(keyField, Matchers.equalTo(elementKey)));
            map.put(elementKey, similarElements);
            copyOfList.removeAll(similarElements);
        }
        return map;
    }

    // TODO this is not lambda stuff
    public static <C> Set<C> newTreeSet(final C... elements) {
        Set<C> result = new TreeSet<C>();
        for (C element : elements) {
            result.add(element);
        }
        return result;
    }

    public static <C> Collection<C> selectDistinct(final Collection<C> collection, final BaseMatcher<Object> having) {
        return Sets.newHashSet(Lambda.select(collection, having));
    }

    public static <C, A> Collection<C> selectAndExtract(final Collection<A> collection,
        final BaseMatcher<Object> having, final C keyField) {
        // TODO to be implemented
        return null;
    }

}
