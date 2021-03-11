package impl;

import seatreservation.Seat;

public class LockDetail {
	private Seat seat;
	private int count;
	private String lockId;
	
	public Seat getSeat() {
		return seat;
	}
	public void setSeat(Seat seat) {
		this.seat = seat;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getLockId() {
		return lockId;
	}
	public void setLockId(String lockId) {
		this.lockId = lockId;
	}
	
	public LockDetail(Seat seat, int count, String lockId) {
		super();
		this.seat = seat;
		this.count = count;
		this.lockId = lockId;
	}
	
	
}
