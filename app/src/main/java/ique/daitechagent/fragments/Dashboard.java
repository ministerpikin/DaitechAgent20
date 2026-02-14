package ique.daitechagent.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ique.daitechagent.R;
import ique.daitechagent.adapters.ChartDataAdapter;
import ique.daitechagent.common.Common;
import ique.daitechagent.model.User;
import ique.daitechagent.model.retrofitgraphs.PerformanceIndex;
import ique.daitechagent.model.retrofitgraphs.Revenue;
import ique.daitechagent.model.retrofitgraphs.Sales;
import ique.daitechagent.model.retrofitgraphs.SalesPie;
import ique.daitechagent.retrofit.GraphClient;
import ique.daitechagent.retrofit.IRetrofitGraphs;
import ique.daitechagent.utils.DateUtils;
import ique.listviewchartitems.BarChartItem;
import ique.listviewchartitems.ChartItem;
import ique.listviewchartitems.LineChartItem;
import ique.listviewchartitems.PieChartItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Dashboard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Dashboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboard extends Fragment {

    private static final String TAG = "Dashboard";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_UUID = "uuid";

    // TODO: Rename and change types of parameters
    private User user;
    private String uuid;

    private OnFragmentInteractionListener mListener;

    ListView rvChartItem;
    RecyclerView.LayoutManager layoutManager;
    ChartDataAdapter adapter;
    ArrayList<ChartItem> list;
    ArrayList<Integer> lineColors = new ArrayList<>();

    private AppBarLayout viewContactsBar, searchBar;
    private EditText mSearchContacts;
    private TextView mSearchToolbarHeading;
    private ImageView mAddCustomerIcon;

    public Dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @param uuid Parameter 2.
     * @return A new instance of fragment Dashboard.
     */
    // TODO: Rename and change types and number of parameters
    public static Dashboard newInstance(User user, String uuid) {
        Dashboard fragment = new Dashboard();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        args.putString(ARG_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User)getArguments().getSerializable(ARG_USER);
            uuid = getArguments().getString(ARG_UUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;

        // Inflate fragment so we can use it
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        viewContactsBar = ((AppCompatActivity)getActivity()).findViewById(R.id.viewContactsToolbar);//.getSupportActionBar();
        //viewContactsBar = (AppBarLayout) container.getChildAt(0).findViewById(R.id.viewContactsToolbar);
        //container.getParent()
        searchBar = ((AppCompatActivity)getActivity()).findViewById(R.id.searchToolbar);
        //contactsList = (ListView) fragment_reportlist.findViewById(R.id.contactsList);
        mSearchContacts = getActivity().findViewById(R.id.etSearchContacts);

        mSearchToolbarHeading = getActivity().findViewById(R.id.toolbarHeading);

        mSearchToolbarHeading.setText("Performance Analytics");

        mAddCustomerIcon = getActivity().findViewById(R.id.ivAddCustomerIcon);
        mAddCustomerIcon.setVisibility(View.GONE);

        // Set up the RecyclerView so we can use it also
        initRecyclerView(view);

        list = new ArrayList<>();

        adapter = new ChartDataAdapter(getContext(), list);
        rvChartItem.setAdapter(adapter);

        initLineColors();

        if (Common.isNetworkAvailable(TAG,getContext())) {
            getGraphData(view);
        }else{
            Toast.makeText(getContext(), "Network is unvailable. To view graphs turn on your data.", Toast.LENGTH_SHORT);
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initRecyclerView(View view){

        rvChartItem =  view.findViewById(R.id.listGraphView);
        // rvChartItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        //layoutManager..setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);

        //RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        //itemAnimator.setAddDuration(1000);
        //itemAnimator.setMoveDuration(1000);
        //itemAnimator.setRemoveDuration(1000);


        //rvChartItem.setLayoutManager(layoutManager);
        //rvChartItem.setItemAnimator(new DefaultItemAnimator());
        //rvChartItem.setItemAnimator(itemAnimator);
        //rvChartItem.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));



    }

    private void initLineColors(){

        //int endColor5 = ContextCompat.getColor(getApplicationContext(), android.R.color.holo_orange_dark);
        int endColor5 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_dark);
        lineColors.add(endColor5);

        int startColor2 = ContextCompat.getColor(getContext(), android.R.color.holo_blue_light);
        lineColors.add(startColor2);

        int startColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_green_light);
        lineColors.add(startColor4);

        int startColor5 = ContextCompat.getColor(getContext(), android.R.color.holo_red_light);
        lineColors.add(startColor5);

        int endColor4 = ContextCompat.getColor(getContext(), android.R.color.holo_red_dark);
        lineColors.add(endColor4);


        int endColor2 = ContextCompat.getColor(getContext(), android.R.color.holo_purple);
        lineColors.add(endColor2);

        int endColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_green_dark);
        lineColors.add(endColor3);

        int startColor3 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
        lineColors.add(startColor3);

        int endColor1 = ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark);
        lineColors.add(endColor1);

        int startColor1 = ContextCompat.getColor(getContext(), android.R.color.holo_orange_light);
        lineColors.add(startColor1);

    }
    private void getGraphData(final View view){

        //String email = "elemoshogbemi@daitechpharma.com";
        String email = user.getEmail();
        //String email = "daitechagent@daitechpharma.com";
        IRetrofitGraphs serviceSales = GraphClient.getRetrofitGraphInstance().create(IRetrofitGraphs.class);

        Call<List<Sales>> call = serviceSales.getSales(email,"all", DateUtils.getCurrentYear(),"months");
        call.enqueue(new Callback<List<Sales>>() {
            @Override
            public void onResponse(Call<List<Sales>> call, Response<List<Sales>> response) {

                try{

                    List<Sales> sales = response.body();
                    setupLineGraph(sales);
                    adapter.notifyDataSetChanged();

                } catch (Exception ex){
                    Log.v(TAG, ex.getMessage());
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<List<Sales>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

        Call<PerformanceIndex> performanceIndexCall = serviceSales.getPerformanceIndex(email);
        performanceIndexCall.enqueue(new Callback<PerformanceIndex>() {
            @Override
            public void onResponse(Call<PerformanceIndex> call, Response<PerformanceIndex> response) {
                PerformanceIndex performanceIndex = response.body();
                updatePerformaceIndex(view, performanceIndex);
            }

            @Override
            public void onFailure(Call<PerformanceIndex> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

        Call <List<Revenue>> revenueCall = serviceSales.getRevenue(email, DateUtils.getCurrentYear(),"months");
        revenueCall.enqueue(new Callback<List<Revenue>>() {
            @Override
            public void onResponse(Call<List<Revenue>> call, Response<List<Revenue>> response) {
                List<Revenue> revenueList = response.body();
                setupRevenueBarGraph(revenueList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Revenue>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

        Call <List<SalesPie>> salesPieCall = serviceSales.getSalesPie(email,"all", DateUtils.getCurrentYear(),"months" );
        salesPieCall.enqueue(new Callback<List<SalesPie>>() {
            @Override
            public void onResponse(Call<List<SalesPie>> call, Response<List<SalesPie>> response) {
                List<SalesPie> salesPies = response.body();
                setupStockSalesPieChart(salesPies);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<SalesPie>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });


    }

    private void updatePerformaceIndex(View view, PerformanceIndex performanceIndex){
        TextView pi = view.findViewById(R.id.txtPerformanceIndex);
        pi.setText(performanceIndex.getPi());
    }

    private void setupLineGraph(List<Sales> sales){
        LineData data = generateSalesDataList(sales);
        //LineChartItem lineChartItem = new LineChartItem(data, getApplicationContext());
        //lineChartItem.get
        list.add(new LineChartItem(data, getContext()));// getApplicationContext()));
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Line data
     */
    private LineData generateSalesDataList(List<Sales> sales) {

        ArrayList<Entry> salesValues = new ArrayList<>();
        String series;
        String seriesMarker = "";
        boolean bfirstTime = true;
        LineDataSet dsLiine;
        ArrayList<ILineDataSet> ardsLiine = new ArrayList<>();
        int i = 0;

        Iterator<Sales> salesIterator = sales.iterator();
        while (salesIterator.hasNext()) {
            Sales salesData = salesIterator.next();
            series = salesData.getProduct();
            series = series.substring(0, series.indexOf("(") - 1);

            if (!series.equalsIgnoreCase(seriesMarker)){
                if (bfirstTime){
                    bfirstTime = false;

                    // Lets fill in the data for the series
                    // This is the first value of the new product
                    salesValues.add(new Entry(salesData.getMonthindex() ,  salesData.getQuantity()));
                    seriesMarker = series;
                }else{
                    // Lets fill in the data for the series
                    // Here, products have changed and the first value for the
                    // new product
                    // We reset the counter (NO NEED)
                    ArrayList<Entry> salesValuesOld = new ArrayList<>();
                    salesValuesOld = (ArrayList<Entry>) salesValues.clone();

                    dsLiine = new LineDataSet(salesValuesOld, seriesMarker);
                    dsLiine.setLineWidth(2.5f);
                    dsLiine.setCircleRadius(4.5f);
                    //dsLiine.setHighLightColor(Color.rgb(Common.randInt(0,255), Common.randInt(0,255), Common.randInt(0,255)));
                    //dsLiine.setHighLightColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_orange_light));
                    dsLiine.setColor(lineColors.get(i++));
                    dsLiine.setDrawValues(false);
                    ardsLiine.add(dsLiine);

                    salesValues.clear();
                    salesValues.add(new Entry(salesData.getMonthindex() ,  salesData.getQuantity()));
                    seriesMarker = series;
                }

            }else{
                // Lets fill in the data for the series
                salesValues.add(new Entry(salesData.getMonthindex() ,  salesData.getQuantity()));
            }
        }

        return new LineData(ardsLiine);
    }

    private void setupRevenueBarGraph(List<Revenue> revenueList){
        BarData data = generateRevenueDataBar(revenueList);

        list.add(new BarChartItem(data, getContext()));
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private BarData generateRevenueDataBar(List<Revenue> revenueList) {

        ArrayList<BarEntry> reveneEntries = new ArrayList<>();

        for (Revenue item : revenueList) {
            reveneEntries.add(new BarEntry(item.getMonthindex(), item.getPayment()));
        }

        BarDataSet dsBar = new BarDataSet(reveneEntries, "Revenue for year " + revenueList.get(0).getSalesyear());



        dsBar.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dsBar.setHighLightAlpha(255);

        BarData cd = new BarData(dsBar);
        cd.setBarWidth(0.9f);
        return cd;
    }

    private void setupStockSalesPieChart(List<SalesPie> salesPies){
        PieData data = generateStockSalesDataPie(salesPies);

        list.add(new PieChartItem(data, getContext()));
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Pie data
     */
    private PieData generateStockSalesDataPie(List<SalesPie> salesPies) {

        ArrayList<PieEntry> stockSalesEntries = new ArrayList<>();

        for (SalesPie item : salesPies) {
            String series = item.getStockname();
            series = series.substring(0, series.indexOf("(") - 1);
            stockSalesEntries.add(new PieEntry(item.getQuantity(),series ));
        }


        PieDataSet pieDataSet = new PieDataSet(stockSalesEntries, "Sales for the year " + DateUtils.getCurrentYear());

        // space between slices
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        return new PieData(pieDataSet);
    }
}
