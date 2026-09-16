package pixelbot.script.scope.js.graal;

import java.util.*;

import org.graalvm.polyglot.Value;

import pixelbot.misc.MapEntry;
import pixelbot.script.scope.general.*;

/** A GraalJS object seen as a scope map. */
public class ScopeMap extends WrappersBase implements IScopeMap {

	protected final Value value;
	private String origin;

	ScopeMap(GraalConverter converter, Value value) {
		super(converter);
		this.value = value;
	}

	private GraalRuntime runtime() {
		return ((GraalConverter) this.converter).getRuntime();
	}

	@Override
	public Object getObject() {
		return this.value;
	}

	@Override
	public Iterator<Map.Entry<String, IScopeObject>> iterator() {
		List<Map.Entry<String, IScopeObject>> result = new ArrayList<Map.Entry<String, IScopeObject>>();
		for (String key : this.getIds()) {
			result.add(new MapEntry<IScopeObject>(key, this.get(key)));
		}
		return result.iterator();
	}

	@Override
	public IScopeObject get(String name) {
		return this.runtime().call(
				() -> this.converter.ScriptObjectToScopeObject(this.value.getMember(name)));
	}

	@Override
	public boolean has(String name) {
		return this.runtime().call(() -> Boolean.valueOf(this.value.hasMember(name)))
				.booleanValue();
	}

	@Override
	public void put(String name, IScopeObject value) {
		this.runtime().run(
				() -> this.value.putMember(name, this.converter.ScopeObjectToScriptObject(value)));
	}

	@Override
	public void delete(String name) {
		this.runtime().run(() -> this.value.removeMember(name));
	}

	@Override
	public String[] getIds() {
		return this.runtime().call(() -> this.value.getMemberKeys().toArray(new String[0]));
	}

	@Override
	public String origin() {
		return this.origin == null || this.origin.isEmpty() ? "graal" : this.origin + "/graal";
	}

	@Override
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public String toString() {
		return this.runtime().call(() -> this.value.toString());
	}
}
