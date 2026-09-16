package pixelbot.script.scope.js.graal;

import java.util.*;

import org.graalvm.polyglot.Value;

import pixelbot.script.scope.general.*;

/** A GraalJS array seen as a scope list. */
public class ScopeList extends WrappersBase implements IScopeList {

	private final Value value;

	ScopeList(GraalConverter converter, Value value) {
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
	public Iterator<IScopeObject> iterator() {
		List<IScopeObject> result = new ArrayList<IScopeObject>();
		int size = this.size();
		for (int i = 0; i < size; ++i) {
			result.add(this.get(i));
		}
		return result.iterator();
	}

	@Override
	public Enumeration<IScopeObject> enumeration() {
		return Collections.enumeration(this.toList());
	}

	private List<IScopeObject> toList() {
		List<IScopeObject> result = new ArrayList<IScopeObject>();
		for (IScopeObject o : this) {
			result.add(o);
		}
		return result;
	}

	@Override
	public IScopeObject get(int pos) {
		return this.runtime().call(
				() -> this.converter.ScriptObjectToScopeObject(this.value.getArrayElement(pos)));
	}

	@Override
	public boolean has(int pos) {
		return pos >= 0 && pos < this.size();
	}

	@Override
	public void put(int pos, IScopeObject value) {
		this.runtime().run(
				() -> this.value.setArrayElement(pos,
						this.converter.ScopeObjectToScriptObject(value)));
	}

	@Override
	public void add(IScopeObject value) {
		this.put(this.size(), value);
	}

	@Override
	public void delete(int pos) {
		this.runtime().run(() -> this.value.removeArrayElement(pos));
	}

	@Override
	public int size() {
		return this.runtime().call(() -> Integer.valueOf((int) this.value.getArraySize()))
				.intValue();
	}

	@Override
	public int[] getIds() {
		int[] ids = new int[this.size()];
		for (int i = 0; i < ids.length; ++i) {
			ids[i] = i;
		}
		return ids;
	}

	@Override
	public String origin() {
		return "graal";
	}

	@Override
	public String toString() {
		return this.runtime().call(() -> this.value.toString());
	}
}
