package com.example.helloworld.fragments

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.helloworld.R
import com.example.helloworld.model.*
import kotlin.random.Random

class GameFragment : Fragment() {

    private lateinit var gameLayout: FrameLayout
    private lateinit var tvScore: TextView
    private lateinit var tvMiss: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button

    private var score = 0
    private var miss = 0
    private var isPlaying = false

    private val handler = Handler(Looper.getMainLooper())
    private var screenWidth = 0
    private var screenHeight = 0

    private val activeBugs = mutableListOf<Pair<ImageView, Bug>>()

    // Настройки
    private var spawnInterval = 1200L // Интервал спавна (мс)
    private var maxBugs = 5           // Максимум жуков на экране
    private var bugSpeedMultiplier = 1.0
    private var roundDuration = 30000L // Время раунда (мс)

    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        initViews(view)
        setupTouchListener()
        loadSettings()
        return view
    }

    private fun initViews(view: View) {
        gameLayout = view.findViewById(R.id.gameLayout)
        tvScore = view.findViewById(R.id.tvScore)
        tvMiss = view.findViewById(R.id.tvMiss)
        tvTimer = view.findViewById(R.id.tvTimer)
        btnStart = view.findViewById(R.id.btnStart)

        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y

        btnStart.setOnClickListener {
            if (!isPlaying) startGame() else stopGame()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        gameLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && isPlaying) {
                miss++
                tvMiss.text = "Промахи: $miss"
            }
            false
        }
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("game_prefs", 0)

        val speedProgress = prefs.getInt("game_speed", 5)
        val maxCockroaches = prefs.getInt("max_cockroaches", 5)
        val bonusInterval = prefs.getInt("bonus_interval", 2)
        val roundDurationSec = prefs.getInt("round_duration", 30)

        // Конвертируем в реальные значения
        bugSpeedMultiplier = 0.5 + (speedProgress / 5.0) // 0.5x - 3.5x
        maxBugs = maxCockroaches.coerceAtLeast(1)
        spawnInterval = (2000L / bonusInterval.coerceAtLeast(1))
        roundDuration = roundDurationSec * 1000L
    }

    private fun startGame() {
        score = 0
        miss = 0
        tvScore.text = "Очки: 0"
        tvMiss.text = "Промахи: 0"
        tvTimer.text = "Время: --"
        isPlaying = true
        btnStart.text = "Стоп"
        handler.post(spawnRunnable)
        startTimer()
    }

    private fun stopGame() {
        isPlaying = false
        timer?.cancel()
        handler.removeCallbacks(spawnRunnable)
        gameLayout.removeAllViews()
        activeBugs.clear()
        btnStart.text = "Старт"
        Toast.makeText(requireContext(), "Игра окончена! Ваш счёт: $score", Toast.LENGTH_SHORT).show()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(roundDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "Время: ${millisUntilFinished / 1000} сек"
            }

            override fun onFinish() {
                stopGame()
            }
        }.start()
    }

    private val spawnRunnable = object : Runnable {
        override fun run() {
            if (!isPlaying) return

            if (activeBugs.size < maxBugs) {
                spawnBug()
            }

            moveBugs()
            handler.postDelayed(this, spawnInterval)
        }
    }

    private fun spawnBug() {
        val bug = getRandomBug()
        val bugView = ImageView(requireContext())

        val resId = resources.getIdentifier(bug.sprite, "drawable", requireContext().packageName)
        bugView.setImageResource(resId)

        val params = FrameLayout.LayoutParams(bug.size, bug.size)
        params.leftMargin = Random.nextInt(0, screenWidth - bug.size)
        params.topMargin = Random.nextInt(0, screenHeight - bug.size * 3)
        bugView.layoutParams = params

        bugView.setOnClickListener {
            if (isPlaying) {
                score += bug.points
                tvScore.text = "Очки: $score"
                gameLayout.removeView(bugView)
                activeBugs.removeAll { it.first == bugView }
            }
        }

        gameLayout.addView(bugView)
        activeBugs.add(bugView to bug)
    }

    private fun moveBugs() {
        val iterator = activeBugs.iterator()
        while (iterator.hasNext()) {
            val (bugView, bug) = iterator.next()
            val params = bugView.layoutParams as FrameLayout.LayoutParams
            params.topMargin += (bug.speed * bugSpeedMultiplier).toInt()

            if (params.topMargin > screenHeight) {
                gameLayout.removeView(bugView)
                iterator.remove()
            } else {
                bugView.layoutParams = params
            }
        }
    }

    private fun getRandomBug(): Bug {
        return when (Random.nextInt(0, 3)) {
            0 -> Cockroach()
            1 -> Beetle()
            else -> Ladybug()
        }
    }
}
