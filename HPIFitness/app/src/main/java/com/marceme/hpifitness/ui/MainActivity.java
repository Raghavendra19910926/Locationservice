package com.marceme.hpifitness.ui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.marceme.hpifitness.R;
import com.marceme.hpifitness.model.User;
import com.marceme.hpifitness.notification.NotificationBroadcaster;
import com.marceme.hpifitness.util.Helper;
import com.marceme.hpifitness.util.PrefManager;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.messageLabel)
    TextView mMessage;
    @BindView(R.id.dailyDistanceData)
    TextView mTotalDist;
    @BindView(R.id.dailyTimeData)
    TextView mTotalTime;
    @BindView(R.id.dailyPaceData)
    TextView mCurrentPace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Realm realm = Realm.getDefaultInstance();
        String id = PrefManager.getID(PrefManager.USER_ID);
        User user = realm.where(User.class).equalTo("id", id).findFirst();
        if (user != null) {
            ;
            setDailyStat(user);
            showAchieveMilestone(user.getDistanceCovered());
        }
        scheduleNotification();
    }

    private void setDailyStat(User user) {
        String message = String.format(getString(R.string.message_label), user.getFirstName());
        String dailyDist = String.valueOf(user.getDistanceCovered());
        String dailyTime = String.valueOf(Helper.secondToHHMMSS(user.getTotalTimeWalk()));
        double dailyPace = user.getPace();

        mMessage.setText(message);
        mTotalDist.setText(dailyDist + " KM");
        mTotalTime.setText(dailyTime);

        double MET = ((0.175 * dailyPace) + (0.9 * dailyPace)) / 3.5;
        double Total_Calories = Helper.secondToMinuteConverter(user.getTotalTimeWalk()) * (MET * 3.5 * 65) / 200;
        mCurrentPace.setText(String.valueOf(dailyPace) + " M/S" + " \nCalories " + Total_Calories);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            PrefManager.setID(PrefManager.USER_ID, null);
            goToDispatchScreen();
            return true;
        } else if (id == R.id.action_cancel_notification) {
            cancelNotification();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.walkBtn)
    public void goToWalkEvent(Button button) {
        startActivity(Helper.getIntent(this, WalkActivity.class));
    }

    private void showAchieveMilestone(float distanceCovered) {
        int numberOfMilestones = Helper.getNumberOfMilestones(distanceCovered);
        if (numberOfMilestones > 0) {
            String title = getString(R.string.achievement_title);
            String message = String.format(getString(R.string.achievement_message), numberOfMilestones);
            Helper.displayMessageToUser(this, title, message).show();
        }
    }

    private void goToDispatchScreen() {
        startActivity(Helper.getIntent(this, DispatchActivity.class));
    }

    // Assume user controls the periodic reminder: no reminder at office if user turn off notification
    private void scheduleNotification() {

        Notification notification = createNotification(getString(R.string.app_name), getString(R.string.notification_message));
        Intent notificationIntent = getIntent(notification);
        PendingIntent pendingIntent = getBroadcast(notificationIntent);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 20);

        AlarmManager alarmManager = getSystemService();

        // Reminder every 1 hour
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
    }

    private AlarmManager getSystemService() {
        return (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent getBroadcast(Intent notificationIntent) {
        return PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @NonNull
    private Intent getIntent(Notification notification) {
        Intent notificationIntent = new Intent(this, NotificationBroadcaster.class);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION_KEY, notification);
        return notificationIntent;
    }

    private Notification createNotification(String title, String message) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent resultIntent = new Intent(this, DispatchActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        return builder.build();
    }

    private void cancelNotification() {
        Intent notificationIntent = new Intent(this, NotificationBroadcaster.class);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION_ID, 1);
        PendingIntent pendingIntent = getBroadcast(notificationIntent);
        AlarmManager alarmManager = getSystemService();
        alarmManager.cancel(pendingIntent);
    }
}
