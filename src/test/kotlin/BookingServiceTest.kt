import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BookingServiceTest {

    @Test
    fun `retrieve available seats returns all seats`() {
        val result = BookingService().availableSeats()
        assertThat(result.size, equalTo(10))
    }

    @Test
    fun `book a seat`() {
        val bookingService = BookingService()
        assertTrue(bookingService.bookSeat(Seat("1A")))
    }

    @Test
    fun `cannot book the same seat twice`() {
        val bookingService = BookingService()
        bookingService.bookSeat(Seat("goodseat"))
        val bookSameSeat = bookingService.bookSeat(Seat("goodseat"))

        assertThat(bookSameSeat, equalTo(false))
    }
}

data class Seat(val seatName: String = "") {

}

class BookingService {
    private val seats  = mutableSetOf<Seat>()

    fun availableSeats() : List<Seat> {
        return List(10) { Seat() }
    }

    fun bookSeat(seat: Seat) = seats.add(seat)
}
