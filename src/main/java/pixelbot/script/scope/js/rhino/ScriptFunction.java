package pixelbot.script.scope.js.rhino;

import org.mozilla.javascript.*;

import pixelbot.script.scope.general.*;

public class ScriptFunction extends WrappersBase implements Function, IScriptWrapper {
	IScopeFunction obj = null;

	public ScriptFunction(IConverter converter, IScopeFunction obj) {
		super(converter);
		this.obj = obj;
	}

	@Override
	public void setPrototype(Scriptable paramScriptable) {}

	@Override
	public void setParentScope(Scriptable paramScriptable) {}

	@Override
	public void put(int paramInt, Scriptable paramScriptable, Object paramObject) {}

	@Override
	public void put(String paramString, Scriptable paramScriptable, Object paramObject) {}

	@Override
	public boolean hasInstance(Scriptable paramScriptable) {
		return false;
	}

	@Override
	public boolean has(int paramInt, Scriptable paramScriptable) {
		return false;
	}

	@Override
	public boolean has(String paramString, Scriptable paramScriptable) {
		return false;
	}

	@Override
	public Scriptable getPrototype() {
		return null;
	}

	@Override
	public Scriptable getParentScope() {
		return new ScriptMap(this.converter, new JScopeMap());
	}

	@Override
	public Object[] getIds() {
		return null;
	}

	@Override
	public Object getDefaultValue(Class<?> paramClass) {
		return null;
	}

	@Override
	public String getClassName() {
		return null;
	}

	@Override
	public Object get(int paramInt, Scriptable paramScriptable) {
		return null;
	}

	@Override
	public Object get(String paramString, Scriptable paramScriptable) {
		return null;
	}

	@Override
	public void delete(int paramInt) {}

	@Override
	public void delete(String paramString) {}

	@Override
	public Scriptable construct(Context paramContext, Scriptable paramScriptable,
			Object[] paramArrayOfObject) {
		return null;
	}

	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		try {
			return this.converter.ScopeObjectToScriptObject(this.obj.call(
					 this.converter.ScriptObjectToScopeObject(thisObj),
					this.converter.ScriptObjectArrayToScopeObjectArray(args)));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IScopeObject getObject() {
		return this.obj;
	}

}
