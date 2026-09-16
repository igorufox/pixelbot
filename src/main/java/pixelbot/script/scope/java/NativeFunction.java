package pixelbot.script.scope.java;

import pixelbot.script.scope.general.*;

public class NativeFunction extends WrappersBase implements INativeCallable, IScriptWrapper {
	IScopeFunction func;

	public NativeFunction(IConverter converter, IScopeFunction func) {
		super(converter);
		this.func = func;
	}

	@Override
	public Object call(Object thisObj, Object... params) throws InterruptedException {
		return this.converter.ScopeObjectToScriptObject(this.func.call(
				this.converter.ScriptObjectToScopeObject(thisObj),
				this.converter.ScriptObjectArrayToScopeObjectArray(params)));
	}

	@Override
	public IScopeObject getObject() {
		return this.func;
	}

}
