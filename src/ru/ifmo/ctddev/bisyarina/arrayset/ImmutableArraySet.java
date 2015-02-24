package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.*;

/**
 * Created by mariashka on 2/22/15.
 */
@SuppressWarnings("NullableProblems")
public class ImmutableArraySet<E> extends AbstractImmutableArraySet<E> {
    private final E[] data;
    private final int leftBound;
    private final int rightBound; // left inclusive, right exclusive

    public ImmutableArraySet(E[] data, Comparator<E> comparator) {
        super(comparator);

        checkInputData(data, comparator);
        this.data = data.clone();
        this.leftBound = 0;
        this.rightBound = data.length;
    }

    private void checkInputData(E[] data, Comparator<E> comparator1) {
        try {
            for (int i = 1; i < data.length; i++) {
                if (comparator1.compare(data[i - 1], data[i]) >= 0) {
                    throw new IllegalArgumentException("Data is not ordered by this comparator");
                }
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Data can't be compared by this comparator");
        }
    }

    private ImmutableArraySet(E[] data, Comparator<E> comparator, int l, int r) {
        super(comparator);
        this.data = data;
        this.leftBound = l;
        this.rightBound = r;
    }

    public static <E extends Comparable<E>> AbstractImmutableArraySet<E> fromArray(E[] data) {
        Comparator<E> order = Comparator.<E>naturalOrder();
        return new ImmutableArraySet<>(data, order);
    }

    private ImmutableArraySet<E> getSubCopy(int l, int r) {
        return new ImmutableArraySet<>(this.data, this.comparator, l, r);
    }

    private int binarySearch(E element, boolean forward, boolean inclusive) {
        int l, r, m;
        if (forward) {
            l = leftBound - 1;
            r = rightBound;
        } else {
            l = leftBound;
            r = rightBound - 1;
        }

        while (r - l > 1) {
            m = (l + r) / 2;
            int compareRes = comparator.compare(data[m], element);
            if (forward ^ inclusive) {
                if (compareRes < 0) {
                    l = m;
                } else {
                    r = m;
                }
            } else {
                if (compareRes <= 0) {
                    l = m;
                } else {
                    r = m;
                }
            }
        }
        return forward ? l : r;
    }

    @Override
    protected E binarySearchElement(E element, boolean forward, boolean inclusive) {
        int idx;
        if ((idx = binarySearch(element, forward, inclusive)) < 0 || idx >= data.length) {
            return null;
        } else {
            return data[idx];
        }
    }


    @Override
    public int size() {
        return rightBound - leftBound;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(data, true, leftBound, rightBound);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOfRange(data, leftBound, rightBound);
    }

    @Override
    public NavigableSet<E> descendingSet() {
        E[] reversed = Arrays.copyOfRange(data, leftBound, rightBound);
        Collections.reverse(Arrays.asList(reversed));
        return new ImmutableArraySet<>(reversed, comparator.reversed());
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new ArrayIterator<>(data, false, leftBound, rightBound);
    }

    @Override
    public NavigableSet<E> subSet(E e, boolean b, E e1, boolean b1) {
        if (comparator.compare(e, e1) > 0)
            throw new IllegalArgumentException("First argument is greater then second");
        int l = binarySearch(e, true, b);
        int r = binarySearch(e1, false, b1);
        if (l < leftBound)
            throw new IllegalArgumentException("First argument lies outside of the range");
        if (r >= rightBound)
            throw new IllegalArgumentException("Second argument lies outside of the range");
        return getSubCopy(l, r);
    }

    @Override
    public NavigableSet<E> headSet(E e, boolean b) {
        int r = binarySearch(e, false, b);
        if (r >= rightBound)
            throw new IllegalArgumentException("Argument lies outside of the range");
        return getSubCopy(leftBound, r + 1);
    }

    @Override
    public NavigableSet<E> tailSet(E e, boolean b) {
        int l = binarySearch(e, true, b);
        if (l < leftBound)
            throw new IllegalArgumentException("Argument lies outside of the range");
        return getSubCopy(l, rightBound);
    }

    @Override
    public E first() {
        if (size() == 0)
            throw new NoSuchElementException("Set is empty. No lowest element.");
        return data[leftBound];
    }

    @Override
    public E last() {
        if (size() == 0)
            throw new NoSuchElementException("Set is empty. No greatest element.");
        return data[rightBound - 1];
    }

    private static final class ArrayIterator<E> implements Iterator<E> {
        private final E[] data;
        private int idx;
        private int last;

        ArrayIterator(E[] data, boolean forward, int l, int r) {
            this.data = data.clone();
            if (!forward) {
                Collections.reverse(Arrays.asList(this.data));
                idx = data.length - r;
                last = data.length - l;
            }
            idx = l;
            last = r;
        }

        @Override
        public boolean hasNext() {
            return idx < last;
        }

        @Override
        public E next() {
            if (hasNext()) {
                return data[idx++];
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
