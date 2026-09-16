package pixelbot.misc;

import java.io.File;
import java.net.*;
import java.util.*;

public class Tools {
	public final static String MESSAGES = "pixelbot.resources.messages";
	public static File workdir = new File(".");

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseQueryString(String query) {

		Map<String, Object> queries = new HashMap<String, Object>();
		try {
			String[] parameters = query.split("&");
			for (String key : parameters) {
				String value = "";
				int n = key.indexOf("=");
				if (n > 0) {
					value = URLDecoder.decode(key.substring(n + 1), "UTF-8");
					key = key.substring(0, n);
				}
				Object t = queries.get(key);
				if (t == null) {
					queries.put(key, value);
				} else if (t instanceof String) {
					ArrayList<String> a = new ArrayList<String>();
					a.add((String) t);
					a.add(value);
					queries.put(key, a);
				} else if (t instanceof List) {
					((List<String>) t).add(value);
				}
			}
		} catch (Exception ex) {}
		return queries;
	}

	@SuppressWarnings("unchecked")
	public static String generateQueryString(Map<String, Object> queries) {
		StringBuffer result = new StringBuffer();
		try {
			for (Map.Entry<String, Object> query : queries.entrySet()) {
				if (query.getValue() instanceof List) {
					for (String s : (List<String>) query.getValue()) {
						result.append(query.getKey()).append('=')
								.append(URLEncoder.encode(s, "UTF-8")).append('&');
					}
				} else {
					result.append(query.getKey()).append('=')
							.append(URLEncoder.encode(query.getValue().toString(), "UTF-8"))
							.append('&');
				}
			}
			if (result.length() != 0) {
				result.setLength(result.length() - 1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result.toString();
	}

}
