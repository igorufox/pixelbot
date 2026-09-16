package pixelbot.misc;

import java.util.Enumeration;

public class Iterable<T> implements java.util.Iterator<T>, java.lang.Iterable<T> {
	private Enumeration<T> enumeration;

	public Iterable(Enumeration<T> enumeration) {
		this.enumeration = enumeration;
	}

	@Override
	public java.util.Iterator<T> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.enumeration.hasMoreElements();
	}

	@Override
	public T next() {
		return this.enumeration.nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}