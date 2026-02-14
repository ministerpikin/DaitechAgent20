package ique.daitechagent.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ique.daitechagent.R;
import ique.daitechagent.model.BarcodeSscanning;
import ique.daitechagent.model.Scannedproduct;
import java.util.ArrayList;
import java.util.Iterator;

public class scannedBarcodeHistoryAdapter extends RecyclerView.Adapter<scannedBarcodeHistoryAdapter.ViewHolder> {
    Context context;
    public ArrayList<BarcodeSscanning> items;
    ViewHolder viewHolder;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView scannedbarcode;

        public ViewHolder(View itemView) {
            super(itemView);
            this.scannedbarcode = itemView.findViewById(R.id.scannedbarcode);
        }
    }

    public scannedBarcodeHistoryAdapter(Context context2, ArrayList<BarcodeSscanning> items2) {
        this.context = context2;
        this.items = items2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this.viewHolder = new ViewHolder(LayoutInflater.from(this.context).inflate(R.layout.scanned_barcode_list_card_template, viewGroup, false));
        return this.viewHolder;
    }

    public void onBindViewHolder(ViewHolder viewHolder2, int i) {
        String barcodes = "";
        Iterator itr = this.items.get(i).getScannedproducts().iterator();
        while (itr.hasNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append(barcodes);
            sb.append(((Scannedproduct) itr.next()).getBarcode());
            sb.append("\n");
            barcodes = sb.toString();
        }
        viewHolder2.scannedbarcode.setText(barcodes);
    }

    public int getItemCount() {
        return this.items.size();
    }
}
