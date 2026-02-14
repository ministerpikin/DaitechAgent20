package ique.daitechagent.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ique.daitechagent.R;
import ique.daitechagent.activities.ChatActivity;
import ique.daitechagent.common.Common;
import ique.daitechagent.fragments.ReportList;
import ique.daitechagent.model.Report;

import static ique.daitechagent.helpers.NotificationHelper.GROUP_KEY;

public class CommentService extends Service {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference drefReport;
    DatabaseReference drefReportComment;
    DatabaseReference drefReportCommentNotification;

    public CommentService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        //Firebase.setAndroidContext(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        final String email = mAuth.getCurrentUser().getEmail();
        database = FirebaseDatabase.getInstance();
        drefReport = database.getReference(Common.REPORT_NODE);
        //drefReportComment = database.getReference(Common.REPORT_COMMENT_NODE);
        drefReportCommentNotification = database.getReference(Common.REPORT_COMMENT_NOTIFICATION_NODE);

        drefReport
                .child(Common.userID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot reportSnapShot: dataSnapshot.getChildren()){
                    final Report report = reportSnapShot.getValue(Report.class);

                    drefReportCommentNotification
                            .child(Common.REPORT_COMMENT_NOTIFICATION_AGENT_NODE)
                            .child(report.getReportId())
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            long reportCount = dataSnapshot.getChildrenCount();

                            if (reportCount > 0){
                                sendNotification(reportCount, email, report.getReportId(),
                                        report.getOwnerid(), report.getCustomerName(),
                                        report.getCustomerReport(), "Manager/Supervisor");


                                drefReportCommentNotification
                                        .child(Common.REPORT_COMMENT_NOTIFICATION_AGENT_NODE)
                                        .child(report.getReportId()).removeValue();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FirebaseUser destroyAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (destroyAuth != null) {
            // User is signed in.
            sendBroadcast(new Intent("ique.daitechagent.service.restartservice"));
        }
    }

    private void notifyUser(long msgCount, String myEmail,
                            String reportid, String ownerid,
                            String customername, String customerreport,
                            String agentName) {

        String message = "You have "+msgCount+" new message from " + agentName;
        NotificationCompat.Builder not = new NotificationCompat.Builder(getApplicationContext(), "new_message_channel_id_agent");
        not.setAutoCancel(true);
        //not.setSmallIcon(R.mipmap.ic_launcher_round);
        not.setSmallIcon(R.drawable.ic_notification);
        not.setTicker("New Message");
        not.setWhen(System.currentTimeMillis());
        not.setContentText(message);

        Intent i;
        i = new Intent(getApplicationContext(), ChatActivity.class);
        not.setContentTitle("Report for " + customername);
        //i.putExtra("Sender", "Manager/Supervisor");
        //i.putExtra("Company", "Daitech Pharmaceuticals");
        i.putExtra(ReportList.COMMENT_REPORT_ID, reportid);
        i.putExtra(ReportList.COMMENT_USER_ID, ownerid);
        i.putExtra(ReportList.COMMENT_CUSTOMER, customername);
        i.putExtra(ReportList.COMMENT_REPORT_MSG, customerreport);

        int uniqueID = Common.createUniqueIdPerUser(myEmail);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueID, i, PendingIntent.FLAG_UPDATE_CURRENT);
        not.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )// Android.OS.BuildVersionCodes.O)
        {
            String channelId = "notification_channel_id_agent";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Daitech Messaging System",
                    NotificationManager.IMPORTANCE_DEFAULT);//Android.App.NotificationImportance.Default);
            nm.createNotificationChannel(channel);
            not.setChannelId(channelId);
        }

        not.setDefaults(Notification.DEFAULT_ALL);
        nm.notify(uniqueID, not.build());
    }

    private void sendNotification(long msgCount, String myEmail,
                                  String reportid, String ownerid,
                                  String customername, String customerreport,
                                  String agentName) {

        String message = "You have "+msgCount+" new message from " + agentName;

        Intent intent;
        intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(ReportList.COMMENT_REPORT_ID, reportid);
        intent.putExtra(ReportList.COMMENT_USER_ID, ownerid);
        intent.putExtra(ReportList.COMMENT_CUSTOMER, customername);
        intent.putExtra(ReportList.COMMENT_REPORT_MSG, customerreport);

        int uniqueID = Common.createUniqueIdPerUser(myEmail);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueID, intent, PendingIntent.FLAG_CANCEL_CURRENT); //FLAG_ONE_SHOT);//PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "new_message_channel_id");
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setContentTitle("Report for " + customername);
        mBuilder.setContentText(message);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE); //Important for heads-up notification
        //mBuilder.setDefaults(Notification.DEFAULT_ALL); //Important for heads-up notification
        mBuilder.setPriority(Notification.PRIORITY_MAX); //Important for heads-up notification
        //mBuilder.setPriority(Notification.PRIORITY_HIGH).setVibrate(new long[0]); //Important for heads-up notification

        mBuilder.setAutoCancel(true);
        mBuilder.setGroup(GROUP_KEY);


        int notifyId = (int) System.currentTimeMillis(); //For each push the older one will not be replaced for this unique id

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "Daitech Messaging System";
            String description = "Chat Messaging System between Agent and Manager/Supervisor";
            String channelId = "notification_channel_id";
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important   for heads-up notification
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);//getSystemService(NotificationManager.class);
            if (notificationManager != null) {

                notificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
                Notification buildNotification = mBuilder.build();
                notificationManager.notify(notifyId, buildNotification);
            }
        }else{

            NotificationManager mNotifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if (mNotifyMgr != null) {
                Notification buildNotification = mBuilder.build();
                mNotifyMgr.notify(notifyId, buildNotification);
            }
        }
    }

}
