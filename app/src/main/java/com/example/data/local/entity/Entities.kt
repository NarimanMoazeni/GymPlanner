package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ActivityType

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "نیما",
    val age: Int? = 26,
    val hasCompletedOnboarding: Boolean = false
)

@Entity(tableName = "workout_days")
data class WorkoutDayEntity(
    @PrimaryKey val dayOfWeek: Int, // 1=Sat, 2=Sun, 3=Mon, 4=Tue, 5=Wed, 6=Thu, 7=Fri
    val isTrainingDay: Boolean = false,
    val focusName: String = "",
    val focusSubtitle: String = "",
    val estimatedDurationMinutes: Int = 45
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: Int, // 1..7
    val name: String,
    val subtitle: String = "",
    val type: ActivityType = ActivityType.WEIGHT_REPS,
    val orderIndex: Int = 0,
    val weight: Double = 0.0,
    val sets: Int = 3,
    val reps: Int = 10,
    val durationMinutes: Int = 15,
    val distanceKm: Double = 3.0,
    val notes: String = ""
)

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // e.g. "2026-09-07"
    val dayOfWeek: Int,
    val isCompleted: Boolean = false,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val totalActivities: Int = 0,
    val completedActivities: Int = 0
)

@Entity(tableName = "completed_activities")
data class CompletedActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val activityId: Long,
    val dateString: String,
    val activityName: String,
    val activityType: ActivityType,
    val isCompleted: Boolean = true,
    val completedAt: Long = System.currentTimeMillis(),
    val actualWeight: Double = 0.0,
    val actualSets: Int = 0,
    val actualReps: Int = 0,
    val actualDurationMinutes: Int = 0,
    val actualDistanceKm: Double = 0.0
)
