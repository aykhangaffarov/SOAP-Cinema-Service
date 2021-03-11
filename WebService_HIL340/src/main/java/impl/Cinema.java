package impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jws.WebService;


import seatreservation.*;

@WebService(
name="CinemaService",
portName="ICinema_HttpSoap11_Port",
targetNamespace="http://www.iit.bme.hu/soi/hw/SeatReservation",
endpointInterface="seatreservation.ICinema",
wsdlLocation="WEB-INF/wsdl/SeatReservation.wsdl")
public class Cinema implements ICinema {

	private Init init;
	private ArrayOfSeat arrayOfSeat;
	private int auto_incr_lockID=0;
	List<LockDetail> lockDetails=new ArrayList<LockDetail>();
	Map<Integer, Reservation> resMap = new TreeMap<Integer, Reservation>();

	@Override
	public void init(int rows, int columns) throws ICinemaInitCinemaException {
		
		if(rows>=1 && rows<=26 && columns>=1 && columns<=100) {
			arrayOfSeat=new ArrayOfSeat();
			
			init=new Init();
			init.setColumns(columns);
			init.setRows(rows);
			
			char ch=64;
			List<Seat> listOfSeats =new ArrayList<Seat>();
			int id=1;
			for(int i=1;i<=init.getRows(); i++) {
				for(int j=1; j<=init.getColumns(); j++) {
					Seat seat=new Seat();
					seat.setRow(Character.toString(ch+i));
					seat.setColumn(Integer.toString(j));
					resMap.put(id, new Reservation(seat, SeatStatus.FREE));
					id++;
					listOfSeats.add(seat);
				}
				
			}
		//	resMap.values().forEach(System.out::println);
			arrayOfSeat.getSeat().addAll(listOfSeats);
			// clear everyhting
			auto_incr_lockID=0;
			lockDetails.clear();
		}
		else {
			throw new ICinemaInitCinemaException("Row or column number is outside of given interval", new CinemaException());
		}

	}

	@Override
	public ArrayOfSeat getAllSeats() throws ICinemaGetAllSeatsCinemaException {
		if(init.getRows()>=1) {
			GetAllSeatsResponse getAllSeatsResponse =new GetAllSeatsResponse();
			getAllSeatsResponse.setGetAllSeatsResult(arrayOfSeat);
			return getAllSeatsResponse.getGetAllSeatsResult();
		}
		else {
			throw new ICinemaGetAllSeatsCinemaException("Row or column number is outside of given interval", new CinemaException());
		}
	}

	@Override
	public SeatStatus getSeatStatus(Seat seat) throws ICinemaGetSeatStatusCinemaException {
		if(arrayOfSeat.getSeat().stream().anyMatch(s -> (s.getColumn().equals(seat.getColumn()) && s.getRow().equals(seat.getRow())))) {
			
			List<Reservation> reservationList =new ArrayList<Reservation>(resMap.values());
			//reservationList.forEach(System.out::println);
			Reservation reservation = reservationList.stream().filter(
						res -> (res.getSeat().getRow().equals(seat.getRow()) && 
						res.getSeat().getColumn().equals(seat.getColumn()))).findAny().get();
			
			GetSeatStatusResponse getSeatStatusResponse =new GetSeatStatusResponse();
			getSeatStatusResponse.setGetSeatStatusResult(reservation.getSeatStatus());
			return getSeatStatusResponse.getGetSeatStatusResult();
		}
		else {
			throw new ICinemaGetSeatStatusCinemaException("Bad seat number", new CinemaException());
		}
	}

	@Override
	public String lock(Seat seat, int count) throws ICinemaLockCinemaException {
		int startingColumnNumber=Integer.parseInt(seat.getColumn());
		if(init.getColumns()<(startingColumnNumber+count)) {
			throw new ICinemaLockCinemaException("Not enough seats", new CinemaException());
		}
		else if(arrayOfSeat.getSeat().stream().anyMatch(s -> (s.getColumn().equals(seat.getColumn()) && s.getRow().equals(seat.getRow())))) {
			List<Reservation> reservationList =new ArrayList<Reservation>(resMap.values());
			for(int i=0; i<count; i++) {
				int columnNumber=Integer.parseInt(seat.getColumn())+i; //1
				Reservation reservation = reservationList.stream().filter(
								res-> (res.getSeat().getRow().equals(seat.getRow()) && 
								res.getSeat().getColumn().equals(Integer.toString(columnNumber))))
								.findAny()
								.get();
								//.orElse(new Reservation(seat, SeatStatus.FREE));
				//System.out.println("Status when we check: "+reservation.getSeatStatus());
				if(!reservation.getSeatStatus().equals(SeatStatus.FREE)) {
					throw new ICinemaLockCinemaException("Seat is not free", new CinemaException());
				}
			}
			
			for(int i=0; i<count; i++) {
				int columnNumber=Integer.parseInt(seat.getColumn())+i;
				Seat s=new Seat();
				s.setColumn(Integer.toString(columnNumber));
				s.setRow(seat.getRow());
				char c=seat.getRow().charAt(0);
				resMap.put((c-65)*init.getColumns()+columnNumber, new Reservation(s, SeatStatus.LOCKED));
				//System.out.println("Status after change: " + reservationList.get((columnNumber-1)*(c-64)).getSeatStatus().toString());
			}
			String newLockId="lock"+ Integer.toString(auto_incr_lockID);
			auto_incr_lockID++;
			lockDetails.add(new LockDetail(seat, count, newLockId));
			//reservationList.forEach(System.out::println);
			LockResponse lockResponse =new LockResponse();
			lockResponse.setLockResult(newLockId);
			return lockResponse.getLockResult();
		}
		else {
			throw new ICinemaLockCinemaException("Other type of error", new CinemaException());
		}
		
	}

	@Override
	public void unlock(String lockId) throws ICinemaUnlockCinemaException {
		if(!lockDetails.stream().anyMatch( loc -> loc.getLockId().equals(lockId))) {
			throw new ICinemaUnlockCinemaException("Lock is invalid", new CinemaException());
		}
		else {
			LockDetail lockDetail = lockDetails.stream().filter( 
						loc -> loc.getLockId().equals(lockId))
						.findAny().get();
			
			List<Reservation> reservationList =new ArrayList<Reservation>(resMap.values());
			for(int i=0; i<lockDetail.getCount(); i++) {
				int columnNumber=Integer.parseInt(lockDetail.getSeat().getColumn())+i; //1
				Reservation reservation = reservationList.stream().filter(
								res-> (res.getSeat().getRow().equals(lockDetail.getSeat().getRow()) && 
								res.getSeat().getColumn().equals(Integer.toString(columnNumber))))
								.findAny()
								.get();
								//.orElse(new Reservation(seat, SeatStatus.FREE));
				//System.out.println("Status when we check: "+reservation.getSeatStatus());
				if(!reservation.getSeatStatus().equals(SeatStatus.LOCKED)) {
					throw new ICinemaUnlockCinemaException("Seat is not locked", new CinemaException());
				}
				
			}
			for(int i=0; i<lockDetail.getCount(); i++) {
				int columnNumber=Integer.parseInt(lockDetail.getSeat().getColumn())+i;
				Seat s=new Seat();
				s.setColumn(Integer.toString(columnNumber));
				s.setRow(lockDetail.getSeat().getRow());
				char c=s.getRow().charAt(0);
				resMap.put((c-65)*init.getColumns()+columnNumber, new Reservation(s, SeatStatus.FREE));
				//System.out.println(reservationList.get(columnNumber-1).getSeatStatus().toString());
			}
			//reservationList.forEach(System.out::println);
			//lockDetails.removeIf(loc -> loc.getLockId().equals(lockId));
		}
	}

	@Override
	public void reserve(String lockId) throws ICinemaReserveCinemaException {
		if(!lockDetails.stream().anyMatch( loc -> loc.getLockId().equals(lockId))) {
			throw new ICinemaReserveCinemaException("Lock is invalid", new CinemaException());
		}
		else {
			LockDetail lockDetail = lockDetails.stream().filter( 
					loc -> loc.getLockId().equals(lockId))
					.findAny().get();
			
			List<Reservation> reservationList =new ArrayList<Reservation>(resMap.values());
			for(int i=0; i<lockDetail.getCount(); i++) {
				int columnNumber=Integer.parseInt(lockDetail.getSeat().getColumn())+i; //1
				Reservation reservation = reservationList.stream().filter(
								res-> (res.getSeat().getRow().equals(lockDetail.getSeat().getRow()) && 
								res.getSeat().getColumn().equals(Integer.toString(columnNumber))))
								.findAny()
								.get();
								//.orElse(new Reservation(seat, SeatStatus.FREE));
				//System.out.println("Status when we check: "+reservation.getSeatStatus());
				if(!reservation.getSeatStatus().equals(SeatStatus.LOCKED)) {
					throw new ICinemaReserveCinemaException("Seat is not locked", new CinemaException());
				}
			}
			
			
			for(int i=0; i<lockDetail.getCount(); i++) {
				int columnNumber=Integer.parseInt(lockDetail.getSeat().getColumn())+i;
				Seat s=new Seat();
				s.setColumn(Integer.toString(columnNumber));
				s.setRow(lockDetail.getSeat().getRow());
				char c=s.getRow().charAt(0);
				resMap.put((c-65)*init.getColumns()+columnNumber, new Reservation(s, SeatStatus.RESERVED));
				//System.out.println(reservationList.get(columnNumber-1).getSeatStatus().toString());
			}
			//reservationList.forEach(System.out::println);
			//lockDetails.removeIf(loc -> loc.getLockId().equals(lockId));
		}
	}

	@Override
	public void buy(String lockId) throws ICinemaBuyCinemaException {
		if(!lockDetails.stream().anyMatch( loc -> loc.getLockId().equals(lockId))) {
			throw new ICinemaBuyCinemaException("Lock is invalid", new CinemaException());
		}
		else {
			LockDetail lockDetail = lockDetails.stream().filter( 
					loc -> loc.getLockId().equals(lockId))
					.findAny().get();
			
			List<Reservation> reservationList =new ArrayList<Reservation>(resMap.values());
			for(int i=0; i<lockDetail.getCount(); i++) {
				int columnNumber=Integer.parseInt(lockDetail.getSeat().getColumn())+i; //1
				Reservation reservation = reservationList.stream().filter(
								res-> (res.getSeat().getRow().equals(lockDetail.getSeat().getRow()) && 
								res.getSeat().getColumn().equals(Integer.toString(columnNumber))))
								.findAny()
								.get();
								//.orElse(new Reservation(seat, SeatStatus.FREE));
				//System.out.println("Status when we check: "+reservation.getSeatStatus());
				if(!reservation.getSeatStatus().equals(SeatStatus.LOCKED) && !reservation.getSeatStatus().equals(SeatStatus.RESERVED)) {
					throw new ICinemaBuyCinemaException("Seat is not locked or reserved", new CinemaException());
				}
			}
			
			
			for(int i=0; i<lockDetail.getCount(); i++) {
				int columnNumber=Integer.parseInt(lockDetail.getSeat().getColumn())+i;
				Seat s=new Seat();
				s.setColumn(Integer.toString(columnNumber));
				s.setRow(lockDetail.getSeat().getRow());
				char c=s.getRow().charAt(0);
				resMap.put((c-65)*init.getColumns()+columnNumber, new Reservation(s, SeatStatus.SOLD));
				//System.out.println(reservationList.get(columnNumber-1).getSeatStatus().toString());
			}
			//reservationList.forEach(System.out::println);
			//lockDetails.removeIf(loc -> loc.getLockId().equals(lockId));
		}

	}

}
