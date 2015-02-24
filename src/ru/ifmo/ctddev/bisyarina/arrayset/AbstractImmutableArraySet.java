package ru.ifmo.ctddev.bisyarina.arrayset;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by mariashka on 2/22/15.
 */
abstract class AbstractImmutableArraySet<E> implements NavigableSet<E> {
    Comparator<E> comparator;

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

    @Override
    public <T> T[] toArray(T[] ts) {
        int s = this.size();
        T[] array = ts.length >= s ? ts : (T[]) (Array.newInstance(ts.getClass().getComponentType(), s));
        Iterator iterator = this.iterator();

        for (int i = 0; i < array.length; ++i) {
            if (!iterator.hasNext()) {
                if (ts == array) {
                    array[i] = null;
                } else {
                    if (ts.length < i) {
                        return Arrays.copyOf(array, i);
                    }

                    System.arraycopy(array, 0, ts, 0, i);
                    if (ts.length > i) {
                        ts[i] = null;
                    }
                }

                return ts;
            }

            array[i] = (T) iterator.next();
        }

        return array;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return binarySearchElement((E) o, true, true) != null;
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
            if (!contains(iterator.next()))
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
        return this.comparator;
    }
}
