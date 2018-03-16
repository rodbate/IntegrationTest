package com.github.rodbate.it;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <DATA STRUCTURE AND ALG/>
 *
 * Created by rodbate on 2018/2/26.
 */
public class DataAlgTest extends Assert{

    private boolean logDebugEnable = true;
    private final List<Integer> initList = Arrays.asList(11,2,1,100,56,3,5,16,4,10);
    private final int sortedArray[] = {1,2,3,4,5,10,11,16,56,100};

    @Test
    public void testAlg() {
        assertTrue(Arrays.equals(bubbleSort(shuffle()), sortedArray));
        assertTrue(Arrays.equals(selectionSort(shuffle()), sortedArray));
        assertTrue(Arrays.equals(selectionSort2(shuffle()), sortedArray));
        assertTrue(Arrays.equals(insertionSort(shuffle()), sortedArray));
        assertTrue(Arrays.equals(heapSort(shuffle()), sortedArray));
        assertEquals(100, maxHeap(shuffle())[0]);
        assertEquals(1, minHeap(shuffle())[0]);

        int a[] = shuffle();
        //quickSort(a, 0, a.length - 1);
        assertTrue(Arrays.equals(quickSort(a, 0, a.length - 1), sortedArray));
    }

    private int[] shuffle() {
        Collections.shuffle(initList);
        int a[] = new int[initList.size()];
        for (int i = 0; i < initList.size(); i++) {
            a[i] = initList.get(i);
        }
        if (logDebugEnable) {
            System.out.print("shuffle array : ");
            show(a);
        }
        return a;
    }

    private void show(int a[]) {
        for (int anA : a) {
            System.out.printf("%5d", anA);
        }
        System.out.println();
    }

    private int[] bubbleSort(int[] arr) {
        int len = arr.length;
        for (int i = 1; i < len; i++) {
            for (int j = 0; j < len - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        return arr;
    }

    private int[] selectionSort(int[] arr) {
        int len = arr.length;
        for (int i = 0; i < len - 1; i++) {
            int maxIdx = 0;
            for (int j = 1; j < len - i; j++) {
                if (arr[j] > arr[maxIdx]) {
                    maxIdx = j;
                }
            }
            int temp = arr[len - i - 1];
            arr[len - i - 1] = arr[maxIdx];
            arr[maxIdx] = temp;
        }
        return arr;
    }

    private int[] selectionSort2(int[] arr) {
        int len = arr.length;
        for (int i = 0; i < len - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < len; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                int temp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = temp;
            }
        }
        return arr;
    }


    private int[] insertionSort(int[] arr) {
        int len = arr.length;
        for (int i = 1; i < len; i++) {
            int curr = arr[i];
            int j = i - 1;
            while (j >=0 && curr < arr[j]) {
                arr[j + 1] = arr[j];
                --j;
            }
            arr[j + 1] = curr;
        }
        return arr;
    }

    private int[] heapSort(int[] arr) {
        return heapSort0(arr, false);
    }

    private int[] heapSort0(int[] arr, boolean descending) {
        for (int len = arr.length; len >= 1; len--) {
            heap(arr, len, descending);
            int temp = arr[len - 1];
            arr[len - 1] = arr[0];
            arr[0] = temp;
        }
        if (logDebugEnable) {
            System.out.print("heap sort : ");
            show(arr);
        }
        return arr;
    }


    private int[] heap(int[] arr, int len, boolean minHeap) {
        int lIdx;
        int rIdx;
        for (int i = (len >>> 1) - 1; i >= 0; i--) {
            lIdx = 2 * i + 1;
            rIdx = 2 * i + 2;

            if (lIdx < len && (minHeap ? arr[lIdx] < arr[i] : arr[lIdx] > arr[i])) {
                int temp = arr[i];
                arr[i] = arr[lIdx];
                arr[lIdx] = temp;
            }
            if (rIdx < len && (minHeap ? arr[rIdx] < arr[i] : arr[rIdx] > arr[i])) {
                int temp = arr[i];
                arr[i] = arr[rIdx];
                arr[rIdx] = temp;
            }
        }
        return arr;
    }


    private int[] maxHeap(int[] arr) {
        return heap(arr, arr.length, false);
    }

    private int[] minHeap(int[] arr) {
        return heap(arr, arr.length, true);
    }


    private int[] quickSort(int[] arr, int leftIdx, int rightIdx) {
        if (leftIdx > rightIdx) {
            return arr;
        }
        //just set the element at index leftIdx to the pivot
        int pivot = arr[leftIdx];

        int leftPointer = leftIdx;
        int rightPointer = rightIdx + 1;

        while (true) {

            while (++leftPointer < rightIdx && arr[leftPointer] < pivot) {
                //no op
            }
            while (--rightPointer > leftIdx && arr[rightPointer] > pivot) {
                //no op
            }
            if (leftPointer >= rightPointer) {
                break;
            }

            //swap
            int temp = arr[leftPointer];
            arr[leftPointer] = arr[rightPointer];
            arr[rightPointer] = temp;
        }

        int temp = arr[rightPointer];
        arr[leftIdx] = temp;
        arr[rightPointer] = pivot;

        quickSort(arr, leftIdx, rightPointer - 1);
        quickSort(arr, rightPointer + 1, rightIdx);
        return arr;
    }


    @Test
    public void testTr() {
        BinaryTree tree = new BinaryTree();
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            tree.add(r.nextInt(100));
        }
        tree.show();
        tree.traverseWithLoop();
        tree.traverseWithStack();
        tree.traverseWithStack2();
    }



    //thread safe
    private static class BinaryTree {

        private Node root;
        private final ReentrantReadWriteLock treeReadWriteLock = new ReentrantReadWriteLock();


        public void add(int ele) {
            treeReadWriteLock.writeLock().lock();
            try {
                Node newNode = new Node(ele);
                if (root == null) {
                    root = newNode;
                } else {
                    boolean isLeftChild = false;
                    Node prevNode = root;
                    Node currentNode = root;
                    while (currentNode != null) {
                        prevNode = currentNode;
                        //left -> less or equal
                        if (ele <= currentNode.val) {
                            currentNode = currentNode.leftChild;
                            isLeftChild = true;
                        } else {
                            currentNode = currentNode.rightChild;
                            isLeftChild = false;
                        }
                    }
                    if (isLeftChild) {
                        prevNode.leftChild = newNode;
                    } else {
                        prevNode.rightChild = newNode;
                    }
                }

            } finally {
                treeReadWriteLock.writeLock().unlock();
            }
        }


        private void show() {
            System.out.println();
            traverseInOrder(root);
            System.out.println();
        }
        public void traverseInOrder(Node node) {
            if (node == null) {
                return;
            }
            traverseInOrder(node.leftChild);
            System.out.printf("%5d", node.val);
            traverseInOrder(node.rightChild);
        }

        public void traverseWithStack() {
            Stack<Node> stack = new Stack<>();
            Node current = root;
            while (current != null) {
                stack.push(current);
                current = current.leftChild;
            }
            System.out.println();
            while (!stack.empty()) {
                Node item = stack.pop();
                System.out.printf("%5d", item.val);
                current = item.rightChild;
                while (current != null) {
                    stack.push(current);
                    current = current.leftChild;
                }
            }
            System.out.println();
        }

        public void traverseWithStack2() {
            Stack<Node> stack = new Stack<>();
            Node current = root;
            System.out.println();
            while (true) {
                if (current != null) {
                    stack.push(current);
                    current = current.leftChild;
                } else {
                    if (!stack.empty()) {
                        Node item = stack.pop();
                        System.out.printf("%5d", item.val);
                        current = item.rightChild;
                    } else {
                        break;
                    }
                }
            }
            System.out.println();
        }


        public void traverseWithLoop() {

            if (root == null) {
                return;
            }

            Node prevNode;
            Node curNode = root;
            System.out.println();
            while (curNode != null) {
                if (curNode.leftChild == null) {
                    System.out.printf("%5d", curNode.val);
                    curNode = curNode.rightChild;
                } else {
                    prevNode = curNode.leftChild;
                    while (prevNode.rightChild != null && prevNode.rightChild != curNode) {
                        prevNode = prevNode.rightChild;
                    }

                    if (prevNode.rightChild == null) {
                        prevNode.rightChild = curNode;
                        curNode = curNode.leftChild;
                    } else {
                        prevNode.rightChild = null;
                        System.out.printf("%5d", curNode.val);
                        curNode = curNode.rightChild;
                    }
                }
            }
            System.out.println();
        }






        private static class Node {
            private int val;
            private Node leftChild;
            private Node rightChild;

            public Node(int val) {
                this.val = val;
            }
        }
    }









}
