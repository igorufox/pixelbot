package pixelbot.script.scope.java;

import pixelbot.script.scope.general.*;

public class NativeList extends JScriptList implements INativeWrapper {

	public NativeList(IConverter converter, IScopeList list) {
		super(converter, list);
	}

	public NativeList() {
		this(new JavaConverter(), new JScopeList());
	}
	
}
