package pixelbot.script.scope.general;

import java.util.*;

import pixelbot.misc.MapEntry;

public class JScriptMap extends WrappersBase implements Map<String, Object>, IScriptWrapper {
	private IScopeMap map;

	public JScriptMap(IConverter converter, IScopeMap map) {
		super(converter);
		this.map = map;
	}

	@Override
	public int size() {
		return this.map.getIds().length;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return this.map.has(key.toString());
	}

	@Override
	public boolean containsValue(Object value) {
		return this.values().contains(value);
	}

	@Override
	public Object get(Object key) {
		return this.converter.ScopeObjectToScriptObject(this.map.get(key.toString()));
	}

	@Override
	public Object put(String key, Object value) {
		this.map.put(key, this.converter.ScriptObjectToScopeObject(value));
		return value;
	}

	@Override
	public Object remove(Object key) {
		Object obj = this.map.get(key.toString());
		this.map.delete(key.toString());
		return obj;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		for (Map.Entry<? extends String, ? extends Object> e : toMerge.entrySet()) {
			this.put(e.getKey(), e.getValue());
		}
	}

	@Override
	public void clear() {
		Object[] ids = null;
		while ((ids = this.map.getIds()) != null && ids.length > 0) {
			this.remove(ids[0]);
		}
	}

	@Override
	public Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (Object o : this.map.getIds()) {
			result.add(o.toString());
		}
		return result;
	}

	@Override
	public Collection<Object> values() {
		Collection<Object> result = new ArrayList<Object>();
		for (Object o : this.map.getIds()) {
			result.add(this.get(o));
		}
		return result;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> result = new TreeSet<Map.Entry<String, Object>>(
				new Comparator<Map.Entry<String, Object>>() {
					@Override
					public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
						return o1.getKey().compareTo(o2.getKey());
					}
				});
		for (Object o : this.map.getIds()) {
			result.add(new MapEntry<Object>(o.toString(), this.get(o)));
		}
		return result;
	}

	@Override
	public IScopeObject getObject() {
		return this.map;
	}

}
