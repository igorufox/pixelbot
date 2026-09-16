package pixelbot.misc;

import java.util.*;

public class EnumUtils {

	public static <T extends Enum<T>> EnumSet<T> toEnumSet(long value, Class<T> type) {
		EnumSet<T> result = EnumSet.noneOf(type);
		try {
			T[] constants = type.getEnumConstants();
			int ordinal = 0;
			while (value != 0) {
				if ((value & 1) == 1) {
					result.add(constants[ordinal]);
				}
				++ordinal;
				value = value >> 1;
				if (ordinal >= constants.length)
					break;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static <T extends Enum<T>> long toLong(EnumSet<T> value) {
		long result = 0;
		try {
			for (T t : value) {
				result |= 1 << t.ordinal();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
