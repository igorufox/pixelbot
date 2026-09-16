package pixelbot.script.scope.general;

import java.util.*;
import java.util.Map.Entry;

import pixelbot.misc.MapEntry;

public class JScopeMap extends WrappersBase implements IScopeMap {
	private Map<String, Object> map = null;
	private String origin = null;

	public JScopeMap() {
		this(new JConverter(), new HashMap<String, Object>());
	}

	public JScopeMap(IConverter converter, Map<String, Object> map) {
		super(converter);
		if (map == null)
			throw new NullPointerException();
		this.map = map;
	}

	@Override
	public Iterator<Map.Entry<String, IScopeObject>> iterator() {
		final Iterator<Map.Entry<String, Object>> iter = this.map.entrySet().iterator();
		return new Iterator<Map.Entry<String, IScopeObject>>() {
			Iterator<Map.Entry<String, Object>> iterator = iter;

			@Override
			public void remove() {
				this.iterator.remove();
			}

			@Override
			public Entry<String, IScopeObject> next() {
				Map.Entry<String, Object> entry = this.iterator.next();
				return new MapEntry<IScopeObject>(entry.getKey(),
						JScopeMap.this.converter.ScriptObjectToScopeObject(entry.getValue()));
			}

			@Override
			public boolean hasNext() {
				return this.iterator.hasNext();
			}
		};

	}

	@Override
	public Object getObject() {
		return this.map;
	}

	@Override
	public IScopeObject get(String name) {
		return this.converter.ScriptObjectToScopeObject(this.map.get(name));
	}

	@Override
	public boolean has(String name) {
		return this.map.containsKey(name);
	}

	@Override
	public void put(String name, IScopeObject value) {
		this.map.put(name, this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void delete(String name) {
		this.map.remove(name);
	}

	@Override
	public String[] getIds() {
		String[] keys = this.map.keySet().toArray(new String[] {});
		Arrays.sort(keys);
		return keys;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}

	@Override
	public String origin() {
		if (this.origin == null || this.origin.length() == 0) {
			return "general";
		} else {
			return this.origin + "/general";
		}
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;

	}
}
