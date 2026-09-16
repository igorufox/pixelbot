package pixelbot.json;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import net.sf.json.*;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.beanutils.PropertyUtils;


public class JSONValue {

	@Target(value = ElementType.METHOD)
	@Retention(value = RetentionPolicy.RUNTIME)
	public @interface JsonIgnore {}

	private static final JsonConfig jsonConfig = new JsonConfig();
	static {
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessor() {

			@Override
			public Object processObjectValue(String paramString, Object paramObject,
					JsonConfig paramJsonConfig) {
				return this.processObjectValue((Date) paramObject);
			}

			@Override
			public Object processArrayValue(Object paramObject, JsonConfig paramJsonConfig) {
				return this.processObjectValue((Date) paramObject);
			}

			private Object processObjectValue(Date paramObject) {
				return Long.valueOf(paramObject.getTime());
			}
		});

		jsonConfig.registerJsonValueProcessor(byte[].class, new JsonValueProcessor() {

			@Override
			public Object processObjectValue(String paramString, Object paramObject,
					JsonConfig paramJsonConfig) {
				return this.processObjectValue((byte[]) paramObject);
			}

			@Override
			public Object processArrayValue(Object paramObject, JsonConfig paramJsonConfig) {
				return this.processObjectValue((byte[]) paramObject);
			}

			private Object processObjectValue(byte[] paramObject) {
				return Base64.getEncoder().encodeToString(paramObject);
			}
		});

		jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				try {
					PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(source, name);
					if ((pd != null)
							&& (((pd.getReadMethod() != null) && (pd.getReadMethod().getAnnotation(
									JsonIgnore.class) != null)) || ((pd.getWriteMethod() != null) && (pd
									.getWriteMethod().getAnnotation(JsonIgnore.class) != null)))) {
						return true;
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
				return false;
			}
		});
	}

	public static String toJSONString_(Object value) {
		if (value == null) {
			return "";
		} else if (value instanceof JSON) {
			return ((JSON) value).toString(4, 0).replace("    ", "\t");
		} else if (value instanceof JSONFunction) {
			return ((JSONFunction) value).toString().replace("    ", "\t");
		} else if (value instanceof String) {
			return "\"" + value.toString() + "\"";
		} else if (value instanceof IJsonSerializable && value instanceof List<?>) {
			return JSONArray.fromObject(value, jsonConfig).toString(4).replace("    ", "\t");
		} else if (value instanceof IJsonSerializable) {
			return JSONObject.fromObject(value, jsonConfig).toString(4).replace("    ", "\t");
		} else if (value instanceof List<?>) {
			return JSONArray.fromObject(value).toString(4).replace("    ", "\t");
		} else if (value instanceof Map<?, ?>) {
			return JSONObject.fromObject(value).toString(4).replace("    ", "\t");
		} else if (value.getClass().isArray()) {
			return JSONArray.fromObject(value, jsonConfig).toString(4).replace("    ", "\t");
		} else {
			return value.toString();
		}
	}

	public static Object parse(String value) {
		try {
			Object o = JSONArray.fromObject("[" + value + "]").get(0);

			if (o instanceof JSONObject) {
				Object[] keys = ((JSONObject) o).keySet().toArray();
				Arrays.sort(keys);
				JSONObject obj = new JSONObject();
				for (Object k : keys) {
					obj.put(k, ((JSONObject) o).get(k));
				}
				o = obj;
			}

			return o;

		} catch (Exception e) {
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object parse(Reader reader) {
		StringBuffer fileData = new StringBuffer(1000);
		char[] buf = new char[1024];
		int numRead = 0;
		try {
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parse(fileData.toString());
	}

	public static String formatJs(String jsstring) {
		try {
			return JsFormater.formatJs(jsstring);
		} catch (NoClassDefFoundError e) {}
		return jsstring;
	}

}
