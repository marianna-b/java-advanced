package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.SortedSet;

/**
 * Created by mariashka on 2/22/15.
 */
@SuppressWarnings("NullableProblems")
abstract class AbstractImmutableArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    protected Comparator<E> comparator;

    protected AbstractImmutableArraySet(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    protected abstract E binarySearchElement(E element, boolean forward, boolean inclusive);

    @Override
    public E lower(E e) {
        return binarySearchElement(e, true, false);
    }

    @Override
    public E floor(E e) {
        return binarySearchElement(e, true, true);
    }

    @Override
    public E ceiling(E e) {
        return binarySearchElement(e, false, true);
    }

    @Override
    public E higher(E e) {
        return binarySearchElement(e, false, false);
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("pollFirst");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("pollLast");
    }

    public boolean contains(Object o) {
        // Cannot check type Object vs generic E
        return binarySearchElement((E) o, true, true) != null;
    }

    @Override
    public SortedSet<E> subSet(E e, E e1) {
        return subSet(e, true, e1, false);
    }

    @Override
    public SortedSet<E> headSet(E e) {
        return headSet(e, false);
    }

    @Override
    public SortedSet<E> tailSet(E e) {
        return tailSet(e, true);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }
}
