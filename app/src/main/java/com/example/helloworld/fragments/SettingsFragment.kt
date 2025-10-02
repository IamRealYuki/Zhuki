package com.example.helloworld.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.helloworld.R
import com.example.helloworld.managers.ThemeManager

class SettingsFragment : Fragment() {

    private lateinit var rgTheme: RadioGroup
    private lateinit var rbThemeDefault: RadioButton
    private lateinit var rbThemeOcean: RadioButton
    private lateinit var rbThemeSunset: RadioButton

    private lateinit var sbGameSpeed: SeekBar
    private lateinit var tvGameSpeed: TextView
    private lateinit var sbMaxCockroaches: SeekBar
    private lateinit var tvMaxCockroaches: TextView
    private lateinit var sbBonusInterval: SeekBar
    private lateinit var tvBonusInterval: TextView
    private lateinit var sbRoundDuration: SeekBar
    private lateinit var tvRoundDuration: TextView
    private lateinit var btnSaveSettings: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        initViews(view)
        setupListeners()
        loadCurrentTheme()
        return view
    }

    private fun initViews(view: View) {
        // Инициализация элементов темы
        rgTheme = view.findViewById(R.id.rgTheme)
        rbThemeDefault = view.findViewById(R.id.rbThemeDefault)
        rbThemeOcean = view.findViewById(R.id.rbThemeOcean)
        rbThemeSunset = view.findViewById(R.id.rbThemeSunset)

        // Инициализация остальных элементов
        sbGameSpeed = view.findViewById(R.id.sbGameSpeed)
        tvGameSpeed = view.findViewById(R.id.tvGameSpeed)
        sbMaxCockroaches = view.findViewById(R.id.sbMaxCockroaches)
        tvMaxCockroaches = view.findViewById(R.id.tvMaxCockroaches)
        sbBonusInterval = view.findViewById(R.id.sbBonusInterval)
        tvBonusInterval = view.findViewById(R.id.tvBonusInterval)
        sbRoundDuration = view.findViewById(R.id.sbRoundDuration)
        tvRoundDuration = view.findViewById(R.id.tvRoundDuration)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)
    }

    private fun setupListeners() {
        // Слушатель для выбора темы
        rgTheme.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbThemeDefault -> applyTheme(R.style.Theme_MyApp)
                R.id.rbThemeOcean -> applyTheme(R.style.Theme_MyApp_Ocean)
                R.id.rbThemeSunset -> applyTheme(R.style.Theme_MyApp_Sunset)
            }
        }

        // Слушатели для SeekBar
        setupSeekBar(sbGameSpeed, tvGameSpeed, "x")
        setupSeekBar(sbMaxCockroaches, tvMaxCockroaches, "шт")
        setupSeekBar(sbBonusInterval, tvBonusInterval, "сек")
        setupSeekBar(sbRoundDuration, tvRoundDuration, "сек")

        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadCurrentTheme() {
        val currentTheme = ThemeManager.getCurrentTheme()

        when (currentTheme) {
            R.style.Theme_MyApp -> rbThemeDefault.isChecked = true
            R.style.Theme_MyApp_Ocean -> rbThemeOcean.isChecked = true
            R.style.Theme_MyApp_Sunset -> rbThemeSunset.isChecked = true
        }
    }

    private fun applyTheme(themeResId: Int) {
        ThemeManager.setTheme(themeResId, requireContext())
        Toast.makeText(requireContext(), "Тема применена!", Toast.LENGTH_SHORT).show()
    }

    private fun setupSeekBar(seekBar: SeekBar, textView: TextView, suffix: String) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = "$progress $suffix"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        // Инициализируем начальные значения
        seekBar.progress = seekBar.progress
    }

    private fun saveSettings() {
        val settings = mapOf(
            "game_speed" to sbGameSpeed.progress,
            "max_cockroaches" to sbMaxCockroaches.progress,
            "bonus_interval" to sbBonusInterval.progress,
            "round_duration" to sbRoundDuration.progress,
            "theme" to when {
                rbThemeDefault.isChecked -> "default"
                rbThemeOcean.isChecked -> "ocean"
                rbThemeSunset.isChecked -> "sunset"
                else -> "default"
            }
        )

        // Здесь можно сохранить настройки в SharedPreferences
        Toast.makeText(requireContext(), "Настройки сохранены!", Toast.LENGTH_SHORT).show()
    }
}