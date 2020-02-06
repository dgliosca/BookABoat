import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BookingServiceTest {
    private val boat = BookingService(List(10) { Seat(it + 1) }.toSet())

    @Test
    fun `retrieve available seats returns all seats`() {
        val result = boat.availableSeats()
        assertThat(result.size, equalTo(10))
    }

    @Test
    fun `book a seat`() {
        assertTrue(boat.bookSeat(Seat(1), name = "Diego"))
    }

    @Test
    fun `cannot book the same seat twice`() {
        boat.bookSeat(Seat(1), name = "Diego")
        val bookSameSeat = boat.bookSeat(Seat(1), name = "Ben")
        assertThat(bookSameSeat, equalTo(false))
    }

    @Test
    fun `cannot book invalid seat`() {
        val invalidSeat = Seat(-1)
        assertThat(boat.bookSeat(invalidSeat, name = "Diego"), equalTo(false))
    }

    @Test
    fun `retrieve passenger information`() {
        boat.bookSeat(Seat(1), "Diego")
        boat.bookSeat(Seat(2), "Ben")
        assertThat(
            boat.passengersInformation(), equalTo(
                mapOf(Seat(1) to "Diego", Seat(2) to "Ben")
            )
        )
    }
}

data class Seat(val seatName: Int)

class BookingService(private val allSeats: Set<Seat>) {
    private val bookedSeats = mutableMapOf<Seat, String>()

    fun availableSeats(): Set<Seat> {
        return allSeats - bookedSeats.keys
    }

    fun bookSeat(seat: Seat, name: String): Boolean =
        if (allSeats.contains(seat) && !bookedSeats.contains(seat)) {
            bookedSeats[seat] = name
            true
        } else false

    fun passengersInformation(): Map<Seat, String> {
        return bookedSeats
    }
}
