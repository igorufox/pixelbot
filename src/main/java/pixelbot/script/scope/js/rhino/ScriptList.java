package pixelbot.script.scope.js.rhino;

import java.util.*;

import org.mozilla.javascript.*;

import pixelbot.script.scope.general.*;

public class ScriptList extends WrappersBase implements Scriptable, Wrapper {
	IScopeList list = null;

	public ScriptList(IConverter converter, IScopeList callable) {
		super(converter);
		this.list = callable;
	}

	@Override
	public String getClassName() {
		return "List";
	}

	@Override
	public Object get(String name, Scriptable paramScriptable) {
		if ("length".equals(name)) {
			return Integer.valueOf(this.list.size());
		} else if ("push".equals(name)) {
			return new ScriptFunction(this.converter, new IScopeFunction() {
				@Override
				public Object getObject() {
					return null;
				}

				@Override
				public Collection<String> getArgsNames() {
					return Arrays.asList(new String[] {});
				}

				@Override
				public IScopeObject call(IScopeObject thisObj, IScopeObject... params) {
					ScriptList.this.list.add(params[0]);
					return params[0];
				}

				@Override
				public String origin() {
					return "rhino";
				}
			});
		}
		return this.converter.ScopeObjectToScriptObject(this.list.get(Integer.parseInt(name)));
	}

	@Override
	public Object get(int pos, Scriptable paramScriptable) {
		return this.converter.ScopeObjectToScriptObject(this.list.get(pos));
	}

	@Override
	public boolean has(String name, Scriptable paramScriptable) {
		return this.list.has(Integer.parseInt(name));
	}

	@Override
	public boolean has(int pos, Scriptable paramScriptable) {
		return this.list.has(pos);
	}

	@Override
	public void put(String name, Scriptable paramScriptable, Object value) {
		this.list.put(Integer.parseInt(name), this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public void put(int pos, Scriptable paramScriptable, Object value) {
		this.list.put(pos, this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public void delete(String name) {
		this.list.delete(Integer.parseInt(name));

	}

	@Override
	public void delete(int pos) {
		this.list.delete(pos);

	}

	@Override
	public Scriptable getPrototype() {
		return null;
	}

	@Override
	public void setPrototype(Scriptable paramScriptable) {

	}

	@Override
	public Scriptable getParentScope() {
		return null;
	}

	@Override
	public void setParentScope(Scriptable paramScriptable) {}

	@Override
	public Object[] getIds() {
		return Arrays.asList(this.list.getIds()).toArray();
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
		return this.list;
	}

	@Override
	public String toString() {
		return this.list.toString();
	}
}
