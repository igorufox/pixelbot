package pixelbot.components.debugtree;

import java.util.*;

import javax.swing.tree.TreeNode;

import pixelbot.script.scope.general.*;

public class BaseTreeNode implements TreeNode {
	private class Child implements Comparable<Child> {
		private String id;
		private Object value;
		private TreeNode node;

		public Child(String id, Object value) {
			this.id = id;
			this.value = value;
			this.node = null;
		}

		public TreeNode getNode() {
			if (this.node == null) {
				this.initNode();
			}
			return this.node;
		}

		private void initNode() {
			if (this.value == null) {
				this.node = new BaseTreeNode(BaseTreeNode.this, this.id + " = null");
			} else if (this.value instanceof String || this.value instanceof Number
					|| this.value instanceof Boolean) {
				this.node = new BaseTreeNode(BaseTreeNode.this, this.id + " = " + this.value);
			} else if (this.value instanceof IScopeString || this.value instanceof IScopeNumber
					|| this.value instanceof IScopeBoolean) {
				this.node = new BaseTreeNode(BaseTreeNode.this, this.id + " = "
						+ ((IScopeObject) this.value).getObject());
			} else if (this.value instanceof IScopeMap) {
				this.node = new MapTreeNode(BaseTreeNode.this, this.id + " = {}",
						(IScopeMap) this.value);
			} else if (this.value instanceof Map<?, ?>) {
				this.node = new MapTreeNode(BaseTreeNode.this, this.id + " = {}",
						(Map<?, ?>) this.value);
			} else if (this.value instanceof IScopeList) {
				this.node = new ListTreeNode(BaseTreeNode.this, this.id + " = []",
						(IScopeList) this.value);
			} else if (this.value instanceof IScopeFunction) {
				this.node = new BaseTreeNode(BaseTreeNode.this, this.id + " = function("
						+ ((IScopeFunction) this.value).getArgsNames() + ")");
				// } else if (this.value instanceof NativeFunction) {
				// this.node = new JsTreeNode(BaseTreeNode.this, this.id + " = "
				// + this.value.getClass().getSimpleName(), (Scriptable) this.value);
				// } else if (this.value instanceof NativeJavaObject) {
				// Object o = ((NativeJavaObject) this.value).unwrap();
				// this.node = new JavaTreeNode(BaseTreeNode.this, this.id + " = "
				// + o.getClass().getSimpleName(), o);
				// } else if (this.value instanceof Scriptable) {
				// this.node = new JsTreeNode(BaseTreeNode.this, this.id + " = "
				// + this.value.getClass().getSimpleName(), (Scriptable) this.value);
				// } else if (this.value instanceof Undefined) {
				// this.node = new BaseTreeNode(BaseTreeNode.this, this.id + " = undefined");
			} else {
				this.node = new JavaTreeNode(BaseTreeNode.this, this.id + " = "
						+ this.value.toString(), this.value);
			}
		}

		@Override
		public int compareTo(Child o) {
			return this.id.compareTo(o.id);
		}
	}

	private TreeNode parent;
	private String text;
	protected TreeSet<Child> chilren;

	public BaseTreeNode(TreeNode parent, String text) {
		this.parent = parent;
		this.text = text;
		this.chilren = new TreeSet<Child>();
	}

	protected void addNode(String id, Object value) {
		this.chilren.add(new Child(id, value));

	}

	@Override
	public int getChildCount() {
		return this.chilren.size();
	}

	@Override
	public TreeNode getParent() {
		return this.parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		int i = 0;
		for (Child c : this.chilren) {
			if (c.getNode() == node)
				return i;
			++i;
		}
		return -1;
	}

	@Override
	public final TreeNode getChildAt(int childIndex) {
		int i = 0;
		for (Child c : this.chilren) {
			if (childIndex == i++) {
				return c.getNode();
			}
		}
		return null;
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return this.chilren.size() == 0;
	}

	@Override
	public Enumeration<? extends TreeNode> children() {
		final Iterator<Child> it = this.chilren.iterator();
		return new Enumeration<TreeNode>() {
			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public TreeNode nextElement() {
				return it.next().getNode();
			}
		};
	}

	@Override
	public String toString() {
		return this.text;
	}
}
