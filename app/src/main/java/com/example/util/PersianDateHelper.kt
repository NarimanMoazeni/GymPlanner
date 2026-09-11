package com.example.util

import com.example.ui.locale.AppLanguage
import java.util.Calendar

data class FormattedDateInfo(
    val dayOfWeekIndex: Int, // 1=Sat, 2=Sun, 3=Mon, 4=Tue, 5=Wed, 6=Thu, 7=Fri
    val dayOfWeekName: String,
    val dayOfWeekShort: String,
    val dayOfMonth: Int,
    val monthName: String,
    val fullDateString: String // e.g., "سه‌شنبه ۲۶ شهریور" or "Tuesday, Sep 26"
)

object PersianDateHelper {

    private val PERSIAN_DAY_NAMES = listOf("شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه")
    private val PERSIAN_DAY_SHORTS = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    private val PERSIAN_MONTH_NAMES = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    private val ENGLISH_DAY_NAMES = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    private val ENGLISH_DAY_SHORTS = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
    private val ENGLISH_MONTH_NAMES = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    /**
     * Map Java Calendar DAY_OF_WEEK to 1=Sat, 2=Sun, ..., 7=Fri
     */
    fun calendarDayToWeekIndex(calendarDay: Int): Int {
        return when (calendarDay) {
            Calendar.SATURDAY -> 1
            Calendar.SUNDAY -> 2
            Calendar.MONDAY -> 3
            Calendar.TUESDAY -> 4
            Calendar.WEDNESDAY -> 5
            Calendar.THURSDAY -> 6
            Calendar.FRIDAY -> 7
            else -> 1
        }
    }

    /**
     * Convert Gregorian (year, month 1..12, day 1..31) to Solar Hijri (Persian) Date (year, month 1..12, day 1..31)
     */
    fun gregorianToPersian(gYear: Int, gMonth: Int, gDay: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy = gYear - 1600
        val gm = gMonth - 1
        val gd = gDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        for (i in 0 until gm) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..10) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i
                break
            }
            jDayNo -= jDaysInMonth[i]
            jm = i + 1
        }

        val jd = jDayNo + 1
        return Triple(jy, jm + 1, jd)
    }

    /**
     * Convert Solar Hijri (Persian) Date (year, month 1..12, day 1..31) to Gregorian Date (year, month 1..12, day 1..31)
     */
    fun persianToGregorian(jYear: Int, jMonth: Int, jDay: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        val jy = jYear - 979
        val jm = jMonth - 1
        val jd = jDay - 1
        var jDayNo = 365 * jy + (jy / 33) * 8 + ((jy % 33 + 3) / 4)
        for (i in 0 until jm) {
            jDayNo += jDaysInMonth[i]
        }
        jDayNo += jd
        var gDayNo = jDayNo + 79
        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) {
                gDayNo++
            }
        }
        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461
        if (gDayNo >= 366) {
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }
        var i = 0
        while (i < 12) {
            var mDays = gDaysInMonth[i]
            if (i == 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
                mDays = 29
            }
            if (gDayNo < mDays) {
                break
            }
            gDayNo -= mDays
            i++
        }
        val gm = i + 1
        val gd = gDayNo + 1
        return Triple(gy, gm, gd)
    }

    fun getDayOfWeekName(weekIndex: Int, language: AppLanguage): String {
        val idx = (weekIndex - 1).coerceIn(0, 6)
        return if (language == AppLanguage.PERSIAN) PERSIAN_DAY_NAMES[idx] else ENGLISH_DAY_NAMES[idx]
    }

    fun getDayOfWeekShort(weekIndex: Int, language: AppLanguage): String {
        val idx = (weekIndex - 1).coerceIn(0, 6)
        return if (language == AppLanguage.PERSIAN) PERSIAN_DAY_SHORTS[idx] else ENGLISH_DAY_SHORTS[idx]
    }

    fun getTodayDateInfo(language: AppLanguage): FormattedDateInfo {
        val cal = Calendar.getInstance()
        val gYear = cal.get(Calendar.YEAR)
        val gMonth = cal.get(Calendar.MONTH) + 1
        val gDay = cal.get(Calendar.DAY_OF_MONTH)
        val weekIndex = calendarDayToWeekIndex(cal.get(Calendar.DAY_OF_WEEK))

        return if (language == AppLanguage.PERSIAN) {
            val (_, pMonth, pDay) = gregorianToPersian(gYear, gMonth, gDay)
            val monthName = PERSIAN_MONTH_NAMES[(pMonth - 1).coerceIn(0, 11)]
            val dayName = PERSIAN_DAY_NAMES[(weekIndex - 1).coerceIn(0, 6)]
            val shortDay = PERSIAN_DAY_SHORTS[(weekIndex - 1).coerceIn(0, 6)]
            val fullDate = "$dayName $pDay $monthName"
            FormattedDateInfo(
                dayOfWeekIndex = weekIndex,
                dayOfWeekName = dayName,
                dayOfWeekShort = shortDay,
                dayOfMonth = pDay,
                monthName = monthName,
                fullDateString = fullDate
            )
        } else {
            val monthName = ENGLISH_MONTH_NAMES[(gMonth - 1).coerceIn(0, 11)]
            val dayName = ENGLISH_DAY_NAMES[(weekIndex - 1).coerceIn(0, 6)]
            val shortDay = ENGLISH_DAY_SHORTS[(weekIndex - 1).coerceIn(0, 6)]
            val fullDate = "$dayName, $monthName $gDay"
            FormattedDateInfo(
                dayOfWeekIndex = weekIndex,
                dayOfWeekName = dayName,
                dayOfWeekShort = shortDay,
                dayOfMonth = gDay,
                monthName = monthName,
                fullDateString = fullDate
            )
        }
    }

    /**
     * Get 7 days date info for the current week (from Saturday to Friday)
     */
    fun getWeekDatesInfo(language: AppLanguage): List<FormattedDateInfo> {
        val todayCal = Calendar.getInstance()
        val currentCalDay = todayCal.get(Calendar.DAY_OF_WEEK)
        val currentWeekIndex = calendarDayToWeekIndex(currentCalDay)

        // Find Saturday of this week:
        // offset from Saturday (weekIndex 1) = currentWeekIndex - 1
        val offsetFromSaturday = currentWeekIndex - 1

        val list = mutableListOf<FormattedDateInfo>()
        for (i in 1..7) {
            val dayCal = Calendar.getInstance().apply {
                timeInMillis = todayCal.timeInMillis
                add(Calendar.DAY_OF_YEAR, (i - 1) - offsetFromSaturday)
            }
            val gy = dayCal.get(Calendar.YEAR)
            val gm = dayCal.get(Calendar.MONTH) + 1
            val gd = dayCal.get(Calendar.DAY_OF_MONTH)

            if (language == AppLanguage.PERSIAN) {
                val (_, pMonth, pDay) = gregorianToPersian(gy, gm, gd)
                val mName = PERSIAN_MONTH_NAMES[(pMonth - 1).coerceIn(0, 11)]
                val dName = PERSIAN_DAY_NAMES[i - 1]
                val sName = PERSIAN_DAY_SHORTS[i - 1]
                list.add(
                    FormattedDateInfo(
                        dayOfWeekIndex = i,
                        dayOfWeekName = dName,
                        dayOfWeekShort = sName,
                        dayOfMonth = pDay,
                        monthName = mName,
                        fullDateString = "$dName $pDay $mName"
                    )
                )
            } else {
                val mName = ENGLISH_MONTH_NAMES[(gm - 1).coerceIn(0, 11)]
                val dName = ENGLISH_DAY_NAMES[i - 1]
                val sName = ENGLISH_DAY_SHORTS[i - 1]
                list.add(
                    FormattedDateInfo(
                        dayOfWeekIndex = i,
                        dayOfWeekName = dName,
                        dayOfWeekShort = sName,
                        dayOfMonth = gd,
                        monthName = mName,
                        fullDateString = "$dName, $mName $gd"
                    )
                )
            }
        }
        return list
    }

    fun getTodayDateKey(): String {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", y, m, d)
    }

    fun getDateKeyForDayOfWeek(dayOfWeek: Int): String {
        val todayCal = Calendar.getInstance()
        val currentWeekIndex = calendarDayToWeekIndex(todayCal.get(Calendar.DAY_OF_WEEK))
        val offset = (dayOfWeek - 1) - (currentWeekIndex - 1)
        val targetCal = Calendar.getInstance().apply {
            timeInMillis = todayCal.timeInMillis
            add(Calendar.DAY_OF_YEAR, offset)
        }
        val y = targetCal.get(Calendar.YEAR)
        val m = targetCal.get(Calendar.MONTH) + 1
        val d = targetCal.get(Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", y, m, d)
    }
}
