package pixelbot.components.editor;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.net.FileNameMap;
import java.net.URLConnection;

import pixelbot.components.editor.hint.ScriptHint;
import pixelbot.components.editor.hint.event.*;
import pixelbot.components.editor.misc.*;
import pixelbot.components.editor.syntax.*;
import pixelbot.components.editor.syntax.NonModifiableEditorKit.NonModifiableDocument;
import pixelbot.script.general.ScriptManager;

public class ScriptEditor extends JPanel {
	private static final long serialVersionUID = -8865892778780763823L;

	private ScriptTextPane textPane;
	private JScrollPane scrollPane;
	protected JNumLinePanel numJPanel;
	private JFindPanel findPanel;

	protected File file = null;
	private boolean modified = false;
	private static final FileNameMap fileNameMap = createFileNameMap();

	static {
		try {
			Class.forName("org.eclipse.jface.text.BadLocationException");
			JEditorPane.registerEditorKitForContentType("text/javascript",
					"pixelbot.components.editor.syntax.JsEditorKit",
					ScriptEditor.class.getClassLoader());
			JEditorPane.registerEditorKitForContentType("text/java",
					"pixelbot.components.editor.syntax.JavaEditorKit",
					ScriptEditor.class.getClassLoader());
		} catch (ClassNotFoundException e) {
		}
		JEditorPane.registerEditorKitForContentType(
				"application/java-byte-code",
				"pixelbot.components.editor.syntax.NonModifiableEditorKit",
				ScriptEditor.class.getClassLoader());
		JEditorPane.registerEditorKitForContentType("application/java-archive",
				"pixelbot.components.editor.syntax.NonModifiableEditorKit",
				ScriptEditor.class.getClassLoader());
		JEditorPane.registerEditorKitForContentType("application/zip",
				"pixelbot.components.editor.syntax.NonModifiableEditorKit",
				ScriptEditor.class.getClassLoader());

	}

	/**
	 * Maps the editor's own script extensions onto the content types registered above; anything
	 * else falls back to the platform's table.
	 */
	private static FileNameMap createFileNameMap() {
		final Map<String, String> byExtension = new HashMap<String, String>();
		byExtension.put("js", "text/javascript");
		byExtension.put("djs", "text/javascript");
		byExtension.put("java", "text/java");
		byExtension.put("djava", "text/java");
		byExtension.put("class", "application/java-byte-code");
		byExtension.put("jar", "application/java-archive");
		byExtension.put("zip", "application/zip");

		final FileNameMap fallback = URLConnection.getFileNameMap();

		return new FileNameMap() {
			@Override
			public String getContentTypeFor(String fileName) {
				int dot = fileName.lastIndexOf('.');
				if (dot >= 0) {
					String type = byExtension.get(fileName.substring(dot + 1).toLowerCase(
							Locale.ROOT));
					if (type != null) {
						return type;
					}
				}
				String type = fallback.getContentTypeFor(fileName);
				return type != null ? type : "text/plain";
			}
		};
	}

	public ScriptEditor(final ScriptManager script) {
		super();
		this.setLayout(new BorderLayout());
		this.numJPanel = new JNumLinePanel(this);
		this.textPane = new ScriptTextPane(script);
		this.scrollPane = new JScrollPane(this.getTextPane());
		this.scrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(
							AdjustmentEvent paramAdjustmentEvent) {
						ScriptEditor.this.numJPanel.repaint();
					}
				});
		this.findPanel = new JFindPanel(this.textPane);
		this.add(this.numJPanel, BorderLayout.WEST);
		this.add(this.scrollPane, BorderLayout.CENTER);
		this.add(this.findPanel, BorderLayout.SOUTH);

		this.addMarkerListener(new MarkerEventListener() {

			@Override
			public void markChanged(MarkerEvent event) {
				if (event.isSet()) {
					script.getScriptDebugger().addBreakPoint(
							ScriptEditor.this.file.getName(), event.getLine());
				} else {
					script.getScriptDebugger().removeBreakPoint(
							ScriptEditor.this.file.getName(), event.getLine());
				}

			}
		});

	}

	public void addHintInitializationEvent(HintEventListener e) {
		this.listenerList.add(HintEventListener.class, e);
	}

	public void removeInitializationEvent(HintEventListener e) {
		this.listenerList.remove(HintEventListener.class, e);
	}

	public void fireHintInitializationEvent(HintEvent e) {
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == HintEventListener.class) {
				HintEventListener listener = ((HintEventListener) listeners[i + 1]);
				listener.initialized(e);
			}
		}
	}

	public byte[] getData() {
		return this.getTextPane().getData();
	}

	public void setData(byte[] value) {
		this.getTextPane().setData(value);
	}

	public ScriptTextPane getTextPane() {
		return this.textPane;
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	public JNumLinePanel getLineNumJPanel() {
		return this.numJPanel;
	}

	public JFindPanel getFindPanel() {
		return this.findPanel;
	}

	public static class MarkerEvent {
		private int line;
		private boolean set;

		public MarkerEvent(int line, boolean set) {
			this.line = line;
			this.set = set;
		}

		public int getLine() {
			return this.line;
		}

		public boolean isSet() {
			return this.set;
		}

	}

	public abstract interface MarkerEventListener extends EventListener {
		public abstract void markChanged(MarkerEvent event);
	}

	public void fireMarkerChange(MarkerEvent event) {
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == MarkerEventListener.class) {
				((MarkerEventListener) listeners[i + 1]).markChanged(event);
			}
		}
	}

	public void addMarkerListener(MarkerEventListener x) {
		this.listenerList.add(MarkerEventListener.class, x);
	}

	public void removeMarkerListener(MarkerEventListener x) {
		this.listenerList.remove(MarkerEventListener.class, x);
	}

	public class ScriptTextPane extends JEditorPane {
		private static final long serialVersionUID = 2860068227265896581L;

		protected Highlighter.Highlight currentLineHighlight;
		protected Highlighter.Highlight markedLineHighlight;
		private ScriptHint hint;

		private ScriptManager script;

		public ScriptTextPane(ScriptManager script) {
			this.script = script;

			this.getDocument().putProperty(
					DefaultEditorKit.EndOfLineStringProperty, "\n");

			this.putClientProperty("caretWidth", Integer.valueOf(2));
			this.getCaret().setBlinkRate(500);
			this.hint = new ScriptHint(this);
			this.addKeyListener(this.hint);
			this.addMouseListener(this.hint);
			this.add(this.hint);

			try {
				this.currentLineHighlight = (Highlighter.Highlight) this
						.getHighlighter().addHighlight(0, 0, new LinePainter());
				((LinePainter) this.currentLineHighlight.getPainter())
						.setColor(new Color(0xE8F2FE));
				this.addCaretListener(new CaretListener() {
					@Override
					public void caretUpdate(CaretEvent event) {
						Document doc = ScriptTextPane.this.getDocument();
						int startline = doc.getDefaultRootElement()
								.getElementIndex(
										ScriptTextPane.this.getCaretPosition());

						try {
							ScriptTextPane.this
									.getHighlighter()
									.changeHighlight(
											ScriptTextPane.this.currentLineHighlight,
											startline, startline);
						} catch (BadLocationException e) {
						}
						ScriptTextPane.this.repaint();

					}
				});
				this.markedLineHighlight = (Highlighter.Highlight) this
						.getHighlighter().addHighlight(0, 0, new LinePainter());
				((LinePainter) this.markedLineHighlight.getPainter())
						.setColor(new Color(0xC6DBAE));
				((LinePainter) this.markedLineHighlight.getPainter())
						.setVisible(false);
			} catch (BadLocationException e) {
			}

			this.initDnD();
		}

		public byte[] getData() {
			if (this.getDocument() instanceof NonModifiableDocument) {
				return ((NonModifiableDocument) (this.getDocument())).getData();
			} else {
				try {
					return this.getText().getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					return new byte[] {};
				}
			}
		}

		public void setData(byte[] data) {
			if (this.getDocument() instanceof NonModifiableDocument) {
				((NonModifiableDocument) (this.getDocument())).setData(data);
			} else {
				try {
					this.setText(new String(data, "UTF-8").replace("\r", ""));
				} catch (UnsupportedEncodingException e) {
				}
				this.setCaretPosition(0);
			}
		}

		private void initDnD() {
			new DropTarget(this, DnDConstants.ACTION_LINK,
					new JDropTargetListener(this));
		}

		public ScriptManager getScriptManager() {
			return this.script;
		}

		public ScriptEditor getEditor() {
			return ScriptEditor.this;
		}

		@Override
		public synchronized void setText(String t) {
			super.setText(t);
		}

		public void removeString(int offs, int len) throws BadLocationException {
			this.getDocument().remove(offs, len);
		}

		public void insertString(int offset, String str) {
			try {
				this.getDocument().insertString(offset, str, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		public Segment getLine() throws BadLocationException {
			return this.getLine(this.getCaretPosition());
		}

		private Segment getLine(int offset) throws BadLocationException {
			Document doc = this.getDocument();
			Element element = doc.getDefaultRootElement();
			int lineIndex = element.getElementIndex(offset);

			Element line = element.getElement(lineIndex);
			int start = line.getStartOffset();
			Segment s = new Segment();
			s.setPartialReturn(true);
			doc.getText(start, line.getEndOffset() - start, s);
			try {
				s.setIndex(offset - start + s.getBeginIndex());
			} catch (java.lang.IllegalArgumentException e) {
				System.err.println("s");
			}
			return s;
		}

		public void setLineMarker(int line) {
			try {
				ScriptTextPane.this.getHighlighter().changeHighlight(
						ScriptTextPane.this.markedLineHighlight, line, line);
				((LinePainter) this.markedLineHighlight.getPainter())
						.setVisible(true);
				ScriptTextPane.this.setCaretPosition(ScriptTextPane.this
						.getDocument().getDefaultRootElement().getElement(line)
						.getStartOffset());
				this.repaint();
			} catch (BadLocationException e) {
			}

		}

		public void removeLineMarker() {
			((LinePainter) this.markedLineHighlight.getPainter())
					.setVisible(false);
			this.repaint();
		}

	}

	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
		try {
			this.textPane.setContentType(fileNameMap.getContentTypeFor(file
					.getName()));
			Document doc = this.textPane.getDocument();
			if (doc instanceof NonModifiableDocument) {
				this.textPane.setEnabled(false);
				this.textPane
						.setText(String
								.format("File Name: %s\nLast Modified: %s\nLength: %,d bytes\n",
										file.getName(),
										new Date(file.lastModified()),
										Long.valueOf(file.length())));
			} else {
				doc.addDocumentListener(new DocumentListener() {

					@Override
					public void removeUpdate(DocumentEvent paramDocumentEvent) {
						this.textChanged();
					}

					@Override
					public void insertUpdate(DocumentEvent paramDocumentEvent) {
						this.textChanged();
					}

					@Override
					public void changedUpdate(DocumentEvent paramDocumentEvent) {
						this.textChanged();
					}

					private void textChanged() {
						ScriptEditor.this.setModified(true);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setModified(boolean modified) {
		this.modified = modified;
		if (this.getFile() != null) {
			JTabbedScriptsPane pane = (JTabbedScriptsPane) this.getParent();
			int pos = pane.indexOfComponent(this);
			if (this.modified) {
				((JLabel) pane.getTabComponentAt(pos)).setText("*"
						+ this.getFile().getName());
			} else {
				((JLabel) pane.getTabComponentAt(pos)).setText(this.getFile()
						.getName());
			}
		}
	}

	public boolean isModified() {
		return this.modified;
	}

	@Override
	public void setLocale(Locale l) {
		super.setLocale(l);
		this.findPanel.setLocale(l);
	}

}