
package com.ksyun.common.util.concurrent;

import java.util.concurrent.TimeUnit;

public final class ThreadUtils {

    /**
     * sleep等待100毫秒
     */
    public static void sleepShortTime() {
        sleep(100L);
    }

    /**
     * Puts a thread to sleep forever.
     */
    public static void sleepForever() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * sleep等待, 单位为毫秒, 已捕捉并处理InterruptedException.
     */
    public static void sleep(long durationMillis) {
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * sleep等待，已捕捉并处理InterruptedException.
     */
    public static void sleep(long duration, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(duration));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------------------------------------------------------------- synchronization

    /**
     * Waits for a object for synchronization purposes.
     */
    public static void wait(Object obj) {
        synchronized (obj) {
            try {
                obj.wait();
            } catch (InterruptedException inex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Waits for a object or a timeout for synchronization purposes.
     */
    public static void wait(Object obj, long timeout) {
        synchronized (obj) {
            try {
                obj.wait(timeout);
            } catch (InterruptedException inex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Notifies an object for synchronization purposes.
     */
    public static void notify(Object obj) {
        synchronized (obj) {
            obj.notify();
        }
    }

    /**
     * Notifies an object for synchronization purposes.
     */
    public static void notifyAll(Object obj) {
        synchronized (obj) {
            obj.notifyAll();
        }
    }


    // ---------------------------------------------------------------- join


    public static void join(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException inex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void join(Thread thread, long millis) {
        try {
            thread.join(millis);
        } catch (InterruptedException inex) {
            Thread.currentThread().interrupt();
        }
    }

    public static void join(Thread thread, long millis, int nanos) {
        try {
            thread.join(millis, nanos);
        } catch (InterruptedException inex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 纯粹为了提醒下处理InterruptedException的正确方式，除非你是在写不可中断的任务.
     */
    public static void handleInterruptedException() {
        Thread.currentThread().interrupt();
    }

    /**
     * A constructor to stop this class being constructed.
     */
    private ThreadUtils() {
        // Unused
    }
}
