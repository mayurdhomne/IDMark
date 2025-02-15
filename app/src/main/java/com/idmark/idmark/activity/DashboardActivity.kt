package com.idmark.idmark.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.idmark.R
import com.idmark.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPieChart()
        setupClickListeners()
    }

    private fun setupPieChart() {
        val pieChart = findViewById<PieChart>(R.id.attendanceChart)

        // Sample Data
        val entries = listOf(
            PieEntry(22f, "Present"),
            PieEntry(8f, "Absent")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(this@DashboardActivity, R.color.colorAccent),
                ContextCompat.getColor(this@DashboardActivity, R.color.yellow)
            )
            valueTextSize = 16f
            setDrawValues(true)
            sliceSpace = 3f
            valueTextColor = Color.BLACK
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE // Labels outside
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val pieData = PieData(dataSet)
        pieChart.apply {
            data = pieData
            description.isEnabled = false
            setDrawEntryLabels(false)
            setUsePercentValues(true)
            isDrawHoleEnabled = true
            holeRadius = 40f
            setTransparentCircleAlpha(0)
            legend.isEnabled = true
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            animateY(1400, Easing.EaseInOutQuad)
            invalidate()
        }
    }


    private fun setupClickListeners() {
        binding.apply {
            menuButton.setOnClickListener {
                // Handle menu click
            }

            searchButton.setOnClickListener {
                // Handle search click
            }

            dateFilterButton.setOnClickListener {
                // Show date range picker
            }

            takeAttendanceButton.setOnClickListener {
                // Handle attendance button click
            }
        }
    }
}
