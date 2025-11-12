package com.example.helloworld.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.helloworld.*

class GoldWidget : AppWidgetProvider() {

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        updateGoldRate(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        updateGoldRate(context)

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateGoldRate(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<GoldWidgetWorker>()
            .setInitialDelay(0, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences("gold_widget", Context.MODE_PRIVATE)
        val goldRate = prefs.getString("current_rate", "₽ ---") ?: "₽ ---"

        val views = RemoteViews(context.packageName, R.layout.gold_widget)
        views.setTextViewText(R.id.tvGoldRate, goldRate)

        val updateIntent = Intent(context, GoldWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
        }
        val pendingUpdateIntent = PendingIntent.getBroadcast(
            context, appWidgetId, updateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.tvGoldRate, pendingUpdateIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}