import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BookingServiceHttpTest {
    private val boat = BookingService(List(10) { Seat(it + 1) }.toSet())
    private val mapper = jacksonObjectMapper()
    private val httpBoat = routes(
        "/seats" bind GET to {
            val availableSeats = boat.availableSeats()
            Response(OK).body(mapper.writeValueAsString(availableSeats))
        },
        "/seat/{id}" bind PUT to { request ->
            val seat = Seat((request.path("id") ?: error("TODO")).toInt())
            val name = request.query("name") ?: error("TODO")
            Response(OK).body(boat.bookSeat(seat, name).toString())
        },
        "/passenger-info" bind GET to {
            Response.invoke(OK).body(mapper.writeValueAsString(boat.passengersInformation().toString()))
        }
    )

    @Test
    fun `retrieve available seats returns all seats`() {
        val response = httpBoat(Request(GET, "/seats"))
        assertThat(response.status, equalTo(OK))
        assertThat(
            response.bodyString(), equalTo(
                """
            [{"id":1},{"id":2},{"id":3},{"id":4},{"id":5},{"id":6},{"id":7},{"id":8},{"id":9},{"id":10}]
        """.trimIndent()
            )
        )
    }

    @Test
    fun `book a seat`() {
        val response = httpBoat(Request(PUT, "/seat/1?name=Diego"))
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("true"))
    }

    @Test
    fun `cannot book the same seat twice`() {
        httpBoat(Request(PUT, "/seat/1?name=Diego"))
        val response = httpBoat(Request(PUT, "/seat/1?name=Diego"))
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("false"))
    }

    @Test
    fun `cannot book invalid seat`() {
        val response = httpBoat(Request(PUT, "/seat/-1?name=Diego"))
        assertThat(response.status, equalTo(OK)) // TODO this is not OK!
        assertThat(response.bodyString(), equalTo("false"))
    }

    @Test
    fun `retrieve passenger information`() {
        httpBoat(Request(PUT, "/seat/1?name=Diego"))
        httpBoat(Request(PUT, "/seat/2?name=Ben"))

        val response = httpBoat(Request(GET, "/passenger-info"))
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("\"{Seat(id=1)=Diego, Seat(id=2)=Ben}\"")) // TODO <- this json is wrong :(
    }
}

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

data class Seat(val id: Int)

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
