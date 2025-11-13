package com.example.helloworld.widgets

import android.content.Context
import android.widget.RemoteViews
import android.appwidget.AppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.util.concurrent.TimeUnit
import com.example.helloworld.*
import androidx.core.content.edit
import android.content.ComponentName
import androidx.core.content.ContentProviderCompat.requireContext

class GoldWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val goldRate = fetchGoldRateFromCBR()
            saveGoldRateToCache(goldRate)
            updateAllWidgets()
            scheduleNextUpdate()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun fetchGoldRateFromCBR(): String = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
            val currentDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH)
                .format(java.util.Date())
            val request = Request.Builder()
                .url("https://www.cbr.ru/scripts/xml_metall.asp?date_req1=$currentDate&date_req2=$currentDate")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body()
            val xmlResponse = responseBody?.string() ?: throw Exception("Empty response")

            parseGoldRateFromXML(xmlResponse)
        } catch (e: Exception) {
            val prefs = applicationContext.getSharedPreferences("gold_widget", 0)
            return@withContext prefs.getString("current_rate", "₽ 0.0") ?: "₽ 0.0"
        }
    }

    private fun parseGoldRateFromXML(xml: String): String {
        return try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType
            var inRecord = false
            var buyPrice = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "Record" && parser.getAttributeValue(null, "Code") == "1") {
                            inRecord = true
                        }
                        if (inRecord && parser.name == "Buy") {
                            eventType = parser.next()
                            if (eventType == XmlPullParser.TEXT) {
                                buyPrice = parser.text
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "Record" && inRecord) {
                            break
                        }
                    }
                }
                eventType = parser.next()
            }

            if (buyPrice.isNotEmpty()) {
                "₽ ${String.format("%.2f", buyPrice.replace(",", ".").toDouble())}"
            } else {
                "Ошибка парсинга"
            }
        } catch (e: Exception) {
            "Ошибка парсинга"
        }
    }

    private fun saveGoldRateToCache(rate: String) {
        val prefs = applicationContext.getSharedPreferences("gold_widget", 0)
        prefs.edit { putString("current_rate", rate) }
    }

    private fun getCachedGoldRate(): String? {
        val prefs = applicationContext.getSharedPreferences("gold_widget", 0)
        return prefs.getString("current_rate", null)
    }

    private fun updateAllWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val thisWidget = ComponentName(applicationContext, GoldWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(applicationContext, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val goldRate = getCachedGoldRate() ?: "??? ₽"
        val views = RemoteViews(context.packageName, R.layout.gold_widget)
        views.setTextViewText(R.id.tvGoldRate, goldRate)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun scheduleNextUpdate() {
        val workRequest = OneTimeWorkRequestBuilder<GoldWidgetWorker>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
}