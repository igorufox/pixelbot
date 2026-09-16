package pixelbot.script.scope.js.nashorn;

import org.openjdk.nashorn.api.scripting.AbstractJSObject;

import pixelbot.script.scope.general.*;

/** A scope function made callable from Nashorn. */
public class ScriptFunction extends AbstractJSObject implements IScriptWrapper {

	private final IConverter converter;
	private final IScopeFunction function;

	public ScriptFunction(IConverter converter, IScopeFunction function) {
		this.converter = converter;
		this.function = function;
	}

	@Override
	public boolean isFunction() {
		return true;
	}

	@Override
	public Object call(Object thiz, Object... args) {
		try {
			return this.converter.ScopeObjectToScriptObject(this.function.call(
					this.converter.ScriptObjectToScopeObject(thiz),
					this.converter.ScriptObjectArrayToScopeObjectArray(args)));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e);
		}
	}

	@Override
	public IScopeObject getObject() {
		return this.function;
	}

	@Override
	public String toString() {
		return this.function.toString();
	}
}
