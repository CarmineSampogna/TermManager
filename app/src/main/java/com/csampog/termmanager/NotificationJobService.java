package com.csampog.termmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.model.Course;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class NotificationJobService extends JobService {

    NotificationManager mNotificationManager;
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();
        Date date = Date.from(Instant.now());
        CourseRepository courseRepository = CourseRepository.getInstance(getApplicationContext());
        List<Course> courses = null;
        if (courseRepository.courses != null) {
            courses = courseRepository.courses.getValue();
            if (courses != null && !courses.isEmpty()) {
                courses.stream().filter(c -> c.getAlertsEnabled() &&
                        c.getStartAlertPending() &&
                        c.getStartDate().after(date))
                        .forEach(c -> {
                            Intent intent = new Intent(this, CourseDetailsActivity.class);
                            intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, c.getCourseId());

                            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                                    .setContentTitle("Course Start Date")
                                    .setContentText(c.getTitle() + " starts on " + dateFormat.format(c.getStartDate()))
                                    .setContentIntent(contentPendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setAutoCancel(true);

                            mNotificationManager.notify(0, builder.build());
                        });
            }
            ;
        }


        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
}
