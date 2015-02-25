package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.*;

/**
 * Created by mariashka on 2/22/15.
 */
abstract class AbstractImmutableArraySet<E> implements NavigableSet<E> {
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


    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        // Cannot check type Object vs generic E
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
        // Cannot iterate over Collection<?>, because of unknown type\
        //noinspection WhileLoopReplaceableByForEach
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
        return comparator;
    }
}
