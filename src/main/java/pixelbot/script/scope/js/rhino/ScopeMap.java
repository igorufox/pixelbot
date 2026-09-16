package pixelbot.script.scope.js.rhino;

import java.util.*;

import org.mozilla.javascript.Scriptable;

import pixelbot.misc.MapEntry;
import pixelbot.script.scope.general.*;

public class ScopeMap extends WrappersBase implements IScopeMap {
	Scriptable scriptable = null;
	private String origin;

	ScopeMap(IConverter converter, Scriptable scriptable) {
		super(converter);
		this.scriptable = scriptable;
	}

	@Override
	public Object getObject() {
		return this.scriptable;
	}

	@Override
	public Iterator<Map.Entry<String, IScopeObject>> iterator() {
		Object[] ids = this.scriptable.getIds();

		Collection<Map.Entry<String, IScopeObject>> sids = new ArrayList<Map.Entry<String, IScopeObject>>(
				ids.length);
		for (int i = 0; i < ids.length; ++i) {
			sids.add(new MapEntry<IScopeObject>(ids[i].toString(), this.get(ids[i].toString())));
		}
		return sids.iterator();

	}

	@Override
	public IScopeObject get(String name) {
		return this.converter.ScriptObjectToScopeObject(this.scriptable.get(name, this.scriptable));
	}

	@Override
	public boolean has(String name) {
		return this.scriptable.has(name, this.scriptable);
	}

	@Override
	public void put(String name, IScopeObject value) {
		this.scriptable.put(name, this.scriptable, this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void delete(String name) {
		this.scriptable.delete(name);
	}

	@Override
	public String[] getIds() {
		return Arrays.asList(this.scriptable.getIds()).toArray(new String[] {});
	}

	@Override
	public String origin() {
		if (this.origin == null || this.origin.length() == 0) {
			return "rhino";
		} else {
			return this.origin + "/rhino";
		}
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;

	}

}
