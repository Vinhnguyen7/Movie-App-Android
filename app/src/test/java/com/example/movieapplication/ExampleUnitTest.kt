import com.example.movieapplication.data.repository.MovieRepository
import com.example.movieapplication.ui.viewmodel.MovieViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals // Thêm cái này để so sánh

class MovieViewModelTest {

    private lateinit var viewModel: MovieViewModel
    private val repository: MovieRepository = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        // Sử dụng UnconfinedTestDispatcher để test chạy ngay lập tức
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = MovieViewModel(repository)
    }

    @Test
    fun `load movies successfully updates state`() {
        val mockData = listOf(Movie(1, "Batman", "url", 9.0, "Dark Knight"))
        coEvery { repository.getMovies() } returns mockData

        viewModel.fetchMovies()

        // Dùng assertEquals để kiểm tra kết quả chuyên nghiệp hơn
        assertEquals(mockData, viewModel.movies.value)
    }
}