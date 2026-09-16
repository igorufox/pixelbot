package pixelbot.misc;

import java.util.Map;

public class MapEntry<T> implements Map.Entry<String, T> {
	String key;
	T value;

	public MapEntry(String key, T value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public T getValue() {
		return this.value;
	}

	@Override
	public T setValue(T value) {
		return this.value = value;
	}

	@Override
	public String toString() {
		return "{'" + this.getKey() + "':'" + this.getValue() + "'}";
	}
}