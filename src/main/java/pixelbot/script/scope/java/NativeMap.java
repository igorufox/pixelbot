package pixelbot.script.scope.java;

import java.util.Map;

import pixelbot.script.scope.general.*;

public class NativeMap extends JScriptMap implements Map<String, Object>, INativeWrapper {

	public NativeMap(IConverter converter, IScopeMap map) {
		super(converter, map);
	}

	public NativeMap getMap(String name) {
		return (NativeMap) this.get(name);
	}

	public NativeList getList(String name) {
		return (NativeList) this.get(name);
	}

	public Object getNative(String name) {
		return this.get(name);
	}

	public Object invoke(String name, Object... params) throws InterruptedException {
		Object result = ((NativeFunction) this.get(name)).call(this, params);
		return result;
	}

}
