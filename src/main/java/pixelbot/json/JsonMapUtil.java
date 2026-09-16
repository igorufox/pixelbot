package pixelbot.json;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;
import pixelbot.misc.EnumUtils;

public class JsonMapUtil {
	public static SimpleDateFormat json_date_formater = new SimpleDateFormat("yyyy-MM-dd");

	public static Serializable getSerializableObject(Map<String, Object> map, String key) {
		Serializable result = null;
		if (map.containsKey(key)) {
			try (ObjectInputStream is = new ObjectInputStream(new GZIPInputStream(
					new ByteArrayInputStream(Base64.getMimeDecoder().decode((String) map
							.get(key)))))) {
				result = (Serializable) is.readObject();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static Date getTimestamp(Map<String, Object> map, String key) {
		Date result = null;
		if (map.containsKey(key)) {
			try {
				Object obj = map.get(key);
				if (obj instanceof Date) {
					return (Date) obj;
				} else if (obj instanceof Number) {
					result = new Date(((Number) obj).longValue());
				} else {
					System.err.println("getTimestamp NULL");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String getString(Map<String, Object> map, String key) {
		return (String) map.get(key);
	}

	public static String getHexString(Map<String, Object> map, String key) {
		return HexFormat.of().withUpperCase().formatHex(Base64.getMimeDecoder().decode((String) map
				.get(key)));
	}

	public static byte[] getBytes(Map<String, Object> map, String key, byte[] default_value) {
		byte[] result = default_value;
		if (map.containsKey(key)) {
			try {
				result = Base64.getMimeDecoder().decode((String) map.get(key));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMap(Map<String, Object> map, String key) {
		Map<String, Object> result = null;
		if (map.containsKey(key)) {
			try {
				Object obj = map.get(key);
				if (obj instanceof Map<?, ?>) {
					result = (Map<String, Object>) obj;
				} else if (obj instanceof String) {
					result = (Map<String, Object>) JSONValue.parse((String) obj);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static List<?> getList(Map<String, Object> map, String key) {
		List<?> result = null;
		if (map.containsKey(key)) {
			try {
				Object obj = map.get(key);
				if (obj instanceof List<?>) {
					result = (List<?>) obj;
				} else if (obj instanceof String) {
					result = (List<?>) JSONValue.parse((String) obj);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static int getInt(Map<String, Object> map, String key) {
		int result = -1;
		if (map.containsKey(key)) {
			try {
				Object obj = map.get(key);
				if (obj instanceof Number) {
					result = ((Number) obj).intValue();
				} else if (obj instanceof String) {
					result = Integer.parseInt((String) obj);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static boolean getBoolean(Map<String, Object> map, String key) {
		boolean result = false;
		if (map.containsKey(key)) {
			try {
				result = ((Boolean) map.get(key)).booleanValue();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static Icon getPngIcon(Map<String, Object> map, String key) {
		Icon result = null;
		if (map.containsKey(key)) {
			try {
				Object obj = map.get(key);
				byte[] img_data = null;
				if (obj instanceof byte[]) {
					img_data = (byte[]) obj;
				} else if (obj instanceof String) {
					img_data = Base64.getMimeDecoder().decode((String) map.get(key));
				} else {
					System.err.println("getPngIcon NULL");
				}

				if (img_data != null) {
					result = new ImageIcon(img_data);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static <T extends Enum<T>> EnumSet<T> getEnumSet(Map<String, Object> map, String key,
			Class<T> type) {
		EnumSet<T> result = EnumSet.noneOf(type);
		if (map.containsKey(key)) {
			try {
				long value = ((Number) map.get(key)).longValue();
				result = EnumUtils.toEnumSet(value, type);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static void putSerializableObject(Map<String, Object> row, String key, Serializable value) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baos));) {
			oos.writeObject(value);
			oos.close();
			row.put(key, baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
