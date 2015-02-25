package ru.ifmo.ctddev.bisyarina.arrayset;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.TreeSet;

public class ImmutableArraySetTest extends TestCase {
    public void testFirst() throws Exception {
        TreeSet<Integer> treeSet = new TreeSet<>();
        ArrayList<Integer> integ = new ArrayList<>();

        treeSet.add(10);
        integ.add(10);
        treeSet.add(1);
        integ.add(1);
        treeSet.add(4);
        integ.add(4);
        treeSet.add(7);
        integ.add(7);
        treeSet.add(0);
        integ.add(0);
        treeSet.add(3);
        integ.add(3);
        treeSet.add(5);
        integ.add(5);
        treeSet.add(-1);
        integ.add(-1);

        ArraySet<Integer> integerArraySet = new ArraySet<>(integ);
        TreeSet<Integer> treeSet1 = (TreeSet<Integer>) treeSet.descendingSet();
        ArraySet<Integer> integerArraySet1 = (ArraySet<Integer>) integerArraySet.descendingSet();


    }

    public void testFirst2() throws Exception {
        TreeSet<Integer> treeSet = new TreeSet<>();
        ArrayList<Integer> integ = new ArrayList<>();
        treeSet.add(10);
        integ.add(10);
        treeSet.add(1);
        integ.add(1);
        treeSet.add(4);
        integ.add(4);
        treeSet.add(7);
        integ.add(7);
        treeSet.add(0);
        integ.add(0);
        treeSet.add(3);
        integ.add(3);
        treeSet.add(5);
        integ.add(5);
        treeSet.add(-1);
        integ.add(-1);

        ArraySet<Integer> integerArraySet = new ArraySet<>(integ);
        TreeSet<Integer> treeSet1 = (TreeSet<Integer>) treeSet.descendingSet();
        ArraySet<Integer> integerArraySet1 = (ArraySet<Integer>) integerArraySet.descendingSet();

        //assertEquals(treeSet1.floor(3), integerArraySet1.floor(3));

        assertEquals(treeSet1.subSet(3, true, -1000, true).size(), integerArraySet1.subSet(3, true, -1000, true).size());

    }
}