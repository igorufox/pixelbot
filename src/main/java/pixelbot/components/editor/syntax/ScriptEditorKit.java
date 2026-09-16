package pixelbot.components.editor.syntax;

import java.awt.Event;
import java.awt.event.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.*;

import pixelbot.components.editor.ScriptEditor.ScriptTextPane;

public abstract class ScriptEditorKit extends StyledEditorKit {
	private static final long serialVersionUID = -6397704716850959203L;

	public static final String undoAction = "text-undo";
	public static final String redoAction = "text-redo";
	public static final String removeTabAction = "remove-tab";
	public static final String formatAction = "text-format";
	public static final String findAction = "text-find";
	public static final String commentAction = "text-comment";

	private static final Action[] defaultActions = { new UndoAction(), new RedoAction(),
			new InsertTabAction(), new RemoveTabAction(), new InsertBreakAction(),
			new FormatAction(), new FindAction(), new CommentAction(), new SelectWordAction() };

	public ScriptEditorKit() {
		super();
	}

	@Override
	public Action[] getActions() {
		return TextAction.augmentList(super.getActions(), defaultActions);
	}

	@Override
	public Document createDefaultDocument() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void install(JEditorPane pane) {
		super.install(pane);
		InputMap inputMap = pane.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK),
				ScriptEditorKit.undoAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK),
				ScriptEditorKit.redoAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK),
				ScriptEditorKit.removeTabAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK | Event.SHIFT_MASK),
				ScriptEditorKit.formatAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK),
				ScriptEditorKit.findAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Event.CTRL_MASK),
				ScriptEditorKit.commentAction);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Event.CTRL_MASK),
				ScriptEditorKit.commentAction);
	}

	@Override
	public void deinstall(JEditorPane pane) {
		super.deinstall(pane);
		InputMap inputMap = pane.getInputMap();
		inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK));
		inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK | Event.SHIFT_MASK));
		inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
		inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, Event.CTRL_MASK));
	}

	private static abstract class AbsctractUndoRedoAction extends TextAction {
		private static final long serialVersionUID = 7211669820325544590L;

		public AbsctractUndoRedoAction(String action) {
			super(action);
		}

		protected UndoManager getUndoManager(ActionEvent event) {
			return ((ScriptDocument) this.getTextComponent(event).getDocument()).getUndoManager();
		}
	}

	private static class UndoAction extends AbsctractUndoRedoAction {
		private static final long serialVersionUID = 5978555268251737996L;

		public UndoAction() {
			super(undoAction);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (this.getUndoManager(e).canUndo()) {
					this.getUndoManager(e).undo();
				}
			} catch (CannotUndoException ex) {}
		}
	}

	private static class RedoAction extends AbsctractUndoRedoAction {
		private static final long serialVersionUID = -7578128915107010518L;

		public RedoAction() {
			super(redoAction);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (this.getUndoManager(e).canRedo()) {
					this.getUndoManager(e).redo();
				}
			} catch (CannotRedoException ex) {}
		}
	}

	private static abstract class BlockAction extends TextAction {
		private static final long serialVersionUID = 68996709147883966L;

		public BlockAction(String action) {
			super(action);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			JTextComponent pane = getTextComponent(event);
			int i = Math.min(pane.getCaret().getDot(), pane.getCaret().getMark());
			int j = Math.max(pane.getCaret().getDot(), pane.getCaret().getMark());

			Document doc = pane.getDocument();
			Element element = doc.getDefaultRootElement();
			int line_i = element.getElementIndex(i);
			int line_j = element.getElementIndex(j);

			for (int k = line_i; k <= line_j; ++k) {
				Element line_desc = element.getElement(k);

				try {
					this.lineAction(doc, line_desc.getStartOffset());
				} catch (BadLocationException e) {}

			}
		}

		protected abstract void lineAction(Document doc, int startOffset)
				throws BadLocationException;

	}

	private static class InsertTabAction extends BlockAction {
		private static final long serialVersionUID = -3136009132831392919L;

		public InsertTabAction() {
			super(DefaultEditorKit.insertTabAction);
		}

		@Override
		protected void lineAction(Document doc, int startOffset) throws BadLocationException {
			doc.insertString(startOffset, "\t", null);
		}
	}

	private static class RemoveTabAction extends BlockAction {
		private static final long serialVersionUID = 3690826889110449430L;

		public RemoveTabAction() {
			super(removeTabAction);
		}

		@Override
		protected void lineAction(Document doc, int startOffset) throws BadLocationException {
			if ("\t".equals(doc.getText(startOffset, 1))) {
				doc.remove(startOffset, 1);
			}
		}
	}

	private static class InsertBreakAction extends DefaultEditorKit.InsertBreakAction {
		private static final long serialVersionUID = 8297631509399231713L;

		public InsertBreakAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			super.actionPerformed(event);
			try {
				InsertBreakAction.doIndent(getTextComponent(event));
			} catch (Exception e) {}

		}

		private static void doIndent(JTextComponent pane) throws BadLocationException {
			Document doc = pane.getDocument();
			Element element = doc.getDefaultRootElement();
			int lineIndex = element.getElementIndex(pane.getCaretPosition());

			Element line_desc = element.getElement(lineIndex - 1);
			int start = line_desc.getStartOffset();

			String line = doc.getText(start, line_desc.getEndOffset() - start);

			int cid = line.split("\\t").length - 1;
			int iobr = line.trim().lastIndexOf('{');
			if ((line.trim().length() - 1) == iobr && iobr > 0) {
				cid++;
			}
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < cid; ++i) {
				sb.append('\t');
			}
			doc.insertString(pane.getCaretPosition(), sb.toString(), null);
		}
	}

	private static class FormatAction extends TextAction {
		private static final long serialVersionUID = -5031483210264742995L;

		public FormatAction() {
			super(formatAction);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				JTextComponent pane = this.getTextComponent(event);
				int pos = pane.getCaretPosition();
				((ScriptDocument) pane.getDocument()).format();
				pane.setCaretPosition(pos);
			} catch (Exception e) {}
		}
	}

	private static class FindAction extends TextAction {
		private static final long serialVersionUID = 5030855211899137101L;

		public FindAction() {
			super(findAction);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			((ScriptTextPane) this.getTextComponent(event)).getEditor().getFindPanel().showPanel();
		}
	}

	private static class CommentAction extends BlockAction {
		private static final long serialVersionUID = 6816908203224885888L;

		public CommentAction() {
			super(commentAction);
		}

		@Override
		protected void lineAction(Document doc, int startOffset) throws BadLocationException {
			if ("//".equals(doc.getText(startOffset, 2))) {
				doc.remove(startOffset, 2);
			} else {
				doc.insertString(startOffset, "//", null);
			}
		}
	}

	private static class SelectWordAction extends TextAction {
		public SelectWordAction() {
			super(DefaultEditorKit.selectWordAction);
		}

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			JTextComponent pane = getTextComponent(paramActionEvent);
			if (pane == null)
				return;
			try {
				Document localDocument = pane.getDocument();
				Element localElement = Utilities.getParagraphElement(pane, pane.getCaretPosition());
				if (localElement == null)
					throw new BadLocationException("No word at " + pane.getCaretPosition(),
							pane.getCaretPosition());
				int i = localElement.getStartOffset();
				int j = Math.min(localElement.getEndOffset(), localDocument.getLength());
				String line = localDocument.getText(i, j - i);
				if (line.length() > 0) {
					Matcher m = Pattern.compile("(\\.|\\s|\\(|\\)|\"|'|\\[|\\]|:)").matcher(line);
					int pos = 0;
					while (m.find()) {
						if (m.start(0) >= (pane.getCaretPosition() - i)) {
							break;
						}
						pos = m.start(0);
					}
					pane.setCaretPosition(i + pos + 1);
					pane.moveCaretPosition(i + m.start(0));
				}

			} catch (BadLocationException localBadLocationException) {
				UIManager.getLookAndFeel().provideErrorFeedback(pane);
			}
		}

	}

}
