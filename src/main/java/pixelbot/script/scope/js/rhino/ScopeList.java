package pixelbot.script.scope.js.rhino;

import java.util.*;

import org.mozilla.javascript.Scriptable;

import pixelbot.script.scope.general.*;

public class ScopeList extends WrappersBase implements IScopeList {
	Scriptable scriptable = null;

	ScopeList(IConverter converter, Scriptable scriptable) {
		super(converter);
		this.scriptable = scriptable;
	}

	@Override
	public Object getObject() {
		return this.scriptable;
	}

	@Override
	public Iterator<IScopeObject> iterator() {
		Object[] ids = this.scriptable.getIds();

		Collection<IScopeObject> sids = new ArrayList<IScopeObject>(ids.length);
		for (int i = 0; i < ids.length; ++i) {
			sids.add(this.get(((Integer) ids[i]).intValue()));
		}
		return sids.iterator();
	}

	@Override
	public Enumeration<IScopeObject> enumeration() {
		final Iterator<IScopeObject> iter = this.iterator();
		return new Enumeration<IScopeObject>() {
			Iterator<IScopeObject> iterator = iter;

			@Override
			public IScopeObject nextElement() {
				return this.iterator.next();
			}

			@Override
			public boolean hasMoreElements() {
				return this.iterator.hasNext();
			}
		};
	}

	@Override
	public IScopeObject get(int pos) {
		return this.converter.ScriptObjectToScopeObject(this.scriptable.get(pos, this.scriptable));
	}

	@Override
	public boolean has(int pos) {
		return this.scriptable.has(pos, this.scriptable);
	}

	@Override
	public void put(int pos, IScopeObject value) {
		this.scriptable.put(pos, this.scriptable, this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void add(IScopeObject value) {
		this.scriptable.put(((Integer) this.scriptable.get("length", this.scriptable)).intValue(),
				this.scriptable, this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void delete(int pos) {
		this.scriptable.delete(pos);
	}

	@Override
	public int[] getIds() {
		int length = ((Number) this.scriptable.get("length", this.scriptable)).intValue();
		int[] result = new int[length];
		for (int i = 0; i < length; ++i) {
			result[i] = i;
		}
		return result;
	}

	@Override
	public int size() {
		return this.scriptable.getIds().length;
	}

	@Override
	public String origin() {
		return "jre";
	}

}
