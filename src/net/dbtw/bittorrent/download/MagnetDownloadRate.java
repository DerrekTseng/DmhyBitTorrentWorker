package net.dbtw.bittorrent.download;

public class MagnetDownloadRate {
	private long bytes;
	private double quantity;
	private String measureUnit;

	protected MagnetDownloadRate(long delta) {
		if (delta < 0) {
			delta = 0;
			quantity = 0;
			measureUnit = "B";
		} else if (delta < (2 << 9)) {
			quantity = delta;
			measureUnit = "B";
		} else if (delta < (2 << 19)) {
			quantity = delta / (2 << 9);
			measureUnit = "KB";
		} else {
			quantity = ((double) delta) / (2 << 19);
			measureUnit = "MB";
		}
		bytes = delta;
	}

	public long getBytes() {
		return bytes;
	}

	public double getQuantity() {
		return quantity;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	@Override
	public String toString() {
		return String.format("%4.1f %s/s", getQuantity(), getMeasureUnit());
	}
}
