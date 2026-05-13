package model;

public enum BoatType {
	Sailboat ("Sailboat"), Motorsailer("Motorsailer"), Kayak("Kayak"), Canoe("Canoe"), Other("Other");
	private String boatType;
	BoatType(String type) {
	      this.boatType =type;
	   }
	@Override
	public String toString() {
		return boatType;
		
	}
		
}