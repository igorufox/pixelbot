package pixelbot.components.generic;

import java.util.*;

import javax.swing.DefaultComboBoxModel;

/*
 *  Custom model to make sure the items are stored in a sorted order.
 *  The default is to sort in the natural order of the item, but a
 *  Comparator can be used to customize the sort order.
 *
 *  The data is initially sorted before the model is created. Any updates
 *  to the model will cause the items to be inserted in sorted order.
 */
public class SortedComboBoxModel<T extends Comparable<T>> extends DefaultComboBoxModel<T> {
	private static final long serialVersionUID = 4431497378987573983L;

	private Comparator<T> comparator;

	/*
	 * Static method is required to make sure the data is in sorted order before it is added to the
	 * model
	 */
	protected static <T> Vector<T> sortVector(Vector<T> items, Comparator<T> comparator) {
		Collections.sort(items, comparator);
		return items;
	}

	/*
	 * Static method is required to make sure the data is in sorted order before it is added to the
	 * model
	 */
	protected static <T> T[] sortArray(T[] items, Comparator<T> comparator) {
		Arrays.sort(items, comparator);
		return items;
	}

	/*
	 * Create an empty model that will use the natural sort order of the item
	 */
	public SortedComboBoxModel() {
		super();
	}

	/*
	 * Create an empty model that will use the specified Comparator
	 */
	public SortedComboBoxModel(Comparator<T> comparator) {
		super();
		this.comparator = comparator;
	}

	/*
	 * Create a model with data and use the nature sort order of the items
	 */
	public SortedComboBoxModel(T[] items) {
		super(sortArray(items, null));
	}

	/*
	 * Create a model with data and use the specified Comparator
	 */
	public SortedComboBoxModel(T[] items, Comparator<T> comparator) {
		super(sortArray(items, comparator));
		this.comparator = comparator;
	}

	/*
	 * Create a model with data and use the nature sort order of the items
	 */
	public SortedComboBoxModel(Vector<T> items) {
		this(items, null);
	}

	/*
	 * Create a model with data and use the specified Comparator
	 */

	public SortedComboBoxModel(Vector<T> items, Comparator<T> comparator) {
		super(sortVector(items, comparator));
		this.comparator = comparator;
	}

	@Override
	public void addElement(T element) {
		insertElementAt(element, 0);
	}

	@Override
	public void insertElementAt(T element, int index) {
		int size = getSize();

		// Determine where to insert element to keep model in sorted order
		int i = 0;
		for (i = 0; i < size; i++) {
			if (this.comparator != null) {
				T o = getElementAt(i);

				if (this.comparator.compare(o, element) > 0)
					break;
			} else {
				Comparable<T> c = getElementAt(i);

				if (c.compareTo(element) > 0)
					break;
			}
		}

		super.insertElementAt(element, i);
	}
}
