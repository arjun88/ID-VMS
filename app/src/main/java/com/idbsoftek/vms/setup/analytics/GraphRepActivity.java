package com.idbsoftek.vms.setup.analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.idbsoftek.vms.R;
import com.idbsoftek.vms.setup.VmsMainActivity;
import com.idbsoftek.vms.util.ChartDisplayer;
import com.idbsoftek.vms.util.IntValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class GraphRepActivity extends VmsMainActivity {
private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept_status_wise_analytics);

        setActionBarTitle("Analytics");

        barChart = findViewById(R.id.dept_bar_chart);

        showBarChartForSelectedMonthAndCategory();
    }

    private void showBarChartForSelectedMonthAndCategory() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        /*for (int i = 0; i < surveyInCategoryList.size(); i++) {
            if (surveyInCategoryList.get(i).getCount() > 0) {
                barEntries.add(new BarEntry(i
                        , surveyInCategoryList.get(i).getCount()));
            }

        }*/

        /*for (int i = 0; i < 3; i++) {

                barEntries.add(new BarEntry(i
                        , 10));
        }*/
        barEntries.add(new BarEntry(0
                , 10));
        barEntries.add(new BarEntry(1
                , 10));
        barEntries.add(new BarEntry(2
                , 10));

        String label = "Monthly";
       /* if (!isMonthly) {
            label = "Yearly";
        }*/

        BarDataSet barDataSets = new BarDataSet(barEntries, label);
        barDataSets.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSets.setValueFormatter(new IntValueFormatter());

        List<String> surveys = new ArrayList<>();

        int maxValueOfBarChartYAxis = 0;

        surveys.add("First");
        surveys.add("Sec");
        surveys.add("Third");

        /*for (SurveysItem surveyInCat : surveyInCategoryList) {
            //if (surveyInCat.getCount() > 0) {
            surveys.add(surveyInCat.getSurTitle());

            if (maxValueOfBarChartYAxis < surveyInCat.getCount())
                maxValueOfBarChartYAxis = surveyInCat.getCount();
            //  }

        }*/
        maxValueOfBarChartYAxis += 1;
        barChart.getAxisLeft().setAxisMaximum(maxValueOfBarChartYAxis);

       /* BarData monthlyYearlyBarData = new BarData();
        monthlyYearlyBarData.addDataSet(barDataSets);*/

        ChartDisplayer chartDisplayer = new ChartDisplayer(barChart, surveys,
                barDataSets);
        chartDisplayer.displayBarChart(maxValueOfBarChartYAxis);

    }

}