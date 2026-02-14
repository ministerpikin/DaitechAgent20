package ique.daitechagent.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ique.daitechagent.R;
import ique.daitechagent.adapters.ReportCommentListAdapter;
import ique.daitechagent.common.Common;
import ique.daitechagent.fragments.ReportList;
import ique.daitechagent.model.ReportComment;
import ique.daitechagent.model.User;
import ique.daitechagent.utils.DateUtils;

public class ChatActivity extends AppCompatActivity {

    private EditText edittext_chatbox;
    private Button button_chatbox_send;
    private TextView msgTopic;
    private Toolbar toolbar;

    RecyclerView rvReportComments;
    RecyclerView.LayoutManager layoutManagerReportComments;
    ReportCommentListAdapter reportCommentListAdapter;
    ArrayList<ReportComment> reportCommentArrayList;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reportHistory;
    DatabaseReference drefReportComment;
    DatabaseReference drefReportCommentNotification;

    private String reportid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupComponents();

        reportid = getIntent().getStringExtra(ReportList.COMMENT_REPORT_ID);
        String ownwerid = getIntent().getStringExtra(ReportList.COMMENT_USER_ID);
        String customername = getIntent().getStringExtra(ReportList.COMMENT_CUSTOMER);
        String reportmsg = getIntent().getStringExtra(ReportList.COMMENT_REPORT_MSG);

        msgTopic.setText(reportmsg);
        getSupportActionBar().setTitle(customername);

        setupChatList(ownwerid,reportid);



    }

    private void setupComponents(){

        toolbar = (Toolbar) findViewById(R.id.toolbarChatActivity);
        setSupportActionBar(toolbar);

        edittext_chatbox = findViewById(R.id.edittext_chatbox);
        button_chatbox_send = findViewById(R.id.button_chatbox_send);
        rvReportComments =  findViewById(R.id.reyclerview_message_list);
        msgTopic = findViewById(R.id.msgTopic);

        button_chatbox_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    private void setupChatList(String ownerid, String reportId){

        rvReportComments.setHasFixedSize(true);
        layoutManagerReportComments = new LinearLayoutManager(getApplicationContext());

        rvReportComments.setLayoutManager(layoutManagerReportComments);
        rvReportComments.setItemAnimator(new DefaultItemAnimator());

        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        drefReportComment = database.getReference(Common.REPORT_COMMENT_NODE);
        drefReportCommentNotification = database.getReference(Common.REPORT_COMMENT_NOTIFICATION_NODE);
        reportCommentArrayList = new ArrayList<>();
        reportCommentListAdapter = new ReportCommentListAdapter(getApplicationContext(), reportCommentArrayList);

        rvReportComments.setAdapter(reportCommentListAdapter);
        getReportComments(ownerid, reportId);
        layoutManagerReportComments.scrollToPosition(reportCommentArrayList.size() - 1);
    }

    private void getReportComments(final String ownerid, final String reportid){


        drefReportComment.child(reportid)
                .orderByChild("createdAt")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reportCommentArrayList.clear();
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                            ReportComment comments = postSnapshot.getValue(ReportComment.class);
                            //comments.setSender(Common.currentUser);
                            reportCommentArrayList.add(comments);

                        }
                        reportCommentListAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sendComment(){

        String sComment = edittext_chatbox.getText().toString();
        if (sComment.trim().isEmpty()){
            Toast.makeText( getApplicationContext(), "No message sent", Toast.LENGTH_SHORT).show();
            return;
        }
        ReportComment rc = new ReportComment();
        rc.setMessage(sComment);
        rc.setCreatedAt(DateUtils.getTimeStamp());
        rc.setUserID(Common.userID);
        String key =  drefReportComment.child(reportid).push().getKey();
        rc.setKey(key);
        User user = Common.currentUser;
        user.setPassword("");
        user.setPhone("");
        rc.setSender(user);

        drefReportComment.child(reportid)
                .child(key)
                .setValue(rc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //waitingDialog.dismiss();
                        if(task.isSuccessful()) {
                            Toast.makeText( getApplicationContext(), "Comment Sent", Toast.LENGTH_SHORT).show();
                            edittext_chatbox.getText().clear();
                            layoutManagerReportComments.scrollToPosition(reportCommentArrayList.size() - 1);
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Comment sending failed",Toast.LENGTH_SHORT).show();

                    }
                });

        drefReportCommentNotification
                .child(Common.REPORT_COMMENT_NOTIFICATION_MANAGER_NODE)
                .child(reportid)
                .child(key)
                .setValue(rc);

        String userRegion = user.getAgentRegion()
                .replace(" ", "")
                .replace("[", "")
                .replace("]","");

        drefReportCommentNotification
                .child(Common.REPORT_COMMENT_NOTIFICATION_SUPERVISOR_NODE)
                .child(userRegion.toLowerCase())
                .child(reportid)
                .child(key)
                .setValue(rc);
    }
}
