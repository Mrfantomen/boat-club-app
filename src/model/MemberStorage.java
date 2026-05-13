package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.service.SqlOperator;

public class MemberStorage {

	private ArrayList<Member> memberList;
	private SqlOperator dataSRC;

	public MemberStorage() {
		this.dataSRC = new SqlOperator();
		this.memberList = this.dataSRC.getMembers(); // Gets all members from database
	}

	public ArrayList<Member> getMemberlist() {
		return memberList;
	}

	public Iterator<Member> getMembersIterator() {
		return memberList.iterator();
	}

	public boolean doesTheMemberIdExist(int memberId) {
		for (int i = 0; i < memberList.size(); i++) {
			Member member = memberList.get(i);
			if (memberId == member.getId()) {
				return true;
			}
		}
		return false;
	}

	public void addMember(String name, String personNum) {
		Member memberAdded = new Member(name, personNum, 0);
		dataSRC.saveMember(memberAdded);
		memberList.add(memberAdded);
	}

	public void deleteMember(Member member) {
		if (member == null) {
			return;
		}
		dataSRC.deleteMember(member);
		memberList.removeIf(mem -> mem.getId() == member.getId());

	}

	public void updateMember(Member member) {
		dataSRC.saveMember(member);
		for (int i = 0; i < memberList.size(); i++) {
			if (memberList.get(i).getId() == member.getId()) {
				memberList.set(i, member);
			}
		}
	}

	public Member getMember(int memId) {
		for (int i = 0; i < memberList.size(); i++) {
			if (memberList.get(i).getId() == memId) {
				return memberList.get(i);
			}

		}
		return null;
	}

	public void addBoat(BoatType type, double length, int memId) {
		Boat boat = new Boat(type, length, memId);
		dataSRC.saveBoat(boat);
		Member member = getMember(memId);
		List<Boat> boatlist = member.getBoatList();
		int boatsBefore = boatlist.size();
		boatlist.add(boat);
	}

	public void deleteBoat(Boat boat) {
		dataSRC.deleteBoat(boat);
		Member member = getMember(boat.getMemberId());
		member.getBoatList().removeIf(b -> b.getId() == boat.getId());

	}

	public void updateBoat(Boat boat) {
		dataSRC.saveBoat(boat);
	}

}