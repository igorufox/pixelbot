package pixelbot.script.scope.js.graal;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;

import pixelbot.script.scope.general.*;

/** A scope list made to look like an ordinary JavaScript array to GraalJS. */
public class ScriptList extends WrappersBase implements ProxyArray, IScriptWrapper {

	private final IScopeList list;

	public ScriptList(IConverter converter, IScopeList list) {
		super(converter);
		this.list = list;
	}

	@Override
	public Object get(long index) {
		return this.converter.ScopeObjectToScriptObject(this.list.get((int) index));
	}

	@Override
	public void set(long index, Value value) {
		this.list.put((int) index, this.converter.ScriptObjectToScopeObject(value));
	}

	@Override
	public boolean remove(long index) {
		if (!this.list.has((int) index)) {
			return false;
		}
		this.list.delete((int) index);
		return true;
	}

	@Override
	public long getSize() {
		return this.list.size();
	}

	@Override
	public IScopeObject getObject() {
		return this.list;
	}

	@Override
	public String toString() {
		return this.list.toString();
	}
}
