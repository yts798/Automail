package simulation;

/**
 * This class is used to store the most recent service fee of a floor
 */
public class Floor {
	private double serviceFee = -1;
	
	public double getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(double serviceFee) {
		this.serviceFee = serviceFee;
	}
}
