package com.fireminder.semele.ui.sessionreport;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fireminder.semele.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: incomplete, need to add line chart and fix annoying border with bar chart
 */
public class SessionReportActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_session_report);


    int[] ts = {0,1,2,3,4,5,6,7,8,9,10};
    float[] val ={3,4,5,9,6,2,4,8,9,9,1};

    for (int i = 0; i < ts.length; i++) {
      //ts[i] = 1000000000 + ts[i];
      val[i] = 134 + val[i];
    }

    List<Entry> entries = new ArrayList<>();
    for (int i = 0; i < ts.length; i++) {
      entries.add(new Entry(val[i], ts[i]));
    }

    initLineGraph(timestampsToLabels(ts), entries);

    /*
    HorizontalBarChart barChart = (HorizontalBarChart) findViewById(R.id.barchart);

    barChart.setGridBackgroundColor(android.R.color.transparent);
    barChart.setDrawGridBackground(false);
    barChart.setDrawHighlightArrow(false);
    barChart.setDrawBarShadow(false);

    barChart.setDescription("");
    barChart.getXAxis().setDrawLabels(false);
    barChart.getXAxis().setGridColor(android.R.color.transparent);
    barChart.getAxisLeft().setDrawLabels(false);
    barChart.getAxisLeft().setGridColor(android.R.color.transparent);
    barChart.getAxisRight().setDrawLabels(false);
    barChart.getAxisRight().setGridColor(android.R.color.transparent);
    barChart.getLegend().setEnabled(false);

    barChart.setBorderColor(android.R.color.transparent);
    barChart.setDrawBorders(false);
    barChart.setBorderWidth(0f);

    List<String> xValues = new ArrayList<>();
    xValues.add("Fat Burn");
    xValues.add("Cardio");
    xValues.add("Peak");

    List<BarEntry> yValues = new ArrayList<>();

    yValues.add(new BarEntry(30, 0));
    yValues.add(new BarEntry(10, 1));
    yValues.add(new BarEntry(7, 2));


    BarDataSet set = new BarDataSet(yValues, "DataSet");
    set.setBarSpacePercent(35f);

    List<IBarDataSet> dataSets = new ArrayList<>();
    dataSets.add(set);

    BarData data = new BarData(xValues, dataSets);

    barChart.setData(data);
    */
  }

  String secondsToHhMmSs(int secondsSinceStart) {
    int hr =  secondsSinceStart / 3600;
    int rem = secondsSinceStart % 3600;
    int min = rem / 60;
    int seconds = rem % 60;
    return new StringBuilder()
        .append(hr > 10 ? "" : "0").append(hr).append(":")
        .append(min > 10 ? "" : "0").append(min).append(":")
        .append(seconds > 10 ? "" : "0").append(seconds)
        .toString();
  }

  List<String> timestampsToLabels(int[] ts) {
    List<String> labels = new ArrayList<>();
    int start = ts[0];
    labels.add(secondsToHhMmSs(0));
    for (int i = 1; i < ts.length; i++) {
      int secondsSinceStart = ts[i] - start;
      labels.add(secondsToHhMmSs(secondsSinceStart));
    }
    return labels;
  }

  void initLineGraph(List<String> labels, List<Entry> entries) {

    LineDataSet dataSet = new LineDataSet(entries, "bpm");

    LineData data = new LineData(labels, dataSet);
    LineChart lineChart = (LineChart) findViewById(R.id.linechart);
    lineChart.setData(data);
    lineChart.setDescription("");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_session_report, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
