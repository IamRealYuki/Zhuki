package com.example.helloworld.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.helloworld.R

class SettingsFragment : Fragment() {

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
        return view
    }

    private fun initViews(view: View) {
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
        setupSeekBar(sbGameSpeed, tvGameSpeed, "x")
        setupSeekBar(sbMaxCockroaches, tvMaxCockroaches, "шт")
        setupSeekBar(sbBonusInterval, tvBonusInterval, "сек")
        setupSeekBar(sbRoundDuration, tvRoundDuration, "сек")

        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
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
            "round_duration" to sbRoundDuration.progress
        )

        Toast.makeText(requireContext(), "Настройки сохранены!", Toast.LENGTH_SHORT).show()
    }
}