package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Member {
	private String name;
	private String personNum;
	private int memberId; // Will be used in database
	private ArrayList<Boat> boatList = new ArrayList<Boat>();

	public Member(String Name, String personNum, int id) {
		this.name = Name;
		this.personNum = personNum;
		this.memberId = id;
	}

	public Member() {

	}

	public void setName(String Name) {
		this.name = Name;

	}

	public String getName() {
		return this.name;
	}

	public void setPersonNum(String num) {
		this.personNum = num;

	}

	public String getPersonNum() {
		return personNum;
	}

	public int getAmountOfBoats() {
		return boatList.size();
	}

	public void addBoat(BoatType type, double lengthOfBoat) {
		Boat boat = new Boat(type, lengthOfBoat, memberId);
		boatList.add(boat);

	}

	public void deleteBoat(int removeBoat) {
		boatList.remove(removeBoat);

	}

	public ArrayList<Boat> getBoatList() {
		return this.boatList;
	}

	public int getId() {
		return memberId;
	}

	public void setId(int id) { // TEMPORARY FOR DATABASE
		this.memberId = id;
	}

	@Override
	public String toString() {
		return String.valueOf(memberId)+": "+name+" boat count: "+String.valueOf(boatList.size());
	}

}