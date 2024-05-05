package com.example.bletest3;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartUtils {
    // Prepares for LineChart
    public static void setupLineChart(Context context, LineChart lineChart, List<Entry> entries, LineDataSet dataSet) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int colorText;
        int colorLine;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            colorText= Color.WHITE;
            colorLine = R.color.purple_200;
        }
        else {
            colorText = Color.BLACK;
            colorLine = R.color.lavender;
        }
        // Disable description text
        Description description = new Description();
        description.setText("Your UVI");
        description.setTextColor(colorText);
        lineChart.setDescription(description);
        Legend legend = lineChart.getLegend();
        legend.setTextColor(colorText);

        // Enable Drag Gestures.
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);

        // Disables the X axis from showing.
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f); // set the minimum value
        xAxis.setAxisMaximum(10f); // set the maximum value
        xAxis.setEnabled(false); // Disable X-axis numbers

        // Customize Y-axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f); // start at zero
        leftAxis.setAxisMaximum(10f); // the axis maximum
        leftAxis.setTextColor(colorText);

        // Customize Y-axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y-axis

        // Initialize LineDataSet with empty data
        dataSet = new LineDataSet(entries, "UV Index");
        dataSet.setValueTextColor(colorText);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(2f);
        dataSet.setColor(colorLine);

        // Add LineDataSet to LineData
        LineData lineData = new LineData(dataSet);

        // Set data to the chart
        lineChart.setData(lineData);

        // Refresh chart
        lineChart.invalidate();
    }

    // Method to add a new data point to the graph
    public static void addEntry(Context context, LineChart lineChart, List<Entry> entries, LineDataSet dataSet, int value) {
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int colorText, colorLine, colorFill;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            colorText= Color.WHITE;
            colorLine = ContextCompat.getColor(context, R.color.purple_200);
            colorFill = ContextCompat.getColor(context, R.color.black);
        }
        else {
            colorText = Color.BLACK;
            colorLine = ContextCompat.getColor(context, R.color.lavender);
            colorFill = ContextCompat.getColor(context, R.color.white);
        }
        // Add the new data point
        if (entries.size() >= 10) {
            // Remove the oldest entry
            entries.remove(0);

            // Shift existing entries.
            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                entry.setX(i);
            }
        }

        // Add the new entry at the end
        entries.add(new Entry(entries.size(), value));

        // Initialize LineDataSet with updated data
        dataSet = new LineDataSet(entries, "UV Index");
        dataSet.setDrawCircles(true);
        dataSet.setLineWidth(2f);
        dataSet.setColor(colorLine);
        dataSet.setCircleColor(colorLine);
        dataSet.setValueTextColor(colorText);
        dataSet.setCircleHoleColor(colorFill); // Set circle fill color

        // Add LineDataSet to LineData
        LineData lineData = new LineData(dataSet);

        // Set data to the chart
        lineChart.setData(lineData);

        // Refresh chart
        lineChart.invalidate();
    }
}
