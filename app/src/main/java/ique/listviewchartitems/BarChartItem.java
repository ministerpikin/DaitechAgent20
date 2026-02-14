package ique.listviewchartitems;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import ique.daitechagent.R;

public class BarChartItem extends ChartItem {
  private final Typeface mTf;

  private static class ViewHolder {
    BarChart chart;

    private ViewHolder() {
    }
  }

  public BarChartItem(ChartData<?> cd, Context c) {
    super(cd);
    this.mTf = Typeface.createFromAsset(c.getAssets(), "fonts/OpenSans-Regular.ttf");
  }

  public int getItemType() {
    return 0;
  }

  public View getView(int position, View convertView, Context c) {
    ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = LayoutInflater.from(c).inflate(R.layout.list_item_barchart, null);
      holder.chart = convertView.findViewById(R.id.chart);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    holder.chart.getDescription().setEnabled(false);
    holder.chart.setDrawGridBackground(false);
    holder.chart.setDrawBarShadow(false);
    XAxis xAxis = holder.chart.getXAxis();
    xAxis.setPosition(XAxisPosition.BOTTOM);
    xAxis.setTypeface(this.mTf);
    xAxis.setDrawGridLines(false);
    xAxis.setDrawAxisLine(true);
    YAxis leftAxis = holder.chart.getAxisLeft();
    leftAxis.setTypeface(this.mTf);
    leftAxis.setLabelCount(5, false);
    leftAxis.setSpaceTop(20.0f);
    leftAxis.setAxisMinimum(0.0f);
    YAxis rightAxis = holder.chart.getAxisRight();
    rightAxis.setTypeface(this.mTf);
    rightAxis.setLabelCount(5, false);
    rightAxis.setSpaceTop(20.0f);
    rightAxis.setAxisMinimum(0.0f);
    this.mChartData.setValueTypeface(this.mTf);
    holder.chart.setData((BarData) this.mChartData);
    holder.chart.setFitBars(true);
    holder.chart.animateY(700);
    return convertView;
  }
}
