package com.github.rodbate.stats;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;





public class LoginStats {


    //10 min
    private static final int MAX_CALL_RECORDS = 600;


    //10min
    private static final int USER_LOGIN_KEEP_TIME = 600 * 1000;




    private final ConcurrentHashMap<Integer, Long> userToLastTimestamp =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, AtomicLong> userToLoginTotalTimes =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, LinkedList<CallSnapshot>> userToCallSnapshot =
            new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService executorService0 = Executors.newSingleThreadScheduledExecutor();


    private final ReentrantLock lock = new ReentrantLock();


    private final Runnable checkExpireUser = () -> {
        this.lock.lock();
        userToLastTimestamp.forEach((id, ts) -> {
            if (ts + USER_LOGIN_KEEP_TIME <= System.currentTimeMillis()) {
                userToLastTimestamp.remove(id);
                userToLoginTotalTimes.remove(id);
                userToCallSnapshot.remove(id);
            }
        });
        this.lock.unlock();
    };


    public LoginStats() {
        this.executorService.scheduleAtFixedRate(checkExpireUser, 0, 1000, TimeUnit.MILLISECONDS);
        this.executorService0.scheduleAtFixedRate(this::run, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public AtomicLong getLoginTimesByUserId(int userId) {

        userToLastTimestamp.put(userId, System.currentTimeMillis());

        AtomicLong newTimes = new AtomicLong(0);
        AtomicLong old = userToLoginTotalTimes.putIfAbsent(userId, newTimes);
        if (old != null) {
            newTimes = old;
        }
        return newTimes;
    }


    //.........



    public double getLoginPs(int userId, int time) {
        double rs;
        this.lock.lock();
        try {
            LinkedList<CallSnapshot> callSnapshots = userToCallSnapshot.get(userId);
            if (callSnapshots == null || callSnapshots.size() < time) {
                return 0.0;
            }

            CallSnapshot begin = callSnapshots.get(callSnapshots.size() - time);
            CallSnapshot end = callSnapshots.getLast();
            rs = CallSnapshot.getTps(begin, end);
        } finally {
            this.lock.unlock();
        }

        return rs;
    }









    private void run(){

        this.lock.lock();
        try {
            userToLoginTotalTimes.forEach((id, totalTimes) -> {
                    LinkedList<CallSnapshot> newList = new LinkedList<>();
                    LinkedList<CallSnapshot> old = userToCallSnapshot.putIfAbsent(id, newList);
                    if (old != null) {
                        newList = old;
                    }
                    newList.addLast(new CallSnapshot(System.currentTimeMillis(), totalTimes.get()));

                    if (newList.size() >= MAX_CALL_RECORDS + 1) {
                        newList.removeFirst();
                    }
            });
        } finally {
            this.lock.unlock();
        }


    }



    public static void shellSort(int arr[]){

        int length = arr.length;

        int incr = length / 2;

        while (incr >= 1) {

            //insertion sort
            for (int i = incr; i - incr >= 0 && i < length; i += incr) {
                int current = arr[i];
                int j = i - incr;
                while (j >= 0 && current < arr[j]) {
                    arr[j + incr] = arr[j];
                    j -= incr;
                }
                arr[j + incr] = current;
            }

            incr--;
        }
    }




    public static void quickSort(int arr[], int left, int right) {

        if (right < left)
            return;

        //left pivot
        int pivot = arr[left];

        //
        int leftP = left;
        int rightP = right + 1;

        while (true) {

            //left <= pivot
            while (++leftP < right && arr[leftP] <= pivot) {}

            //right >= pivot
            while (--rightP > left && arr[rightP] >= pivot) {}

            if (leftP >= rightP) {
                break;
            }

            int temp = arr[leftP];
            arr[leftP] = arr[rightP];
            arr[rightP] = temp;
        }

        int temp = arr[rightP];
        arr[rightP] = pivot;
        arr[left] = temp;

        quickSort(arr, left, rightP - 1);
        quickSort(arr, rightP + 1, right);
    }


    static void merge(int arr[], int left, int mid, int right){

        //[left,mid]
        //[mid+1,right]

        int sub1Len = mid - left + 1;
        int sub2Len = right - mid;

        int sub1[] = new int[sub1Len];
        int sub2[] = new int[sub2Len];

        System.arraycopy(arr, left, sub1, 0, sub1Len);
        System.arraycopy(arr, mid + 1, sub2, 0, sub2Len);

        int sub1Index = 0;
        int sub2Index = 0;
        int parentIndex = left;


        while (sub1Index < sub1Len && sub2Index < sub2Len) {
            if (sub1[sub1Index] < sub2[sub2Index]) {
                arr[parentIndex++] = sub1[sub1Index++];
            } else {
                arr[parentIndex++] = sub2[sub2Index++];
            }
        }

        while (sub1Index < sub1Len) {
            arr[parentIndex++] = sub1[sub1Index++];
        }

        while (sub2Index < sub2Len) {
            arr[parentIndex++] = sub2[sub2Index++];
        }

    }

    static void mergeSort(int arr[], int left, int right){

        if (right <= left)
            return;

        int mid = (left + right) / 2;

        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);

        merge(arr, left, mid, right);
    }


    static void heap(int arr[], int n, int i) {
        int max = i;

        int leftChild = 2 * i + 1;
        int rightChild = 2 * i + 2;

        if (leftChild < n && arr[leftChild] > arr[max]) {
            max = leftChild;
        }

        if (rightChild < n && arr[rightChild] > arr[max]) {
            max = rightChild;
        }

        if (max != i) {
            //swap
            int temp = arr[i];
            arr[i] = arr[max];
            arr[max] = temp;

            //recursive
            heap(arr, n, max);
        }

    }

    static void heapSort(int arr[]){

        int len = arr.length;

        for (int i = len / 2 - 1; i >= 0; i--) {
            heap(arr, len, i);
        }

        for (int i = len - 1; i >= 0; i--) {

            //swap
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heap(arr, i, 0);
        }
    }






    private static class CallSnapshot {

        private long timestamp;

        private long callTotalTimes;

        CallSnapshot(long timestamp, long callTotalTimes) {
            this.timestamp = timestamp;
            this.callTotalTimes = callTotalTimes;
        }

        static double getTps(CallSnapshot begin, CallSnapshot end){
            long ts = end.timestamp - begin.timestamp;
            Long times = end.callTotalTimes - begin.callTotalTimes;
            return ts == 0 ? 0.0 : times.doubleValue() / ts * 1000;
        }
    }

    public static void map(Map<String, ?> map) {
        System.out.println(map.size());
    }

    public static void main(String[] args) {
        map(new HashMap<String, Integer>());
    }







    public static void show(int a[]){
        for (int i = 0; i < a.length; i++) {
            System.out.printf("%5d", a[i]);
        }
        System.out.println();
    }

}
