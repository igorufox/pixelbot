package pixelbot.components.generic;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

public class TreeTableModel extends DefaultTreeTableModel {
	public TreeTableModel() {
		super();
	}

	public void fireTreeStructureChanged(TreePath subTreePath) {
		this.modelSupport.fireTreeStructureChanged(subTreePath);
	}

	public void fireNewRoot() {
		this.modelSupport.fireNewRoot();
	}

	public void fireChildAdded(TreePath parentPath, int index, Object child) {
		this.modelSupport.fireChildAdded(parentPath, index, child);
	}

	public void firePathChanged(TreePath path) {
		this.modelSupport.firePathChanged(path);
	}

}