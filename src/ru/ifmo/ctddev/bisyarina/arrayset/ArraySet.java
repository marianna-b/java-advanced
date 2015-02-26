package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.*;

/**
 * Created by mariashka on 2/22/15.
 */
@SuppressWarnings("NullableProblems")
public class ArraySet<E> extends AbstractImmutableArraySet<E> {
    private ReversibleArrayList<E> data;

    public ArraySet() {
        super(null);
        this.data = new ReversibleArrayList<>();
    }

    public ArraySet(Collection<E> collection) {
        super(null);
        this.data = new ReversibleArrayList<E>(collection, null);
    }

    public ArraySet(Collection<E> collection, Comparator<E> comparator) {
        super(comparator);
        this.data = new ReversibleArrayList<>(collection, comparator);
    }

    private ArraySet(ReversibleArrayList<E> list, Comparator<E> comparator1) {
        super(comparator1);
        this.data = list;
    }

    @Override
    protected E binarySearchElement(E element, boolean forward, boolean inclusive) {
        int idx = data.binarySearch(element, forward, inclusive);
        if (idx < 0 || idx >= size()) {
            return null;
        } else {
            return data.get(idx);
        }
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(data);
    }

    @Override
    public NavigableSet<E> descendingSet() {
        if (comparator == null) {
            return new ArraySet<E>(data.reverse(), (Comparator<E>) Comparator.naturalOrder().reversed());
        }
        return new ArraySet<>(data.reverse(), comparator.reversed());
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new ArrayIterator<>(data.reverse());
    }

    @Override
    public NavigableSet<E> subSet(E e, boolean b, E e1, boolean b1) {
        if (compare(e, e1) > 0) {
            throw new IllegalArgumentException("First argument is greater than second");
        }

        int l = Math.max(data.binarySearch(e, false, b), 0);
        int r = data.binarySearch(e1, true, b1);
        r = r < size() ? r + 1 : r;
        if (r <= l) {
            return new ArraySet<>();
        }
        return new ArraySet<>(data.subList(l, r), comparator);
    }

    @Override
    public NavigableSet<E> headSet(E e, boolean b) {
        int r = data.binarySearch(e, true, b);
        r = r < size() ? r + 1 : r;
        return new ArraySet<>(data.subList(0, r), comparator);
    }

    @Override
    public NavigableSet<E> tailSet(E e, boolean b) {
        int l = Math.max(data.binarySearch(e, false, b), 0);
        return new ArraySet<>(data.subList(l, size()), comparator);
    }

    @Override
    public E first() {
        if (size() == 0)
            throw new NoSuchElementException("Set is empty. No lowest element.");
        return data.get(0);
    }

    @Override
    public E last() {
        if (size() == 0)
            throw new NoSuchElementException("Set is empty. No greatest element.");
        return data.get(size() - 1);
    }

    private static final class ArrayIterator<E> implements Iterator<E> {
        private final ReversibleArrayList<E> data;
        private int idx;

        ArrayIterator(ReversibleArrayList<E> data) {
            this.data = data;
            idx = 0;
        }

        @Override
        public boolean hasNext() {
            return idx < data.size();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int oldIdx = idx;
            idx++;
            return data.get(oldIdx);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    private int compare(E e1, E e2) {
        if (comparator == null) {
            return ((Comparable<E>) e1).compareTo(e2);
        } else {
            return comparator.compare(e1, e2);
        }
    }
}
