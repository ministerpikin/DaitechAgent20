package ique.daitechagent.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

//import com.iramml.uberclone.Model.History;
//import com.iramml.uberclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ique.daitechagent.R;
import java.util.ArrayList;
import java.util.Locale;

import ique.daitechagent.common.ClickListener;
import ique.daitechagent.common.Common;
import ique.daitechagent.common.OnCommentClickListener;
import ique.daitechagent.model.Report;

public class rptHistoryAdapter extends RecyclerView.Adapter<rptHistoryAdapter.ViewHolder>{
    Context context;
    private ArrayList<Report> items;
    public ArrayList<Report> arrayList; //used for the search bar
    ClickListener listener;
    ViewHolder viewHolder;

    OnCommentClickListener commentlistener;
    DatabaseReference drefNewComments, drefLastLoggedIn;
    DatabaseReference drefReportCommentNotification;
    String userID;

    public rptHistoryAdapter(Context context, ArrayList<Report> items, ClickListener listener, OnCommentClickListener commentlistener ){
        this.context=context;
        this.items=items;
        this.listener=listener;

        this.commentlistener = commentlistener;
        // Search capabilities
        arrayList = new ArrayList<>();
        //this.arrayList.addAll(items);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.report_history_template,viewGroup,false);
        //View view= LayoutInflater.from(context).inflate(R.layout.layout_report_history_template,viewGroup,false);
        //this.arrayList.clear();
        //this.arrayList.addAll(items);
        drefLastLoggedIn = FirebaseDatabase.getInstance().getReference(Common.USER_LAST_LOGGED_IN_NODE);
        drefNewComments = FirebaseDatabase.getInstance().getReference(Common.REPORT_COMMENT_NODE);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        drefReportCommentNotification = FirebaseDatabase.getInstance().getReference(Common.REPORT_COMMENT_NOTIFICATION_NODE);

        viewHolder = new ViewHolder(view, listener, commentlistener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.tvCustomerName.setText(items.get(i).getCustomerName());
        viewHolder.tvReportpDate.setText(items.get(i).getReportDate());
        viewHolder.tvReport.setText(items.get(i).getCustomerReport());

        String reportID = items.get(i).getReportId();

        drefReportCommentNotification
                .child(Common.REPORT_COMMENT_NOTIFICATION_AGENT_NODE)
                .child(reportID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long reportCount = dataSnapshot.getChildrenCount();

                        if (reportCount > 0){
                            viewHolder.newcomment.setVisibility(View.VISIBLE);
                        }else{
                            viewHolder.newcomment.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        /*
        drefLastLoggedIn.child(userID)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            Long lLastLogIn = postSnapshot.getValue(Long.class);
                            Query query = drefNewComments.child(items.get(i).getReportId()).orderByChild("createdAt").startAt(lLastLogIn);
                            query.addValueEventListener(new ValueEventListener() {

                                long reportCount;

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        reportCount = dataSnapshot.getChildrenCount();
                                        if (reportCount > 0){
                                            viewHolder.newcomment.setVisibility(View.VISIBLE);
                                        }else{
                                            viewHolder.newcomment.setVisibility(View.GONE);
                                        }
                                    } else {
                                        reportCount = 0;
                                        viewHolder.newcomment.setVisibility(View.GONE);
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

         */



    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvCustomerName, tvReportpDate, tvReport;
        ClickListener listener;
        OnCommentClickListener commentlistener;
        ImageButton commentbutton, newcomment;

        public ViewHolder(View itemView, ClickListener listener, final OnCommentClickListener commentlistener) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.customerNameList);
            tvReportpDate = itemView.findViewById(R.id.reportDate);
            tvReport = itemView.findViewById(R.id.customerReportList);
            commentbutton = itemView.findViewById(R.id.makecomments);
            newcomment = itemView.findViewById(R.id.newcomments);

            this.listener = listener;
            this.commentlistener = commentlistener;

            commentbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentlistener.onClick(v, getAdapterPosition());
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.listener.onClick(view, getAdapterPosition());
        }
    }

    //Filter class
    public void filter(String characterText){

        characterText = characterText.toLowerCase(Locale.getDefault());

        items.clear();
        if( characterText.length() == 0){
            items.addAll(arrayList);
        }
        else{
            items.clear();
            for(Report report: arrayList){
                if(report.getCustomerName().toLowerCase(Locale.getDefault()).contains(characterText)
                    || report.getCustomerReport().toLowerCase(Locale.getDefault()).contains(characterText)
                    || report.getReportDate().toLowerCase(Locale.getDefault()).contains(characterText)){

                    items.add(report);
                }
            }
        }
        notifyDataSetChanged();
    }
}
