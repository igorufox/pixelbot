package pixelbot.components.generic;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import pixelbot.misc.Tools;

public class JUserList extends JPanel {
	private static final long serialVersionUID = 6822354474035422375L;

	protected JList<?> jList;
	protected JTextField jTextField;
	private JButton btn_add;
	private JButton btn_del;

	public JUserList() {
		JScrollPane scroll_list = new JScrollPane();
		this.jList = new JList<String>(new SortedListModel<String>());
		this.jTextField = new JTextField();

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, Locale.getDefault());

		if (messages != null) {
			this.btn_add = new JButton(messages.getString("control.params.add"));
			this.btn_del = new JButton(messages.getString("control.params.delete"));
		} else {
			this.btn_add = new JButton("Add");
			this.btn_del = new JButton("Delete");
		}

		this.jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (JUserList.this.jList.getSelectedValue() != null) {
					JUserList.this.jTextField.setText(JUserList.this.jList.getSelectedValue()
							.toString());
				}
			}
		});

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout
				.createParallelGroup()
				.addComponent(scroll_list, 0, 0, Short.MAX_VALUE)
				.addComponent(this.jTextField, 0, 0, Short.MAX_VALUE)
				.addGroup(
						groupLayout
								.createSequentialGroup()
								.addComponent(this.btn_add, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(this.btn_del, GroupLayout.DEFAULT_SIZE,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout
				.createSequentialGroup()
				.addComponent(scroll_list, -1, -1, 100)
				.addComponent(this.jTextField)
				.addGroup(
						groupLayout.createParallelGroup().addComponent(this.btn_add)
								.addComponent(this.btn_del)));
		setLayout(groupLayout);

		this.jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scroll_list.setViewportView(this.jList);

		this.btn_add.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!((SortedListModel<String>) JUserList.this.jList.getModel())
						.hasElement(JUserList.this.jTextField.getText())) {
					((SortedListModel<String>) JUserList.this.jList.getModel())
							.addElement(JUserList.this.jTextField.getText());
				}
				JUserList.this.fireActionPerformed(null);
			}
		});
		this.btn_del.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JUserList.this.jList.getSelectedIndex() >= 0) {
					((SortedListModel<String>) JUserList.this.jList.getModel())
							.remove(JUserList.this.jList.getSelectedIndex());
				}
				JUserList.this.fireActionPerformed(null);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void setValues(List<String> data) {
		((SortedListModel<String>) this.jList.getModel()).setData(data);
	}

	@SuppressWarnings("unchecked")
	public List<String> getValues() {
		return ((SortedListModel<String>) this.jList.getModel()).getData();
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
		if (this.btn_add != null) {
			this.btn_add.setBackground(bg);
			this.btn_del.setBackground(bg);
		}

	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The
	 * event instance is lazily created using the <code>event</code> parameter.
	 * 
	 * @param event the <code>ActionEvent</code> object
	 * @see EventListenerList
	 */
	protected void fireActionPerformed(String obj) {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, obj);
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

}
