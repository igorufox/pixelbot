package pixelbot.components.debugtree;

import java.lang.reflect.*;

import javax.swing.tree.TreeNode;

public class JavaTreeNode extends BaseTreeNode {
	public JavaTreeNode(TreeNode parent, String text, Object value) {
		super(parent, text);

		if (value != null) {
			Class<?> c = value.getClass();
			while (c != null) {
				for (Field f : c.getFields()) {
					if (Modifier.isStatic(f.getModifiers()))
						continue;
					f.setAccessible(true);
					try {
						this.addNode(f.getName(), f.get(value));
					} catch (Exception e) {}
				}
				c = c.getSuperclass();
			}
		}
	}
}
