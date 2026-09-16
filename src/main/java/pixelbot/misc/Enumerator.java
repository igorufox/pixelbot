package pixelbot.misc;

import java.util.*;

public final class Enumerator<T> implements Enumeration<T>, Iterator<T>, java.lang.Iterable<T> {
	private Iterator<T> iterator = null;

	/**
	 * Return an Enumeration over the values of the specified Collection.
	 * 
	 * @param collection Collection whose values should be enumerated
	 */
	public Enumerator(Collection<T> collection) {
		this(collection.iterator());
	}

	/**
	 * Return an Enumeration over the values of the specified Collection.
	 * 
	 * @param collection Collection whose values should be enumerated
	 * @param clone true to clone iterator
	 */
	public Enumerator(Collection<T> collection, boolean clone) {
		this(collection.iterator(), clone);
	}

	/**
	 * Return an Enumeration over the values returned by the specified Iterator.
	 * 
	 * @param iterator Iterator to be wrapped
	 */
	public Enumerator(Iterator<T> iterator) {
		super();
		this.iterator = iterator;
	}

	/**
	 * Return an Enumeration over the values returned by the specified Iterator.
	 * 
	 * @param iterator Iterator to be wrapped
	 * @param clone true to clone iterator
	 */
	public Enumerator(Iterator<T> iterator, boolean clone) {

		super();
		if (!clone) {
			this.iterator = iterator;
		} else {
			List<T> list = new ArrayList<T>();
			while (iterator.hasNext()) {
				list.add(iterator.next());
			}
			this.iterator = list.iterator();
		}

	}

	/**
	 * Tests if this enumeration contains more elements.
	 * 
	 * @return <code>true</code> if and only if this enumeration object contains at least one more
	 * element to provide, <code>false</code> otherwise
	 */
	@Override
	public boolean hasMoreElements() {
		return this.iterator.hasNext();
	}

	/**
	 * Returns the next element of this enumeration if this enumeration has at least one more
	 * element to provide.
	 * 
	 * @return the next element of this enumeration
	 * 
	 * @exception NoSuchElementException if no more elements exist
	 */
	@Override
	public T nextElement() throws NoSuchElementException {
		return this.iterator.next();
	}

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public T next() {
		return this.iterator.next();
	}

	@Override
	public void remove() {
		this.iterator.remove();
	}

	@Override
	public Iterator<T> iterator() {
		return this.iterator;
	}

}
