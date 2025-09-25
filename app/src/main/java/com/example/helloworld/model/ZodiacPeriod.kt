package com.example.helloworld.model

data class ZodiacPeriod(
    val name: String,
    val startMonth: Int,
    val startDay: Int,
    val endMonth: Int,
    val endDay: Int,
    val drawableRes: Int
)