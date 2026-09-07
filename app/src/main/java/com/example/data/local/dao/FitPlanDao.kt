package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ActivityEntity
import com.example.data.local.entity.CompletedActivityEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.local.entity.WorkoutDayEntity
import com.example.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FitPlanDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfileEntity)

    // Workout Days
    @Query("SELECT * FROM workout_days ORDER BY dayOfWeek ASC")
    fun getAllWorkoutDays(): Flow<List<WorkoutDayEntity>>

    @Query("SELECT * FROM workout_days ORDER BY dayOfWeek ASC")
    suspend fun getAllWorkoutDaysOnce(): List<WorkoutDayEntity>

    @Query("SELECT * FROM workout_days WHERE dayOfWeek = :dayOfWeek LIMIT 1")
    fun getWorkoutDay(dayOfWeek: Int): Flow<WorkoutDayEntity?>

    @Query("SELECT * FROM workout_days WHERE dayOfWeek = :dayOfWeek LIMIT 1")
    suspend fun getWorkoutDayOnce(dayOfWeek: Int): WorkoutDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWorkoutDay(day: WorkoutDayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWorkoutDays(days: List<WorkoutDayEntity>)

    // Activities
    @Query("SELECT * FROM activities WHERE dayOfWeek = :dayOfWeek ORDER BY orderIndex ASC, id ASC")
    fun getActivitiesForDay(dayOfWeek: Int): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE dayOfWeek = :dayOfWeek ORDER BY orderIndex ASC, id ASC")
    suspend fun getActivitiesForDayOnce(dayOfWeek: Int): List<ActivityEntity>

    @Query("SELECT * FROM activities ORDER BY dayOfWeek ASC, orderIndex ASC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities ORDER BY dayOfWeek ASC, orderIndex ASC")
    suspend fun getAllActivitiesOnce(): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE id = :id LIMIT 1")
    fun getActivityById(id: Long): Flow<ActivityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Update
    suspend fun updateActivities(activities: List<ActivityEntity>)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteActivityById(id: Long)

    @Query("DELETE FROM activities WHERE dayOfWeek = :dayOfWeek")
    suspend fun deleteActivitiesForDay(dayOfWeek: Int)

    // Sessions
    @Query("SELECT * FROM workout_sessions WHERE dateString = :dateString LIMIT 1")
    fun getSessionForDate(dateString: String): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE dateString = :dateString LIMIT 1")
    suspend fun getSessionForDateOnce(dateString: String): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions ORDER BY startedAt DESC")
    suspend fun getAllSessionsOnce(): List<WorkoutSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    // Completed Activities
    @Query("SELECT * FROM completed_activities WHERE sessionId = :sessionId")
    fun getCompletedActivitiesForSession(sessionId: Long): Flow<List<CompletedActivityEntity>>

    @Query("SELECT * FROM completed_activities WHERE dateString = :dateString")
    fun getCompletedActivitiesForDate(dateString: String): Flow<List<CompletedActivityEntity>>

    @Query("SELECT * FROM completed_activities WHERE dateString = :dateString")
    suspend fun getCompletedActivitiesForDateOnce(dateString: String): List<CompletedActivityEntity>

    @Query("SELECT * FROM completed_activities WHERE activityName = :activityName AND isCompleted = 1 ORDER BY completedAt DESC LIMIT 1")
    suspend fun getLatestCompletedRecord(activityName: String): CompletedActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletedActivity(completedActivity: CompletedActivityEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletedActivities(completedActivities: List<CompletedActivityEntity>)

    @Query("DELETE FROM completed_activities WHERE sessionId = :sessionId AND activityId = :activityId")
    suspend fun deleteCompletedActivity(sessionId: Long, activityId: Long)

    @Query("SELECT * FROM completed_activities ORDER BY completedAt DESC")
    fun getAllCompletedActivities(): Flow<List<CompletedActivityEntity>>

    @Query("SELECT * FROM completed_activities ORDER BY completedAt DESC")
    suspend fun getAllCompletedActivitiesOnce(): List<CompletedActivityEntity>

    // Full clear for restore
    @Query("DELETE FROM activities")
    suspend fun clearAllActivities()

    @Query("DELETE FROM workout_days")
    suspend fun clearAllWorkoutDays()

    @Query("DELETE FROM workout_sessions")
    suspend fun clearAllSessions()

    @Query("DELETE FROM completed_activities")
    suspend fun clearAllCompletedActivities()
}
