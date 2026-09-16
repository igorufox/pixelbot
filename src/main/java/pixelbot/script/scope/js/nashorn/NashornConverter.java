package pixelbot.script.scope.js.nashorn;

import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import pixelbot.script.scope.general.*;

/**
 * Translates between Nashorn's {@link JSObject} mirrors and the engine-independent scope model.
 * <p>
 * Nashorn presents every script object through the same mirror type, so what kind of scope object
 * it is has to be asked rather than inferred from the class: {@code isFunction}, {@code isArray},
 * then plain object.
 */
public class NashornConverter extends JConverter {

	@Override
	public IScopeObject ScriptObjectToScopeObject(Object obj) {
		if (obj == null || ScriptObjectMirror.isUndefined(obj)) {
			return null;
		}
		if (obj instanceof JSObject) {
			JSObject js = (JSObject) obj;
			if (js.isFunction()) {
				return new ScopeFunction(this, js);
			} else if (js.isArray()) {
				return new ScopeList(this, js);
			}
			return new ScopeMap(this, js);
		}
		return super.ScriptObjectToScopeObject(obj);
	}

	@Override
	public Object ScopeObjectToScriptObject(IScopeObject obj) {
		if (obj == null) {
			return null;
		} else if (obj.getObject() instanceof JSObject) {
			return obj.getObject();
		} else if (obj instanceof IScopeFunction) {
			return new ScriptFunction(this, (IScopeFunction) obj);
		} else if (obj instanceof IScopeList) {
			return new ScriptList(this, (IScopeList) obj);
		} else if (obj instanceof IScopeMap) {
			return new ScriptMap(this, (IScopeMap) obj);
		}
		return super.ScopeObjectToScriptObject(obj);
	}
}
