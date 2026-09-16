package pixelbot.components.editor.syntax;

import java.util.*;

import javax.swing.text.*;

import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.core.formatter.*;

/**
 * Originally backed by Eclipse WST JSDT. JSDT was never published to Maven Central and the WTP
 * project is retired, so the lexer and formatter come from JDT instead: the two APIs are identical
 * in shape (JSDT was forked from JDT) and Java's token grammar covers JavaScript closely enough for
 * colouring. Keywords JDT does not know (function, var, let) simply lex as identifiers.
 */
public class JsEditorKit extends ScriptEditorKit {
	private static final long serialVersionUID = 6714009155349212710L;

	@Override
	public Document createDefaultDocument() {
		return new JsDocument();
	}

	public class JsDocument extends ScriptDocument {
		private static final long serialVersionUID = 5494085389487673552L;
		protected org.eclipse.jdt.core.compiler.IScanner scaner = null;
		protected CodeFormatter codeFormatter = null;

		public JsDocument() {
			super();
			this.scaner = ToolFactory.createScanner(true, true, false, true);
			Map<Object, Object> options = new HashMap<Object, Object>();

			options.put(
					DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_METHOD_BODY,
					DefaultCodeFormatterConstants.FALSE);
			options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK,
					DefaultCodeFormatterConstants.FALSE);
			options.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, "160");

			this.codeFormatter = ToolFactory.createCodeFormatter(options);
		}

		@Override
		protected IScanner getScanner() {
			return new IScanner() {
				@Override
				public void setSource(char[] source) {
					JsDocument.this.scaner.setSource(source);
				}

				@Override
				public int getTokenNameEOF() {
					return ITerminalSymbols.TokenNameEOF;
				}

				@Override
				public int getTokenNameEOL() {
					return ITerminalSymbols.TokenNameEOF - 1;
				}

				@Override
				public int[] getTokenNameCOMMENTs() {
					return new int[] { ITerminalSymbols.TokenNameCOMMENT_BLOCK,
							ITerminalSymbols.TokenNameCOMMENT_JAVADOC,
							ITerminalSymbols.TokenNameCOMMENT_LINE, this.getTokenNameEOL() };
				}

				@Override
				public int getNextToken() {
					try {
						return JsDocument.this.scaner.getNextToken();
					} catch (InvalidInputException e) {
						if ("Unterminated_Comment".equals(e.getMessage())) {
							return ITerminalSymbols.TokenNameCOMMENT_LINE;
						} else if ("Invalid_Char_In_String".equals(e.getMessage())) {
							return ITerminalSymbols.TokenNameStringLiteral;
						} else if ("Invalid_Character_Constant".equals(e.getMessage())) {
							return ITerminalSymbols.TokenNameCharacterLiteral;
						} else {
							return 0;
						}
					}

				}

				@Override
				public int[] getLineEnds() {
					return JsDocument.this.scaner.getLineEnds();
				}

				@Override
				public int getCurrentTokenStartPosition() {
					return JsDocument.this.scaner.getCurrentTokenStartPosition();
				}

				@Override
				public int getCurrentTokenEndPosition() {
					return JsDocument.this.scaner.getCurrentTokenEndPosition();
				}

				@Override
				public String getCurrentTokenSource() {
					return new String(JsDocument.this.scaner.getCurrentTokenSource());
				}

			};
		}

		@Override
		public void format() {
			writeLock();
			try {
				IDocument doc = new org.eclipse.jface.text.Document();

				Segment s = new Segment();
				int length = this.getLength();
				getContent().getChars(0, length, s);

				String contents = s.toString();
				doc.set(contents);

				TextEdit edit = this.codeFormatter.format(CodeFormatter.K_STATEMENTS
						| CodeFormatter.F_INCLUDE_COMMENTS, contents, 0, contents.length(), 0, null);
				if (edit != null) {
					edit.apply(doc);

					this.remove(0, length);
					this.insertString(0, doc.get(), null);
				}
			} catch (BadLocationException e) {} catch (org.eclipse.jface.text.BadLocationException e) {}
			writeUnlock();
		}

	}

}
