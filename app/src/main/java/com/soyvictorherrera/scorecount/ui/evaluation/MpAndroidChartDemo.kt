package com.soyvictorherrera.scorecount.ui.evaluation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.soyvictorherrera.scorecount.domain.model.Player
import com.soyvictorherrera.scorecount.domain.model.Set
import android.graphics.Color as AndroidColor

@Composable
fun MpAndroidChartDemo(
    set: Set,
    player1: Player,
    player2: Player,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "MPAndroidChart - Set ${set.setNumber}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Score: ${set.finalScore.player1Score} - ${set.finalScore.player2Score}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            AndroidView(
                factory = { context ->
                    LineChart(context).apply {
                        description.isEnabled = false
                        setTouchEnabled(false)
                        isDragEnabled = false
                        setScaleEnabled(false)
                        setPinchZoom(false)
                        legend.isEnabled = true

                        // Configure axes
                        xAxis.apply {
                            setDrawGridLines(true)
                            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                        }

                        axisLeft.apply {
                            setDrawGridLines(true)
                            axisMinimum = 0f
                        }

                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->
                    if (set.points.isEmpty()) {
                        chart.clear()
                        return@AndroidView
                    }

                    // Convert Point data to chart entries
                    val player1Entries =
                        set.points.map { point ->
                            Entry(point.sequence.toFloat(), point.player1Score.toFloat())
                        }
                    val player2Entries =
                        set.points.map { point ->
                            Entry(point.sequence.toFloat(), point.player2Score.toFloat())
                        }

                    // Create datasets
                    val player1DataSet =
                        LineDataSet(player1Entries, player1.name).apply {
                            color = parseColor(player1.color ?: "#135BEC")
                            lineWidth = 2.5f
                            setDrawCircles(false)
                            setDrawValues(false)
                            mode = LineDataSet.Mode.LINEAR
                        }

                    val player2DataSet =
                        LineDataSet(player2Entries, player2.name).apply {
                            color = parseColor(player2.color ?: "#FB923C")
                            lineWidth = 2.5f
                            setDrawCircles(false)
                            setDrawValues(false)
                            mode = LineDataSet.Mode.LINEAR
                        }

                    chart.data = LineData(player1DataSet, player2DataSet)
                    chart.invalidate()
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(240.dp)
            )
        }
    }
}

private fun parseColor(hexColor: String): Int = AndroidColor.parseColor(hexColor)
