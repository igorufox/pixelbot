package pixelbot.script.scope.js.rhino;

import org.mozilla.javascript.*;

import pixelbot.script.scope.general.*;

public class ScriptMap extends WrappersBase implements Scriptable, Wrapper {
	IScopeMap map = null;
	Scriptable parent = null;
	Scriptable prototype = null;

	public ScriptMap(IConverter converter, IScopeMap callable) {
		super(converter);
		this.map = callable;
	}

	@Override
	public String getClassName() {
		return "Map";
	}

	@Override
	public Object get(String name, Scriptable paramScriptable) {
		if (this.map.has(name)) {
			return this.converter.ScopeObjectToScriptObject(this.map.get(name));
		} else {
			return Scriptable.NOT_FOUND;
		}
	}

	@Override
	public Object get(int pos, Scriptable paramScriptable) {
		if (this.map.has(Integer.toString(pos))) {
			return this.converter.ScopeObjectToScriptObject(this.map.get(Integer.toString(pos)));
		} else {
			return Scriptable.NOT_FOUND;
		}
	}

	@Override
	public boolean has(String name, Scriptable paramScriptable) {
		return this.map.has(name);
	}

	@Override
	public boolean has(int pos, Scriptable paramScriptable) {
		return this.map.has(Integer.toString(pos));
	}

	@Override
	public void put(String name, Scriptable paramScriptable, Object value) {
		this.map.put(name, this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public void put(int pos, Scriptable paramScriptable, Object value) {
		this.map.put(Integer.toString(pos), this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public void delete(String name) {
		this.map.delete(name);

	}

	@Override
	public void delete(int pos) {
		this.map.delete(Integer.toString(pos));

	}

	@Override
	public Scriptable getPrototype() {
		return this.prototype;
	}

	@Override
	public void setPrototype(Scriptable prototype) {
		this.prototype = prototype;

	}

	@Override
	public Scriptable getParentScope() {
		return this.parent;
	}

	@Override
	public void setParentScope(Scriptable parent) {
		this.parent = parent;
	}

	@Override
	public Object[] getIds() {
		return this.map.getIds();
	}

	@Override
	public Object getDefaultValue(Class<?> paramClass) {
		if (paramClass == null || paramClass == String.class) {
			return this.toString();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasInstance(Scriptable paramScriptable) {
		return false;
	}

	@Override
	public Object unwrap() {
		return this.map;
	}
	
	@Override
	public String toString() {
		return this.map.toString();
	}

}