package com.example.uasproject.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.uasproject.data.model.FootballMatch
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.util.Date

class MatchRepository private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        .create()

    companion object {
        private const val TAG = "MatchRepository"
        private const val PREFS_NAME = "football_match_prefs"
        private const val KEY_MATCHES = "matches"

        @Volatile
        private var INSTANCE: MatchRepository? = null

        /**
         * Singleton pattern untuk memastikan hanya ada satu instance repository
         */
        fun getInstance(context: Context): MatchRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MatchRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    /**
     * Menyimpan semua pertandingan ke SharedPreferences
     */
    private fun saveMatches(matches: List<FootballMatch>) {
        try {
            val json = gson.toJson(matches)
            sharedPreferences.edit().putString(KEY_MATCHES, json).apply()
            Log.d(TAG, "Successfully saved ${matches.size} matches")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving matches: ${e.message}", e)
        }
    }

    /**
     * Mengambil semua pertandingan dari SharedPreferences
     */
    fun getAllMatches(): List<FootballMatch> {
        val json = sharedPreferences.getString(KEY_MATCHES, null) ?: return emptyList()
        val type = object : TypeToken<List<FootballMatch>>() {}.type
        return try {
            gson.fromJson<List<FootballMatch>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Menambah pertandingan baru
     */
    fun addMatch(match: FootballMatch): Boolean {
        return try {
            val matches = getAllMatches().toMutableList()
            matches.add(match)
            saveMatches(matches)
            Log.d(TAG, "Match added: ${match.getTitle()}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding match: ${e.message}", e)
            false
        }
    }

    fun insertMatch(match: FootballMatch): Boolean {
        return addMatch(match)
    }

    /**
     * Update pertandingan yang sudah ada
     */
    fun updateMatch(match: FootballMatch): Boolean {
        return try {
            val matches = getAllMatches().toMutableList()
            val index = matches.indexOfFirst { it.id == match.id }

            if (index != -1) {
                matches[index] = match
                saveMatches(matches)
                Log.d(TAG, "Match updated: ${match.getTitle()}")
                true
            } else {
                Log.w(TAG, "Match not found for update: ${match.id}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating match: ${e.message}", e)
            false
        }
    }

    /**
     * Hapus pertandingan berdasarkan ID
     */
    fun deleteMatch(matchId: String): Boolean {
        return try {
            val matches = getAllMatches().toMutableList()
            val removed = matches.removeIf { it.id == matchId }
            if (removed) {
                saveMatches(matches)
                Log.d(TAG, "Match deleted: $matchId")
            } else {
                Log.w(TAG, "Match not found for deletion: $matchId")
            }
            removed
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting match: ${e.message}", e)
            false
        }
    }

    /**
     * Hapus pertandingan berdasarkan object
     */
    fun deleteMatch(match: FootballMatch): Boolean {
        return deleteMatch(match.id)
    }

    /**
     * Cari pertandingan berdasarkan ID
     */
    fun getMatchById(matchId: String): FootballMatch? {
        return try {
            getAllMatches().find { it.id == matchId }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting match by ID: ${e.message}", e)
            null
        }
    }

    /**
     * Dapatkan pertandingan yang akan datang (belum lewat)
     */
    fun getUpcomingMatches(): List<FootballMatch> {
        return try {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val todayEnd = calendar.time

            getAllMatches()
                .filter { it.matchDateTime.after(todayEnd) } // 🔥 BESOK KE ATAS
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting upcoming matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan pertandingan yang sudah lewat
     */
    fun getPastMatches(): List<FootballMatch> {
        return try {
            getAllMatches()
                .filter { it.isPast() }
                .sortedByDescending { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting past matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan pertandingan hari ini
     */
    fun getTodayMatches(): List<FootballMatch> {
        return try {
            getAllMatches()
                .filter { it.isToday() }
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan pertandingan besok
     */
    fun getTomorrowMatches(): List<FootballMatch> {
        return try {
            getAllMatches()
                .filter { it.isTomorrow() }
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tomorrow matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan pertandingan minggu ini
     */
    fun getThisWeekMatches(): List<FootballMatch> {
        return try {
            val calendar = Calendar.getInstance()

            // Set ke awal minggu (Minggu)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val weekStart = calendar.time

            // Set ke akhir minggu (Sabtu)
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val weekEnd = calendar.time

            getAllMatches()
                .filter { it.matchDateTime in weekStart..weekEnd }
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting this week matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan pertandingan berdasarkan kompetisi
     */
    fun getMatchesByCompetition(competition: String): List<FootballMatch> {
        return try {
            getAllMatches()
                .filter { it.competition.equals(competition, ignoreCase = true) }
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting matches by competition: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan pertandingan berdasarkan tim
     */
    fun getMatchesByTeam(teamName: String): List<FootballMatch> {
        return try {
            getAllMatches()
                .filter {
                    it.homeTeam.contains(teamName, ignoreCase = true) ||
                            it.awayTeam.contains(teamName, ignoreCase = true)
                }
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting matches by team: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan pertandingan yang akan dimulai dalam waktu dekat
     * @param minutesThreshold batas waktu dalam menit (default 60 menit)
     */
    fun getImmediateUpcomingMatches(minutesThreshold: Int = 60): List<FootballMatch> {
        return try {
            getAllMatches()
                .filter { it.isUpcoming(minutesThreshold) }
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting immediate upcoming matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Cari pertandingan berdasarkan keyword
     */
    fun searchMatches(keyword: String): List<FootballMatch> {
        return try {
            if (keyword.isBlank()) return getAllMatches()

            getAllMatches()
                .filter { match ->
                    match.homeTeam.contains(keyword, ignoreCase = true) ||
                            match.awayTeam.contains(keyword, ignoreCase = true) ||
                            match.competition.contains(keyword, ignoreCase = true) ||
                            match.venue.contains(keyword, ignoreCase = true)
                }
                .sortedBy { it.matchDateTime }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching matches: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan jumlah total pertandingan
     */
    fun getMatchCount(): Int {
        return try {
            getAllMatches().size
        } catch (e: Exception) {
            Log.e(TAG, "Error getting match count: ${e.message}", e)
            0
        }
    }

    /**
     * Dapatkan jumlah pertandingan upcoming
     */
    fun getUpcomingMatchCount(): Int {
        return getUpcomingMatches().size
    }

    /**
     * Dapatkan jumlah pertandingan hari ini
     */
    fun getTodayMatchCount(): Int {
        return getTodayMatches().size
    }

    /**
     * Dapatkan jumlah pertandingan besok
     */
    fun getTomorrowMatchCount(): Int {
        return getTomorrowMatches().size
    }

    /**
     * Hapus semua pertandingan
     */
    fun deleteAllMatches(): Boolean {
        return try {
            sharedPreferences.edit().remove(KEY_MATCHES).apply()
            Log.d(TAG, "All matches deleted")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all matches: ${e.message}", e)
            false
        }
    }

    /**
     * Hapus pertandingan yang sudah lewat
     */
    fun deletePastMatches(): Int {
        return try {
            val matches = getAllMatches().toMutableList()
            val initialSize = matches.size

            matches.removeIf { it.isPast() }
            saveMatches(matches)

            val deletedCount = initialSize - matches.size
            Log.d(TAG, "Deleted $deletedCount past matches")
            deletedCount
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting past matches: ${e.message}", e)
            0
        }
    }

    /**
     * Dapatkan kompetisi unik dari semua pertandingan
     */
    fun getAllCompetitions(): List<String> {
        return try {
            getAllMatches()
                .map { it.competition }
                .distinct()
                .sorted()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting competitions: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan semua tim unik
     */
    fun getAllTeams(): List<String> {
        return try {
            val matches = getAllMatches()
            val teams = mutableSetOf<String>()
            matches.forEach {
                teams.add(it.homeTeam)
                teams.add(it.awayTeam)
            }
            teams.sorted()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting teams: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Dapatkan statistik pertandingan
     */
    fun getMatchStatistics(): MatchStatistics {
        return try {
            val allMatches = getAllMatches()
            MatchStatistics(
                totalMatches = allMatches.size,
                upcomingMatches = allMatches.count { !it.isPast() },
                pastMatches = allMatches.count { it.isPast() },
                todayMatches = allMatches.count { it.isToday() },
                tomorrowMatches = allMatches.count { it.isTomorrow() },
                thisWeekMatches = getThisWeekMatches().size,
                totalCompetitions = getAllCompetitions().size,
                totalTeams = getAllTeams().size
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting match statistics: ${e.message}", e)
            MatchStatistics()
        }
    }

    /**
     * Export data ke JSON string (untuk backup)
     */
    fun exportToJson(): String? {
        return try {
            gson.toJson(getAllMatches())
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting to JSON: ${e.message}", e)
            null
        }
    }

    /**
     * Import data dari JSON string (untuk restore)
     */
    fun importFromJson(json: String): Boolean {
        return try {
            val type = object : TypeToken<List<FootballMatch>>() {}.type
            val matches: List<FootballMatch> = gson.fromJson(json, type)
            saveMatches(matches)
            Log.d(TAG, "Imported ${matches.size} matches")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error importing from JSON: ${e.message}", e)
            false
        }
    }
}

/**
 * Data class untuk statistik pertandingan
 */
data class MatchStatistics(
    val totalMatches: Int = 0,
    val upcomingMatches: Int = 0,
    val pastMatches: Int = 0,
    val todayMatches: Int = 0,
    val tomorrowMatches: Int = 0,
    val thisWeekMatches: Int = 0,
    val totalCompetitions: Int = 0,
    val totalTeams: Int = 0
)