package pixelbot.script.scope.js.nashorn;

import java.util.*;

import org.openjdk.nashorn.api.scripting.JSObject;

import pixelbot.script.scope.general.*;

/** A Nashorn array seen as a scope list. */
public class ScopeList extends WrappersBase implements IScopeList {

	private final JSObject object;

	ScopeList(NashornConverter converter, JSObject object) {
		super(converter);
		this.object = object;
	}

	@Override
	public Object getObject() {
		return this.object;
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
		List<IScopeObject> result = new ArrayList<IScopeObject>();
		for (IScopeObject o : this) {
			result.add(o);
		}
		return Collections.enumeration(result);
	}

	@Override
	public IScopeObject get(int pos) {
		return this.converter.ScriptObjectToScopeObject(this.object.getSlot(pos));
	}

	@Override
	public boolean has(int pos) {
		return pos >= 0 && pos < this.size();
	}

	@Override
	public void put(int pos, IScopeObject value) {
		this.object.setSlot(pos, this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void add(IScopeObject value) {
		this.put(this.size(), value);
	}

	@Override
	public void delete(int pos) {
		this.object.removeMember(Integer.toString(pos));
	}

	@Override
	public int size() {
		Object length = this.object.getMember("length");
		return length instanceof Number ? ((Number) length).intValue() : 0;
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
		return "nashorn";
	}

	@Override
	public String toString() {
		return String.valueOf(this.object);
	}
}
