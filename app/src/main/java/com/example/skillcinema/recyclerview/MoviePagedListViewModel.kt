package com.example.skillcinema.recyclerview

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.entity.FilmsPremier
import com.example.skillcinema.entity.FilmsSerDramDet
import com.example.skillcinema.entity.FilmsTopPopular
import com.example.skillcinema.entity.MovieInfo
import com.example.skillcinema.recyclerview.detectives.DetectivesPagedSource
import com.example.skillcinema.recyclerview.dram.DramPagedSource
import com.example.skillcinema.recyclerview.popular.PopularPagedSource
import com.example.skillcinema.recyclerview.ser.SerPagedSource
import com.example.skillcinema.recyclerview.top.TopPagedSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class MoviePagedListViewModel private constructor(
    private val repository: MoviePagedListRepository
) : ViewModel() {
    constructor() : this(MoviePagedListRepository())

    private val _filmsSer = MutableStateFlow<List<FilmsSerDramDet>>(emptyList())
    private val _filmsDram = MutableStateFlow<List<FilmsSerDramDet>>(emptyList())
    private val _filmsDet = MutableStateFlow<List<FilmsSerDramDet>>(emptyList())
    private val _filmsTop = MutableStateFlow<List<FilmsTopPopular>>(emptyList())
    private val _filmsPopular = MutableStateFlow<List<FilmsTopPopular>>(emptyList())
    private val _filmsPremier = MutableStateFlow<List<FilmsPremier>>(emptyList())
    val filmsSer = _filmsSer.asStateFlow()
    val filmsDram = _filmsDram.asStateFlow()
    val filmsDet = _filmsDet.asStateFlow()
    val filmsTop = _filmsTop.asStateFlow()
    val filmsPopular = _filmsPopular.asStateFlow()
    val filmsPremier = _filmsPremier.asStateFlow()


    init {
        loadFilmsSer()
        loadFilmsDram()
        loadFilmsDet()
        loadFilmsTop()
        loadFilmsPopular()
        loadFilmsPremier()
    }

    suspend fun infoMovie(url: String): MovieInfo {
        return MoviePagedListRepository().getMovieInfo(url)
    }

    //Методы получения списков для главного экрана(20 шт.)
    private fun loadFilmsSer() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getSerListHome()
            }.fold(
                onSuccess = { _filmsSer.value = it },
                onFailure = { Log.d("filmsSer", it.message ?: "") }
            )
        }
    }

    private fun loadFilmsDram() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getDramListHome()
            }.fold(
                onSuccess = { _filmsDram.value = it },
                onFailure = { Log.d("filmsDram", it.message ?: "") }
            )
        }
    }

    private fun loadFilmsDet() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getDetectivesListHome()
            }.fold(
                onSuccess = { _filmsDet.value = it },
                onFailure = { Log.d("filmsDet", it.message ?: "") }
            )
        }
    }

    private fun loadFilmsTop() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getTopListHome()
            }.fold(
                onSuccess = { _filmsTop.value = it },
                onFailure = { Log.d("filmsTop", it.message ?: "") }
            )
        }
    }

    private fun loadFilmsPopular() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getPopularListHome()
            }.fold(
                onSuccess = { _filmsPopular.value = it },
                onFailure = { Log.d("filmsPopular", it.message ?: "") }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadFilmsPremier() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                repository.getPremierListHome(year, month)
            }.fold(
                onSuccess = { _filmsPremier.value = it },
                onFailure = { Log.d("filmsPremier", it.message ?: "") }
            )
        }
    }

    //Переменные для получения списков для экрана AllMovies
    val pagedPopular: Flow<PagingData<FilmsTopPopular>> = Pager(
        config = PagingConfig(pageSize = 5),
        pagingSourceFactory = { PopularPagedSource() }
    ).flow.cachedIn(viewModelScope)

    val pagedDetectives: Flow<PagingData<FilmsSerDramDet>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { DetectivesPagedSource() }
    ).flow.cachedIn(viewModelScope)

    val pagedTop: Flow<PagingData<FilmsTopPopular>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { TopPagedSource() }
    ).flow.cachedIn(viewModelScope)

    val pagedDram: Flow<PagingData<FilmsSerDramDet>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { DramPagedSource() }
    ).flow.cachedIn(viewModelScope)

    val pagedSer: Flow<PagingData<FilmsSerDramDet>> = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { SerPagedSource() }
    ).flow.cachedIn(viewModelScope)

    //Компаньон для получения даты в метод loadFilmsPremier
    private companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val month = Calendar.getInstance()
            .getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.ENGLISH)!!.toString()
            .uppercase()

        @SuppressLint("SimpleDateFormat")
        val year = SimpleDateFormat("yyyy").format(Calendar.getInstance().time).toInt()
    }
}