# Shamsi (Jalali) & Gregorian Calendar Engine Reference

## 1. PersianDateHelper Overview
The engine in `com.example.util.PersianDateHelper` provides zero-dependency, pure mathematical conversions between Jalali (Hijri Shamsi) and Gregorian dates.

### Month Durations
- Months 1 to 6 (Farvardin to Shahrivar): 31 days each.
- Months 7 to 11 (Mehr to Bahman): 30 days each.
- Month 12 (Esfand): 29 days (30 days in leap years).

### Day of Week Alignment
- Saturday = شنبه (Index 0 in Persian weekly bar)
- Sunday = یکشنبه
- Monday = دوشنبه
- Tuesday = سه‌شنبه
- Wednesday = چهارشنبه
- Thursday = پنجشنبه
- Friday = جمعه (Index 6)

### Weekday Chip Wrapping Rule
To avoid breaking Persian weekday names (e.g. «پنجشنبه» splitting into two lines):
```kotlin
Text(
    text = dayName,
    maxLines = 1,
    softWrap = false,
    letterSpacing = 0.sp,
    modifier = Modifier.wrapContentSize()
)
```
