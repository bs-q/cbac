package hq.remview.ui.main.revenue.statistic.pie;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import hq.remview.BR;
import hq.remview.R;
import hq.remview.data.model.api.response.top.TopSaleResponse;
import hq.remview.databinding.ActivityPieChartBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;

public class PieChartActivity extends BaseActivity<ActivityPieChartBinding,PieChartViewModel> implements OnChartValueSelectedListener {

    private PieChart chart;
    public static List<TopSaleResponse.TopSaleItem> topSaleItems;

    @Override
    public int getLayoutId() {
        return R.layout.activity_pie_chart;
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
        chart = viewBinding.chart;
        chart.setUsePercentValues(true);

        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);


        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setTransparentCircleRadius(61f);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);
        chart.getLegend().setEnabled(false);
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        // entry label styling
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        chart.setEntryLabelTextSize(12f);
        setData();
    }
    private void setData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        int sum = topSaleItems.stream().mapToInt(TopSaleResponse.TopSaleItem::getValue).sum();
        for (int i = 0; i < topSaleItems.size() ; i++) {
            entries.add(new PieEntry((float) topSaleItems.get(i).getValue()/sum,
                    topSaleItems.get(i).getName()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            final DecimalFormat decimalFormat = new  DecimalFormat("###,###,##0.0");
            @Override
            public String getFormattedValue(float value) {
                return "??";
            }

            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return decimalFormat.format(value) + "%";
            }
        });
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));


        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        chart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
