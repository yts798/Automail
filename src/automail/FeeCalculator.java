package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;
import simulation.Floor;
import simulation.Building;

/**
 * This class is used to calculate cost, fee and charges of a mail
 */
public class FeeCalculator {
	/** The destination floor of the mail */
	private int destFloor;
	/** The service fee charged for the mail */
	private double serviceFee;
	/** The number of failing lookups */
	private int failLookup = 0;
	/** The number of successful lookups */
	private int successLookup = 0;
	
	public static double MARKUP_PERCENTAGE = 0.059;
	public static double ACTIVITY_UNIT_PRICE = 0.224;
	public final static int FLOOR_COST = 5;
	public final static double LOOKUP_COST = 0.1;
	
	public static Floor[] floors = new Floor[Building.FLOORS];
	
    /**
     * Constructor for a FeeCalculator
     * @param destFloor the destination floor intended for this mail item
     */
	public FeeCalculator(int destFloor) {
		this.destFloor = destFloor;
	}
	
	/**
	 * Calculate the activity unit for a mail
	 * @return the activity unit
	 */
	public double calcActivity() {
		return 2 * FLOOR_COST * (destFloor - 1) + LOOKUP_COST;
	}
	
	/**
	 * Calculate the activity cost for a mail
	 * @return the activity cost
	 */
	public double calcCost() {
		double activity = calcActivity();
		return activity * ACTIVITY_UNIT_PRICE + findServiceFee();
	}
	
	/**
	 * Calculate the charge for a mail
	 * @return the charge
	 */
	public double calcCharge() {
		double cost = calcCost();
		return cost * (1 + MARKUP_PERCENTAGE);
	}
	
	/**
	 * Find the current service fee
	 * @return the service fee
	 */
	private double findServiceFee() {
		double fee = -1;
		try {
			fee = WifiModem.getInstance(destFloor).forwardCallToAPI_LookupPrice(destFloor);
			if (fee < 0) {
				// When the lookup failed
				failLookup++;
				if (floors[destFloor - 1] == null) {
					floors[destFloor - 1] = new Floor();
				}
				if (floors[destFloor - 1].getServiceFee() < 0) {
					fee = WifiModem.getInstance(destFloor).forwardCallToAPI_LookupPrice(destFloor);
					while (fee < 0) {
						failLookup++;
						fee = WifiModem.getInstance(destFloor).forwardCallToAPI_LookupPrice(destFloor);
					}
					floors[destFloor - 1].setServiceFee(fee);
				}
				fee = floors[destFloor - 1].getServiceFee();
			}
			serviceFee = fee;
		} catch (Exception e) {
			e.printStackTrace();
		}
		successLookup++;
		return fee;
	}
	
	public String toString() {
		double fee = findServiceFee();
		return String.format("Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f", 
				calcCharge(), calcCost(), fee, calcActivity());
	}

	public double getServiceFee() {
		return serviceFee;
	}

	public int getFailLookup() {
		return failLookup;
	}

	public int getSuccessLookup() {
		return successLookup;
	}	
}
