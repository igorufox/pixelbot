package pixelbot.components.generic;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.*;

import pixelbot.components.generic.JListSelector.DublicateHandler.Result;

public class JListSelector<T extends Comparable<T>> extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = -7710189116485784838L;

	protected JList<Object> jleft;
	protected JList<Object> jright;
	private JButton btn_rigthall;
	private JButton btn_leftall;
	private JButton btn_left;
	private JButton btn_right;
	protected DublicateHandler dublicateHandler = null;

	public enum Source {
		Left, Right;

		public Source opposite() {
			switch (this) {
			case Left:
				return Right;
			case Right:
				return Left;
			}
			return null;
		}
	}

	public enum Action {
		Add, Remove, AddMany, Clear
	}

	public interface DublicateHandler {
		public enum Result {
			Replace, Dublicate, Cancel, Stop
		}

		public void startBatch();

		public void endBatch();

		public Result onDublicate(Source source, Object o);
	}

	public JListSelector() {
		JScrollPane scroll_left = new JScrollPane();
		JScrollPane scroll_right = new JScrollPane();
		this.jleft = new JList<Object>(new SortedSeparatedListModel<T>());
		this.jright = new JList<Object>(new SortedSeparatedListModel<T>());

		try {
			this.jleft.setUI(SeparatorSynthListUIFactory.getUI());
			this.jright.setUI(SeparatorSynthListUIFactory.getUI());
		} catch (Throwable t) {}

		this.jleft.addListSelectionListener(this);
		this.jright.addListSelectionListener(this);

		this.btn_leftall = new JButton("<<");
		this.btn_left = new JButton("<");
		this.btn_right = new JButton(">");
		this.btn_rigthall = new JButton(">>");

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout
				.createSequentialGroup()
				.addComponent(scroll_left, 0, 0, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(
						groupLayout
								.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(this.btn_rigthall, Alignment.LEADING,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(this.btn_leftall, Alignment.LEADING,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(this.btn_left, Alignment.LEADING,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(this.btn_right, Alignment.LEADING,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(scroll_right, 0, 0, Short.MAX_VALUE));
		groupLayout.setVerticalGroup(groupLayout
				.createParallelGroup(Alignment.CENTER)
				.addComponent(scroll_left)
				.addGroup(
						groupLayout.createSequentialGroup().addComponent(this.btn_leftall)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(this.btn_left)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(this.btn_right)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(this.btn_rigthall)).addComponent(scroll_right));
		setLayout(groupLayout);

		this.jleft.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.jleft.setDragEnabled(true);

		this.jright.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.jright.setDragEnabled(true);

		scroll_left.setViewportView(this.jleft);
		scroll_right.setViewportView(this.jright);

		// create a transfer handler that can move an item
		// from one list to the other
		MyTransferHandler th = new MyTransferHandler(this.jleft, this.jright);

		// associate this transfer handler with each JList
		this.jleft.setTransferHandler(th);
		this.jright.setTransferHandler(th);

		this.btn_leftall.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				SortedSeparatedListModel<T> left = (SortedSeparatedListModel<T>) JListSelector.this.jleft
						.getModel();
				SortedSeparatedListModel<T> right = (SortedSeparatedListModel<T>) JListSelector.this.jright
						.getModel();
				if (JListSelector.this.dublicateHandler != null) {
					JListSelector.this.dublicateHandler.startBatch();
					loop: for (T o : right.elements()) {
						if (left.hasElement(o)) {
							Result r = JListSelector.this.dublicateHandler.onDublicate(
									Source.Right, o);
							switch (r) {
							case Replace:
								left.removeElement(o);
								JListSelector.this.fireActionPerformed(Source.Left, Action.Remove,
										o);
								left.addElement(o);
								JListSelector.this.fireActionPerformed(Source.Left, Action.Add, o);
								right.removeElement(o);
								JListSelector.this.fireActionPerformed(Source.Right, Action.Remove,
										o);
								break;
							case Dublicate:
								left.addElement(o);
								JListSelector.this.fireActionPerformed(Source.Left, Action.Add, o);
								right.removeElement(o);
								JListSelector.this.fireActionPerformed(Source.Right, Action.Remove,
										o);
								break;
							case Cancel:
								break;
							case Stop:
								break loop;
							}
						} else {
							left.addElement(o);
						}
					}
					JListSelector.this.dublicateHandler.endBatch();
				} else {
					for (T o : right.elements()) {
						left.addElement(o);
					}
					JListSelector.this.fireActionPerformed(Source.Left, Action.AddMany, null);
					right.clear();
					JListSelector.this.fireActionPerformed(Source.Right, Action.Clear, null);
				}
			}
		});
		this.btn_left.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JListSelector.this.jright.getSelectedValue() == null)
					return;
				T selected = (T) JListSelector.this.jright.getSelectedValue();
				if (selected != null) {
					SortedSeparatedListModel<T> left = (SortedSeparatedListModel<T>) JListSelector.this.jleft
							.getModel();
					SortedSeparatedListModel<T> right = (SortedSeparatedListModel<T>) JListSelector.this.jright
							.getModel();
					if (JListSelector.this.dublicateHandler == null || !left.hasElement(selected)) {
						left.addElement(selected);
						JListSelector.this.fireActionPerformed(Source.Left, Action.Add, selected);
						right.removeElement(selected);
						JListSelector.this.fireActionPerformed(Source.Right, Action.Remove,
								selected);
					} else {
						Result r = JListSelector.this.dublicateHandler.onDublicate(Source.Right,
								selected);
						switch (r) {
						case Replace:
							left.removeElement(selected);
							JListSelector.this.fireActionPerformed(Source.Left, Action.Remove,
									selected);
							left.addElement(selected);
							JListSelector.this.fireActionPerformed(Source.Left, Action.Add,
									selected);
							right.removeElement(selected);
							JListSelector.this.fireActionPerformed(Source.Right, Action.Remove,
									selected);

							break;
						case Dublicate:
							left.addElement(selected);
							JListSelector.this.fireActionPerformed(Source.Left, Action.Add,
									selected);
							right.removeElement(selected);
							JListSelector.this.fireActionPerformed(Source.Right, Action.Remove,
									selected);
							break;
						case Cancel:
							break;
						case Stop:
							break;
						}
					}
				}
			}
		});
		this.btn_right.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JListSelector.this.jleft.getSelectedValue() == null)
					return;
				T selected = (T) JListSelector.this.jleft.getSelectedValue();

				SortedSeparatedListModel<T> left = (SortedSeparatedListModel<T>) JListSelector.this.jleft
						.getModel();
				SortedSeparatedListModel<T> right = (SortedSeparatedListModel<T>) JListSelector.this.jright
						.getModel();
				if (JListSelector.this.dublicateHandler == null || !right.hasElement(selected)) {
					right.addElement(selected);
					JListSelector.this.fireActionPerformed(Source.Right, Action.Add, selected);
					left.removeElement(selected);
					JListSelector.this.fireActionPerformed(Source.Left, Action.Remove, selected);
				} else {
					Result r = JListSelector.this.dublicateHandler.onDublicate(Source.Left,
							selected);
					switch (r) {
					case Replace:
						right.removeElement(selected);
						JListSelector.this.fireActionPerformed(Source.Right, Action.Remove,
								selected);
						right.addElement(selected);
						JListSelector.this.fireActionPerformed(Source.Right, Action.Add, selected);
						left.removeElement(selected);
						JListSelector.this
								.fireActionPerformed(Source.Left, Action.Remove, selected);
						break;
					case Dublicate:
						right.addElement(selected);
						JListSelector.this.fireActionPerformed(Source.Right, Action.Add, selected);
						left.removeElement(selected);
						JListSelector.this
								.fireActionPerformed(Source.Left, Action.Remove, selected);
						break;
					case Cancel:
						break;
					case Stop:
						break;
					}
				}

			}
		});
		this.btn_rigthall.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				SortedSeparatedListModel<T> left = (SortedSeparatedListModel<T>) JListSelector.this.jleft
						.getModel();
				SortedSeparatedListModel<T> right = (SortedSeparatedListModel<T>) JListSelector.this.jright
						.getModel();
				if (JListSelector.this.dublicateHandler != null) {
					JListSelector.this.dublicateHandler.startBatch();
					loop: for (T o : left.elements()) {
						if (right.hasElement(o)) {
							Result r = JListSelector.this.dublicateHandler.onDublicate(Source.Left,
									o);
							switch (r) {
							case Replace:
								right.removeElement(o);
								JListSelector.this.fireActionPerformed(Source.Right, Action.Remove,
										o);
								right.addElement(o);
								JListSelector.this.fireActionPerformed(Source.Right, Action.Add, o);
								left.removeElement(o);
								JListSelector.this.fireActionPerformed(Source.Left, Action.Remove,
										o);
								break;
							case Dublicate:
								right.addElement(o);
								JListSelector.this.fireActionPerformed(Source.Right, Action.Add, o);
								left.removeElement(o);
								JListSelector.this.fireActionPerformed(Source.Left, Action.Remove,
										o);
								break;
							case Cancel:
								break;
							case Stop:
								break loop;
							}
						} else {
							right.addElement(o);
						}
					}
					JListSelector.this.dublicateHandler.endBatch();
				} else {
					for (T o : left.elements()) {
						right.addElement(o);
					}
					JListSelector.this.fireActionPerformed(Source.Right, Action.AddMany, null);
					left.clear();
					JListSelector.this.fireActionPerformed(Source.Left, Action.Clear, null);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void setLeft(List<T> data) {
		((SortedSeparatedListModel<T>) this.jleft.getModel()).setData(data);
	}

	@SuppressWarnings("unchecked")
	public void setRight(List<T> data) {
		((SortedSeparatedListModel<T>) this.jright.getModel()).setData(data);
	}

	@SuppressWarnings("unchecked")
	public List<T> getLeft() {
		return ((SortedSeparatedListModel<T>) this.jleft.getModel()).getData();
	}

	@SuppressWarnings("unchecked")
	public List<T> getRight() {
		return ((SortedSeparatedListModel<T>) this.jright.getModel()).getData();
	}

	public void setSelectedValues(List<T> all, List<?> selected) {
		ArrayList<T> left = new ArrayList<T>(all);
		ArrayList<T> right = new ArrayList<T>();
		for (T t : all) {
			for (Object v : selected) {
				if (t.equals(v)) {
					left.remove(t);
					right.add(t);
					break;
				}
			}
		}
		this.setLeft(left);
		this.setRight(right);
	}

	public List<String> getSelectedValues() {
		List<T> values = this.getRight();
		List<String> result = new ArrayList<String>();
		for (T value : values) {
			result.add(value == null ? null : value.toString());
		}
		return result;
	}

	/**
	 * create a JList that can hold Candy objects (using a custom cell renderer and ListModel.) If a
	 * path is not null, use it to initialize the list. We need to turn on dragging support for the
	 * JList and to register a handler for drop events.
	 */

	private class MyTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 3486879318135775942L;
		// Our transfer handler needs to know about the
		// two JLists !
		JList<?> left, right;

		public MyTransferHandler(JList<?> l, JList<?> r) {
			super();
			this.left = l;
			this.right = r;
		}

		// This is the method called when a drop occurs.
		// The parameter c is where something is being dropped
		// (one of our two JLists).
		// The parameter t is the thing being dropped
		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(JComponent c, Transferable t) {
			JList<?> jl = (JList<?>) c;

			try {
				int sourceIndex = Integer.parseInt((String) t
						.getTransferData(DataFlavor.stringFlavor));

				// figure out which is the source and which is the dest.
				SortedSeparatedListModel<T> source, dest;
				Source s = null;

				if (jl == this.left) {
					source = (SortedSeparatedListModel<T>) this.right.getModel();
					dest = (SortedSeparatedListModel<T>) this.left.getModel();
					s = Source.Right;
				} else {
					source = (SortedSeparatedListModel<T>) this.left.getModel();
					dest = (SortedSeparatedListModel<T>) this.right.getModel();
					s = Source.Left;
				}

				T o = (T) source.getElementAt(sourceIndex);

				if (JListSelector.this.dublicateHandler == null || !dest.hasElement(o)) {
					dest.addElement(o);
					JListSelector.this.fireActionPerformed(s.opposite(), Action.Add, o);
					source.remove(sourceIndex);
					JListSelector.this.fireActionPerformed(s, Action.Remove, o);
				} else {
					Result r = JListSelector.this.dublicateHandler.onDublicate(s.opposite(), o);
					switch (r) {
					case Replace:
						dest.removeElement(o);
						JListSelector.this.fireActionPerformed(s.opposite(), Action.Remove, o);
						dest.addElement(o);
						JListSelector.this.fireActionPerformed(s.opposite(), Action.Add, o);
						source.removeElement(o);
						JListSelector.this.fireActionPerformed(s, Action.Remove, o);
						break;
					case Dublicate:
						dest.addElement(o);
						JListSelector.this.fireActionPerformed(s.opposite(), Action.Add, o);
						source.removeElement(o);
						JListSelector.this.fireActionPerformed(s, Action.Remove, o);
						break;
					case Cancel:
						break;
					case Stop:
						break;
					}
				}

			} catch (Exception e) {
				return false;
			}
			return true;
		}

		/**
		 * creates a StringSelection transferrable. The string is just an integer converted to
		 * string (if we actually transferred an int, we would need to write our own Transferrable,
		 * this way we can just use StringSelection).
		 * 
		 * The JComponent passed in is the JList from which something is being dragged. We look up
		 * the selected index, convert this to a string and wrap in a StringSelection transferrable.
		 */

		@Override
		protected Transferable createTransferable(JComponent c) {
			JList<?> jl = (JList<?>) c;
			// we want the currently selected index
			Integer index = new Integer(jl.getSelectedIndex());
			// and we transfer it as a string
			Transferable t = new StringSelection(index.toString());
			return t;
		}

		// We have to specify that things can be dragged
		// MOVE or
		@Override
		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		// This is called before a drop, to make sure that we
		// can handle the incoming data type. The flavors array
		// is a list of the data flavors the incoming data is available in.
		// All we want is a Unicode String, so we look for that and return
		// true if we find it.
		// This is more significant if we were supporting drag and drop
		// from non-java programs....

		@Override
		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].getHumanPresentableName().equals("Unicode String")) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Short.MAX_VALUE, 150);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(300, 150);
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (this.btn_leftall != null) {
			this.btn_leftall.setBackground(bg);
			this.btn_left.setBackground(bg);
			this.btn_right.setBackground(bg);
			this.btn_rigthall.setBackground(bg);
		}

	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The
	 * event instance is lazily created using the <code>event</code> parameter.
	 * 
	 * @param event the <code>ActionEvent</code> object
	 * @see EventListenerList
	 */
	protected void fireActionPerformed(Source source, Action action, T obj) {
		ActionEvent e = new ActionEvent(source, ActionEvent.ACTION_PERFORMED, obj == null ? null
				: obj.toString(), action.ordinal());
		// Guaranteed to return a non-null array
		Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}

	/**
	 * Adds an <code>ActionListener</code> to the button.
	 * 
	 * @param l the <code>ActionListener</code> to be added
	 */
	public void addActionListener(ActionListener l) {
		this.listenerList.add(ActionListener.class, l);
	}

	/**
	 * Removes an <code>ActionListener</code> from the button. If the listener is the currently set
	 * <code>Action</code> for the button, then the <code>Action</code> is set to <code>null</code>.
	 * 
	 * @param l the listener to be removed
	 */
	public void removeActionListener(ActionListener l) {
		this.listenerList.remove(ActionListener.class, l);
	}

	public static class SelectionEvent {
		private Source source;
		private int index;
		private Object data;

		public SelectionEvent(Source source, int index, Object data) {
			this.source = source;
			this.index = index;
			this.data = data;
		}

		public Source getSource() {
			return this.source;
		}

		public int getIndex() {
			return this.index;
		}

		public Object getData() {
			return this.data;
		}

	}

	public interface SelectionListener extends EventListener {
		public void valueChanged(SelectionEvent event);
	}

	private void fireSelectionValueChanged(SelectionEvent event) {
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SelectionListener.class) {
				((SelectionListener) listeners[i + 1]).valueChanged(event);
			}
		}
	}

	public void addSelectionListener(SelectionListener x) {
		this.listenerList.add(SelectionListener.class, x);
	}

	public void removeSelectionListener(SelectionListener x) {
		this.listenerList.remove(SelectionListener.class, x);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == this.jleft) {
			this.fireSelectionValueChanged(new SelectionEvent(Source.Left, e.getFirstIndex(),
					this.jleft.getSelectedValue()));
		} else if (e.getSource() == this.jright) {
			this.fireSelectionValueChanged(new SelectionEvent(Source.Right, e.getFirstIndex(),
					this.jright.getSelectedValue()));
		}
	}

	public void setDublicateHandler(DublicateHandler handler) {
		this.dublicateHandler = handler;
	}

}
