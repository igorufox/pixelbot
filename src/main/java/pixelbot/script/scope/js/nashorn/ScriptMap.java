package pixelbot.script.scope.js.nashorn;

import java.util.*;

import org.openjdk.nashorn.api.scripting.AbstractJSObject;

import pixelbot.script.scope.general.*;

/** A scope map made to look like an ordinary JavaScript object to Nashorn. */
public class ScriptMap extends AbstractJSObject implements IScriptWrapper {

	private final IConverter converter;
	private final IScopeMap map;

	public ScriptMap(IConverter converter, IScopeMap map) {
		this.converter = converter;
		this.map = map;
	}

	@Override
	public Object getMember(String name) {
		return this.map.has(name) ? this.converter.ScopeObjectToScriptObject(this.map.get(name))
				: null;
	}

	@Override
	public boolean hasMember(String name) {
		return this.map.has(name);
	}

	@Override
	public void setMember(String name, Object value) {
		this.map.put(name, this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public void removeMember(String name) {
		this.map.delete(name);
	}

	@Override
	public Set<String> keySet() {
		return new LinkedHashSet<String>(Arrays.asList(this.map.getIds()));
	}

	@Override
	public Collection<Object> values() {
		List<Object> result = new ArrayList<Object>();
		for (String key : this.map.getIds()) {
			result.add(this.getMember(key));
		}
		return result;
	}

	@Override
	public IScopeObject getObject() {
		return this.map;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
