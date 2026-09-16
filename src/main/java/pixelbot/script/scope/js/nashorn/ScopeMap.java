package pixelbot.script.scope.js.nashorn;

import java.util.*;

import org.openjdk.nashorn.api.scripting.JSObject;

import pixelbot.misc.MapEntry;
import pixelbot.script.scope.general.*;

/** A Nashorn object seen as a scope map. */
public class ScopeMap extends WrappersBase implements IScopeMap {

	protected final JSObject object;
	private String origin;

	ScopeMap(NashornConverter converter, JSObject object) {
		super(converter);
		this.object = object;
	}

	@Override
	public Object getObject() {
		return this.object;
	}

	@Override
	public Iterator<Map.Entry<String, IScopeObject>> iterator() {
		List<Map.Entry<String, IScopeObject>> result = new ArrayList<Map.Entry<String, IScopeObject>>();
		for (String key : this.getIds()) {
			result.add(new MapEntry<IScopeObject>(key, this.get(key)));
		}
		return result.iterator();
	}

	@Override
	public IScopeObject get(String name) {
		return this.converter.ScriptObjectToScopeObject(this.object.getMember(name));
	}

	@Override
	public boolean has(String name) {
		return this.object.hasMember(name);
	}

	@Override
	public void put(String name, IScopeObject value) {
		this.object.setMember(name, this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void delete(String name) {
		this.object.removeMember(name);
	}

	@Override
	public String[] getIds() {
		return this.object.keySet().toArray(new String[0]);
	}

	@Override
	public String origin() {
		return this.origin == null || this.origin.isEmpty() ? "nashorn" : this.origin + "/nashorn";
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public String toString() {
		return String.valueOf(this.object);
	}
}
