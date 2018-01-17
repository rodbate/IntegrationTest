package com.github.rodbate.fts;

import java.util.*;

/**
 *
 * <pre>Not ThreadSafe</pre>
 *
 * Created by rodbate on 2017/12/13.
 */
public final class PriorityQueue<E> implements Iterable<E> {

    private static final int INITIAL_SIZE = 16;
    private E[] heap;
    private int size;
    private int capacity;
    private final Comparator<E> comparator;


    public PriorityQueue() {
        this(INITIAL_SIZE, null);
    }

    public PriorityQueue(Comparator<E> comparator) {
        this(INITIAL_SIZE, comparator);
    }

    public PriorityQueue(int initialSize) {
        this(initialSize, null);
    }

    @SuppressWarnings("unchecked")
    public PriorityQueue(int initialSize, Comparator<E> comparator) {
        if (initialSize <= 0) {
            initialSize = 2;
        }
        heap = (E[]) new Object[initialSize];
        capacity = initialSize;
        this.comparator = comparator;
    }


    public void add(E e) {
        checkElement(e);
        ensureCapacity();
        heap[++size] = e;
        upHeap(size);
    }

    public E pop() {
        if (size <= 0) {
            return null;
        }
        E top = heap[1];
        heap[1] = heap[size];
        heap[size] = null;
        --size;
        downHeap(1);
        return top;
    }

    public E peekTop() {
        return heap[1];
    }

    public E peekBottom() {
        return heap[size];
    }

    public E updateTop() {
        downHeap(1);
        return heap[1];
    }

    public int size() {
        return size;
    }

    private void upHeap(int index){
        E node = heap[index];
        int i = index >>> 1;
        while (i > 0 && importantThan(node, heap[i])) {
            heap[index] = heap[i];
            index = i;
            i = index >>> 1;
        }
        heap[index] = node;
    }

    private void downHeap(int index) {
        E node = heap[index];
        int i = index << 1;
        int j = i + 1;
        if (j <= size && importantThan(heap[j], heap[i])) {
            i = j;
        }
        while (i <= size && importantThan(heap[i], node)) {
            heap[index] = heap[i];
            index = i;

            i = index << 1;
            j = i + 1;
            if (j <= size && importantThan(heap[j], heap[i])) {
                i = j;
            }
        }
        heap[index] = node;
    }


    @SuppressWarnings("unchecked")
    private boolean importantThan(E e1, E e2) {
        if (comparator != null) {
            return comparator.compare(e1, e2) > 0;
        }
        return ((Comparable)e1).compareTo(e2) > 0;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity(){
        if (size + 1 >= capacity) {
            capacity *= 2;
            E newHeap[] = (E[]) new Object[capacity];
            System.arraycopy(heap, 0, newHeap, 0, size + 1);
            heap = newHeap;
        }
    }

    private void checkElement(E e) {
        Objects.requireNonNull(e);
        if (comparator == null && !(e instanceof Comparable)) {
            throw new IllegalArgumentException("");
        }
    }


    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return size >= 1;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return pop();
            }
        };
    }

    @Override
    public String toString() {
        return Arrays.asList(heap).toString();
    }

}
