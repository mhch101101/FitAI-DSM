package app.dsm.fitai.ui.screens.progress

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.dsm.fitai.R
import app.dsm.fitai.di.FitAIApp
import app.dsm.fitai.viewmodel.ProgressViewModel
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.point.FilledCircularPointDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer as BarXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer as BarYAxisDrawer
import com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer as LineXAxisDrawer
import com.github.tehras.charts.line.renderer.yaxis.SimpleYAxisDrawer as LineYAxisDrawer
import app.dsm.fitai.ui.screens.home.LayoutScreen

@Composable
fun ProgressScreen(
    navigateToLogin: () -> Unit = {},
    navigateToProfileEdit: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val appComponent = (context.applicationContext as FitAIApp).appComponent

    val viewModel = remember {
        ProgressViewModel(
            trainingRepository = appComponent.trainingRepository(),
            stepRepository = appComponent.stepRepository(),
            authRepository = appComponent.authRepository()
        )
    }

    val exercises by viewModel.exercises.collectAsState()
    val selectedExerciseId by viewModel.selectedExerciseId.collectAsState()
    val oneRmPoints by viewModel.oneRmPoints.collectAsState()
    val weeklySteps by viewModel.weeklySteps.collectAsState()

    LayoutScreen(
        title = stringResource(R.string.progress_title),
        context = context,
        showStepsCard = false,
        navigateToLogin = navigateToLogin,
        navigateToProfileEdit = navigateToProfileEdit,
        onNavigateBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Grafico de linea: progresion de fuerza (1RM) ---
            SectionCard(title = stringResource(R.string.progress_strength_title)) {
                if (exercises.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        exercises.forEach { exercise ->
                            FilterChip(
                                selected = exercise.id == selectedExerciseId,
                                onClick = { viewModel.selectExercise(exercise.id) },
                                label = { Text(exercise.name) }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }

                if (oneRmPoints.size >= 2) {
                    val dateFormat = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) }
                    val lineColor = MaterialTheme.colorScheme.primary
                    val pointColor = MaterialTheme.colorScheme.tertiary
                    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant

                    val points = oneRmPoints.map { point ->
                        LineChartData.Point(
                            value = point.oneRm.toFloat(),
                            label = dateFormat.format(Date(point.dateMillis))
                        )
                    }

                    LineChart(
                        linesChartData = listOf(
                            LineChartData(
                                points = points,
                                lineDrawer = SolidLineDrawer(color = lineColor)
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        animation = simpleChartAnimation(),
                        pointDrawer = FilledCircularPointDrawer(color = pointColor),
                        xAxisDrawer = LineXAxisDrawer(
                            labelTextColor = axisColor,
                            axisLineColor = axisColor
                        ),
                        yAxisDrawer = LineYAxisDrawer(
                            labelTextColor = axisColor,
                            axisLineColor = axisColor
                        )
                    )
                } else {
                    EmptyChart(stringResource(R.string.progress_strength_empty))
                }
            }

            // --- Grafico de barras: pasos de la semana ---
            SectionCard(title = stringResource(R.string.progress_steps_title)) {
                if (weeklySteps.isNotEmpty()) {
                    val dayFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
                    val parseFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
                    val barColor = MaterialTheme.colorScheme.primary
                    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant

                    val bars = weeklySteps.map { record ->
                        val label = runCatching {
                            dayFormat.format(parseFormat.parse(record.date)!!)
                                // Spanish "EEE" yields e.g. "lun." -> clean to "Lun".
                                .trim().trimEnd('.').replaceFirstChar { it.uppercase() }
                        }.getOrDefault(record.date)
                        BarChartData.Bar(
                            label = label,
                            value = record.steps.toFloat(),
                            color = barColor
                        )
                    }

                    BarChart(
                        barChartData = BarChartData(bars = bars),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        animation = simpleChartAnimation(),
                        xAxisDrawer = BarXAxisDrawer(
                            axisLineColor = axisColor
                        ),
                        yAxisDrawer = BarYAxisDrawer(
                            labelTextColor = axisColor,
                            axisLineColor = axisColor
                        ),
                        // DrawLocation.XAxis places the day label below each bar.
                        labelDrawer = SimpleValueDrawer(
                            drawLocation = SimpleValueDrawer.DrawLocation.XAxis,
                            labelTextColor = axisColor
                        )
                    )
                } else {
                    EmptyChart(stringResource(R.string.progress_steps_empty))
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun EmptyChart(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}
