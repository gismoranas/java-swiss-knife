package com.dilax.mobile.model.network.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matchers;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

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
import com.google.common.collect.Sets;

//TODO move to utils non lambda stuff 
//TODO move to commons when out of beta
//TODO is needed to handle special cases? avoid null pointers?

/**
 * @author manigrasso 
 */
public class LambdaUtils {

    public static <C, A> A coalesce(final Iterable<C> iterable, final A field) {
        List<A> list = Lambda.extract(iterable, field);
        return CollectionUtils.coalesce(list);
    }

    public static <C, A> Set<A> extractDistinct(final Collection<C> collection, final A field) {
        return Sets.newLinkedHashSet(Lambda.extract(collection, field));
    }

    public static <C, A> Collection<TwinPair<A>> extractTwinPairs(final Collection<C> collection, final A firstField,
        final A secondField) {
        Collection<TwinPair<A>> pairs = Lists.newArrayList();
        ArgumentConverter<C, A> firstArgumentConverter = new ArgumentConverter<C, A>(firstField);
        ArgumentConverter<C, A> secondArgumentConverter = new ArgumentConverter<C, A>(secondField);
        for (C element : collection) {
            pairs.add(TwinPair.from(firstArgumentConverter.convert(element), secondArgumentConverter.convert(element)));
        }
        return pairs;
    }

    public static <C, A, T> Collection<Pair<A, T>> extractPairs(final Collection<C> collection, final A firstField,
        final T secondField) {
        Collection<Pair<A, T>> pairs = Lists.newArrayList();
        ArgumentConverter<C, A> firstArgumentConverter = new ArgumentConverter<C, A>(firstField);
        ArgumentConverter<C, T> secondArgumentConverter = new ArgumentConverter<C, T>(secondField);
        for (C element : collection) {
            pairs.add(Pair.of(firstArgumentConverter.convert(element), secondArgumentConverter.convert(element)));
        }
        return pairs;
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
    public static int getSecondsOfDay(final LocalDateTime localDateTime) {
        return Seconds.secondsBetween(localDateTime.toLocalDate(), localDateTime).getSeconds();
    }

    public static <C, A, T> Map<C, A> indexAndConvert(final Collection<T> collection, final C keyField,
        final A valueField) {
        Map<C, T> map = Lambda.index(collection, keyField);
        return Lambda.convertMap(map, valueField);
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

    public static <C> Collection<C> selectDistinct(final Collection<C> collection, final BaseMatcher<Object> having) {
        return Sets.newHashSet(Lambda.select(collection, having));
    }

    public static <C, A> Collection<C> selectAndExtract(final Collection<A> collection,
        final BaseMatcher<Object> having, final C keyField) {
        // TODO to be implemented
        return null;
    }

}
