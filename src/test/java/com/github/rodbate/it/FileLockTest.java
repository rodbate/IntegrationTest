package com.github.rodbate.it;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * File Lock
 *
 * Created by rodbate on 2017/12/6.
 */
public class FileLockTest{

    private FileChannel fileLock;
    private final String lockFileDir = "E:\\lock";
    private final String lockFilename = "m.lock";

    @Before
    public void setup() throws IOException {
        try {
            Files.createDirectories(Paths.get(lockFileDir));
            Files.createFile(Paths.get(lockFileDir).resolve(lockFilename));
        } catch (FileAlreadyExistsException ex) {
            //ignore ...
        }
        fileLock = FileChannel.open(Paths.get(lockFileDir, lockFilename), StandardOpenOption.APPEND);
    }

    @Test
    public void test() throws IOException {
        final CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            FileLock lock = null;
            try {
                lock = fileLock.tryLock();
                System.out.println("File Lock : " + lock);
                fileLock.write(ByteBuffer.wrap("WROD".getBytes(StandardCharsets.UTF_8)));
                Thread.sleep(5000);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (lock != null) {
                    try {
                        lock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                latch.countDown();
            }

        }).start();

        new Thread(() -> {
            try {
                fileLock.tryLock();
                fileLock.write(ByteBuffer.wrap("Thread2".getBytes(StandardCharsets.UTF_8)));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (fileLock != null) {
                fileLock.close();
            }
        }
    }




    @Test
    public void testA() {
        List<String> result = new ArrayList<>();
        String fileNames = "part1\\,part2,part3";
        for (String file : fileNames.split("(?<=\\\\),")) {
            System.out.println(file);
            result.add(file.replaceAll("\\\\(?=,)", ""));
        }
        System.out.println("Size : " + result.size() + result);
    }
}
