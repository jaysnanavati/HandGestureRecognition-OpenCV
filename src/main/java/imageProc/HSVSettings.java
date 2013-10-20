package imageProc;
/**
 * 
 * @author Jay
 *
 */
public class HSVSettings {
	private int hueUpper , hueLower = 0;
	private int saturationUpper, saturationLower = 0;
	private int valueUpper, valueLower = 0;

	private int MAX_HUE, MAX_SATURATION,MAX_VALUE;
	
	public HSVSettings(int mAX_HUE, int mAX_SATURATION, int mAX_VALUE) {
		super();
		MAX_HUE = mAX_HUE;
		MAX_SATURATION = mAX_SATURATION;
		MAX_VALUE = mAX_VALUE;
	}

	public void setHueRange(int lower, int upper) {
		this.hueUpper = upper;
		this.hueLower = lower;
	}

	public void setSaturationRange(int lower, int upper) {
		this.saturationUpper = upper;
		this.saturationLower = lower;
	}
	
	public void setValueRange(int lower, int upper) {
		this.valueUpper = upper;
		this.valueLower = lower;
	}

	public int getMAX_HUE() {
		return MAX_HUE;
	}

	public void setMAX_HUE(int mAX_HUE) {
		MAX_HUE = mAX_HUE;
	}

	public int getMAX_SATURATION() {
		return MAX_SATURATION;
	}

	public void setMAX_SATURATION(int mAX_SATURATION) {
		MAX_SATURATION = mAX_SATURATION;
	}

	public int getMAX_VALUE() {
		return MAX_VALUE;
	}

	public void setMAX_VALUE(int mAX_VALUE) {
		MAX_VALUE = mAX_VALUE;
	}

	public int getHueUpper() {
		return hueUpper;
	}

	public int getHueLower() {
		return hueLower;
	}

	public int getSaturationUpper() {
		return saturationUpper;
	}

	public int getSaturationLower() {
		return saturationLower;
	}

	public int getValueUpper() {
		return valueUpper;
	}

	public int getValueLower() {
		return valueLower;
	}

}
