package pixelbot.components.table;

import java.util.Comparator;

public class ObjectComparator implements Comparator<Object> {

	@Override
	public int compare(Object obj1, Object obj2) {
		if ((obj1 == null) && (obj2 == null)) {
			return 0;
		}
		if (obj1 == null) {
			return 1;
		}
		if (obj2 == null) {
			return -1;
		}

		return String.CASE_INSENSITIVE_ORDER.compare(obj1.toString(), obj2.toString());

	}

}