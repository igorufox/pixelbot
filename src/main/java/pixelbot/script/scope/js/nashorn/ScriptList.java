package pixelbot.script.scope.js.nashorn;

import java.util.*;

import org.openjdk.nashorn.api.scripting.AbstractJSObject;

import pixelbot.script.scope.general.*;

/** A scope list made to look like an ordinary JavaScript array to Nashorn. */
public class ScriptList extends AbstractJSObject implements IScriptWrapper {

	private final IConverter converter;
	private final IScopeList list;

	public ScriptList(IConverter converter, IScopeList list) {
		this.converter = converter;
		this.list = list;
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public Object getSlot(int index) {
		return this.converter.ScopeObjectToScriptObject(this.list.get(index));
	}

	@Override
	public boolean hasSlot(int slot) {
		return this.list.has(slot);
	}

	@Override
	public void setSlot(int index, Object value) {
		this.list.put(index, this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public Object getMember(String name) {
		if ("length".equals(name)) {
			return Integer.valueOf(this.list.size());
		}
		try {
			return this.getSlot(Integer.parseInt(name));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public boolean hasMember(String name) {
		return "length".equals(name) || this.getMember(name) != null;
	}

	@Override
	public Set<String> keySet() {
		Set<String> keys = new LinkedHashSet<String>();
		for (int id : this.list.getIds()) {
			keys.add(Integer.toString(id));
		}
		return keys;
	}

	@Override
	public Collection<Object> values() {
		List<Object> result = new ArrayList<Object>();
		for (int id : this.list.getIds()) {
			result.add(this.getSlot(id));
		}
		return result;
	}

	@Override
	public IScopeObject getObject() {
		return this.list;
	}

	@Override
	public String toString() {
		return this.list.toString();
	}
}
