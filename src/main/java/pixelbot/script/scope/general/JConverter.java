package pixelbot.script.scope.general;

import java.util.*;

public class JConverter implements IConverter {

	@SuppressWarnings("unchecked")
	@Override
	public IScopeObject ScriptObjectToScopeObject(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof IScopeObject) {
			return (IScopeObject) obj;
		} else if (obj instanceof IScriptWrapper) {
			return ((IScriptWrapper) obj).getObject();
		} else if (obj instanceof String) {
			return new JScopeString((String) obj);
		} else if (obj instanceof CharSequence) {
			return new JScopeString(obj.toString());
		} else if (obj instanceof Number) {
			return new JScopeNumber((Number) obj);
		} else if (obj instanceof Boolean) {
			return new JScopeBoolean((Boolean) obj);
		} else if (obj instanceof Map<?, ?>) {
			return new JScopeMap(this, (Map<String, Object>) obj);
		} else if (obj instanceof List<?>) {
			return new JScopeList(this, (List<Object>) obj);
		} else if (obj.getClass().isArray()) {
			return new JScopeList(this, Arrays.asList((Object[]) obj));
		} else {
			return new JScopeNativeObject(this, obj);
		}
	}

	@Override
	public Object ScopeObjectToScriptObject(IScopeObject obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof JScopeString) {
			return ((JScopeString) obj).getObject();
		} else if (obj instanceof JScopeNumber) {
			return ((JScopeNumber) obj).getObject();
		} else if (obj instanceof JScopeBoolean) {
			return ((JScopeBoolean) obj).getObject();
		} else if (obj instanceof JScopeNativeObject) {
			return ((JScopeNativeObject) obj).getObject();
		} else if (obj instanceof IScopeList) {
			return new JScriptList(this, (IScopeList) obj);
		} else if (obj instanceof IScopeMap) {
			return new JScriptMap(this, (IScopeMap) obj);
		} else if (obj instanceof JScopeBoolean) {
			return ((JScopeBoolean) obj).getObject();
		} else {
			return obj.getObject();
		}
		// } else if (obj instanceof JScopeString) {
		// return ((JScopeString) obj).getObject();
		// } else if (obj instanceof JScopeNumber) {
		// return ((JScopeNumber) obj).getObject();
		// } else if (obj instanceof JScopeBoolean) {
		// return ((JScopeBoolean) obj).getObject();
		// } else {
		// return ((JScopeNativeObject) obj).getObject();

	}

	@Override
	public IScopeObject[] ScriptObjectArrayToScopeObjectArray(Object[] obj) {
		if (obj == null)
			return null;
		IScopeObject[] result = new IScopeObject[obj.length];
		for (int i = 0; i < obj.length; ++i) {
			result[i] = this.ScriptObjectToScopeObject(obj[i]);
		}
		return result;

	}

	@Override
	public Object[] ScopeObjectArrayToScriptObjectArray(IScopeObject[] obj) {
		if (obj == null)
			return null;
		Object[] result = new Object[obj.length];
		for (int i = 0; i < obj.length; ++i) {
			result[i] = this.ScopeObjectToScriptObject(obj[i]);
		}
		return result;
	}

}
