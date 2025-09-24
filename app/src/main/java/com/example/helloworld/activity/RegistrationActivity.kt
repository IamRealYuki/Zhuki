package com.example.helloworld.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.helloworld.R
import com.example.helloworld.model.Player

class RegistrationActivity : ComponentActivity() {
    private lateinit var etFullName: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var spCourse: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initViews()
        setupCourseSpinner()
        setupListeners();
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        rgGender = findViewById(R.id.rgGender)
        spCourse = findViewById(R.id.spCourse)
    }

    private fun setupCourseSpinner() {
        val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCourse.adapter = adapter
    }

    private fun setupListeners() {
        btnSubmit.setOnClickListener {
            submitData()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun submitData() {
        val player = Player(
            fullName = etFullName.text.toString(),
            gender = when (rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "Мужской"
                R.id.rbFemale -> "Женский"
                else -> "Не указан"
            },
            course = spCourse.selectedItem.toString(),
            difficulty = 0,
            birthDate = "",
            zodiacSign = ""
        )

        tvResult.text = """
            ФИО: ${player.fullName}
            Пол: ${player.gender}
            Курс: ${player.course}
            Уровень сложности: ${player.difficulty}/10
            Дата рождения: ${player.birthDate}
            Знак зодиака: ${player.zodiacSign}
        """.trimIndent()
    }

}