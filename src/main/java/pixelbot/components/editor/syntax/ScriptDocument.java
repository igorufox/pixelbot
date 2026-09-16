package pixelbot.components.editor.syntax;

import java.awt.*;
import java.util.Vector;

import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;

public abstract class ScriptDocument extends AbstractDocument implements StyledDocument {
	public interface IScanner {

		void setSource(char[] source);

		int[] getLineEnds();

		int getNextToken();

		int getTokenNameEOF();

		int getTokenNameEOL();

		int[] getTokenNameCOMMENTs();

		int getCurrentTokenStartPosition();

		int getCurrentTokenEndPosition();

		String getCurrentTokenSource();

	}

	private static final long serialVersionUID = 1302131732684868568L;
	private static final String TOKEN_TYPE = "TOKEN_TYPE";
	private AbstractElement defaultRoot;
	private ParagraphTokenizer scanner = null;
	private StyleProvider style = new StyleProvider();
	protected UndoManager undoManager;
	private SimpleAttributeSet paragraphAttributes = null;

	/**
	 * Constructs a plain text document. A default model using <code>GapContent</code> is
	 * constructed and set.
	 */
	public ScriptDocument() {
		this(new GapContent());
	}

	/**
	 * Constructs a plain text document. A default root element is created, and the tab size set to
	 * 8.
	 * 
	 * @param c the container for the content
	 */
	protected ScriptDocument(Content c) {
		super(c);
		this.defaultRoot = createDefaultRoot();
		this.scanner = new ParagraphTokenizer(this.getScanner());
		this.undoManager = new UndoManager();
		this.addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent event) {
				ScriptDocument.this.undoManager.addEdit(event.getEdit());
			}
		});

		TabSet tabSet = new TabSet(new TabStop[] { new TabStop(36f), new TabStop(72f),
				new TabStop(108f), new TabStop(144f), new TabStop(180f), new TabStop(216f),
				new TabStop(252f), new TabStop(288f), new TabStop(324f), new TabStop(360f),
				new TabStop(396f), new TabStop(432f), new TabStop(468f), new TabStop(504f) });

		this.paragraphAttributes = new SimpleAttributeSet();
		this.paragraphAttributes.addAttribute(StyleConstants.TabSet, tabSet);

	}

	protected abstract IScanner getScanner();

	@Override
	public Style addStyle(String nm, Style parent) {
		return null;
	}

	@Override
	public void removeStyle(String nm) {}

	@Override
	public Style getStyle(String nm) {
		return null;
	}

	@Override
	public void setCharacterAttributes(int offset, int length, AttributeSet s, boolean replace) {}

	@Override
	public void setParagraphAttributes(int offset, int length, AttributeSet s, boolean replace) {}

	@Override
	public void setLogicalStyle(int pos, Style s) {}

	@Override
	public Style getLogicalStyle(int p) {
		return null;
	}

	/**
	 * Gets a character element based on a position.
	 * 
	 * @param pos the position in the document >= 0
	 * @return the element
	 */
	@Override
	public Element getCharacterElement(int pos) {
		Element e;
		for (e = getDefaultRootElement(); !e.isLeaf();) {
			int index = e.getElementIndex(pos);
			e = e.getElement(index);
		}
		return e;
	}

	@Override
	public Color getForeground(AttributeSet attr) {
		return this.style.getColor(((Integer) attr.getAttribute(TOKEN_TYPE)).intValue());
	}

	@Override
	public Color getBackground(AttributeSet attr) {
		return Color.white;
	}

	@Override
	public Font getFont(AttributeSet attr) {
		return this.style.getFont(((Integer) attr.getAttribute(TOKEN_TYPE)).intValue());
	}

	/**
	 * Gets the default root element for the document model.
	 * 
	 * @return the root
	 * @see Document#getDefaultRootElement
	 */
	@Override
	public Element getDefaultRootElement() {
		return this.defaultRoot;
	}

	/**
	 * Creates the root element to be used to represent the default document structure.
	 * 
	 * @return the element base
	 */
	protected AbstractElement createDefaultRoot() {
		writeLock();
		BranchElement section = new SectionElement();
		BranchElement paragraph = new BranchElement(section, null);

		SimpleAttributeSet sas = new SimpleAttributeSet();
		sas.addAttribute(TOKEN_TYPE, Integer.valueOf(0));
		LeafElement brk = new LeafElement(paragraph, sas, 0, 1);
		paragraph.replace(0, 0, new Element[] { brk });

		section.replace(0, 0, new Element[] { paragraph });
		writeUnlock();
		return section;
	}

	/**
	 * Get the paragraph element containing the given position. Since this document only models
	 * lines, it returns the line instead.
	 */
	@Override
	public Element getParagraphElement(int pos) {
		Element lineMap = getDefaultRootElement();
		return lineMap.getElement(lineMap.getElementIndex(pos));
	}

	/**
	 * Updates document structure as a result of text insertion. This will happen within a write
	 * lock. Since this document simply maps out lines, we refresh the line map.
	 * 
	 * @param chng the change event describing the dit
	 * @param attr the set of attributes for the inserted text
	 */
	@Override
	protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
		calculateDocumentStructure(chng);
		super.insertUpdate(chng, attr);
	}

	@Override
	protected void removeUpdate(DefaultDocumentEvent chng) {
		calculateDocumentStructure(chng);
		super.removeUpdate(chng);
	}

	private void calculateDocumentStructure(DefaultDocumentEvent chng) {
//		long m = System.currentTimeMillis();
		BranchElement lineMap = (BranchElement) getDefaultRootElement();
		Vector<Element> added = new Vector<Element>();
		Vector<Element> removed = new Vector<Element>();
		Segment s = new Segment();

		int start_line = lineMap.getElementIndex(chng.getOffset());
		int end_line = lineMap.getElementIndex(chng.getOffset() + chng.getLength());

		while (start_line > 0 && this.isComment(lineMap.getElement(start_line).getElement(0))) {
			start_line--;
		}

		int start_offset = lineMap.getElement(start_line).getStartOffset();
		try {
			getContent().getChars(start_offset, this.getLength() - start_offset + 1, s);
		} catch (BadLocationException e1) {}
		try {
			String str = s.toString();
			if (chng.getType() == EventType.REMOVE) {
				str = str.substring(0, chng.getOffset() - start_offset)
						+ str.substring(chng.getOffset() + chng.getLength() - start_offset);
				char[] source = str.toCharArray();
				this.scanner.setSource(source, chng.getOffset() - start_offset, chng.getLength());
			} else {
				char[] source = str.toCharArray();
				this.scanner.setSource(source, 0, 0);
			}
			int t = 0;

			BranchElement paragraph = (BranchElement) createBranchElement(lineMap,
					this.paragraphAttributes);
			Vector<Element> content = new Vector<Element>();
			while ((t = this.scanner.getNextToken()) != this.scanner.getTokenNameEOF()) {

				SimpleAttributeSet attributes = new SimpleAttributeSet();
				attributes.addAttribute(TOKEN_TYPE, Integer.valueOf(t));
				Element token = createLeafElement(paragraph, attributes,
						this.scanner.getCurrentTokenStartPosition() + start_offset,
						this.scanner.getCurrentTokenEndPosition() + 1 + start_offset);
				content.add(token);

				if (t == this.scanner.getTokenNameEOL()) {
					paragraph.replace(0, 0, content.toArray(new Element[] {}));
					content.clear();
					added.addElement(paragraph);
					paragraph = (BranchElement) createBranchElement(lineMap,
							this.paragraphAttributes);
					while ((this.scanner.getCurrentTokenEndPosition() + start_offset + 1) > lineMap
							.getElement(end_line).getEndOffset()) {
						end_line++;
					}
					if ((this.scanner.getCurrentTokenEndPosition() + start_offset + 1) == lineMap
							.getElement(end_line).getEndOffset()) {
						Element p = added.get(added.size() - 1);
						if (end_line >= lineMap.getElementCount() - 1) {
							break;
						}
						if (p.getElementCount() == 1) {
							end_line++;
							continue;
						}
						if ((p.getElementCount() > 1 && this.isComment(p.getElement(p
								.getElementCount() - 2)))) {
							end_line++;
							continue;
						}
						if ((lineMap.getElement(end_line).getElementCount() > 1 && this
								.isComment(lineMap.getElement(end_line).getElement(
										lineMap.getElement(end_line).getElementCount() - 2)))) {
							end_line++;
							continue;
						}
						break;
					}
				}
			}
			if (content.size() != 0) {
				paragraph.replace(0, 0, content.toArray(new Element[] {}));
				content.clear();
				added.addElement(paragraph);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = start_line; i <= end_line; ++i) {
			removed.add(lineMap.getElement(i));
		}

		// System.out.println("+" + (System.currentTimeMillis() - m) + " -[" + removed.size() + ":"
		// + removed.get(0).getStartOffset() + ","
		// + removed.get(removed.size() - 1).getEndOffset() + "] +[" + added.size() + ":"
		// + added.get(0).getStartOffset() + "," + added.get(added.size() - 1).getEndOffset()
		// + "]");
		if ((removed.get(0).getStartOffset() != added.get(0).getStartOffset())
				|| (removed.get(removed.size() - 1).getEndOffset() != added.get(added.size() - 1)
						.getEndOffset())) {
			System.out.println("fignja");
		}
		ElementEdit ee = new ElementEdit(lineMap, start_line, removed.toArray(new Element[] {}),
				added.toArray(new Element[] {}));
		chng.addEdit(ee);
		lineMap.replace(start_line, removed.size(), added.toArray(new Element[] {}));
	}

	private boolean isComment(Element element) {
		int[] comments = this.scanner.getTokenNameCOMMENTs();
		int t = ((Integer) element.getAttributes().getAttribute(TOKEN_TYPE)).intValue();

		for (int c : comments) {
			if (c == t) {
				return true;
			}
		}
		return false;
	}

	public abstract void format();

	protected class SectionElement extends BranchElement {
		private static final long serialVersionUID = 1771171084766834540L;

		public SectionElement() {
			super(null, null);
		}

		@Override
		public String getName() {
			return SectionElementName;
		}
	}

	public UndoManager getUndoManager() {
		return this.undoManager;
	}

}
