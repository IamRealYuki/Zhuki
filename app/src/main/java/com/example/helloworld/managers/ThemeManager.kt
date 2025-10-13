package com.example.helloworld.managers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.helloworld.R

object ThemeManager {
    private val themeListeners = mutableListOf<(Int) -> Unit>()
    private var currentTheme: Int = R.style.Theme_MyApp

    fun setTheme(themeResId: Int, context: Context) {
        if (currentTheme == themeResId) return

        currentTheme = themeResId

        saveToPreferences(themeResId, context)

        notifyThemeChanged(themeResId)
    }

    fun getCurrentTheme(): Int = currentTheme

    fun loadSavedTheme(context: Context) {
        val prefs = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
        currentTheme = prefs.getInt("current_theme", R.style.Theme_MyApp_Ocean)
    }

    fun addThemeListener(listener: (Int) -> Unit) {
        themeListeners.add(listener)
    }

    fun removeThemeListener(listener: (Int) -> Unit) {
        themeListeners.remove(listener)
    }

    private fun notifyThemeChanged(themeResId: Int) {
        themeListeners.forEach { listener ->
            listener(themeResId)
        }
    }

    // ФУНКЦИЯ СОХРАНЕНИЯ В PREFERENCES
    private fun saveToPreferences(themeResId: Int, context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences("app_theme", Context.MODE_PRIVATE)
        prefs.edit().putInt("current_theme", themeResId).apply()
    }
}