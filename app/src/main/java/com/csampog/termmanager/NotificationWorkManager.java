package com.csampog.termmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.model.Course;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationWorkManager extends Worker {

    NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";


    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

    public NotificationWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    private void createNotificationChannel() {

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "alertChannel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Term Manager alerts channel");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @NonNull
    @Override
    public Result doWork() {

        createNotificationChannel();
        Date date = Date.from(Instant.now());
        CourseRepository courseRepository = CourseRepository.getInstance(getApplicationContext());
        new Thread(() -> {
            // Do your stuff here with Room

            List<Course> courses = courseRepository.getCoursesForAlerts();
            if (courses != null && !courses.isEmpty()) {
                courses.stream()
                        //.filter(c -> c.getAlertsEnabled() &&
                        //c.getStartAlertPending() &&
                        //c.getStartDate().after(date))
                        .forEach(c -> {
                            Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                            intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, c.getCourseId());

                            PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
                                    .setContentTitle("Course Start Date")
                                    .setContentText(c.getTitle() + " starts on " + dateFormat.format(c.getStartDate()))
                                    .setContentIntent(contentPendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setSmallIcon(R.drawable.ic_baseline_class_24);

                            mNotificationManager.notify(c.getCourseId(), builder.build());
                        });
            }


        }).start();

//        if (courseRepository.courses.getValue() != null) {
//            List<Course> courses = courseRepository.getCoursesForAlerts();
//            if (courses != null && !courses.isEmpty()) {
//                courses.stream()
//                        //.filter(c -> c.getAlertsEnabled() &&
//                        //c.getStartAlertPending() &&
//                        //c.getStartDate().after(date))
//                        .forEach(c -> {
//                            Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
//                            intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, c.getCourseId());
//
//                            PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
//                                    .setContentTitle("Course Start Date")
//                                    .setContentText(c.getTitle() + " starts on " + dateFormat.format(c.getStartDate()))
//                                    .setContentIntent(contentPendingIntent)
//                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
//                                    .setSmallIcon(R.drawable.ic_baseline_class_24);
//
//                            mNotificationManager.notify(c.getCourseId(), builder.build());
//                        });
//            }
//        }
        return Result.success();
    }
}
