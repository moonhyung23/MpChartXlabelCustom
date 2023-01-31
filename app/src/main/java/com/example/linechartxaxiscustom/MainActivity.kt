package com.example.linechartxaxiscustom

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat.getBestDateTimePattern
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.linechartxaxiscustom.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    //class name {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val xAxisLabel: ArrayList<String> = ArrayList()
    private var lastX = 0f
    private var realBpmTask: Timer? = null
    private var randomVal = 0f
    var xLabelCoorArray = floatArrayOf(0f) //x라벨의 좌표값을 담은 배열
    val standardNum = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val now = System.currentTimeMillis()
        val date = Date(now)
        xAxisLabel.add("")

/*
        xAxisLabel.add(date.time)
        xAxisLabel.add(date.time)
        xAxisLabel.add(date.time)
        xAxisLabel.add(date.time)
        xAxisLabel.add(date.time)
        xAxisLabel.add(date.time)
*/

        binding.lcChart.run {
            setDrawGridBackground(false)
            // x-axis limit line
            val llXAxis = LimitLine(10f, "Index 10")
            llXAxis.lineWidth = 4f
            llXAxis.enableDashedLine(10f, 10f, 0f)
            llXAxis.textSize = 10f
            //x좌표를 반환하는 메서드
            xAxis.run {
                isEnabled = true
                position = XAxis.XAxisPosition.BOTTOM
                enableGridDashedLine(10f, 10f, 0f)
                textSize = 12f
                textColor = getColor(R.color.white)
                granularity = 1f
//                setLabelCount(6, true)
                axisMinimum = 0f
                axisMaximum = 100f
                isShowSpecificLabelPositions = true
            }
            axisRight.isEnabled = false


//            setData(45, 100f)
            xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
                val index = (value / standardNum)
                if (index.toInt() >= xAxisLabel.size) { //측정 값 없는 경우
                    Log.e("test222:", "index.toInt(): $index")
                    ""
                } else {
                    Log.e("test555:", "else {: $index")
                    Log.e("test4444:", xAxisLabel[index.toInt()])
                    xAxisLabel[index.toInt()]
                }
            }
        }

        binding.btnStart.setOnClickListener {
            startTotalGraph()
        }
    }

    fun append(floatArray: FloatArray, element: Float): FloatArray {
        val list: MutableList<Float> = floatArray.toMutableList()
        list.add(element)
        return list.toFloatArray()
    }

    //누적 그래프 그리기 시작
    fun startTotalGraph() {
        val heartTask: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread(object : TimerTask() {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun run() {
                        randomVal = (Math.random() * 10).toFloat() // 앱1 값
                        addEntrySingleData(randomVal, binding.lcChart)
                    }
                })
            }
        }
        realBpmTask = Timer()
        realBpmTask?.schedule(heartTask, 0, 1000)
    }

    private fun createSetLinear(): LineDataSet {
        val set = LineDataSet(null, "Real-time Line Data")
        set.lineWidth = 0.5f
        set.circleRadius = 2.5f
        set.setDrawValues(false)
        set.setDrawCircles(true)
        set.valueTextColor = resources.getColor(R.color.red)
        set.color = resources.getColor(R.color.white)
        set.highLightColor = Color.rgb(190, 190, 190)
        set.mode = LineDataSet.Mode.LINEAR
        return set
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addEntrySingleData(value: Float, lineChart: LineChart) {
        var data = lineChart.data
        if (data == null) {
            data = LineData()
            lineChart.data = data
        }
        var set = data.getDataSetByIndex(0)
        // set.addEntry(...); // can be called as well
        if (set == null) {
            set = createSetLinear()
            data.addDataSet(set)
            data.addEntry(Entry(0f, 0f), 0)
        }

        data.addEntry(Entry(set.entryCount.toFloat(), value), 0)
        binding.lcChart.run {
            val highestVisibleIndex: Float = maxVisibleCount.toFloat()
            if (data.entryCount > highestVisibleIndex) {
                lastX += 1.toFloat()
            } else {
                lastX = lineChart.maxVisibleCount.toFloat()
            }
            //5로 나누어서 나머지가 1인 경우
            //ex) 6 -> 11 -> 16 -> 21
            if (set.entryCount % standardNum == 1) {
                Log.e("test555: set.entryCount: ", "${set.entryCount}")
                //시스템 언어 정보 갖고오기
                val languageCode = Locale.getDefault().language //ex) ko, en
                val languageCountry = Locale.getDefault().country //ex) KR, US
                // 현재시간을 가져오기
                val long_now = System.currentTimeMillis()
                // 현재 시간을 Date 타입으로 변환
                val t_date = Date(long_now)
                // 날짜, 시간을 가져오고 싶은 형태 선언
                val t_dateFormat =
                    SimpleDateFormat("hh:mm", Locale(languageCode, languageCountry))
                xAxisLabel.add(t_dateFormat.format(t_date))
                //entryCount 값이 6 -> 11 -> 16 -> 21순으로 가기 때문에 -1해줌으로써 좌표값을 5의 배수로 맞춰준다.
                xLabelCoorArray = append(xLabelCoorArray, set.entryCount.toFloat() - 1)
                println("printlntest: " + xLabelCoorArray.contentToString())
                xAxis.specificLabelPositions = xLabelCoorArray
            }

            xAxis.axisMaximum = lastX
            notifyDataSetChanged()
            invalidate()
        }

    }

}