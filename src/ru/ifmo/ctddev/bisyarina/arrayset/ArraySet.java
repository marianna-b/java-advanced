package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.*;

/**
 * Created by mariashka on 2/22/15.
 */
public class ArraySet<E> extends ImmutableAbstractSet<E> {
    private E[] data;

    ArraySet(E[] data, Comparator<E> comparator) {
        this.comparator = comparator;
        this.data = data.clone();
    }

    public static <E extends Comparable<E>> ImmutableAbstractSet<E> fromArray(E[] data) {
        Comparator<E> order = Comparator.<E>naturalOrder();
        return new ArraySet<>(data, order);
    }

    @Override
    E binarySearch(E element, boolean forward, boolean strict) {
        return null;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(data);
    }

    @Override
    public Object[] toArray() {
        return data.clone();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return null;
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return null;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public NavigableSet<E> subSet(E e, boolean b, E e1, boolean b1) {
        return null;
    }

    @Override
    public NavigableSet<E> headSet(E e, boolean b) {
        return null;
    }

    @Override
    public NavigableSet<E> tailSet(E e, boolean b) {
        return null;
    }

    @Override
    public SortedSet<E> subSet(E e, E e1) {
        return null;
    }

    @Override
    public SortedSet<E> headSet(E e) {
        return null;
    }

    @Override
    public SortedSet<E> tailSet(E e) {
        return null;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    public class ArrayIterator<E> implements Iterator<E> {
        private E[] data;
        private int idx;

        ArrayIterator(E[] data) {
            this.data = data;
            idx = 0;
        }

        @Override
        public boolean hasNext() {
            return idx < data.length;
        }

        @Override
        public E next() {
            if (hasNext()) {
                idx++;
                return data[idx];
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
