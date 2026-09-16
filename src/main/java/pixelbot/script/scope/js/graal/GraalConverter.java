package pixelbot.script.scope.js.graal;

import org.graalvm.polyglot.Value;

import pixelbot.script.scope.general.*;

/**
 * Translates between GraalJS {@link Value}s and the engine-independent scope model.
 * <p>
 * Going the other way the values are {@code Proxy} implementations, which is how Graal lets a host
 * object appear to JavaScript as an ordinary object, array or function.
 */
public class GraalConverter extends JConverter {

	private final GraalRuntime runtime;

	public GraalConverter(GraalRuntime runtime) {
		this.runtime = runtime;
	}

	public GraalRuntime getRuntime() {
		return this.runtime;
	}

	@Override
	public IScopeObject ScriptObjectToScopeObject(Object obj) {
		if (!(obj instanceof Value)) {
			return super.ScriptObjectToScopeObject(obj);
		}

		Value value = (Value) obj;
		return this.runtime.call(() -> {
			if (value.isNull()) {
				return null;
			} else if (value.isString()) {
				return new JScopeString(value.asString());
			} else if (value.isBoolean()) {
				return new JScopeBoolean(Boolean.valueOf(value.asBoolean()));
			} else if (value.isNumber()) {
				return new JScopeNumber(value.as(Number.class));
			} else if (value.canExecute()) {
				return new ScopeFunction(this, value);
			} else if (value.hasArrayElements()) {
				return new ScopeList(this, value);
			} else if (value.isHostObject()) {
				return super.ScriptObjectToScopeObject(value.asHostObject());
			} else if (value.hasMembers()) {
				return new ScopeMap(this, value);
			}
			return null;
		});
	}

	@Override
	public Object ScopeObjectToScriptObject(IScopeObject obj) {
		if (obj == null) {
			return null;
		} else if (obj.getObject() instanceof Value) {
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
