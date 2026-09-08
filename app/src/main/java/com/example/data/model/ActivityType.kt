package com.example.data.model

import java.util.Locale
import kotlin.math.roundToInt

enum class ActivityType {
    WEIGHT_REPS,
    DURATION,
    DISTANCE,
    SIMPLE
}

enum class WeightUnit {
    KG, LBS;

    companion object {
        // Standard international conversion: 1 kg ≈ 2.20462 lbs
        const val KG_TO_LBS = 2.20462
        const val LBS_TO_KG = 1.0 / KG_TO_LBS

        /**
         * Converts canonical weight in KG to target unit value.
         */
        fun fromKg(weightInKg: Double, targetUnit: WeightUnit): Double {
            return when (targetUnit) {
                KG -> weightInKg
                LBS -> {
                    val converted = weightInKg * KG_TO_LBS
                    (converted * 10.0).roundToInt() / 10.0
                }
            }
        }

        /**
         * Converts entered weight in unit to canonical weight in KG.
         */
        fun toKg(weightInUnit: Double, sourceUnit: WeightUnit): Double {
            return when (sourceUnit) {
                KG -> weightInUnit
                LBS -> {
                    val converted = weightInUnit * LBS_TO_KG
                    (converted * 100.0).roundToInt() / 100.0
                }
            }
        }

        /**
         * Formats weight to a clean string suitable for display.
         * If it's a whole number (e.g. 20.0), displays "20".
         * Otherwise displays 1 decimal place (e.g. "44.1").
         */
        fun formatDisplay(weightInKg: Double, targetUnit: WeightUnit): String {
            val value = fromKg(weightInKg, targetUnit)
            return if (value % 1.0 == 0.0) {
                value.toLong().toString()
            } else {
                String.format(Locale.US, "%.1f", value)
            }
        }

        fun getUnitLabel(unit: WeightUnit, isPersian: Boolean = true): String {
            return if (isPersian) {
                if (unit == KG) "کیلوگرم" else "پوند"
            } else {
                if (unit == KG) "kg" else "lbs"
            }
        }
    }
}
