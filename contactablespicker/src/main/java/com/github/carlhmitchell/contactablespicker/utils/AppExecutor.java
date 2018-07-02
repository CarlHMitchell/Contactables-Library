package com.github.carlhmitchell.contactablespicker.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by enyason on 5/31/18.
 */


@SuppressWarnings("SpellCheckingInspection")
public class AppExecutor {
    // Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutor sInstance;

    private final Executor diskIO;

    private AppExecutor(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static AppExecutor getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutor(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }
}
