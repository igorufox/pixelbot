package pixelbot.components.editor.hint;

import java.util.*;

import javax.swing.AbstractListModel;

public class HintListModel extends AbstractListModel<String> {

	private static final long serialVersionUID = -4398207879231365934L;

	private ArrayList<String> items = new ArrayList<String>();

	public HintListModel() {}

	@Override
	public String getElementAt(int index) {
		return this.items.get(index);
	}

	@Override
	public int getSize() {
		return this.items.size();
	}

	public void clear() {
		this.items.clear();
	}

	public void sort() {
		Collections.sort(this.items);
		this.fireContentsChanged(this, 0, this.getSize());
	}

	public void add(String item) {
		this.items.add(item);
	}
}
