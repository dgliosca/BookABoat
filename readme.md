- hello world
    - main() with http4k server in its own file; curl from command line
    - basic http4k client
- BookingServiceHttpTest
    - `get available seats` http test; minimal application() function
    - `book a seat` http test start from assertion, then make it @Disabled; introduce BookingService
    - BookingServiceTests `get available seats`, introduce Seat, add json objectmapper, update http assertions
    - BookingServiceTests `book a seat`, extract `allSeats` and `bookedSeats`
    - BookingServiceTests `book the same seat`, extract `allSeats` and `bookedSeats`
    - enable `book a seat` http test; add routing to application