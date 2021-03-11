# SOAP-Cinema-Service

This is SOAP based web service for Cinema Seat Reservation. WSDL-first method has been used in this project.

# Description

The main task is to create and publish a SOAP web service for cinema seat reservations.
The interface of the service is described by the following pseudo-code:

```
[Uri("http://www.iit.bme.hu/soi/hw/SeatReservation")]
namespace SeatReservation
{
 exception CinemaException
 {
 int ErrorCode;
 string ErrorMessage;
 }
 struct Seat
 {
 string Row;
 string Column;
 }
 enum SeatStatus
 {
 Free,
 Locked,
 Reserved,
 Sold
 }
 interface ICinema
 {
 void Init(int rows, int columns) throws CinemaException;
 Seat[] GetAllSeats()throws CinemaException;
 SeatStatus GetSeatStatus(Seat seat) throws CinemaException;
 string Lock(Seat seat, int count) throws CinemaException;
 void Unlock(string lockId) throws CinemaException;
 void Reserve(string lockId) throws CinemaException;
 void Buy(string lockId) throws CinemaException;
 }
}
```


## Server 
Server produces functionalities such as Init, Lock, Reserve, Buy and others as well. 

## Client
Client-side can send only Lock, Reserve and Buy.
