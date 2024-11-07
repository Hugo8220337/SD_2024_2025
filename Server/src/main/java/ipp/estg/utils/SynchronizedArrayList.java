package ipp.estg.utils;

import java.util.ArrayList;
import java.util.Collection;

public class SynchronizedArrayList<T> extends ArrayList<T> {
    @Override
    public synchronized boolean add(T t) {
            return super.add(t);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends T> c) {
        return super.addAll(c);
    }

    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    @Override
    public synchronized T get(int index) {
        return super.get(index);
    }

    @Override
    public synchronized T set(int index, T element) {
        return super.set(index, element);
    }

    @Override
    public synchronized int indexOf(Object o) {
        return super.indexOf(o);
    }

    @Override
    public synchronized int lastIndexOf(Object o) {
        return super.lastIndexOf(o);
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return super.contains(o);
    }
}
