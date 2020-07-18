package com.csampog.termmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.csampog.termmanager.dataAccess.repositories.AssessmentRepository;
import com.csampog.termmanager.dataAccess.repositories.CourseRepository;
import com.csampog.termmanager.model.Assessment;
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
        AssessmentRepository assessmentRepository = AssessmentRepository.getInstance(getApplicationContext());
        sendAssessmentNotifications(date, assessmentRepository);
        sendCourseNotifications(date, courseRepository);
        return Result.success();
    }

    private void sendAssessmentNotifications(Date date, AssessmentRepository assessmentRepository) {
        new Thread(() -> {

            List<Assessment> assessments = assessmentRepository.getAssessmentsForAlerts();
            if (assessments != null && !assessments.isEmpty()) {
                assessments.stream()
                        .filter(a -> a.getAlertsEnabled() &&
                                !date.after(a.getGoalDate()))
                        .forEach(assessment -> {
                            Intent intent = new Intent(getApplicationContext(), EditAssessmentActivity.class);
                            intent.putExtra(EditAssessmentActivity.ASSESSMENT_ID_PARAM, assessment.getAssessmentId());

                            boolean isBefore = date.before(assessment.getGoalDate());
                            String alertText = isBefore ? "on " + dateFormat.format(assessment.getGoalDate()) : "today!";

                            PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
                                    .setContentTitle("Assessment Date")
                                    .setContentText(assessment.getTitle() + " is " + alertText)
                                    .setContentIntent(contentPendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setSmallIcon(R.drawable.ic_baseline_class_24);

                            mNotificationManager.notify(assessment.getAssessmentId(), builder.build());
                        });
            }


        }).start();
    }

    private void sendCourseNotifications(Date date, CourseRepository courseRepository) {
        new Thread(() -> {

            List<Course> courses = courseRepository.getCoursesForAlerts();
            if (courses != null && !courses.isEmpty()) {
                courses.stream()
                        .forEach(c -> {

                            boolean isBefore = date.before(c.getStartDate());
                            boolean isAfter = date.after(c.getStartDate());

                            Intent intent = new Intent(getApplicationContext(), CourseDetailsActivity.class);
                            intent.putExtra(CourseDetailsActivity.COURSE_ID_KEY, c.getCourseId());

                            String titleText = !isAfter && c.getStartAlertEnabled() ? "Start" : "End";
                            String alertText = !isAfter && c.getStartAlertEnabled() ? " starts on " : " ends on ";
                            Date targetDate = !isAfter && c.getStartAlertEnabled() ? c.getStartDate() : c.getAnticipatedEndDate();

                            PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID)
                                    .setContentTitle("Course " + titleText + " Date")
                                    .setContentText(c.getTitle() + alertText + dateFormat.format(targetDate))
                                    .setContentIntent(contentPendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setSmallIcon(R.drawable.ic_baseline_class_24);

                            mNotificationManager.notify(c.getCourseId(), builder.build());
                        });
            }


        }).start();
    }
}
