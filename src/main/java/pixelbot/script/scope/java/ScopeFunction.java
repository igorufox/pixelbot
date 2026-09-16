package pixelbot.script.scope.java;

import java.util.*;

import pixelbot.script.scope.general.*;

public class ScopeFunction extends WrappersBase implements IScopeFunction {
	private INativeCallable callable;

	public ScopeFunction(IConverter converter, INativeCallable callable) {
		super(converter);
		this.callable = callable;
	}

	@Override
	public String origin() {
		return "java";
	}

	@Override
	public Object getObject() {
		return this.callable;
	}

	@Override
	public IScopeObject call(IScopeObject thisObj, IScopeObject... params)
			throws InterruptedException {

		return this.converter.ScriptObjectToScopeObject(this.callable.call(
				this.converter.ScopeObjectToScriptObject(thisObj),
				this.converter.ScopeObjectArrayToScriptObjectArray(params)));

	}

	@Override
	public Collection<String> getArgsNames() {
		return Arrays.asList(new String[] {});
	}

}
