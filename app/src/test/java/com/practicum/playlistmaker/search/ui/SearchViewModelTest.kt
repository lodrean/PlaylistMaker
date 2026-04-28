package com.practicum.playlistmaker.search.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var tracksInteractor: FakeTracksInteractor
    private lateinit var historyInteractor: FakeTracksHistoryInteractor
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = mock(Application::class.java)
        `when`(application.getString(anyInt())).thenReturn("Nothing found")
        tracksInteractor = FakeTracksInteractor()
        historyInteractor = FakeTracksHistoryInteractor()
        viewModel = SearchViewModel(
            application = application,
            tracksInteractor = tracksInteractor,
            tracksHistoryInteractor = historyInteractor
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchRequest with tracks returns Content state`() = runTest {
        val tracks = arrayListOf(
            Track(trackId = "1", trackName = "Song 1", artistName = "Artist 1"),
            Track(trackId = "2", trackName = "Song 2", artistName = "Artist 2"),
        )
        tracksInteractor.result = Pair(tracks, null)

        viewModel.searchRequest("query")
        advanceUntilIdle()

        val state = viewModel.observeState().value
        assertTrue(state is SearchState.Content)
        assertEquals(2, (state as SearchState.Content).trackList.size)
    }

    @Test
    fun `searchRequest with empty result returns Empty state`() = runTest {
        tracksInteractor.result = Pair(arrayListOf(), null)

        viewModel.searchRequest("query")
        advanceUntilIdle()

        val state = viewModel.observeState().value
        assertTrue(state is SearchState.Empty)
    }

    @Test
    fun `searchRequest with error returns Error state`() = runTest {
        tracksInteractor.result = Pair(null, "Network error")

        viewModel.searchRequest("query")
        advanceUntilIdle()

        val state = viewModel.observeState().value
        assertTrue(state is SearchState.Error)
        assertEquals("Network error", (state as SearchState.Error).errorMessage)
    }

    @Test
    fun `showHistoryTrackList returns History state`() = runTest {
        val history = listOf(
            Track(trackId = "1", trackName = "History Song"),
        )
        historyInteractor.setHistory(history)

        viewModel.showHistoryTrackList()
        advanceUntilIdle()

        val state = viewModel.observeState().value
        assertTrue(state is SearchState.History)
        assertEquals(1, (state as SearchState.History).trackHistoryList.size)
    }

    @Test
    fun `searchDebounce triggers search after delay`() = runTest {
        val tracks = arrayListOf(Track(trackId = "1", trackName = "Debounced"))
        tracksInteractor.result = Pair(tracks, null)

        viewModel.searchDebounce("test")
        advanceUntilIdle()

        val state = viewModel.observeState().value
        assertTrue(state is SearchState.Content)
    }
}
