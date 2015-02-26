package ru.ifmo.ctddev.bisyarina.arrayset;

import java.util.*;

/**
 * Created by mariashka on 2/26/15.
 */
@SuppressWarnings("NullableProblems")
public class ReversibleArrayList<E> {
    private List<E> arrayList;
    private boolean reversed;
    private Comparator<E> comparator;

    private int getIndex(int x) {
        if (reversed) {
            return arrayList.size() - x - 1;
        } else {
            return x;
        }
    }

    private ReversibleArrayList(List<E> list, boolean reversed, Comparator<E> c) {
        arrayList = list;
        this.reversed = reversed;
        this.comparator = c;
    }

    public ReversibleArrayList() {
        arrayList = new ArrayList<>();
        reversed = false;
    }

    public ReversibleArrayList(Collection<E> collection, Comparator<E> c) {
        ArrayList<E> list = new ArrayList<>(collection);
        this.comparator = c;
        Collections.sort(list, comparator);
        initNoDouble(list);
    }

    ReversibleArrayList<E> reverse() {
        return new ReversibleArrayList<>(arrayList, !reversed, comparator);
    }

    public int binarySearch(E element, boolean forward, boolean inclusive) {
        int idx = binarySearchIdx(element, forward, inclusive);
        if (reversed) {
            return getIndex(idx);
        } else {
            return idx;
        }

    }

    private int binarySearchIdx(E element, boolean forward, boolean inclusive) {
        int idx = Collections.binarySearch(arrayList, element, comparator);
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

    public E get(int i) {
        return arrayList.get(getIndex(i));
    }

    public ReversibleArrayList<E> subList(int i, int i1) {
        if (reversed) {
            int x = arrayList.size() - i;
            i = arrayList.size() - i1;
            i1 = x;
        }
        return new ReversibleArrayList<>(arrayList.subList(i, i1), reversed, comparator);
    }

    public int size() {
        return arrayList.size();
    }

    public void initNoDouble(ArrayList<E> list) {
        arrayList = new ArrayList<>();
        for (E aList : list) {
            if (arrayList.size() > 0) {
                if (compare(arrayList.get(arrayList.size() - 1), aList) != 0) {
                    arrayList.add(aList);
                }
            } else {
                arrayList.add(aList);
            }
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
