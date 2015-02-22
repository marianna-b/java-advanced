package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

/**
 * Created by mariashka on 2/22/15.
 */
abstract class ImmutableAbstractSet<E> implements NavigableSet<E> {
    Comparator<E> comparator;

    abstract E binarySearch(E element, boolean forward, boolean strict);

    @Override
    public E lower(E e) {
        return binarySearch(e, true, true);
    }

    @Override
    public E floor(E e) {
        return binarySearch(e, true, false);
    }

    @Override
    public E ceiling(E e) {
        return binarySearch(e, false, false);
    }

    @Override
    public E higher(E e) {
        return binarySearch(e, false, true);
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException("pollFirst");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("pollLast");
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return binarySearch((E) o, true, false) != null;
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (contains(iterator.next()))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }
}
