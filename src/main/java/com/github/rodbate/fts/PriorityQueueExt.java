package com.github.rodbate.fts;


import org.apache.lucene.util.PriorityQueue;

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

        PriorityQueueExt queueExt = new PriorityQueueExt();

        final Random r = new Random();
        for (int i = 0; i < 10; i++) {
            queueExt.add(r.nextInt(100));
        }

        /*for (int item : queueExt) {
            System.out.println(item);
        }*/

        Integer item;
        while ( (item = queueExt.pop()) != null ) {
            System.out.println(item);
        }

    }
}
