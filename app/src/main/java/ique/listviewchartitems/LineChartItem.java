package ique.listviewchartitems;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import ique.daitechagent.R;

public class LineChartItem extends ChartItem {
    private final Typeface mTf;

    private static class ViewHolder {
        LineChart chart;

        private ViewHolder() {
        }
    }

    public LineChartItem(ChartData<?> cd, Context c) {
        super(cd);
        this.mTf = Typeface.createFromAsset(c.getAssets(), "fonts/OpenSans-Regular.ttf");
    }

    public int getItemType() {
        return 1;
    }

    public View getView(int position, View convertView, Context c) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.list_item_linechart, null);
            holder.chart = convertView.findViewById(R.id.chart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setDrawGridBackground(false);
        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(this.mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setTypeface(this.mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0.0f);
        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setTypeface(this.mTf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0.0f);
        holder.chart.setData((LineData) this.mChartData);
        holder.chart.animateX(750);
        return convertView;
    }
}
