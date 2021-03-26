package com.example.accechartpratice;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
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

    private SensorManager sensorManager;//給Sensor管理員
    private Sensor mAcce;//給加速規

    //宣告三軸的LineChart
    private LineChart lineChartX;
    private LineChart lineChartY;
    private LineChart lineChartZ;

    private Button rearange;
    private TextView acceText;

    private int maxDataCount = 250;
    private int curCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayoutComponents();
        initSensor();
        initChart();
        initText();
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
        rearange = (Button) findViewById(R.id.rearange);
        rearange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChartX.fitScreen();
                lineChartY.fitScreen();
                lineChartZ.fitScreen();
            }
        });
        rearange.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resetChart();
                return false;
            }
        });
    }

    private void resetChart() {
        lineChartX.clear();
        lineChartY.clear();
        lineChartZ.clear();
        curCounter = 0;
        initChart();
    }

    private void initSensor() {
        //像系統註冊感感測器使用
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//選擇感測器
        sensorManager.registerListener(this, mAcce, SensorManager.SENSOR_DELAY_NORMAL);
        mAcce = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//選擇加速規
    }

    private void initChart() {
        int red[] = {255, 0, 0};
        int green[] = {0, 255, 0};
        int blue[] = {0, 0, 255};

        //取得LineChart元件
        lineChartX = (LineChart) findViewById(R.id.XAcce);
        lineChartY = (LineChart) findViewById(R.id.YAcce);
        lineChartZ = (LineChart) findViewById(R.id.ZAcce);

        //資料陣列
        ArrayList<Entry> arrayX = new ArrayList<>();
        ArrayList<Entry> arrayY = new ArrayList<>();
        ArrayList<Entry> arrayZ = new ArrayList<>();

        //創建空的以利生成
        arrayX.add(new Entry());
        arrayY.add(new Entry());
        arrayZ.add(new Entry());

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

        lineChartX.getLegend().setTextColor(Color.rgb(244, 57, 144));
        lineChartY.getLegend().setTextColor(Color.rgb(17, 213, 104));
        lineChartZ.getLegend().setTextColor(Color.rgb(34, 166, 242));

        removeFirstDataInChart();

        setChartXAxisRange(0, maxDataCount);
        setChartYAxisRange();
    }

    private void setChartXAxisRange(float Minimum, float Maximum) {

        lineChartX.getXAxis().setAxisMinimum(Minimum);
        lineChartX.getXAxis().setAxisMaximum(Maximum);

        lineChartY.getXAxis().setAxisMinimum(Minimum);
        lineChartY.getXAxis().setAxisMaximum(Maximum);

        lineChartZ.getXAxis().setAxisMinimum(Minimum);
        lineChartZ.getXAxis().setAxisMaximum(Maximum);
    }

    private void setChartYAxisRange() {
        //自動縮放Y軸
        lineChartX.setAutoScaleMinMaxEnabled(true);
        lineChartY.setAutoScaleMinMaxEnabled(true);
        lineChartZ.setAutoScaleMinMaxEnabled(true);
//        //y軸下限
//        lineChartX.getAxisLeft().setAxisMinimum(-20);
//        lineChartY.getAxisLeft().setAxisMinimum(-20);
//        lineChartZ.getAxisLeft().setAxisMinimum(-20);
//
//        //y軸上限
//        lineChartX.getAxisLeft().setAxisMaximum(20);
//        lineChartY.getAxisLeft().setAxisMaximum(20);
//        lineChartZ.getAxisLeft().setAxisMaximum(20);

        //不要輸出值
        lineChartX.getLineData().setDrawValues(false);
        lineChartY.getLineData().setDrawValues(false);
        lineChartZ.getLineData().setDrawValues(false);

        //不要顯示Description label
        lineChartX.getDescription().setEnabled(false);
        lineChartY.getDescription().setEnabled(false);
        lineChartZ.getDescription().setEnabled(false);

        //左方Y軸文字顏色
        lineChartX.getAxisLeft().setTextColor(Color.rgb(235, 245, 255));
        lineChartY.getAxisLeft().setTextColor(Color.rgb(235, 245, 255));
        lineChartZ.getAxisLeft().setTextColor(Color.rgb(235, 245, 255));

        //上方文字
        lineChartX.getXAxis().setTextColor(Color.rgb(235, 245, 255));
        lineChartY.getXAxis().setTextColor(Color.rgb(235, 245, 255));
        lineChartZ.getXAxis().setTextColor(Color.rgb(235, 245, 255));

        //右方Y軸不顯示
        lineChartX.getAxisRight().setDrawLabels(false);
        lineChartY.getAxisRight().setDrawLabels(false);
        lineChartZ.getAxisRight().setDrawLabels(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        acceText.setText("X軸 : " + event.values[0] + "\nY軸 : " + event.values[1] + "\nZ軸 : " + event.values[2]);

        if (curCounter >= maxDataCount) {
            removeFirstDataInChart();
            setChartXAxisRange((float) (curCounter - maxDataCount), (float) curCounter);
        } else {
            setChartXAxisRange(0, (float) maxDataCount);
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

        curCounter++;

        //Log.e(TAG, Long.toString(event.timestamp));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void removeFirstDataInChart() {//移除第一筆資料

        lineChartX.getLineData().getDataSets().get(0).removeFirst();
        lineChartY.getLineData().getDataSets().get(0).removeFirst();
        lineChartZ.getLineData().getDataSets().get(0).removeFirst();
    }

    private void initText() {
        acceText = (TextView) findViewById(R.id.acceInfo);
        acceText.setText("加速規訊息處理中....");
    }
}