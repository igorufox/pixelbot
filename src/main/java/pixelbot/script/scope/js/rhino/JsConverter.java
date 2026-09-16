package pixelbot.script.scope.js.rhino;

import org.mozilla.javascript.*;

import pixelbot.script.scope.general.*;

public class JsConverter extends JConverter {

	@Override
	@SuppressWarnings("unchecked")
	public IScopeObject ScriptObjectToScopeObject(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Function) {
			return new ScopeFunction(this, (Function) obj);
		} else if (obj instanceof NativeArray) {
			return new JScopeList(this, (NativeArray) obj);
		} else if ("org.mozilla.javascript.Arguments".equals(obj.getClass().getName())) {
			return new ScopeList(this, (Scriptable) obj);
		} else if (obj instanceof IdScriptableObject) {
			return new ScopeMap(this, (Scriptable) obj);
		} else if (obj instanceof UniqueTag) {
			return null;
		} else if (obj instanceof Undefined) {
			return null;
		} else if (obj instanceof Wrapper) {
			return this.ScriptObjectToScopeObject(((Wrapper) obj).unwrap());
		} else {
			return super.ScriptObjectToScopeObject(obj);
		}

	}

	@Override
	public Object ScopeObjectToScriptObject(IScopeObject obj) {
		if (obj == null) {
			return null;
			// } else if (obj instanceof JScopeNativeObject) {
			// return obj.getObject();
		} else if (obj.getObject() instanceof Scriptable) {
			return obj.getObject();
		} else if (obj instanceof IScopeMap) {
			return new ScriptMap(this, (IScopeMap) obj);
		} else if (obj instanceof IScopeList) {
			return new ScriptList(this, (IScopeList) obj);
		} else if (obj instanceof IScopeFunction) {
			return new ScriptFunction(this, (IScopeFunction) obj);
		} else {
			return super.ScopeObjectToScriptObject(obj);
		}

	}
}
