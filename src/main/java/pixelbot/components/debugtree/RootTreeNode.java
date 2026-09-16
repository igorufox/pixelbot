package pixelbot.components.debugtree;

import java.util.Map;

import pixelbot.script.scope.general.*;

public class RootTreeNode extends BaseTreeNode {

	public RootTreeNode() {
		super(null, "Root");
	}

	public void fill(IScopeMap thisObj, IScopeMap locals) {
		this.chilren.clear();
		this.addNode("this", thisObj);
		for (Map.Entry<String, IScopeObject> e : locals) {
			this.addNode(e.getKey(), e.getValue());
		}
	}
}
