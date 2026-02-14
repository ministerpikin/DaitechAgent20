package ique.daitechagent.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ique.daitechagent.R;
import ique.daitechagent.common.Common;
import ique.daitechagent.model.ReportComment;
import ique.daitechagent.utils.DateUtils;
import ique.daitechagent.utils.ImageUtils;
import java.util.ArrayList;

public class ReportCommentListAdapter extends Adapter {
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private Context mContext;
    private ArrayList<ReportComment> mReportCommentList;

    private class ReceivedReportCommentHolder extends ViewHolder {
        TextView messageText;
        TextView nameText;
        ImageView profileImage;
        TextView timeText;

        public ReceivedReportCommentHolder(View itemView) {
            super(itemView);
            this.messageText = itemView.findViewById(R.id.text_message_body);
            this.timeText = itemView.findViewById(R.id.text_message_time);
            this.nameText = itemView.findViewById(R.id.text_message_name);
            this.profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        /* access modifiers changed from: 0000 */
        public void bind(ReportComment message) {
            this.messageText.setText(message.getMessage());
            this.timeText.setText(DateUtils.formatDateTime(message.getCreatedAt()));
            this.nameText.setText(message.getSender().getUsername());
            ImageUtils.displayRoundImageFromUrl(ReportCommentListAdapter.this.mContext, message.getSender().getAvatarUrl(), this.profileImage);
        }
    }

    private class SentReportCommentHolder extends ViewHolder {
        TextView messageText;
        TextView timeText;

        public SentReportCommentHolder(View itemView) {
            super(itemView);
            this.messageText = itemView.findViewById(R.id.text_message_body);
            this.timeText = itemView.findViewById(R.id.text_message_time);
        }

        /* access modifiers changed from: 0000 */
        public void bind(ReportComment message) {
            this.messageText.setText(message.getMessage());
            this.timeText.setText(DateUtils.formatDateTime(message.getCreatedAt()));
        }
    }

    public ReportCommentListAdapter(Context context, ArrayList<ReportComment> messageList) {
        this.mContext = context;
        this.mReportCommentList = messageList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_MESSAGE_SENT) {
            return new SentReportCommentHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_sent, viewGroup, false));
        }
        if (i == VIEW_TYPE_MESSAGE_RECEIVED) {
            return new ReceivedReportCommentHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_received, viewGroup, false));
        }
        return null;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ReportComment message = this.mReportCommentList.get(i);
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == VIEW_TYPE_MESSAGE_SENT) {
            ((SentReportCommentHolder) viewHolder).bind(message);
        } else if (itemViewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            ((ReceivedReportCommentHolder) viewHolder).bind(message);
        }
    }

    public int getItemCount() {
        return this.mReportCommentList.size();
    }

    public int getItemViewType(int position) {
        ReportComment message = this.mReportCommentList.get(position);
        if (message.getSender() == null || message.getSender().getUserID().equals(Common.userID)) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        return VIEW_TYPE_MESSAGE_RECEIVED;
    }
}
