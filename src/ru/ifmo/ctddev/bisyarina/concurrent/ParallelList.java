package ru.ifmo.ctddev.bisyarina.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ParallelList implements ListIP {

    @Override
    public <T> T maximum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        if (i == 0) {
            return null;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);
        ParallelInvoker<T> parallelInvoker =
                new ParallelInvoker<>(i);
        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(new Supplier<T>() {
                int idx = finalL;
                @Override
                public T get() {
                    for (int j = finalL; j < r; j++) {
                        if (comparator.compare(list.get(idx), list.get(j)) < 0) {
                            idx = j;
                        }
                    }
                    return list.get(idx);
                }
            });
        }

        return parallelInvoker.getAll(ts -> {
            int idx = 0;
            for (int j = 0; j < ts.length; j++) {
                if (ts[idx] == null) {
                    idx = j;
                } else {
                    if (ts[j] != null) {
                        if (comparator.compare(ts[j], ts[idx]) > 0) {
                            idx = j;
                        }
                    }
                }
            }
            return ts[idx];
        });
    }

    @Override
    public <T> T minimum(int i, List<? extends T> list, Comparator<? super T> comparator) throws InterruptedException {
        if (i == 0) {
            return null;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<T> parallelInvoker =
                new ParallelInvoker<>(i);

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(new Supplier<T>() {
                int idx = finalL;
                @Override
                public T get() {
                    for (int j = finalL; j < r; j++) {
                        if (comparator.compare(list.get(idx), list.get(j)) > 0) {
                            idx = j;
                        }
                    }
                    return list.get(idx);
                }
            });
        }

        return parallelInvoker.getAll(ts -> {
            int idx = 0;
            for (int j = 0; j < ts.length; j++) {
                if (ts[idx] == null) {
                    idx = j;
                } else {
                    if (ts[j] != null) {
                        if (comparator.compare(ts[j], ts[idx]) < 0) {
                            idx = j;
                        }
                    }
                }
            }
            return ts[idx];
        });
    }

    @Override
    public <T> boolean all(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        if (i == 0) {
            return false;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<Boolean> parallelInvoker =
                new ParallelInvoker<>(i);

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                for (int j = finalL; j < r; j++) {
                    if (!predicate.test(list.get(j))) {
                        return false;
                    }
                }
                return true;
            });
        }

        return parallelInvoker.getAll(booleans -> {
            for (Boolean aBoolean : booleans) {
                if (aBoolean != null && !aBoolean) {
                    return false;
                }
            }
            return true;
        });
    }

    @Override
    public <T> boolean any(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        if (i == 0) {
            return false;
        }
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<Boolean> parallelInvoker =
                new ParallelInvoker<>(i);

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                for (int j = finalL; j < r; j++) {
                    if (predicate.test(list.get(j))) {
                        return true;
                    }
                }
                return false;
            });
        }

        return parallelInvoker.getAll(booleans -> {
            for (Boolean aBoolean : booleans) {
                if (aBoolean != null && aBoolean) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public String concat(int i, List<?> list) throws InterruptedException {
        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<String> parallelInvoker = new ParallelInvoker<>(i);

        for (int l = 0; l < list.size(); l += interval) {

            int r = Math.min(list.size(), l + interval);
            final int finalL = l;

            parallelInvoker.add(() -> {
                StringBuilder builder = new StringBuilder();
                for (int j = finalL; j < r; j++) {
                    builder.append(list.get(j));
                }
                return builder.toString();
            });
        }

        return parallelInvoker.getAll(strings -> {
            StringBuilder builder = new StringBuilder();
            for (String string : strings) {
                if (string != null)
                    builder.append(string);
            }
            return builder.toString();
        });
    }

    @Override
    public <T> List<T> filter(int i, List<? extends T> list, Predicate<? super T> predicate) throws InterruptedException {
        if (i == 0) {
            return null;
        }

        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<List<T>> parallelInvoker =
                new ParallelInvoker<>(i);

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                List<T> res = new ArrayList<T>();
                for (int j = finalL; j < r; j++) {
                    if (predicate.test(list.get(j)))
                        res.add(list.get(j));
                }
                return res;
            });
        }

        return parallelInvoker.getAll(lists -> {
            List<T> res = new ArrayList<T>();
            for (List<T> list1 : lists) {
                if (list1 != null)
                    res.addAll(list1);
            }
            return res;
        });
    }

    @Override
    public <T, U> List<U> map(int i, List<? extends T> list, Function<? super T, ? extends U> function) throws InterruptedException {
        if (i == 0) {
            return null;
        }

        int interval = list.size() / i + (list.size() % i == 0 ? 0 : 1);

        ParallelInvoker<List<U>> parallelInvoker = new ParallelInvoker<>(i);

        for (int l = 0; l < list.size(); l += interval) {
            int r = Math.min(list.size(), l + interval);
            final int finalL = l;
            parallelInvoker.add(() -> {
                List<U> res = new ArrayList<>();
                for (int j = finalL; j < r; j++) {
                    res.add(function.apply(list.get(j)));
                }
                return res;
            });
        }

        return parallelInvoker.getAll(lists -> {
            List<U> res = new ArrayList<>();
            for (List<U> list1 : lists) {
                if (list1 != null)
                    res.addAll(list1);
            }
            return res;
        });
    }
}
