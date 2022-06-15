package hq.remview.ui.main.revenue.statistic;

import hq.remview.R;
import hq.remview.BR;
import hq.remview.data.model.api.response.top.TopSaleResponse;
import hq.remview.databinding.ActivityStatisticBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class StatisticActivity extends BaseActivity<ActivityStatisticBinding,StatisticViewModel> {

    private BarChart chart;
    public static List<TopSaleResponse.TopSaleItem> topSaleItems;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        topSaleItems = null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_statistic;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chart = viewBinding.chart1;

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

//        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        xAxis.setTextColor(getColor(R.color.white));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);

//        ValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        leftAxis.setTextColor(getColor(R.color.white));
        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        setData();
        chart.invalidate();
    }
    private void setData() {


        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < topSaleItems.size(); i++) {
             values.add(new BarEntry(i, topSaleItems.get(i).getValue(),topSaleItems.get(i).getName()));
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, null);
            set1.setDrawIcons(false);
            set1.setColor(getColor(R.color.bar));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            data.setValueTextColor(getColor(R.color.white));
            data.setBarWidth(0.5f);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int)value);
                }
            });
            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return topSaleItems.get((int)value).getName();
                }

                @Override
                public String getBarLabel(BarEntry barEntry) {
                    return barEntry.getData().toString();
                }
            });
            chart.getXAxis().setLabelCount(3);
            chart.getXAxis().setGranularityEnabled(true);
            chart.getXAxis().setGranularity(1f);

            chart.zoom(2.321f,0,0,0);
            chart.setData(data);
        }
    }

}
