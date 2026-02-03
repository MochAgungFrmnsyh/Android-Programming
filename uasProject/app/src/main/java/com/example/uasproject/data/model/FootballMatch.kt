package com.example.uasproject.data.model

import java.io.Serializable
import java.util.Calendar
import java.util.Date

data class FootballMatch(
    val id: String = generateId(),
    val homeTeam: String,
    val awayTeam: String,
    val competition: String,
    val matchDateTime: Date,
    val venue: String = "",
    val matchUrl: String = "",
    val logoHomeTeam: String = "",
    val logoAwayTeam: String = "",
    val notes: String = ""
) : Serializable {

    init {
        require(homeTeam.isNotBlank()) { "Home team tidak boleh kosong" }
        require(awayTeam.isNotBlank()) { "Away team tidak boleh kosong" }
        require(competition.isNotBlank()) { "Competition tidak boleh kosong" }
    }

    companion object {
        private const val serialVersionUID = 1L

        private fun generateId(): String {
            return "match_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}"
        }

        fun getMatchTitle(homeTeam: String, awayTeam: String): String {
            return "$homeTeam vs $awayTeam"
        }
    }

    fun getTitle(): String {
        return getMatchTitle(homeTeam, awayTeam)
    }

    fun isPast(): Boolean {
        val now = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val matchTime = Calendar.getInstance().apply {
            time = matchDateTime
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return matchTime <= now
    }

    fun isUpcoming(minutesThreshold: Int = 60): Boolean {
        if (isPast()) return false

        val now = System.currentTimeMillis()
        val matchTime = matchDateTime.time
        val diffInMillis = matchTime - now
        val diffInMinutes = diffInMillis / (1000 * 60)

        return diffInMinutes in 0..minutesThreshold
    }

    /**
     * Format waktu tersisa hingga pertandingan dimulai
     */
    fun getTimeUntilMatch(): String {
        val now = Date()
        val diffInMillis = matchDateTime.time - now.time

        if (diffInMillis <= 0) {
            return "Pertandingan sudah berlalu"
        }

        val days = diffInMillis / (1000 * 60 * 60 * 24)
        val hours = (diffInMillis / (1000 * 60 * 60)) % 24
        val minutes = (diffInMillis / (1000 * 60)) % 60

        return when {
            days > 0 -> "${days}h ${hours}j lagi"
            hours > 0 -> "${hours}j ${minutes}m lagi"
            else -> "${minutes}m lagi"
        }
    }

    fun getDetailedTimeUntilMatch(): String {
        val now = Date()
        val diffInMillis = matchDateTime.time - now.time

        if (diffInMillis <= 0) {
            return "Pertandingan sudah selesai"
        }

        val days = diffInMillis / (1000 * 60 * 60 * 24)
        val hours = (diffInMillis / (1000 * 60 * 60)) % 24
        val minutes = (diffInMillis / (1000 * 60)) % 60
        val seconds = (diffInMillis / 1000) % 60

        return buildString {
            if (days > 0) append("${days} hari ")
            if (hours > 0) append("${hours} jam ")
            if (minutes > 0) append("${minutes} menit ")
            if (days == 0L && hours == 0L) append("${seconds} detik ")
            append("lagi")
        }.trim()
    }

    fun getMinutesUntilMatch(): Long {
        val now = Date()
        val diffInMillis = matchDateTime.time - now.time
        return diffInMillis / (1000 * 60)
    }

    fun isToday(): Boolean {
        val today = Calendar.getInstance()
        val matchDay = Calendar.getInstance().apply {
            time = matchDateTime
        }

        return today.get(Calendar.YEAR) == matchDay.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == matchDay.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Cek apakah pertandingan besok
     */
    fun isTomorrow(): Boolean {
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val matchDay = Calendar.getInstance().apply {
            time = matchDateTime
        }

        return tomorrow.get(Calendar.YEAR) == matchDay.get(Calendar.YEAR) &&
                tomorrow.get(Calendar.DAY_OF_YEAR) == matchDay.get(Calendar.DAY_OF_YEAR)
    }

    fun getStatus(): String {
        return when {
            isPast() -> "Selesai"
            isUpcoming(60) -> "Segera Dimulai"
            isToday() -> "Hari Ini"
            isTomorrow() -> "Besok"
            else -> "Akan Datang"
        }
    }

    fun getReminderText(): String {
        return when {
            isPast() -> "Pertandingan ${getTitle()} sudah selesai"
            isToday() -> "Hari ini: ${getTitle()} dimulai ${getTimeUntilMatch()}"
            isTomorrow() -> "Besok: ${getTitle()} dimulai ${getTimeUntilMatch()}"
            else -> "${getTitle()} dimulai ${getTimeUntilMatch()}"
        }
    }
}