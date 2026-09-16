package pixelbot.components.generic;

import java.util.*;

import javax.swing.*;

import pixelbot.misc.IClassifierItem;

public class SortedSeparatedListModel<T extends Comparable<T>> extends AbstractListModel<Object> {
	private static final long serialVersionUID = -2300901189487831952L;
	private final static JSeparator SEPARATOR = new JSeparator();

	protected List<T> data;
	private List<Integer> indexes;

	protected Object selected;

	public SortedSeparatedListModel() {
		this.data = new ArrayList<T>(0);
		this.indexes = new Vector<Integer>(0);
	}

	public SortedSeparatedListModel(List<T> data) {
		this();
		this.setData(data);
	}

	public void setData(List<T> data) {
		this.data.clear();

		this.data.addAll(data);
		Collections.sort(this.data);
		this.calculateIndexes();
		this.fireContentsChanged(this, 0, this.getSize() - 1);
	}

	private void calculateIndexes() {
		this.indexes.clear();
		if (this.data.size() != 0) {

			Object order = this.getOrder(this.data.get(0));
			int pos = 0;
			for (T item : this.data) {
				if (!order.equals(this.getOrder(item))) {
					order = this.getOrder(item);
					this.indexes.add(Integer.valueOf(-1));
				}
				this.indexes.add(Integer.valueOf(pos));
				++pos;
			}

		}
	}

	private Object getOrder(T item) {
		if (item instanceof IClassifierItem) {
			return Integer.valueOf(((IClassifierItem) item).getOrder());
		} else {
			String t = item.toString();
			if (t.contains(".")) {
				return t.substring(0, t.lastIndexOf('.'));
			} else {
				return "";
			}
		}
	}

	@Override
	public Object getElementAt(int index) throws IndexOutOfBoundsException {
		int pos = this.indexes.get(index).intValue();
		if (pos == -1) {
			return SEPARATOR;
		}
		return this.data.get(pos);
	}

	@Override
	public int getSize() {
		return this.indexes.size();
	}

	public boolean hasElement(T elem) {
		return this.data.contains(elem);
	}

	private int dataIndextoModelIndex(int index) {
		return this.indexes.indexOf(Integer.valueOf(index));
	}

	public void addElement(T elem) {

		int insertionPoint = Collections.binarySearch(this.data, elem);
		if (insertionPoint < 0) {
			insertionPoint = -(insertionPoint + 1);
		}

		this.data.add(insertionPoint, elem);

		int t = this.indexes.size();
		this.calculateIndexes();
		int model_index = this.dataIndextoModelIndex(insertionPoint);
		if (t + 1 != this.indexes.size()) {
			fireIntervalAdded(this, model_index, model_index + 1);
		} else {
			fireIntervalAdded(this, model_index, model_index);
		}

	}

	public void removeElement(T elem) {
		int index = this.data.indexOf(elem);
		if (index >= 0) {
			this.remove(index);
		}
	}

	public void remove(int index) {
		this.data.remove(index);
		int t = this.indexes.size();
		this.calculateIndexes();
		int model_index = this.dataIndextoModelIndex(index);
		if (t - 1 != this.indexes.size()) {
			if (model_index == -1) {
				fireIntervalRemoved(this, this.indexes.size(), this.indexes.size() + 1);
			} else {
				fireIntervalRemoved(this, model_index, model_index + 1);
			}
		} else {
			if (model_index == -1) {
				fireIntervalRemoved(this, this.indexes.size(), this.indexes.size());
			} else {
				fireIntervalRemoved(this, model_index, model_index);
			}
		}
	}

	public void clear() {
		this.data.clear();
		this.indexes.clear();
		fireContentsChanged(this, 0, 0);
	}

	public List<T> getData() {
		return Collections.unmodifiableList(this.data);
	}

	public Iterable<T> elements() {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new ArrayList<T>(SortedSeparatedListModel.this.data).iterator();
			}
		};
	}

}
