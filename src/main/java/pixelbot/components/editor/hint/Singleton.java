package pixelbot.components.editor.hint;

import java.util.*;

import javax.swing.text.Segment;

import pixelbot.components.editor.ScriptEditor.ScriptTextPane;

public class Singleton implements Iterable<String> {
	private Deque<String> path = new ArrayDeque<String>();
	private String member = null;

	private Singleton() {}

	private void addPath(String node) {
		if (this.member == null) {
			this.member = node.toLowerCase();
		} else {
			this.path.add(node);
		}

	}

	public String getMember() {
		return this.member == null ? "" : this.member;
	}

	public static Singleton init(ScriptTextPane pane, char next) {
		Singleton result = new Singleton();
		try {
			Segment line = pane.getLine();
			StringBuilder sb = new StringBuilder();

			char ch = next;
			processChar(result, sb, ch);

			for (int j = line.getIndex() - 1; j >= line.getBeginIndex(); j--) {
				ch = line.array[j];
				if (!processChar(result, sb, ch)) {
					break;
				}
			}
			if (sb.length() > 0)
				result.addPath(sb.reverse().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean processChar(Singleton result, StringBuilder sb, char ch) {
		if ('.' == ch) {// dot found
			result.addPath(sb.reverse().toString());
			sb.setLength(0);
		} else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9')
				|| ch == '_' || ch == '$') {
			sb.append(ch);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public Iterator<String> iterator() {
		return this.path.descendingIterator();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String node : this) {
			sb.append(node);
			sb.append('.');
		}
		sb.append(this.member);
		return sb.toString();
	}
}
