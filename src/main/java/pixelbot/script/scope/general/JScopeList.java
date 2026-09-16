package pixelbot.script.scope.general;

import java.util.*;

public class JScopeList extends WrappersBase implements IScopeList {
	private List<Object> list = null;

	public JScopeList() {
		this(new JConverter(), new ArrayList<Object>());
	}

	public JScopeList(IConverter converter, List<Object> list) {
		super(converter);
		if (list == null)
			throw new NullPointerException();
		this.list = list;
	}

	@Override
	public Iterator<IScopeObject> iterator() {
		final Iterator<Object> iter = this.list.iterator();
		return new Iterator<IScopeObject>() {
			Iterator<Object> iterator = iter;

			@Override
			public void remove() {
				this.iterator.remove();
			}

			@Override
			public IScopeObject next() {
				Object obj = this.iterator.next();
				return JScopeList.this.converter.ScriptObjectToScopeObject(obj);
			}

			@Override
			public boolean hasNext() {
				return this.iterator.hasNext();
			}
		};
	}

	@Override
	public Enumeration<IScopeObject> enumeration() {
		final Iterator<Object> iter = this.list.iterator();
		return new Enumeration<IScopeObject>() {
			Iterator<Object> iterator = iter;

			@Override
			public IScopeObject nextElement() {
				Object obj = this.iterator.next();
				return JScopeList.this.converter.ScriptObjectToScopeObject(obj);
			}

			@Override
			public boolean hasMoreElements() {
				return this.iterator.hasNext();
			}
		};
	}

	@Override
	public Object getObject() {
		return this.list;
	}

	@Override
	public IScopeObject get(int pos) {
		return this.converter.ScriptObjectToScopeObject(this.list.get(pos));
	}

	@Override
	public boolean has(int pos) {
		return pos >= 0 && pos < this.list.size();
	}

	@Override
	public void put(int pos, IScopeObject value) {
		this.list.set(pos, this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void add(IScopeObject value) {
		this.list.add(this.converter.ScopeObjectToScriptObject(value));
	}

	@Override
	public void delete(int pos) {
		this.list.remove(pos);
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public int[] getIds() {
		int[] ids = new int[this.list.size()];
		for (int i = 0; i < ids.length; ++i) {
			ids[i] = i;
		}
		return ids;
	}

	@Override
	public String toString() {
		return this.list.toString();
	}

	@Override
	public String origin() {
		return "general";
	}

}
