package ipp.estg.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A thread-safe implementation of an ArrayList.
 * This class extends the {@link ArrayList} class and overrides several methods to ensure thread-safety by synchronizing access.
 * It ensures that operations such as adding, removing, and accessing elements are safe for concurrent use.
 *
 * @param <T> the type of elements in this list
 */
public class SynchronizedArrayList<T> extends ArrayList<T> {

    /**
     * Adds the specified element to the list in a thread-safe manner.
     * This method ensures that the add operation is synchronized to prevent concurrent modification.
     *
     * @param t the element to be added
     * @return true if the element was successfully added
     */
    @Override
    public synchronized boolean add(T t) {
            return super.add(t);
    }

    /**
     * Adds all elements in the specified collection to the list in a thread-safe manner.
     * This method ensures that the addAll operation is synchronized to prevent concurrent modification.
     *
     * @param c the collection of elements to be added
     * @return true if the list was modified as a result of the operation
     */
    @Override
    public synchronized boolean addAll(Collection<? extends T> c) {
        return super.addAll(c);
    }

    /**
     * Removes the specified element from the list in a thread-safe manner.
     * This method ensures that the remove operation is synchronized to prevent concurrent modification.
     *
     * @param o the element to be removed
     * @return true if the element was successfully removed
     */
    @Override
    public synchronized boolean remove(Object o) {
        return super.remove(o);
    }

    /**
     * Removes all elements in the specified collection from the list in a thread-safe manner.
     * This method ensures that the removeAll operation is synchronized to prevent concurrent modification.
     *
     * @param c the collection of elements to be removed
     * @return true if the list was modified as a result of the operation
     */
    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }

    /**
     * Retrieves the element at the specified index in a thread-safe manner.
     * This method ensures that the get operation is synchronized to prevent concurrent modification.
     *
     * @param index the index of the element to retrieve
     * @return the element at the specified index
     */
    @Override
    public synchronized T get(int index) {
        return super.get(index);
    }

    /**
     * Replaces the element at the specified index with the specified element in a thread-safe manner.
     * This method ensures that the set operation is synchronized to prevent concurrent modification.
     *
     * @param index the index of the element to replace
     * @param element the element to be stored at the specified index
     * @return the previous element at the specified index
     */
    @Override
    public synchronized T set(int index, T element) {
        return super.set(index, element);
    }

    /**
     * Returns the index of the first occurrence of the specified element in the list in a thread-safe manner.
     * This method ensures that the indexOf operation is synchronized to prevent concurrent modification.
     *
     * @param o the element to search for
     * @return the index of the first occurrence of the specified element, or -1 if the element is not found
     */
    @Override
    public synchronized int indexOf(Object o) {
        return super.indexOf(o);
    }

    /**
     * Returns the index of the last occurrence of the specified element in the list in a thread-safe manner.
     * This method ensures that the lastIndexOf operation is synchronized to prevent concurrent modification.
     *
     * @param o the element to search for
     * @return the index of the last occurrence of the specified element, or -1 if the element is not found
     */
    @Override
    public synchronized int lastIndexOf(Object o) {
        return super.lastIndexOf(o);
    }

    /**
     * Checks if the list is empty in a thread-safe manner.
     * This method ensures that the isEmpty operation is synchronized to prevent concurrent modification.
     *
     * @return true if the list is empty, false otherwise
     */
    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    /**
     * Checks if the list contains the specified element in a thread-safe manner.
     * This method ensures that the contains operation is synchronized to prevent concurrent modification.
     *
     * @param o the element to check for
     * @return true if the list contains the specified element, false otherwise
     */
    @Override
    public synchronized boolean contains(Object o) {
        return super.contains(o);
    }
}
