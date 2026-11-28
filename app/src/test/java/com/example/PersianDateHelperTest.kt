package com.example

import com.example.ui.locale.AppLanguage
import com.example.util.PersianDateHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class PersianDateHelperTest {

    @Test
    fun dateKeys_alwaysUseAsciiDigits_evenUnderPersianDefaultLocale() {
        val originalLocale = Locale.getDefault()
        try {
            // Set default locale to Persian/Farsi (Iran)
            Locale.setDefault(Locale("fa", "IR"))

            val todayKey = PersianDateHelper.getTodayDateKey()
            // Must strictly match YYYY-MM-DD in standard ASCII digits
            assertTrue("todayKey must match ASCII format: $todayKey", todayKey.matches(Regex("""^\d{4}-\d{2}-\d{2}$""")))

            for (dayIndex in 1..7) {
                val dayKey = PersianDateHelper.getDateKeyForDayOfWeek(dayIndex)
                assertTrue("dayKey for day $dayIndex must match ASCII format: $dayKey", dayKey.matches(Regex("""^\d{4}-\d{2}-\d{2}$""")))
            }
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun daysInPersianMonth_returnsCorrectCount() {
        // First 6 months have 31 days
        for (m in 1..6) {
            assertEquals("Month $m should have 31 days", 31, PersianDateHelper.getDaysInPersianMonth(1403, m))
        }

        // Months 7..11 have 30 days
        for (m in 7..11) {
            assertEquals("Month $m should have 30 days", 30, PersianDateHelper.getDaysInPersianMonth(1403, m))
        }

        // Esfand (month 12):
        // 1403 is a leap year (1403 % 33 == 17) -> 30 days
        assertEquals("1403 Esfand should have 30 days (leap)", 30, PersianDateHelper.getDaysInPersianMonth(1403, 12))
        // 1404 is not a leap year -> 29 days
        assertEquals("1404 Esfand should have 29 days (normal)", 29, PersianDateHelper.getDaysInPersianMonth(1404, 12))
    }

    @Test
    fun bidirectionalDateConversion_isAccurate() {
        // 2024-03-20 -> 1403-01-01 (Nowruz)
        val (jy1, jm1, jd1) = PersianDateHelper.gregorianToPersian(2024, 3, 20)
        assertEquals(1403, jy1)
        assertEquals(1, jm1)
        assertEquals(1, jd1)

        val (gy1, gm1, gd1) = PersianDateHelper.persianToGregorian(1403, 1, 1)
        assertEquals(2024, gy1)
        assertEquals(3, gm1)
        assertEquals(20, gd1)

        // 2026-11-28 -> 1405-09-07
        val (jy2, jm2, jd2) = PersianDateHelper.gregorianToPersian(2026, 11, 28)
        assertEquals(1405, jy2)
        assertEquals(9, jm2)
        assertEquals(7, jd2)

        val (gy2, gm2, gd2) = PersianDateHelper.persianToGregorian(1405, 9, 7)
        assertEquals(2026, gy2)
        assertEquals(11, gm2)
        assertEquals(28, gd2)
    }

    @Test
    fun weekDatesInfo_returnsSevenDays() {
        val weekDates = PersianDateHelper.getWeekDatesInfo(AppLanguage.PERSIAN)
        assertEquals(7, weekDates.size)
        for (i in 0 until 7) {
            assertEquals(i + 1, weekDates[i].dayOfWeekIndex)
            assertTrue(weekDates[i].dayOfWeekName.isNotBlank())
            assertTrue(weekDates[i].dayOfMonth in 1..31)
        }
    }
}
