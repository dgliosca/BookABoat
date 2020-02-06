import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BookingServiceTest {
    private val bookingService = BookingService(List(10) { Seat(it + 1) }.toSet())

    @Test
    fun `retrieve available seats returns all seats`() {
        val result = bookingService.availableSeats()
        assertThat(result.size, equalTo(10))
    }

    @Test
    fun `book a seat`() {
        assertTrue(bookingService.bookSeat(Seat(1)))
    }

    @Test
    fun `cannot book the same seat twice`() {
        bookingService.bookSeat(Seat(1))
        val bookSameSeat = bookingService.bookSeat(Seat(1))

        assertThat(bookSameSeat, equalTo(false))
    }

    @Test
    fun `cannot book invalid seat`() {
        val invalidSeat = Seat(-1)
        assertThat(bookingService.bookSeat(invalidSeat), equalTo(false))
    }
}

data class Seat(val seatName: Int)

class BookingService(private val allSeats: Set<Seat>) {
    private val bookedSeats = mutableSetOf<Seat>()

    fun availableSeats(): Set<Seat> {
        return allSeats - bookedSeats
    }

    fun bookSeat(seat: Seat) = if (allSeats.contains(seat)) bookedSeats.add(seat) else false

}
