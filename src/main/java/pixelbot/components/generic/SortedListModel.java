package pixelbot.components.generic;

import java.util.*;

import javax.swing.*;

public class SortedListModel<T extends Comparable<T>> extends AbstractListModel<T> implements
		ComboBoxModel<T> {
	private static final long serialVersionUID = -8055845079019503478L;
	protected ArrayList<T> data;
	protected Object selected;

	public SortedListModel() {
		this.data = new ArrayList<T>(0);
	}

	public SortedListModel(List<T> data) {
		this();
		this.setData(data);
	}

	@Override
	public T getElementAt(int index) throws IndexOutOfBoundsException {
		return this.data.get(index);
	}

	@Override
	public int getSize() {
		return this.data.size();
	}

	public void setData(List<T> data) {
		this.data.clear();
		this.data.addAll(data);

		Collections.sort(this.data);

		fireContentsChanged(this, 0, this.data.size() - 1);
	}

	public List<T> getData() {
		return new ArrayList<T>(this.data);
	}

	public void addElement(T elem) {
		int insertionPoint = Collections.binarySearch(this.data, elem);
		if (insertionPoint < 0) {
			insertionPoint = -(insertionPoint + 1);
		}

		this.data.add(insertionPoint, elem);
		fireIntervalAdded(this, insertionPoint, insertionPoint);
	}

	public void removeElement(T elem) {
		int index = this.data.indexOf(elem);
		if (index >= 0) {
			this.data.remove(elem);
			fireIntervalRemoved(this, index, index);
		}
	}

	public boolean hasElement(T elem) {
		return this.data.contains(elem);
	}

	public void remove(int index) {
		this.data.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	public void clear() {
		this.data.clear();
		fireContentsChanged(this, 0, 0);
	}

	public Iterable<T> elements() {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return Collections.unmodifiableCollection(SortedListModel.this.data).iterator();
			}
		};
	}

	@Override
	public void setSelectedItem(Object anItem) {
		if (this.data.contains(anItem)) {
			this.selected = anItem;
		} else {
			if (this.data.size() > 0) {
				this.selected = this.data.get(0);
			} else {
				this.selected = null;
			}
		}
	}

	@Override
	public Object getSelectedItem() {
		return this.selected;
	}

}
