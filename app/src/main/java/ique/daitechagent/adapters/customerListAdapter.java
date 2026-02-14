package ique.daitechagent.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import ique.daitechagent.R;
import ique.daitechagent.activities.MapsCustomerLocation;
import ique.daitechagent.common.ClickListener;
import ique.daitechagent.model.Customer;
import ique.daitechagent.parcels.MapCustomerDetailsEnvelope;

//import com.iramml.uberclone.Model.History;
//import com.iramml.uberclone.R;

public class customerListAdapter extends RecyclerView.Adapter<customerListAdapter.ViewHolder> {
    Context context;
    public ArrayList<Customer> items;
    public ArrayList<Customer> arrayList; //used for the search bar
    ClickListener listener;
    ViewHolder viewHolder;

    public customerListAdapter(Context context, ArrayList<Customer> items, ClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;

        // Search capabilities
        arrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_list_card_template, viewGroup, false);
        viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvCustomerListName.setText(items.get(i).getCustomername());
        viewHolder.tvCustomerListNumber.setText(items.get(i).getCustomernumber());
        viewHolder.tvCustomerListAddress.setText(items.get(i).getAddress());
        viewHolder.tvCustomerReportCount.setText(String.valueOf(items.get(i).getReportcount()));
        viewHolder.tvCustomerRating.setText(String.valueOf(items.get(i).getCustomerrating()));

        if (items.get(i).getPhoneno() != null) {
            if (items.get(i).getPhoneno().trim().equals("") || items.get(i).getPhoneno().trim().equals("null")
                    || items.get(i).getPhoneno().equalsIgnoreCase("null") || items.get(i).getPhoneno() == null) {
                viewHolder.customerMakeCall.setVisibility(View.GONE);
                viewHolder.customerMakeCallLabel.setVisibility(View.GONE);
            } else {
                viewHolder.customerMakeCall.setVisibility(View.VISIBLE);
                viewHolder.customerMakeCallLabel.setVisibility(View.VISIBLE);
            }
        }else{
            viewHolder.customerMakeCall.setVisibility(View.GONE);
            viewHolder.customerMakeCallLabel.setVisibility(View.GONE);
        }

        if (items.get(i).getEmail() != null) {
            if (items.get(i).getEmail().trim().equals("") || Objects.equals(items.get(i).getEmail(), "null")
                    || items.get(i).getEmail().equalsIgnoreCase("null") || items.get(i).getEmail() == null) {
                viewHolder.customerSendEmail.setVisibility(View.GONE);
                viewHolder.customerSendEmailLabel.setVisibility(View.GONE);
            } else {
                viewHolder.customerSendEmail.setVisibility(View.VISIBLE);
                viewHolder.customerSendEmailLabel.setVisibility(View.VISIBLE);
            }
        }else{
            viewHolder.customerSendEmail.setVisibility(View.GONE);
            viewHolder.customerSendEmailLabel.setVisibility(View.GONE);
        }


        if (items.get(i).getLat() == 0.0 && items.get(i).getLng() == 0.0) {
            viewHolder.locationPresent.setVisibility(View.GONE);
            viewHolder.locationPresentLabel.setVisibility(View.GONE);
        } else {
            viewHolder.locationPresent.setVisibility(View.VISIBLE);
            viewHolder.locationPresentLabel.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCustomerListName, tvCustomerListNumber, tvCustomerListAddress, tvCustomerReportCount, tvCustomerRating;
        ImageView customerMakeCall, customerSendEmail, locationPresent;
        TextView customerMakeCallLabel, customerSendEmailLabel, locationPresentLabel;

        ClickListener listener;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            tvCustomerListName = itemView.findViewById(R.id.customerlistname);
            tvCustomerListNumber = itemView.findViewById(R.id.customerlistnumber);
            tvCustomerListAddress = itemView.findViewById(R.id.customerlistaddress);
            tvCustomerReportCount = itemView.findViewById(R.id.reportcount);
            tvCustomerRating = itemView.findViewById(R.id.customerrating);

            customerMakeCall = itemView.findViewById(R.id.makecall);
            customerSendEmail = itemView.findViewById(R.id.sendEmail);

            customerMakeCallLabel = itemView.findViewById(R.id.makecalllabel);
            customerSendEmailLabel = itemView.findViewById(R.id.sendEmaillabel);

            locationPresent = itemView.findViewById(R.id.locationPresent);
            locationPresentLabel = itemView.findViewById(R.id.locationPresentLabel);



            customerMakeCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeCall();
                }
            });

            customerMakeCallLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeCall();
                }
            });

            customerSendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail();
                }
            });

            customerSendEmailLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail();
                }
            });

            locationPresent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    Customer customer = items.get(pos);
                    showCustomerOnMap(customer);

                }
            });

            locationPresentLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    Customer customer = items.get(pos);
                    showCustomerOnMap(customer);

                }
            });


            this.listener=listener;
            itemView.setOnClickListener(this);
        }

        private void makeCall(){
            Intent intent = new Intent(Intent.ACTION_CALL);
            int pos = getAdapterPosition();
            String tel = items.get(pos).getPhoneno();
            intent.setData(Uri.parse("tel:" + tel));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            context.startActivity(intent);
        }

        private void sendEmail(){

            int pos = getAdapterPosition();
            String toEmailAdd = items.get(pos).getEmail();

            Intent emailIntent = new Intent(
                    Intent.ACTION_SEND);
            emailIntent.setAction(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[] { toEmailAdd });
            emailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
            emailIntent.putExtra(android.content.Intent.EXTRA_BCC, "");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Enquiries");
            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("Hello Sir/Madam"));
            emailIntent.setType("text/html");
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // FOLLOWING STATEMENT CHECKS WHETHER THERE IS ANY APP THAT CAN HANDLE OUR EMAIL INTENT
            context.startActivity(Intent.createChooser(emailIntent,
                    "Send Email Using: ").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        private void showCustomerOnMap(Customer customer){

            MapCustomerDetailsEnvelope mapCustomerDetailsEnvelope = new MapCustomerDetailsEnvelope(customer.getCustomername(),
                    customer.getCustomernumber(),
                    customer.getAddress(),
                    customer.getLng(),
                    customer.getLat());

            Intent intent = new Intent(context, MapsCustomerLocation.class);
            intent.putExtra(MapsCustomerLocation.MAP_PLOT_ENVELOPE, mapCustomerDetailsEnvelope);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
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
            for(Customer customer: arrayList){
                if(customer.getCustomername().toLowerCase(Locale.getDefault()).contains(characterText)
                        || customer.getCustomernumber().toLowerCase(Locale.getDefault()).contains(characterText)
                        || customer.getAddress().toLowerCase(Locale.getDefault()).contains(characterText)){

                    items.add(customer);
                }
            }
        }
        notifyDataSetChanged();
    }
}
