package pixelbot.script.scope.general;

import java.lang.reflect.Array;
import java.util.*;

public class JScriptList extends WrappersBase implements List<Object>, IScriptWrapper {
	private IScopeList list;

	public JScriptList(IConverter converter, IScopeList list) {
		super(converter);
		this.list = list;
	}

	@Override
	public boolean add(Object value) {
		this.list.add(this.converter.ScriptObjectToScopeObject(value));
		return true;
	}

	@Override
	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		boolean r = false;
		for (Object o : c) {
			r |= this.add(o);
		}
		return r;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		while (this.size() != 0) {
			this.remove(0);
		}

	}

	@Override
	public boolean contains(Object paramObject) {
		return (indexOf(paramObject) > -1);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Iterator<?> localIterator = c.iterator(); localIterator.hasNext();) {
			Object localObject = localIterator.next();
			if (!(contains(localObject)))
				return false;
		}
		return true;
	}

	@Override
	public Object get(int index) {
		return this.converter.ScopeObjectToScriptObject(this.list.get(index));
	}

	@Override
	public int indexOf(Object o) {
		int i = this.list.size();
		int j;
		if (o == null) {
			for (j = 0; j < i; ++j) {
				if (get(j) == null)
					return j;
			}
		} else {
			for (j = 0; j < i; ++j) {
				if (o.equals(get(j))) {
					return j;
				}
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int i = this.list.size();
		int j;
		if (o == null) {
			for (j = i - 1; j >= 0; --j) {
				if (get(j) == null)
					return j;
			}
		} else {
			for (j = i - 1; j >= 0; --j) {
				if (o.equals(get(j))) {
					return j;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public Iterator<Object> iterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<Object> listIterator(final int index) {

		final int i = this.list.size();

		if ((index < 0) || (index > i)) {
			throw new IndexOutOfBoundsException("Index: " + index);
		}

		return new ListIterator<Object>() {
			int cursor = index;
			int len = i;

			@Override
			public boolean hasNext() {
				return (this.cursor < this.len);
			}

			@Override
			public Object next() {
				if (this.cursor == this.len) {
					throw new NoSuchElementException();
				}
				return JScriptList.this.get(this.cursor++);
			}

			@Override
			public boolean hasPrevious() {
				return (this.cursor > 0);
			}

			@Override
			public Object previous() {
				if (this.cursor == 0) {
					throw new NoSuchElementException();
				}
				return JScriptList.this.get(--this.cursor);
			}

			@Override
			public int nextIndex() {
				return this.cursor;
			}

			@Override
			public int previousIndex() {
				return (this.cursor - 1);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void add(Object paramObject) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(Object paramObject) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public boolean remove(Object o) {
		int i = this.indexOf(o);
		if (i < 0 || i > this.size())
			return false;
		return this.remove(i) != null;
	}

	@Override
	public Object remove(int index) {
		Object obj = this.get(index);
		this.list.delete(index);
		return obj;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean b = false;
		for (Object o : c) {
			b |= this.remove(o);

		}
		return b;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object set(int index, Object element) {
		this.list.put(index, this.converter.ScriptObjectToScopeObject(element));
		return element;
	}

	@Override
	public int size() {
		return this.list.size();
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return toArray(new Object[] {});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {

		int i = this.list.size();
		T[] arrayOfObject = (a.length >= i) ? a : (T[]) Array.newInstance(a.getClass()
				.getComponentType(), i);

		for (int j = 0; j < i; ++j) {
			arrayOfObject[j] = (T) get(j);
		}
		return arrayOfObject;
	}

	@Override
	public IScopeObject getObject() {
		return this.list;
	}
}
