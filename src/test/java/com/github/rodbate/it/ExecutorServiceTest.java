package com.github.rodbate.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by rodbate on 2018/2/8.
 */
public class ExecutorServiceTest {

    private final ExecutorService service = Executors.newFixedThreadPool(1);
    private volatile Thread t;
    private final Runnable exampleTask = () -> {
        final int timeout = 10;
        int times = 0;
        while (true) {
            t = Thread.currentThread();
            if (++times > timeout) {
                break;
            }
            if (Thread.interrupted()) {
                System.out.println("Current Thread interrupt !");
                break;
            }
            System.out.println("-- " + LocalDateTime.now());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Sleep : Current Thread interrupt !");
                break;
            }
        }
    };

    @Before
    public void setUp() {
        service.execute(exampleTask);
    }

    @After
    public void tearDown() {
        service.shutdownNow();
    }


    @Test
    public void testShutdown() {
        service.shutdown();
        System.out.println("State Shutdown: " + service.isShutdown());
        System.out.println("State Terminate: " + service.isTerminated());
        try {
            service.execute(exampleTask);
        } catch (RejectedExecutionException ex) {
            System.out.println("RejectedExecutionException " + ex.getMessage());
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            //
        }
        System.out.println("State Terminate: " + service.isTerminated());
    }


    @Test
    public void testShutdownNow() {
        service.shutdownNow();
        System.out.println("State Shutdown: " + service.isShutdown());
        System.out.println("State Terminate: " + service.isTerminated());
        try {
            service.execute(exampleTask);
        } catch (RejectedExecutionException ex) {
            System.out.println("RejectedExecutionException " + ex.getMessage());
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            //
        }
        System.out.println("State Terminate: " + service.isTerminated());
    }

    @Test
    public void testAwaitTermination() {
        service.shutdown();
        System.out.println("State Shutdown: " + service.isShutdown());
        System.out.println("State Terminate: " + service.isTerminated());
        try {
            service.execute(exampleTask);
        } catch (RejectedExecutionException ex) {
            System.out.println("RejectedExecutionException " + ex.getMessage());
        }
        try {
            boolean rs = service.awaitTermination(5, TimeUnit.SECONDS);
            if (!rs) {
                service.shutdownNow();
            }
            System.out.println("State Terminate: " + service.isTerminated());
        } catch (InterruptedException e) {
            //
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            //
        }
        System.out.println("State Terminate: " + service.isTerminated());
    }
}
