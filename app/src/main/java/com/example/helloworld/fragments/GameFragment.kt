package com.example.helloworld.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.helloworld.R
import com.example.helloworld.data.AppDatabase
import com.example.helloworld.data.ScoreRecord
import com.example.helloworld.model.enemy.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class GameFragment : Fragment(), SensorEventListener {

    private lateinit var gameLayout: FrameLayout
    private lateinit var tvScore: TextView
    private lateinit var tvMiss: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvBonus: TextView
    private lateinit var btnStart: Button

    private var score = 0
    private var miss = 0
    private var isPlaying = false

    private val handler = Handler(Looper.getMainLooper())
    private var screenWidth = 0
    private var screenHeight = 0

    private val activeBugs = mutableListOf<Pair<ImageView, Bug>>()
    private var bonusView: ImageView? = null
    private var isBonusActive = false
    private var bonusEndTime = 0L

    private var spawnInterval = 1200L
    private var maxBugs = 5
    private var bugSpeedMultiplier = 1.0
    private var roundDuration = 30000L

    private var difficulty = 1
    private var lastSpawnTime = System.currentTimeMillis()
    private var lastBonusSpawnTime = System.currentTimeMillis()

    private var timer: CountDownTimer? = null

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var accelerometerX = 0f
    private var accelerometerY = 0f

    private var mediaPlayer: MediaPlayer? = null

    private var viewPager: ViewPager2? = null

    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_game, container, false)
        initViews()
        setupSensors()
        setupTouchListener()
        loadSettings()
        findViewPager()
        return view
    }

    private fun findViewPager() {
        viewPager = activity?.findViewById(R.id.viewPager)
    }

    private fun setSwipeEnabled(enabled: Boolean) {
        viewPager?.isUserInputEnabled = enabled
    }

    private fun initViews() {
        gameLayout = view.findViewById(R.id.gameLayout)
        tvScore = view.findViewById(R.id.tvScore)
        tvMiss = view.findViewById(R.id.tvMiss)
        tvTimer = view.findViewById(R.id.tvTimer)
        tvBonus = view.findViewById(R.id.tvBonus)
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

    private fun setupSensors() {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor == null)
            throw IllegalArgumentException("Sensor is null...")
        accelerometer = sensor
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

        setSwipeEnabled(false)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        handler.post(spawnRunnable)
        startTimer()

        lastBonusSpawnTime = System.currentTimeMillis()
    }

    private fun stopGame() {
        isPlaying = false
        isBonusActive = false
        timer?.cancel()
        handler.removeCallbacks(spawnRunnable)

        setSwipeEnabled(true)

        sensorManager.unregisterListener(this)

        tvBonus.visibility = View.GONE
        bonusView?.let {
            gameLayout.removeView(it)
            bonusView = null
        }

        mediaPlayer?.release()
        mediaPlayer = null

        for (pair in activeBugs) {
            gameLayout.removeView(pair.first);
        }
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
            val dao = database.scoreRecordDao()

            val existingRecord = dao.getRecordByPlayer(playerName)

            if (existingRecord != null) {
                val updatedRecord = existingRecord.copy(
                    playerName = record.playerName,
                    score = record.score,
                    difficultyLevel = record.difficultyLevel,
                )
                dao.updateRecord(updatedRecord)
            } else {
                dao.insertRecord(record)
            }
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

            if (System.currentTimeMillis() - lastBonusSpawnTime >= 15000 && bonusView == null) {
                spawnBonus()
                lastBonusSpawnTime = System.currentTimeMillis()
            }

            if (isBonusActive && System.currentTimeMillis() > bonusEndTime) {
                deactivateBonus()
            }

            if (System.currentTimeMillis() - lastSpawnTime >= spawnInterval) {
                if (activeBugs.size < maxBugs) {
                    spawnBug()
                }
                lastSpawnTime = System.currentTimeMillis()
            }
            handler.postDelayed(this, 1000/60)
        }
    }

    private fun spawnBonus() {
        val imageView = ImageView(requireContext())
        imageView.setImageResource(R.drawable.bonus)

        val displayMetrics = resources.displayMetrics
        val sizePx = (100 * displayMetrics.density).toInt()
        val params = FrameLayout.LayoutParams(sizePx, sizePx)
        params.leftMargin = Random.nextInt(0, gameLayout.width - sizePx)
        params.topMargin = Random.nextInt(0, gameLayout.height - sizePx)
        imageView.layoutParams = params

        imageView.setOnClickListener {
            if (isPlaying && !isBonusActive) {
                activateBonus()
                gameLayout.removeView(imageView)
                bonusView = null
            }
        }

        gameLayout.addView(imageView)
        bonusView = imageView

        handler.postDelayed({
            if (bonusView == imageView) {
                gameLayout.removeView(imageView)
                bonusView = null
            }
        }, 5000)
    }

    private fun activateBonus() {
        isBonusActive = true
        bonusEndTime = System.currentTimeMillis() + 10000 // 10 секунд действия

        tvBonus.visibility = View.VISIBLE

        playScreamSound()

        handler.postDelayed({
            tvBonus.visibility = View.GONE
        }, 2000)

        Toast.makeText(requireContext(), "Бонус активирован! Наклоняйте телефон!", Toast.LENGTH_SHORT).show()
    }

    private fun deactivateBonus() {
        isBonusActive = false
        tvBonus.visibility = View.GONE
        Toast.makeText(requireContext(), "Бонус закончился", Toast.LENGTH_SHORT).show()
    }

    private fun playScreamSound() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bonus)
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
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

            val (newX, newY) = if (isBonusActive) {
                calculateBugPositionWithTilt(
                    currentX = params.leftMargin,
                    currentY = params.topMargin,
                    viewWidth = bugView.width,
                    viewHeight = bugView.height,
                    bug = bug
                )
            } else {
                bug.calculateNewPosition(
                    currentX = params.leftMargin,
                    currentY = params.topMargin,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    viewWidth = bugView.width,
                    viewHeight = bugView.height,
                    speedMultiplier = bugSpeedMultiplier
                )
            }

            params.leftMargin = newX
            params.topMargin = newY
            bugView.layoutParams = params

            if (params.topMargin > screenHeight) {
                gameLayout.removeView(bugView)
                iterator.remove()
            }
        }
    }

    private fun calculateBugPositionWithTilt(
        currentX: Int,
        currentY: Int,
        viewWidth: Int,
        viewHeight: Int,
        bug: Bug
    ): Pair<Int, Int> {
        val tiltMultiplier = 12f
        var newX = currentX + (accelerometerX * tiltMultiplier).toInt()
        var newY = currentY + (accelerometerY * tiltMultiplier).toInt()

        newX = newX.coerceIn(0, screenWidth - viewWidth)
        newY = newY.coerceIn(0, screenHeight - viewHeight)

        return Pair(newX, newY)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && isBonusActive) {
            accelerometerX = event.values[0]
            accelerometerY = event.values[1]

            accelerometerX = -accelerometerX
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        mediaPlayer?.release()
    }

    private fun getRandomBug(): Bug {
        if (difficulty < 4)
            return when (Random.nextInt(0, 9)) {
                in 0..2 -> Cockroach() // 30%
                in 3..7 -> Grasshopper() // 50%
                else -> Luntik()  // 20%
            }
        return when (Random.nextInt(0, 15)) {
            in 0..3 -> Cockroach() // 27%
            in 4..8 -> Grasshopper() // 33%
            in 9 .. 11 -> Luntik() // 20%
            else -> Solodov()  // 20%
        }
//            Как получить курс золота из кеша
//        // В любом Activity/Fragment
//        val prefs = getSharedPreferences("gold_widget", Context.MODE_PRIVATE)
//        val goldRate = prefs.getString("current_rate", "₽ 0.0") ?: "₽ 0.0"
//
//// Преобразовать в число для расчетов
//        val rateValue = goldRate.replace("₽", "").replace(" ", "").toDoubleOrNull() ?: 0.0
//
//// Начислить очки за таракана
//        val cockroachPoints = (basePoints * rateValue).toInt()
    }
}