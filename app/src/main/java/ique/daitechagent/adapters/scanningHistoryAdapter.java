package ique.daitechagent.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import ique.daitechagent.R;
import ique.daitechagent.common.ClickListener;
import ique.daitechagent.model.BarcodeSscanning;
import ique.daitechagent.model.Scannedproduct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class scanningHistoryAdapter extends RecyclerView.Adapter<scanningHistoryAdapter.ViewHolder> {
    public ArrayList<BarcodeSscanning> arrayList = new ArrayList<>();
    Context context;
    public ArrayList<BarcodeSscanning> items;
    ClickListener listener;
    ViewHolder viewHolder;

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ClickListener listener;
        TextView scancount;
        TextView scancustomername;
        TextView scanedate;
        TextView scanproductcount;

        public ViewHolder(View itemView, ClickListener listener2) {
            super(itemView);
            this.scancustomername = itemView.findViewById(R.id.scancustomername);
            this.scanedate = itemView.findViewById(R.id.scandate);
            this.scancount = itemView.findViewById(R.id.scancount);
            this.scanproductcount = itemView.findViewById(R.id.scanproductcount);
            this.listener = listener2;
            itemView.setOnClickListener(this);
        }

        public void onClick(View view) {
            this.listener.onClick(view, getAdapterPosition());
        }
    }

    public scanningHistoryAdapter(Context context2, ArrayList<BarcodeSscanning> items2, ClickListener listener2) {
        this.context = context2;
        this.items = items2;
        this.listener = listener2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.viewHolder = new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.scanning_list_card_template, viewGroup, false), this.listener);
        return this.viewHolder;
    }

    public void onBindViewHolder(ViewHolder viewHolder2, int i) {
        int productquantity = 0;
        viewHolder2.scancustomername.setText(this.items.get(i).getCustomername());
        viewHolder2.scanedate.setText(this.items.get(i).getLongDate());
        viewHolder2.scancount.setText(String.valueOf(this.items.get(i).getScannedproducts().size()));
        Iterator itr = this.items.get(i).getScannedproducts().iterator();
        while (itr.hasNext()) {
            productquantity += ((Scannedproduct) itr.next()).getQuantity();
        }
        TextView textView = viewHolder2.scanproductcount;
        StringBuilder sb = new StringBuilder();
        sb.append("Total Quantity: ");
        sb.append(productquantity);
        textView.setText(sb.toString());
    }

    public int getItemCount() {
        return this.items.size();
    }

    public void filter(String characterText) {
        String characterText2 = characterText.toLowerCase(Locale.getDefault());
        this.items.clear();
        if (characterText2.length() == 0) {
            this.items.addAll(this.arrayList);
        } else {
            this.items.clear();
            Iterator it = this.arrayList.iterator();
            while (it.hasNext()) {
                BarcodeSscanning scanHistory = (BarcodeSscanning) it.next();
                if (scanHistory.getCustomername().toLowerCase(Locale.getDefault()).contains(characterText2) || scanHistory.getScandate().toLowerCase(Locale.getDefault()).contains(characterText2)) {
                    this.items.add(scanHistory);
                }
            }
        }
        notifyDataSetChanged();
    }
}
