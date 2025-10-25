package com.example.helloworld.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.helloworld.R
import com.example.helloworld.data.AppDatabase
import com.example.helloworld.data.ScoreRecord
import com.example.helloworld.model.enemy.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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

    private var spawnInterval = 1200L
    private var maxBugs = 5
    private var bugSpeedMultiplier = 1.0
    private var roundDuration = 30000L

    private var difficulty = 1
    private var lastSpawnTime = System.currentTimeMillis()

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
        val gameDifficulty = prefs.getInt("game_difficulty", 1)

        bugSpeedMultiplier = (0.5 + (speedProgress / 5.0)) * 2
        maxBugs = maxCockroaches.coerceAtLeast(1)
        spawnInterval = (2000L / bonusInterval.coerceAtLeast(1))
        roundDuration = roundDurationSec * 1000L
        difficulty = gameDifficulty
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
        saveRecord()
    }

    private fun saveRecord() {
        val playerName = getCurrentPlayerName()

        val record = ScoreRecord(
            playerName = playerName,
            score = score,
            difficultyLevel = difficulty
        )

        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.scoreRecordDao().insertRecord(record)
        }
    }

    private fun getCurrentPlayerName(): String {
        val prefs = requireContext().getSharedPreferences("game_prefs", 0)
        return prefs.getString("username", "Аноним") ?: "Анонимчик"
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
            if (System.currentTimeMillis() - lastSpawnTime >= spawnInterval) {
                if (activeBugs.size < maxBugs) {
                    spawnBug()
                }
                lastSpawnTime = System.currentTimeMillis()
            }
            handler.postDelayed(this, 1000/60)
        }
    }

    private fun spawnBug() {
        val bug = getRandomBug()
        val imageView = ImageView(requireContext())

        try {
            val ims = requireContext().assets.open("sprites/bugs/${bug.sprite}")
            val bitmap = BitmapFactory.decodeStream(ims)
            ims.close()
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        val displayMetrics = resources.displayMetrics
        val sizePx = (bug.size * displayMetrics.density*25).toInt()
        val params = FrameLayout.LayoutParams(sizePx, sizePx)
        params.leftMargin = Random.nextInt(0, gameLayout.width - sizePx)
        params.topMargin = Random.nextInt(0, gameLayout.height - sizePx)
        imageView.layoutParams = params

        imageView.setOnClickListener {
            score += bug.points
            tvScore.text = "Очки: $score"
            gameLayout.removeView(imageView)
            activeBugs.removeAll { it.first == imageView }
        }

        gameLayout.addView(imageView)
        activeBugs.add(imageView to bug)
    }

    private fun moveBugs() {
        val iterator = activeBugs.iterator()
        while (iterator.hasNext()) {
            val (bugView, bug) = iterator.next()
            val params = bugView.layoutParams as FrameLayout.LayoutParams

            val (newX, newY) = bug.calculateNewPosition(
                currentX = params.leftMargin,
                currentY = params.topMargin,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                viewWidth = bugView.width,
                viewHeight = bugView.height,
                speedMultiplier = bugSpeedMultiplier
            )

            params.leftMargin = newX
            params.topMargin = newY
            bugView.layoutParams = params

            if (params.topMargin > screenHeight) {
                gameLayout.removeView(bugView)
                iterator.remove()
            }
        }
    }

    private fun getRandomBug(): Bug {
        if(difficulty < 4)
        return when (Random.nextInt(0, 9)) {
            in 0..2 -> Cockroach() // 30%
            in 3..7 -> Grasshopper() // 50%
            else -> Luntik()  // 20%
        }
        else return when (Random.nextInt(0, 15)) {
            in 0..3 -> Cockroach() // 27%
            in 4..8 -> Grasshopper() // 33%
            in 9 .. 11 -> Luntik() // 20%
            else -> Solodov()  // 20%
        }
    }
}
