package pixelbot.script.scope.js.graal;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

import pixelbot.script.scope.general.*;

/** A scope function made callable from GraalJS. */
public class ScriptFunction extends WrappersBase implements ProxyExecutable, IScriptWrapper {

	private final IScopeFunction function;

	public ScriptFunction(IConverter converter, IScopeFunction function) {
		super(converter);
		this.function = function;
	}

	@Override
	public Object execute(Value... arguments) {
		try {
			IScopeObject[] params = new IScopeObject[arguments.length];
			for (int i = 0; i < arguments.length; ++i) {
				params[i] = this.converter.ScriptObjectToScopeObject(arguments[i]);
			}
			return this.converter.ScopeObjectToScriptObject(this.function.call(null, params));
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
