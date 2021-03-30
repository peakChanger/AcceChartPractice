package com.example.accechartpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final static String TAG = "MainActivity";

    //Sensor管理員
    private SensorManager sensorManager;

    //加速規
    private Sensor mAcce;

    //儲存螢幕當前方向
    private int screenOrientation;

    //螢幕直向時的三軸的LineChart
    private LineChart lineChartX;
    private LineChart lineChartY;
    private LineChart lineChartZ;

    //螢幕橫向時的綜合LineChart
    private LineChart lineChartLand;

    private Button resetChart;
    private TextView acceText;

    private static final int maxDataCount = 500;
    private int curCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSensor();//初始化感測器
        initLayoutComponents();//初始化圖表以外元件
        screenOrientation = this.getResources().getConfiguration().orientation;
        switch (screenOrientation) {
            case Configuration.ORIENTATION_PORTRAIT: //portrait
                initChart();
                break;
            case Configuration.ORIENTATION_LANDSCAPE://landscape
                initChartLand();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //當回到應用生命週期中，註冊感測器管理員
        sensorManager.registerListener(this, mAcce, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //應用暫停生命週期，所以取消感測器管理員使用權
        sensorManager.unregisterListener(this);
    }

    private void initLayoutComponents() {
        resetChart = (Button) findViewById(R.id.rearange);
        acceText = (TextView) findViewById(R.id.acceInfo);
        acceText.setText("加速規訊息處理中....");
        resetChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (screenOrientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        lineChartX.fitScreen();
                        lineChartY.fitScreen();
                        lineChartZ.fitScreen();
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        lineChartLand.fitScreen();
                        break;
                }
            }
        });
        resetChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                switch (screenOrientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        lineChartX.clear();
                        lineChartY.clear();
                        lineChartZ.clear();
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        lineChartLand.clear();
                        break;
                }
                curCounter = 0;
                initChart();
                return false;
            }
        });
    }


    private void initSensor() {
        //像系統註冊感感測器使用
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//選擇感測器
        sensorManager.registerListener(this, mAcce, SensorManager.SENSOR_DELAY_NORMAL);
        mAcce = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//選擇加速規
    }

    private void initChart() {
        //取得LineChart元件
        lineChartX = (LineChart) findViewById(R.id.AcceChartX);
        lineChartY = (LineChart) findViewById(R.id.AcceChartY);
        lineChartZ = (LineChart) findViewById(R.id.AcceChartZ);

        //資料陣列
        ArrayList<Entry> arrayX = new ArrayList<>();
        ArrayList<Entry> arrayY = new ArrayList<>();
        ArrayList<Entry> arrayZ = new ArrayList<>();

        //創建空的以利生成
//            arrayX.add(new Entry());
//            arrayY.add(new Entry());
//            arrayZ.add(new Entry());

        //創建LineDataSet
        LineDataSet lineDataSetX = new LineDataSet(arrayX, "acceX");
        LineDataSet lineDataSetY = new LineDataSet(arrayY, "acceY");
        LineDataSet lineDataSetZ = new LineDataSet(arrayZ, "acceZ");

        //不要畫製圈圈
        lineDataSetX.setDrawCircles(false);
        lineDataSetY.setDrawCircles(false);
        lineDataSetZ.setDrawCircles(false);

        //設定線條顏色
        lineDataSetX.setColor(Color.rgb(244, 57, 144));
        lineDataSetY.setColor(Color.rgb(17, 213, 104));
        lineDataSetZ.setColor(Color.rgb(34, 166, 242));

        ArrayList<ILineDataSet> dataSetsX = new ArrayList<>();
        ArrayList<ILineDataSet> dataSetsY = new ArrayList<>();
        ArrayList<ILineDataSet> dataSetsZ = new ArrayList<>();

        dataSetsX.add(lineDataSetX);
        dataSetsY.add(lineDataSetY);
        dataSetsZ.add(lineDataSetZ);

        LineData lineDataX = new LineData(dataSetsX);
        LineData lineDataY = new LineData(dataSetsY);
        LineData lineDataZ = new LineData(dataSetsZ);

        lineChartX.setData(lineDataX);
        lineChartY.setData(lineDataY);
        lineChartZ.setData(lineDataZ);

        //設定Legend顏色
        lineChartX.getLegend().setTextColor(Color.rgb(244, 57, 144));
        lineChartY.getLegend().setTextColor(Color.rgb(17, 213, 104));
        lineChartZ.getLegend().setTextColor(Color.rgb(34, 166, 242));

        setChartXAxisRange(lineChartX, 0, maxDataCount);
        setChartXAxisRange(lineChartY, 0, maxDataCount);
        setChartXAxisRange(lineChartZ, 0, maxDataCount);
        setChartYAxisProperties(lineChartX, true);
        setChartYAxisProperties(lineChartY, true);
        setChartYAxisProperties(lineChartZ, true);


    }

    private void initChartLand() {
        //取得LineChart元件
        lineChartLand = (LineChart) findViewById(R.id.AcceChartLand);

        //資料陣列
        ArrayList<Entry> arrayX = new ArrayList<>();
        ArrayList<Entry> arrayY = new ArrayList<>();
        ArrayList<Entry> arrayZ = new ArrayList<>();

        //創建LineDataSet
        LineDataSet lineDataSetX = new LineDataSet(arrayX, "acceX");
        LineDataSet lineDataSetY = new LineDataSet(arrayY, "acceY");
        LineDataSet lineDataSetZ = new LineDataSet(arrayZ, "acceZ");

        //不要畫製圈圈
        lineDataSetX.setDrawCircles(false);
        lineDataSetY.setDrawCircles(false);
        lineDataSetZ.setDrawCircles(false);

        //設定線條顏色
        lineDataSetX.setColor(Color.rgb(244, 57, 144));
        lineDataSetY.setColor(Color.rgb(17, 213, 104));
        lineDataSetZ.setColor(Color.rgb(34, 166, 242));

        ArrayList<ILineDataSet> dataSetsLand = new ArrayList<>();

        dataSetsLand.add(lineDataSetX);
        dataSetsLand.add(lineDataSetY);
        dataSetsLand.add(lineDataSetZ);

        LineData lineDataLand = new LineData(dataSetsLand);

        lineChartLand.setData(lineDataLand);

        //設定Legend顏色
        lineChartLand.getLegend().setTextColor(Color.rgb(235, 235, 235));

        setChartXAxisRange(lineChartLand, 0, maxDataCount);
        setChartYAxisProperties(lineChartLand, true);
    }

    private void setChartXAxisRange(LineChart lineChart, float Minimum, float Maximum) {
        lineChart.getXAxis().setAxisMinimum(Minimum);
        lineChart.getXAxis().setAxisMaximum(Maximum);
    }

    private void setChartYAxisProperties(LineChart lineChart, boolean autoScale) {
        if (autoScale) {
            //自動縮放Y軸
            lineChart.setAutoScaleMinMaxEnabled(true);
        } else {
            //y軸下限
            lineChart.getAxisLeft().setAxisMinimum(-25);

            //y軸上限
            lineChart.getAxisLeft().setAxisMaximum(25);
        }
        //不要輸出值
        lineChart.getLineData().setDrawValues(false);

        //不要顯示Description label
        lineChart.getDescription().setEnabled(false);

        //左方Y軸文字顏色
        lineChart.getAxisLeft().setTextColor(Color.rgb(235, 245, 255));

        //上方文字
        lineChart.getXAxis().setTextColor(Color.rgb(235, 245, 255));

        //右方Y軸不顯示
        lineChart.getAxisRight().setDrawLabels(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        acceText.setText("X軸 : " + event.values[0] + "\nY軸 : " + event.values[1] + "\nZ軸 : " + event.values[2]);
        switch (screenOrientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (curCounter >= maxDataCount) {
                    setChartXAxisRange(lineChartX, (float) (curCounter - maxDataCount), (float) curCounter);
                    setChartXAxisRange(lineChartY, (float) (curCounter - maxDataCount), (float) curCounter);
                    setChartXAxisRange(lineChartZ, (float) (curCounter - maxDataCount), (float) curCounter);
                } else {
                    setChartXAxisRange(lineChartX, 0, (float) maxDataCount);
                    setChartXAxisRange(lineChartY, 0, (float) maxDataCount);
                    setChartXAxisRange(lineChartZ, 0, (float) maxDataCount);
                }
                lineChartX.getLineData().getDataSets().get(0).addEntry(new Entry((float) curCounter, event.values[0]));
                lineChartX.notifyDataSetChanged();
                lineChartX.invalidate();

                lineChartY.getLineData().getDataSets().get(0).addEntry(new Entry((float) curCounter, event.values[1]));
                lineChartY.notifyDataSetChanged();
                lineChartY.invalidate();

                lineChartZ.getLineData().getDataSets().get(0).addEntry(new Entry((float) curCounter, event.values[2]));
                lineChartZ.notifyDataSetChanged();
                lineChartZ.invalidate();
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                if (curCounter >= maxDataCount) {
                    setChartXAxisRange(lineChartLand, (float) (curCounter - maxDataCount), (float) curCounter);
                } else {
                    setChartXAxisRange(lineChartLand, 0, (float) maxDataCount);
                }
                lineChartLand.getLineData().getDataSets().get(0).addEntry(new Entry((float) curCounter, event.values[0]));
                lineChartLand.getLineData().getDataSets().get(1).addEntry(new Entry((float) curCounter, event.values[1]));
                lineChartLand.getLineData().getDataSets().get(2).addEntry(new Entry((float) curCounter, event.values[2]));
                lineChartLand.notifyDataSetChanged();
                lineChartLand.invalidate();
                break;
        }
        curCounter++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}