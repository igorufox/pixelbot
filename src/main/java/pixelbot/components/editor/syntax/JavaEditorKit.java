package pixelbot.components.editor.syntax;

import java.util.*;

import javax.swing.text.*;

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

public class JavaEditorKit extends ScriptEditorKit {
	private static final long serialVersionUID = 6714009155349212710L;

	@Override
	public Document createDefaultDocument() {
		return new JavaDocument();
	}

	public class JavaDocument extends ScriptDocument {
		private static final long serialVersionUID = 5494085389487673552L;
		protected org.eclipse.jdt.core.compiler.IScanner scaner = null;
		protected CodeFormatter codeFormatter = null;

		public JavaDocument() {
			super();
			this.scaner = ToolFactory.createScanner(true, true, false, true);

			Map<Object, Object> options = new HashMap<Object, Object>();

			options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
			options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);

			options.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, "160");
			this.codeFormatter = ToolFactory.createCodeFormatter(options);
		}

		@Override
		protected IScanner getScanner() {
			return new IScanner() {
				@Override
				public void setSource(char[] source) {
					JavaDocument.this.scaner.setSource(source);
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
						return JavaDocument.this.scaner.getNextToken();
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
					return JavaDocument.this.scaner.getLineEnds();
				}

				@Override
				public int getCurrentTokenStartPosition() {
					return JavaDocument.this.scaner.getCurrentTokenStartPosition();
				}

				@Override
				public int getCurrentTokenEndPosition() {
					return JavaDocument.this.scaner.getCurrentTokenEndPosition();
				}

				@Override
				public String getCurrentTokenSource() {
					return new String(JavaDocument.this.scaner.getCurrentTokenSource());
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

				TextEdit edit = this.codeFormatter.format(4104, contents, 0, contents.length(), 0,
						null);
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
