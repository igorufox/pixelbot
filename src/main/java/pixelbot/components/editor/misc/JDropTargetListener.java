package pixelbot.components.editor.misc;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.net.URL;
import java.util.List;

import javax.swing.text.*;

import pixelbot.components.editor.ScriptEditor.ScriptTextPane;

public class JDropTargetListener extends DropTargetAdapter {

	private ScriptTextPane pane;

	public JDropTargetListener(ScriptTextPane pane) {
		this.pane = pane;
	}

	private void readFile(final String fileName) throws Exception {
		String[] urls = fileName.split("\n");
		URL url = new URL(urls[0]);
		this.loadFile(url.getPath());
	}

	private void loadFile(final String path) throws Exception {
		EditorKit kit = this.pane.getEditorKit();
		Document document = this.pane.getDocument();
		document.remove(0, document.getLength());
		try (FileReader reader = new FileReader(path)) {
			kit.read(reader, document, 0);
		}
		this.pane.setCaretPosition(0);
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			Transferable t = dtde.getTransferable();
			dtde.acceptDrop(DnDConstants.ACTION_LINK);
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String fileName = (String) t.getTransferData(DataFlavor.stringFlavor);
				this.readFile(fileName);
				dtde.dropComplete(true);
			} else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				List<?> list = (List<?>) t.getTransferData(DataFlavor.javaFileListFlavor);
				if (list.size() > 0) {
					this.loadFile(((File) list.get(0)).getPath());
				}
				dtde.dropComplete(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
