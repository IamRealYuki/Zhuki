package com.example.helloworld.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.helloworld.R
import com.example.helloworld.model.Player
import com.example.helloworld.model.ZodiacPeriod
import java.text.SimpleDateFormat
import java.util.*

class RegistrationFragment : Fragment() {
    private lateinit var etFullName: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var spCourse: Spinner
    private lateinit var sbDifficulty: SeekBar
    private lateinit var tvDifficultyValue: TextView
    private lateinit var cvBirthDate: CalendarView
    private lateinit var ivZodiac: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var tvResult: TextView

    private var selectedDate: Calendar = Calendar.getInstance()
    private val zodiacSigns = listOf(
        ZodiacPeriod("Овен", Calendar.MARCH, 21, Calendar.APRIL, 19, R.drawable.oven),
        ZodiacPeriod("Телец", Calendar.APRIL, 20, Calendar.MAY, 20, R.drawable.taurus),
        ZodiacPeriod("Близнецы", Calendar.MAY, 21, Calendar.JUNE, 20, R.drawable.twins),
        ZodiacPeriod("Рак", Calendar.JUNE, 21, Calendar.JULY, 22, R.drawable.cancer),
        ZodiacPeriod("Лев", Calendar.JULY, 23, Calendar.AUGUST, 22, R.drawable.leo),
        ZodiacPeriod("Дева", Calendar.AUGUST, 23, Calendar.SEPTEMBER, 22, R.drawable.virgo),
        ZodiacPeriod("Весы", Calendar.SEPTEMBER, 23, Calendar.OCTOBER, 22, R.drawable.oi_nepomnu),
        ZodiacPeriod("Скорпион", Calendar.OCTOBER, 23, Calendar.NOVEMBER, 21, R.drawable.scorpio),
        ZodiacPeriod("Стрелец", Calendar.NOVEMBER, 22, Calendar.DECEMBER, 21, R.drawable.saggittarus),
        ZodiacPeriod("Козерог", Calendar.DECEMBER, 22, Calendar.JANUARY, 19, R.drawable.caprikorn),
        ZodiacPeriod("Водолей", Calendar.JANUARY, 20, Calendar.FEBRUARY, 18, R.drawable.aqarius),
        ZodiacPeriod("Рыбы", Calendar.FEBRUARY, 19, Calendar.MARCH, 20, R.drawable.ryby)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        initViews(view)
        setupCourseSpinner()
        setupListeners()
        updateZodiacSign()
        return view
    }

    private fun initViews(view: View) {
        etFullName = view.findViewById(R.id.etFullName)
        rgGender = view.findViewById(R.id.rgGender)
        spCourse = view.findViewById(R.id.spCourse)
        sbDifficulty = view.findViewById(R.id.sbDifficulty)
        tvDifficultyValue = view.findViewById(R.id.tvDifficultyValue)
        cvBirthDate = view.findViewById(R.id.cvBirthDate)
        ivZodiac = view.findViewById(R.id.ivZodiac)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        tvResult = view.findViewById(R.id.tvResult)
    }

    private fun setupCourseSpinner() {
        val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCourse.adapter = adapter
    }

    private fun setupListeners() {
        if (!::btnSubmit.isInitialized) {
            btnSubmit = requireView().findViewById(R.id.btnSubmit)
        }

        sbDifficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val levels = listOf("Младенец", "Ребенок", "Пацан с района", "Милешко", "Солодов")
                tvDifficultyValue.text = levels[progress]
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        cvBirthDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            updateZodiacSign()
        }

        btnSubmit.setOnClickListener {
            submitData()
        }
    }

    private fun updateZodiacSign() {
        val zodiacInfo = getZodiacSignInfo(selectedDate)
        zodiacInfo?.let { sign ->
            ivZodiac.setImageResource(sign.drawableRes)
        } ?: run {
            ivZodiac.setImageResource(R.drawable.ryby)
        }
    }

    private fun getZodiacSignInfo(date: Calendar): ZodiacPeriod? {
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)

        return zodiacSigns.firstOrNull { sign ->
            isDateInZodiacPeriod(month, day, sign)
        }
    }

    private fun isDateInZodiacPeriod(month: Int, day: Int, sign: ZodiacPeriod): Boolean {
        return if (sign.startMonth <= sign.endMonth) {
            (month == sign.startMonth && day >= sign.startDay) ||
                    (month == sign.endMonth && day <= sign.endDay) ||
                    (month > sign.startMonth && month < sign.endMonth)
        } else {
            (month == sign.startMonth && day >= sign.startDay) ||
                    (month == sign.endMonth && day <= sign.endDay) ||
                    (month > sign.startMonth) || // После декабря
                    (month < sign.endMonth)      // До января
        }
    }

    @SuppressLint("SetTextI18n")
    private fun submitData() {
        val zodiacInfo = getZodiacSignInfo(selectedDate)

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val birthDateString = dateFormat.format(selectedDate.time)

        val difficultyLevel = sbDifficulty.progress

        val player = Player(
            fullName = etFullName.text.toString().trim(),
            gender = when (rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "Мужской"
                R.id.rbFemale -> "Женский"
                else -> "Не указан"
            },
            course = spCourse.selectedItem?.toString() ?: "Не выбран",
            difficulty = difficultyLevel,
            birthDate = birthDateString,
            zodiacSign = zodiacInfo?.name ?: "Рыбы"
        )

        if (player.fullName.isEmpty()) {
            Toast.makeText(requireContext(), "Введите ФИО", Toast.LENGTH_SHORT).show()
            return
        }

        tvResult.text = """
            ФИО: ${player.fullName}
            Пол: ${player.gender}
            Курс: ${player.course}
            Уровень сложности: ${tvDifficultyValue.text} [${player.difficulty + 1}/5]
            Дата рождения: ${player.birthDate}
            Знак зодиака: ${player.zodiacSign}
        """.trimIndent()

        tvResult.post {
            val scrollView = requireView().findViewById<ScrollView>(R.id.mainScroll)
            scrollView?.smoothScrollTo(0, tvResult.bottom)
        }
    }
}