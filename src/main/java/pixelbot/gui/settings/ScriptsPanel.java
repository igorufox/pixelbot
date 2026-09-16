package pixelbot.gui.settings;

import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.tree.DefaultTreeModel;

import pixelbot.components.Splash;
import pixelbot.components.debugtree.RootTreeNode;
import pixelbot.components.editor.*;
import pixelbot.components.editor.JTabbedScriptsPane.CancelException;
import pixelbot.misc.Tools;
import pixelbot.script.debuger.general.DebugHandler.CallFrame;
import pixelbot.script.general.ScriptManager;

public class ScriptsPanel extends JPanel {
	private static final long serialVersionUID = -1921345341181637060L;

	private JToolBar jToolBar_buttons;
	private JButton jButton_script_load;
	private JButton jButton_script_create;
	private JButton jButton_script_save;
	private JButton jButton_script_save_as;

	private JSplitPane jSplitPaneScripts;

	protected JTabbedScriptsPane jTabbedPane_script;

	private JPanel jPanel_scripts_bottom;
	protected JList<CallFrame> jList_call_stack;
	protected JTree jTree_local_variables;
	private JScrollPane jScrollPane_local_variables;
	private JScrollPane jScrollPane_call_stack;

	private ScriptManager script;

	public ScriptsPanel(ScriptManager script) {
		super();
		this.script = script;

		this.jToolBar_buttons = new JToolBar();
		this.jSplitPaneScripts = new JSplitPane();
		this.jTabbedPane_script = new JTabbedScriptsPane(this.script);
		this.jButton_script_load = new JButton();
		this.jButton_script_create = new JButton();
		this.jButton_script_save = new JButton();
		this.jButton_script_save_as = new JButton();
		this.jPanel_scripts_bottom = new JPanel();
		this.jScrollPane_call_stack = new JScrollPane();
		this.jList_call_stack = new JList<CallFrame>();
		this.jScrollPane_local_variables = new JScrollPane();
		this.jTree_local_variables = new JTree(new RootTreeNode());

		this.jSplitPaneScripts.setOrientation(JSplitPane.VERTICAL_SPLIT);

		this.jSplitPaneScripts.setResizeWeight(0.7);

		this.jSplitPaneScripts.setTopComponent(this.jTabbedPane_script);
		this.jSplitPaneScripts.getActionMap().getParent().remove("startResize");
		this.jSplitPaneScripts.getActionMap().getParent().remove("toggleFocus");

		this.jToolBar_buttons.setFloatable(false);

		this.jButton_script_load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ScriptsPanel.this.jTabbedPane_script.loadScript();
			}
		});
		this.jButton_script_create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ScriptsPanel.this.jTabbedPane_script.newScript();
			}
		});
		this.jButton_script_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ScriptsPanel.this.jTabbedPane_script.saveScript();
			}
		});

		this.jButton_script_save_as.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ScriptsPanel.this.jTabbedPane_script.saveAsScript();
			}
		});

		this.jToolBar_buttons.add(this.jButton_script_create);
		this.jToolBar_buttons.add(this.jButton_script_load);
		this.jToolBar_buttons.add(new JToolBar.Separator());
		this.jToolBar_buttons.add(this.jButton_script_save);
		this.jToolBar_buttons.add(this.jButton_script_save_as);

		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);

		gl.setHorizontalGroup(gl
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(this.jToolBar_buttons, 0, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(this.jSplitPaneScripts, GroupLayout.DEFAULT_SIZE, 434,
						Short.MAX_VALUE));

		gl.setVerticalGroup(gl
				.createSequentialGroup()
				.addComponent(this.jToolBar_buttons, GroupLayout.PREFERRED_SIZE,
						GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.jSplitPaneScripts, GroupLayout.DEFAULT_SIZE, 434,
						Short.MAX_VALUE));

		this.jSplitPaneScripts.setRightComponent(this.jPanel_scripts_bottom);

		GroupLayout gl_jPanel_scripts_bottom = new GroupLayout(this.jPanel_scripts_bottom);
		gl_jPanel_scripts_bottom.setHorizontalGroup(gl_jPanel_scripts_bottom
				.createSequentialGroup()
				.addComponent(this.jScrollPane_call_stack, 0, 0, Short.MAX_VALUE)
				.addComponent(this.jScrollPane_local_variables, 0, 0, Short.MAX_VALUE));
		gl_jPanel_scripts_bottom.setVerticalGroup(gl_jPanel_scripts_bottom
				.createParallelGroup(Alignment.BASELINE).addComponent(this.jScrollPane_call_stack)
				.addComponent(this.jScrollPane_local_variables));

		this.jTree_local_variables.setRootVisible(false);
		this.jTree_local_variables.setFont(new Font("Tahoma", Font.PLAIN, 11));
		this.jScrollPane_local_variables.setViewportView(this.jTree_local_variables);
		this.jList_call_stack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		this.jScrollPane_call_stack.setViewportView(this.jList_call_stack);
		this.jPanel_scripts_bottom.setLayout(gl_jPanel_scripts_bottom);

	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);

		ResourceBundle messages = ResourceBundle.getBundle(Tools.MESSAGES, l);

		this.jButton_script_load.setText(messages.getString("tuning.scripts.buttons.load"));
		this.jButton_script_create.setText(messages.getString("tuning.scripts.buttons.create"));
		this.jButton_script_save.setText(messages.getString("tuning.scripts.buttons.save"));
		this.jButton_script_save_as.setText(messages.getString("tuning.scripts.buttons.save_as"));
	}

	public void populateStack() {
		CallFrame[] stack = this.script.getScriptDebugger().getCallStack();
		Collections.reverse(Arrays.asList(stack));
		this.jList_call_stack.setListData(stack);

		DefaultTreeModel model = (DefaultTreeModel) this.jTree_local_variables.getModel();
		RootTreeNode root = (RootTreeNode) model.getRoot();
		root.fill(stack[0].getThisObj(), stack[0].getLocals());
		model.nodeStructureChanged(root);

	}

	public void loadScripts(Splash splash, List<String> file_names, String active,
			Object breakpoints) {
		this.jTabbedPane_script.loadScripts(splash, file_names);
		splash.process("Set Active Script");
		int i = 0;
		if (active != null) {
			for (i = 0; i < this.jTabbedPane_script.getTabCount(); ++i) {
				ScriptEditor sta = (ScriptEditor) this.jTabbedPane_script.getComponentAt(i);
				if (sta.getFile().getName().equals(active)) {
					this.jTabbedPane_script.setSelectedIndex(i);
					break;
				}
			}
		}

		splash.process("Init Breakpoints");

		if (breakpoints != null && breakpoints instanceof Map<?, ?>) {
			for (i = 0; i < this.jTabbedPane_script.getTabCount(); ++i) {
				try {
					ScriptEditor sta = (ScriptEditor) this.jTabbedPane_script.getComponentAt(i);
					Object bb = ((Map<?, ?>) breakpoints).get(sta.getFile().getName());
					if (bb != null && bb instanceof Collection<?>) {
						for (Object b : (Collection<?>) bb) {
							sta.getLineNumJPanel().setMarker(((Number) b).intValue());
						}
					}
				} catch (Exception e) {}
			}
		}

	}

	public String getActive() {
		if (this.jTabbedPane_script.getSelectedComponent() != null) {
			return ((ScriptEditor) this.jTabbedPane_script.getSelectedComponent()).getFile()
					.getName();
		}
		return null;
	}

	public List<File> closeScripts() throws IOException, CancelException {
		return this.jTabbedPane_script.closeScripts();
	}
}
