package pixelbot.components.generic;

import java.util.*;

import javax.swing.tree.*;

public class SortableTreeNode extends DefaultMutableTreeNode implements
		Comparable<DefaultMutableTreeNode> {
	private static final long serialVersionUID = -8046603292659204550L;

	public SortableTreeNode(String object) {
		super(object);
	}

	@Override
	public void insert(MutableTreeNode paramMutableTreeNode, int paramInt) {
		super.insert(paramMutableTreeNode, paramInt);
		this.children.sort(Comparator.comparing(SortableTreeNode::textOf));
	}

	private static String textOf(TreeNode node) {
		return node instanceof DefaultMutableTreeNode ? String
				.valueOf(((DefaultMutableTreeNode) node).getUserObject()) : String.valueOf(node);
	}

	@Override
	public int compareTo(DefaultMutableTreeNode other) {
		try {
			return this.getUserObject().toString().compareTo(other.getUserObject().toString());
		} catch (Exception e) {
			return 0;
		}
	}

}
