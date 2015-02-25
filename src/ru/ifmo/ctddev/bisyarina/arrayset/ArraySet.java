package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.*;

/**
 * Created by mariashka on 2/22/15.
 */
public class ArraySet<E> extends AbstractImmutableArraySet<E> {
    private List<E> data;
    private boolean reversed;
    private Comparator<E> actualComparator;

    private void removeDoubleElements() {
        Collections.sort(data, actualComparator);
        ArrayList<E> list = new ArrayList<>();
        for (E aData : data) {
            if (list.size() > 0) {
                if (actualComparator.compare(list.get(list.size() - 1), aData) != 0) {
                    list.add(aData);
                }
            } else {
                list.add(aData);
            }
        }
        data = list;
    }

    public ArraySet() {
        super(null);
        this.actualComparator = (Comparator<E>) Comparator.naturalOrder();
        this.data = new ArrayList<>();
        this.reversed = false;
    }

    public ArraySet(Collection<E> collection) {
        super(null);
        this.actualComparator = (Comparator<E>) Comparator.naturalOrder();
        this.data = new ArrayList<>(collection);
        this.reversed = false;
        removeDoubleElements();
    }

    public ArraySet(Collection<E> collection, Comparator<E> comparator) {
        super(comparator);
        this.actualComparator = comparator;
        this.data = new ArrayList<>(collection);
        this.reversed = false;
        removeDoubleElements();
    }

    private ArraySet(List<E> list, Comparator<E> comparator1, Comparator<E> comparator2, boolean reversed) {
        super(comparator1);
        this.actualComparator = comparator2;
        this.data = list;
        this.reversed = reversed;
    }

    private int binarySearch(E element, boolean forward, boolean inclusive) {
        int idx = Collections.binarySearch(data, element, this.comparator);
        if (idx >= 0) {
            if (inclusive) {
                return idx;
            } else {
                if (forward ^ reversed) {
                    return idx - 1;
                } else {
                    return idx + 1;
                }
            }
        } else {
            idx = (idx + 1) * -1;
            if (forward ^ reversed) {
                return idx - 1;
            } else {
                return idx;
            }
        }
    }

    @Override
    protected E binarySearchElement(E element, boolean forward, boolean inclusive) {
        int idx;
        if ((idx = binarySearch(element, forward, inclusive)) < 0 || idx >= size()) {
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
        return new ArrayIterator<>(data, reversed);
    }

    @Override
    public Object[] toArray() {
        Object[] newArray = data.toArray();
        if (reversed) {
            Collections.reverse(Arrays.asList(newArray));
        }
        return newArray;
    }


    @Override
    public <T> T[] toArray(T[] ts) {
        T[] newArray = data.toArray(ts);
        if (reversed) {
            Collections.reverse(Arrays.asList(newArray));
        }
        return newArray;
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(data, this.comparator, this.actualComparator, !reversed);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new ArrayIterator<>(data, !reversed);
    }

    @Override
    public NavigableSet<E> subSet(E e, boolean b, E e1, boolean b1) {
        if ((!reversed && actualComparator.compare(e, e1) > 0) && (reversed && actualComparator.compare(e, e1) < 0))
            throw new IllegalArgumentException("First argument is greater then second");

        int l = Math.max(binarySearch(e, false, b), 0);
        int r = binarySearch(e1, true, b1);
        if (reversed) {
            int x = l;
            l = r;
            r = x;
        }
        r = r < size() ? r + 1 : r;
        if (r <= l) {
            return new ArraySet<E>();
        }
        return new ArraySet<E>(data.subList(l, r), comparator, actualComparator, reversed);
    }

    @Override
    public NavigableSet<E> headSet(E e, boolean b) {
        int r = binarySearch(e, true, b);
        if (!reversed) {
            r = r < size() ? r + 1 : r;
            return new ArraySet<E>(data.subList(0, r), comparator, actualComparator, reversed);
        } else {
            r = Math.max(r, 0);
            return new ArraySet<E>(data.subList(r, size()), comparator, actualComparator, reversed);
        }
    }

    @Override
    public NavigableSet<E> tailSet(E e, boolean b) {
        int l = Math.max(binarySearch(e, false, b), 0);
        if (!reversed) {
            return new ArraySet<E>(data.subList(l, size()), comparator, actualComparator, reversed);
        } else {
            l = l < size() ? l + 1 : l;
            return new ArraySet<E>(data.subList(0, l), comparator, actualComparator, reversed);
        }
    }

    @Override
    public E first() {
        if (size() == 0)
            throw new NoSuchElementException("Set is empty. No lowest element.");
        if (reversed) {
            return data.get(size() - 1);
        } else {
            return data.get(0);
        }
    }

    @Override
    public E last() {
        if (size() == 0)
            throw new NoSuchElementException("Set is empty. No greatest element.");
        if (reversed) {
            return data.get(0);
        } else {
            return data.get(size() - 1);
        }
    }

    private static final class ArrayIterator<E> implements Iterator<E> {
        private List<E> data;
        private int idx;
        private boolean reversed;

        ArrayIterator(List<E> data, boolean reversed) {
            this.data = data;
            this.reversed = reversed;
            if (reversed) {
                idx = data.size() - 1;
            } else {
                idx = 0;
            }
        }

        @Override
        public boolean hasNext() {
            if (reversed) {
                return idx > -1;
            } else {
                return idx < data.size();
            }
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int oldIdx = idx;
            if (reversed) {
                idx--;
            } else {
                idx++;
            }
            return data.get(oldIdx);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
