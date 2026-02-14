package ique.listviewchartitems;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment;
import com.github.mikephil.charting.components.Legend.LegendOrientation;
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import ique.daitechagent.R;

public class PieChartItem extends ChartItem {
    private final SpannableString mCenterText = generateCenterText();
    private final Typeface mTf;

    private static class ViewHolder {
        PieChart chart;

        private ViewHolder() {
        }
    }

    public PieChartItem(ChartData<?> cd, Context c) {
        super(cd);
        this.mTf = Typeface.createFromAsset(c.getAssets(), "fonts/OpenSans-Regular.ttf");
    }

    public int getItemType() {
        return 2;
    }

    public View getView(int position, View convertView, Context c) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(c).inflate(R.layout.list_item_piechart, null);
            holder.chart = convertView.findViewById(R.id.chart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setHoleRadius(52.0f);
        holder.chart.setTransparentCircleRadius(57.0f);
        holder.chart.setCenterText(this.mCenterText);
        holder.chart.setCenterTextTypeface(this.mTf);
        holder.chart.setCenterTextSize(9.0f);
        holder.chart.setUsePercentValues(true);
        holder.chart.setExtraOffsets(5.0f, 10.0f, 50.0f, 10.0f);
        this.mChartData.setValueFormatter(new PercentFormatter());
        this.mChartData.setValueTypeface(this.mTf);
        this.mChartData.setValueTextSize(11.0f);
        this.mChartData.setValueTextColor(-1);
        holder.chart.setData((PieData) this.mChartData);
        Legend l = holder.chart.getLegend();
        l.setVerticalAlignment(LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(LegendHorizontalAlignment.RIGHT);
        l.setOrientation(LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYEntrySpace(0.0f);
        l.setYOffset(0.0f);
        holder.chart.animateY(900);
        return convertView;
    }

    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("MPAndroidChart\ncreated by\nPhilipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.6f), 0, 14, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.VORDIPLOM_COLORS[0]), 0, 14, 0);
        s.setSpan(new RelativeSizeSpan(0.9f), 14, 25, 0);
        s.setSpan(new ForegroundColorSpan(-7829368), 14, 25, 0);
        s.setSpan(new RelativeSizeSpan(1.4f), 25, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 25, s.length(), 0);
        return s;
    }
}
