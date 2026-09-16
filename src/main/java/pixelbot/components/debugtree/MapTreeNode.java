package pixelbot.components.debugtree;

import java.util.Map;

import javax.swing.tree.TreeNode;

import pixelbot.script.scope.general.IScopeMap;

public class MapTreeNode extends BaseTreeNode {

	public MapTreeNode(TreeNode parent, String text, Map<?, ?> value) {
		super(parent, text);
		if (value != null) {
			for (Map.Entry<?, ?> entry : value.entrySet()) {
				this.addNode(entry.getKey().toString(), entry.getValue());
			}
		}
	}

	public MapTreeNode(TreeNode parent, String text, IScopeMap value) {
		super(parent, text);
		if (value != null) {
			for (String id : value.getIds()) {
				this.addNode(id, value.get(id));
			}
		}
	}

}
