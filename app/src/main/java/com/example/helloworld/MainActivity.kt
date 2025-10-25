package com.example.helloworld

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.helloworld.adapters.MainPagerAdapter
import com.example.helloworld.managers.ThemeManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val themeListener = { themeResId: Int ->
        updateTabLayoutColors(themeResId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.addThemeListener(themeListener)
        setContentView(R.layout.activity_main)
        initViews()
        setupViewPager()
        ThemeManager.setTheme(R.style.Theme_MyApp_Ocean, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ThemeManager.removeThemeListener(themeListener)
    }

    @SuppressLint("ResourceType")
    private fun updateTabLayoutColors(themeResId: Int) {
        val typedArray = obtainStyledAttributes(themeResId,
            intArrayOf(
                R.attr.myPrimaryColor,
                R.attr.myBackgroundColor,
                R.attr.myTextColor,
                R.attr.myPrimaryColor
            )
        )

        val selectedColor = typedArray.getColor(0, Color.BLACK)
        val unselectedColor = typedArray.getColor(1, Color.GRAY)
        val indicatorColor = typedArray.getColor(2, Color.BLACK)
        val backgroundColor = typedArray.getColor(3, Color.WHITE)

        typedArray.recycle()

        tabLayout.setBackgroundColor(backgroundColor)
        tabLayout.setSelectedTabIndicatorColor(indicatorColor)

        tabLayout.setTabTextColors(unselectedColor, selectedColor)

        tabLayout.setTabIconTint(
            ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf(-android.R.attr.state_selected)
                ),
                intArrayOf(selectedColor, unselectedColor)
            )
        )
    }

    fun onOceanThemeClicked() {
        ThemeManager.setTheme(R.style.Theme_MyApp_Ocean, this)
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Регистрация"
                1 -> "Правила"
                2 -> "Авторы"
                3 -> "Настройки"
                4 -> "Игра"
                5 -> "Рекорды"
                else -> null
            }
        }.attach()
    }
}