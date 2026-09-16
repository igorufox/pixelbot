package pixelbot.script.scope.js.graal;

import org.graalvm.polyglot.proxy.ProxyObject;

import pixelbot.script.scope.general.*;

/** A scope map made to look like an ordinary JavaScript object to GraalJS. */
public class ScriptMap extends WrappersBase implements ProxyObject, IScriptWrapper {

	private final IScopeMap map;

	public ScriptMap(IConverter converter, IScopeMap map) {
		super(converter);
		this.map = map;
	}

	@Override
	public Object getMember(String key) {
		return this.converter.ScopeObjectToScriptObject(this.map.get(key));
	}

	@Override
	public Object getMemberKeys() {
		return this.map.getIds();
	}

	@Override
	public boolean hasMember(String key) {
		return this.map.has(key);
	}

	@Override
	public void putMember(String key, org.graalvm.polyglot.Value value) {
		this.map.put(key, this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public boolean removeMember(String key) {
		if (!this.map.has(key)) {
			return false;
		}
		this.map.delete(key);
		return true;
	}

	@Override
	public IScopeObject getObject() {
		return this.map;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
