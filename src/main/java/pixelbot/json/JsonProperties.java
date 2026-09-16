package pixelbot.json;

import java.io.*;
import java.util.*;

import pixelbot.misc.UnicodeReader;

public class JsonProperties {

	public static void save(Map<String, Object> map, String filename) {
		try {
			String data = JSONValue.toJSONString_(map);
			try (FileOutputStream fos = new FileOutputStream(filename)) {
				fos.write(data.getBytes("UTF-8"));
			}
		} catch (Exception e) {}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> load(String filename) {
		try {
			return (Map<String, Object>) JSONValue.parse(new UnicodeReader(new FileInputStream(
					filename), "UTF-8"));
		} catch (Exception e) {}
		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public static void put(String key, Object value, String filename) {
		Map<String, Object> startmap = load(filename);
		Map<String, Object> map = startmap;
		String[] keys = key.split("\\.");
		for (int i = 0; i < keys.length - 1; ++i) {
			Map<String, Object> child = (Map<String, Object>) map.get(keys[i]);
			if (child == null) {
				child = new HashMap<String, Object>();
				map.put(keys[i], child);
				child = (Map<String, Object>) map.get(keys[i]);
			}
			map = child;
		}
		map.put(keys[keys.length - 1], value);
		save(startmap, filename);
	}

	@SuppressWarnings("unchecked")
	public static Object get(String key, String filename) {
		Map<String, Object> startmap = load(filename);
		Map<String, Object> map = startmap;
		String[] keys = key.split("\\.");
		for (int i = 0; i < keys.length - 1; ++i) {
			Map<String, Object> child = (Map<String, Object>) map.get(keys[i]);
			if (child == null) {
				child = new HashMap<String, Object>();
				map.put(keys[i], child);
				child = (Map<String, Object>) map.get(keys[i]);
			}
			map = child;
		}
		return map.get(keys[keys.length - 1]);
	}

}
