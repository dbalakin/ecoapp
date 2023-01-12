package com.example.myapplication.data.noise_information

import com.example.myapplication.data.common.Field

data class NoiseInfo(
    val features: List<NoiseFeature>,
    val fields: List<Field>,
)
