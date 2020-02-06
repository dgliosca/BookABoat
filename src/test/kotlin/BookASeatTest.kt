import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class BookASeatTest {

    @Test
    fun `retrieve available seats`() {
        val bookASeat = BookASeat()

        val result = bookASeat.availableSeats()

        assertThat(result, equalTo(listOf(Seat())))
    }
}

data class Seat(val seat: String = "") {

}

class BookASeat {
    fun availableSeats() : List<Seat> {
        return listOf(Seat())
    }
}
