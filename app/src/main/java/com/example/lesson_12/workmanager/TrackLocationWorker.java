package com.example.lesson_12.workmanager;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.lesson_12.repository.UserRepository;

public class TrackLocationWorker extends Worker {

    private final UserRepository repository;

    public TrackLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        repository = new UserRepository((Application) context);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            repository.findLocation();
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }


}
