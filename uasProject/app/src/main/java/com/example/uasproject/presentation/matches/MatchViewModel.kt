package com.example.uasproject.presentation.matches

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.uasproject.data.model.FootballMatch
import com.example.uasproject.data.repository.MatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel untuk mengelola data pertandingan
 * Mendukung LiveData & StateFlow
 */
class MatchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MatchRepository.getInstance(application)

    // ===== StateFlow (Jetpack Compose) =====
    private val _matches = MutableStateFlow<List<FootballMatch>>(emptyList())
    val matches: StateFlow<List<FootballMatch>> = _matches.asStateFlow()

    private val _upcomingMatches = MutableStateFlow<List<FootballMatch>>(emptyList())
    val upcomingMatches: StateFlow<List<FootballMatch>> = _upcomingMatches.asStateFlow()

    private val _todayMatches = MutableStateFlow<List<FootballMatch>>(emptyList())
    val todayMatches: StateFlow<List<FootballMatch>> = _todayMatches.asStateFlow()

    private val _pastMatches = MutableStateFlow<List<FootballMatch>>(emptyList())
    val pastMatches: StateFlow<List<FootballMatch>> = _pastMatches.asStateFlow()

    private val _selectedMatch = MutableStateFlow<FootballMatch?>(null)
    val selectedMatch: StateFlow<FootballMatch?> = _selectedMatch.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ===== LiveData (XML / legacy) =====
    private val _matchesLiveData = MutableLiveData<List<FootballMatch>>()
    val matchesLiveData: LiveData<List<FootballMatch>> = _matchesLiveData

    private val _upcomingMatchesLiveData = MutableLiveData<List<FootballMatch>>()
    val upcomingMatchesLiveData: LiveData<List<FootballMatch>> = _upcomingMatchesLiveData

    // ===== Statistik =====
    private val _totalMatches = MutableStateFlow(0)
    val totalMatches: StateFlow<Int> = _totalMatches.asStateFlow()

    private val _upcomingCount = MutableStateFlow(0)
    val upcomingCount: StateFlow<Int> = _upcomingCount.asStateFlow()

    init {
        loadAllMatches()
    }

    /**
     * Load semua pertandingan
     */
    fun loadAllMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    val allMatches = repository.getAllMatches()

                    _matches.value = allMatches
                    _matchesLiveData.postValue(allMatches)
                    _totalMatches.value = allMatches.size

                    _upcomingMatches.value = repository.getUpcomingMatches()
                    _upcomingMatchesLiveData.postValue(_upcomingMatches.value)
                    _upcomingCount.value = _upcomingMatches.value.size

                    _todayMatches.value = repository.getTodayMatches()
                    _pastMatches.value = repository.getPastMatches()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Tambah pertandingan
     */
    fun addMatch(match: FootballMatch, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = withContext(Dispatchers.IO) {
                    repository.addMatch(match)
                }
                if (success) loadAllMatches()
                onResult(success)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menambah pertandingan: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update pertandingan
     */
    fun updateMatch(match: FootballMatch, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = withContext(Dispatchers.IO) {
                    repository.updateMatch(match)
                }
                if (success) loadAllMatches()
                onResult(success)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal update pertandingan: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Hapus pertandingan
     */
    fun deleteMatch(matchId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = withContext(Dispatchers.IO) {
                    repository.deleteMatch(matchId)
                }
                if (success) loadAllMatches()
                onResult(success)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal hapus pertandingan: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Ambil pertandingan berdasarkan ID
     */
    fun getMatchById(matchId: String) {
        viewModelScope.launch {
            try {
                _selectedMatch.value = withContext(Dispatchers.IO) {
                    repository.getMatchById(matchId)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat pertandingan: ${e.message}"
            }
        }
    }

    /**
     * Cari pertandingan
     */
    fun searchMatches(keyword: String) {
        viewModelScope.launch {
            try {
                val results = withContext(Dispatchers.IO) {
                    repository.searchMatches(keyword)
                }
                _matches.value = results
                _matchesLiveData.postValue(results)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mencari: ${e.message}"
            }
        }
    }

    /**
     * Filter berdasarkan kompetisi
     */
    fun filterByCompetition(competition: String) {
        viewModelScope.launch {
            try {
                _matches.value = withContext(Dispatchers.IO) {
                    repository.getMatchesByCompetition(competition)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal filter: ${e.message}"
            }
        }
    }

    /**
     * Filter berdasarkan tim
     */
    fun filterByTeam(teamName: String) {
        viewModelScope.launch {
            try {
                _matches.value = withContext(Dispatchers.IO) {
                    repository.getMatchesByTeam(teamName)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal filter: ${e.message}"
            }
        }
    }

    /**
     * Hapus semua pertandingan
     */
    fun deleteAllMatches(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = withContext(Dispatchers.IO) {
                    repository.deleteAllMatches()
                }
                if (success) loadAllMatches()
                onResult(success)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal hapus semua: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Hapus pertandingan yang sudah lewat
     */
    fun deletePastMatches(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val deletedCount = withContext(Dispatchers.IO) {
                    repository.deletePastMatches()
                }
                loadAllMatches()
                onResult(deletedCount)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal hapus pertandingan lama: ${e.message}"
                onResult(0)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Statistik pertandingan
     */
    fun getMatchStatistics(): MatchStatistics {
        return MatchStatistics(
            total = _matches.value.size,
            upcoming = _upcomingMatches.value.size,
            today = _todayMatches.value.size,
            past = _pastMatches.value.size,
            competitions = getUniqueCompetitions().size
        )
    }

    fun getUniqueCompetitions(): List<String> =
        _matches.value.map { it.competition }.distinct().sorted()

    fun getUniqueTeams(): List<String> =
        _matches.value.flatMap { listOf(it.homeTeam, it.awayTeam) }.distinct().sorted()

    fun clearError() {
        _errorMessage.value = null
    }

    fun refresh() {
        loadAllMatches()
    }
}

/**
 * Statistik pertandingan
 */
data class MatchStatistics(
    val total: Int,
    val upcoming: Int,
    val today: Int,
    val past: Int,
    val competitions: Int
)
