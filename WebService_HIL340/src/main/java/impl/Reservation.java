package impl;



import seatreservation.Seat;
import seatreservation.SeatStatus;


public class Reservation {
	
	private Seat seat;
	
	private SeatStatus seatStatus;
	
	
	public Reservation(Seat seat, SeatStatus seatStatus) {
		super();
		this.seat = seat;
		this.seatStatus = seatStatus;
	}

	public Seat getSeat() {
		return seat;
	}
	
	public void setSeat(Seat seat) {
		this.seat = seat;
	}
	
	public SeatStatus getSeatStatus() {
		return seatStatus;
	}
	
	public void setSeatStatus(SeatStatus seatStatus) {
		this.seatStatus = seatStatus;
	}

	@Override
	public String toString() {
		return "Reservation [seat=" + "ROW: "+ seat.getRow() +" COLUMN: "+seat.getColumn() + ", seatStatus=" + seatStatus + "]";
	}
	

}
