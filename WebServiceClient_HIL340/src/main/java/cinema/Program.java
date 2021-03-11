package cinema;

import javax.xml.ws.BindingProvider;

import seatreservation.CinemaService;
import seatreservation.ICinema;
import seatreservation.ICinemaBuyCinemaException;
import seatreservation.ICinemaLockCinemaException;
import seatreservation.ICinemaReserveCinemaException;
import seatreservation.Seat;


public class Program {
	
	public static void main(String[] args){
		
		if(args.length!=4) {
			System.out.println("Bad input");
			return;
		}
		String url;//="http://localhost:8080/WebService_HIL340/Cinema";
		String row;//="A";
		String column;//="4";
		String task;//="Lock";
		String result;
		try {
			url=args[0];
			row=args[1];
			column=args[2];
			task=args[3];
		}
		catch(Exception e) {
			System.out.println("Bad input");
			return;
		}
		
		// Create the proxy factory:
		CinemaService cinemaService = new CinemaService();
		// Create the hello proxy object:
		ICinema cinema = cinemaService.getICinemaHttpSoap11Port();
		// Cast it to a BindingProvider:
		
		BindingProvider bp = (BindingProvider)cinema;
		// Set the URL of the service:
		
		bp.getRequestContext().put(
		BindingProvider.ENDPOINT_ADDRESS_PROPERTY,url);
		// Call the service:
		
		//in case that serverside hasn't called init
		//try{ cinema.init(10, 20); } catch(Exception e) {}

		Seat seat=new Seat();
		switch(task) {
			case "Lock":
				seat.setRow(row);
				seat.setColumn(column);
				try {
					result=cinema.lock(seat, 1);
					System.out.println(result);
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "Reserve":
				seat.setRow(row);
				seat.setColumn(column);
				try {
					result=cinema.lock(seat, 1);
					cinema.reserve(result);
					System.out.println("Succesfully reserved");
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case "Buy":
				seat.setRow(row);
				seat.setColumn(column);
				try {
					result=cinema.lock(seat, 1);
					cinema.buy(result);
					System.out.println("Succesfully sold");
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
				break;
				
		}
		}
	
	
}
