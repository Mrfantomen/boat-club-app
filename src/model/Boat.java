package model;

public class Boat {
	private double length;
	private BoatType type;
	private int id; //Will be used in database
	private int memberId; // If boat is sold  between members or just change owner this will be updated - NOT IMPLEMENTED


	public Boat(BoatType type, double lengthOfBoat, int memberId) {
		this.type = type;
		this.length = lengthOfBoat;
		this.id = -1;
		this.memberId = memberId;
	}
	
	public int getMemberId() {
		return this.memberId;
	}
	
	public void setMember(Member member) {
		this.memberId = member.getId();
	}
	
	public int getId () {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setLength(double length) {
		if (length >= 0) {
			this.length = length;
		}
	}

	public double getLength() {
		return this.length;
	}

	public void setType(BoatType type) {
		this.type = type;
	}

	public BoatType getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return this.id+ " "+this.type.toString()+ " " + this.length;
	}

}
