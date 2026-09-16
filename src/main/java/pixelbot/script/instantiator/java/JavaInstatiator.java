package pixelbot.script.instantiator.java;

import pixelbot.script.instantiator.general.IScriptInstantiator;
import pixelbot.script.scope.general.*;
import pixelbot.script.scope.java.*;
import pixelbot.script.scope.java.GlobalScope;

public class JavaInstatiator implements IScriptInstantiator {
	private final IConverter converter;
	private final GlobalScope scope;
	private final String origin;
	private final INativeJob job;

	public JavaInstatiator(IConverter converter, GlobalScope scope, String origin, INativeJob job) {
		this.converter = converter;
		this.scope = scope;
		this.origin = origin;
		this.job = job;
	}

	@Override
	public void instantiate() {
		try {
			this.scope.setCurrentOrigin(this.origin);
			this.job.setContext(this.scope);
			JScopeNativeObject jsno = new JScopeNativeObject(this.converter, this.job);
			this.scope.put(this.job.getClass().getSimpleName(), jsno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IScopeFunction getFunction() {
		throw new UnsupportedOperationException();
	}

}
