package pixelbot.components.editor.syntax;

import java.util.*;

import pixelbot.components.editor.syntax.ScriptDocument.IScanner;

public class ParagraphTokenizer {
	private class Token {
		private int type = 0;
		private int start = 0;
		private int end = 0;

		public Token(int type, int start, int end) {
			this.type = type;
			this.start = start;
			this.end = end;
		}

		public int getType() {
			return this.type;
		}

		public int getStart() {
			return this.start;
		}

		public int getEnd() {
			return this.end;
		}

		@Override
		public String toString() {
			return "{" + this.type + ", " + this.start + ", " + this.end + "}";
		}
	}

	private IScanner scanner = null;
	private Queue<Token> queue = new LinkedList<Token>();
	private char[] source = null;
	private int del_start = 0;
	private int del_length = 0;

	public ParagraphTokenizer(IScanner scanner) {
		this.scanner = scanner;
	}

	public int getTokenNameEOF() {
		return this.scanner.getTokenNameEOF();
	}

	public int getTokenNameEOL() {
		return this.scanner.getTokenNameEOF() - 1;
	}

	public int[] getTokenNameCOMMENTs() {
		return this.scanner.getTokenNameCOMMENTs();
	}

	public void setSource(char[] source, int del_start, int del_length) {
		this.source = source;
		this.scanner.setSource(source);
		this.queue.clear();
		this.del_start = del_start;
		this.del_length = del_length;
	}

	public int getCurrentTokenStartPosition() {
		if (this.queue.size() == 0) {
			return 0;
		} else {
			int pos = this.queue.peek().getStart();
			if (pos <= this.del_start) {
				return pos;
			} else {
				return pos + this.del_length;
			}
		}
	}

	public int getCurrentTokenEndPosition() {
		if (this.queue.size() == 0) {
			return 0;
		} else {
			int pos = this.queue.peek().getEnd();
			if (pos < this.del_start) {
				return pos;
			} else {
				return pos + this.del_length;
			}
		}
	}

	public int getNextToken() {
		this.queue.poll();

		if (this.queue.size() == 0) {
			int lines = this.scanner.getLineEnds().length;
			int t = this.scanner.getNextToken();
			if (lines != this.scanner.getLineEnds().length) {
				int pp0 = this.scanner.getCurrentTokenStartPosition();
				int pp1 = this.scanner.getCurrentTokenEndPosition();
				int lastOffset = pp0;
				for (int i = lastOffset; i <= pp1; ++i) {
					char c = this.source[i];
					if (c == '\n') {
						if (i - 1 >= lastOffset) {
							this.queue.add(new Token(t, lastOffset, i - 1));
						}
						this.queue.add(new Token(this.getTokenNameEOL(), i, i));
						lastOffset = i + 1;

					}
				}
				if (lastOffset <= pp1) {
					this.queue.add(new Token(t, lastOffset, pp1));
				}
			} else {
				this.queue.add(new Token(t, this.scanner.getCurrentTokenStartPosition(),
						this.scanner.getCurrentTokenEndPosition()));
			}
		}

		return this.queue.peek().getType();

	}

}
