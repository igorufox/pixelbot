package pixelbot.script.scope.java;

import pixelbot.script.scope.general.*;

public class JavaConverter extends JConverter {

	@Override
	public Object ScopeObjectToScriptObject(IScopeObject obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof IScopeNative) {
			return ((IScopeNative) obj).getObject();
		} else if (obj instanceof IScopeMap) {
			return new NativeMap(this, (IScopeMap) obj);
		} else if (obj instanceof IScopeList) {
			return new NativeList(this, (IScopeList) obj);
		} else if (obj instanceof IScopeFunction) {
			return new NativeFunction(this, (IScopeFunction) obj);
		} else {
			return super.ScopeObjectToScriptObject(obj);
		}
	}

	@Override
	public IScopeObject ScriptObjectToScopeObject(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof INativeCallable) {
			return new ScopeFunction(this, (INativeCallable) obj);
		} else if (obj instanceof INativeWrapper) {
			return ((INativeWrapper) obj).getObject();
		} else {
			return super.ScriptObjectToScopeObject(obj);
		}
	}

}
