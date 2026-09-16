package pixelbot.components.debugtree;

import java.util.List;

import javax.swing.tree.TreeNode;

import pixelbot.script.scope.general.IScopeList;

public class ListTreeNode extends BaseTreeNode {

	public ListTreeNode(TreeNode parent, String text, List<?> value) {
		super(parent, text);
		if (value != null) {
			for (int i = 0; i < value.size(); ++i) {
				this.addNode(Integer.toString(i), value.get(i));
			}
		}
	}

	public ListTreeNode(TreeNode parent, String text, IScopeList value) {
		super(parent, text);
		if (value != null) {
			for (int id : value.getIds()) {
				this.addNode(Integer.toString(id), value.get(id));
			}
		}
	}

}
