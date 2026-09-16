package pixelbot.components.logviewer;

import java.util.Date;

public class DateSeq implements Comparable<DateSeq> {

	private Date date;
	private long sequence;

	public DateSeq(long millis, long sequence) {
		this.date = new Date(millis);
		this.sequence = sequence;
	}

	public Date getDate() {

		return this.date;
	}

	@Override
	public int compareTo(DateSeq o) {
		int res = this.date.compareTo(o.getDate());
		if (res == 0) {
			return (int) (this.sequence - o.sequence);
		}

		return res;
	}

	@Override
	public String toString() {
		return this.date.toString();
	}

}
