import com.fasterxml.jackson.databind.ObjectMapper
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Test

class BookingServiceHttpTest {
    private val httpHandler : HttpHandler = application()

    @Test
    fun `get available seats`() {
        val response = httpHandler(Request(GET, "/seats"))
        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("[1,2,3,4,5]"))
    }

    @Test
    fun `book a seat`() {
        assertThat(httpHandler(Request(PUT, "/book-seat?id=3")).status, equalTo(OK))
        assertThat(httpHandler(Request(PUT, "/book-seat?id=3")).status, equalTo(CONFLICT))

        assertThat(
            httpHandler(Request(GET, "/seats")).bodyString(),
            equalTo("[1,2,4,5]")
        )
    }
}

private fun application(): HttpHandler {
    val objectMapper = ObjectMapper()
    val bookingService = BookingService()

    return routes(
        "seats" bind GET to {
            Response(OK).body(objectMapper.writeValueAsString(bookingService.availableSeats().map { it.id }))

        },
        "book-seat" bind PUT to { request ->
            val id = request.query("id")?.toInt() ?: TODO()
            val wasBooked = bookingService.book(Seat(id))
            Response(if (wasBooked) OK else CONFLICT)
        }
    )
}

class BookingServiceTests {
    private val bookingService = BookingService()

    @Test
    fun `get available seats`() {
        assertThat(bookingService.availableSeats(), equalTo(listOf(
            Seat(1), Seat(2), Seat(3), Seat(4), Seat(5)
        )))
    }

    @Test
    fun `book a seat`() {
        assertThat(bookingService.book(Seat(3)), equalTo(true))
        assertThat(bookingService.book(Seat(3)), equalTo(false))
        assertThat(bookingService.availableSeats(), equalTo(listOf(
            Seat(1), Seat(2), Seat(4), Seat(5)
        )))
    }
}

data class Seat(val id: Int)

class BookingService(private val allSeats: List<Seat> = listOf(Seat(1), Seat(2), Seat(3), Seat(4), Seat(5))) {
    private val bookedSeats = LinkedHashSet<Seat>()

    fun availableSeats(): List<Seat> {
       return allSeats - bookedSeats
    }

    fun book(seat: Seat): Boolean {
        return bookedSeats.add(seat)
    }
}

/*
fun main() {
    val httpHandler: HttpHandler = { request : Request ->
        Response(OK).body("Hello world\n$request")
    }

    httpHandler.asServer(Jetty(port = 9000)).start()

    val httpClient: HttpHandler = ApacheClient()
    val response = httpClient(Request(GET, "http://localhost:9000"))

    println("response = $response")
}
*/

