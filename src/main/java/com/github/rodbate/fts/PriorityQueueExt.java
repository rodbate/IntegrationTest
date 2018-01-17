package com.github.rodbate.fts;


import org.apache.lucene.util.PriorityQueue;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.validation.constraints.Null;
import java.io.Closeable;
import java.lang.reflect.ParameterizedType;
import java.util.Random;

public class PriorityQueueExt extends PriorityQueue<Integer> {

    public PriorityQueueExt() {
        super(16);
    }

    @Override
    protected boolean lessThan(Integer a, Integer b) {
        return a - b < 0;
    }




    public static void main(String[] args) {
        PriorityQueueExt ext = new PriorityQueueExt();
        System.out.println(((ParameterizedType)ext.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());

        com.github.rodbate.fts.PriorityQueue<Integer> priorityQueue = new com.github.rodbate.fts.PriorityQueue<>(3);
        priorityQueue.add(1);
        priorityQueue.add(10);
        priorityQueue.add(2);
        priorityQueue.add(7);
        priorityQueue.add(17);
        priorityQueue.add(7);
        System.out.println(priorityQueue);
        priorityQueue.pop();
        priorityQueue.pop();
        System.out.println(priorityQueue);

        /*Integer i;
        while ( (i = priorityQueue.pop()) != null ) {
            System.out.println(i);
        }*/

        for (int i : priorityQueue) {
            System.out.println(i);
        }
    }
}
