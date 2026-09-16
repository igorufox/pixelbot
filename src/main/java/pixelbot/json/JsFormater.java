package pixelbot.json;

import java.util.*;

import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.*;

public class JsFormater {

	private static CodeFormatter codeFormatter = null;
	static {
		try {
			Map<Object, Object> options = new HashMap<Object, Object>();

			options.put(
					DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_METHOD_BODY,
					DefaultCodeFormatterConstants.FALSE);
			options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_IN_EMPTY_BLOCK,
					DefaultCodeFormatterConstants.FALSE);
			options.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, "160");

			JsFormater.codeFormatter = ToolFactory.createCodeFormatter(options);
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		}

	}

	public static String formatJs(String jsstring) {
		try {
			if (JsFormater.codeFormatter == null) {
				return jsstring;
			}
			try {
				IDocument doc = new org.eclipse.jface.text.Document();
				doc.set(jsstring);

				TextEdit edit = JsFormater.codeFormatter.format(CodeFormatter.K_EXPRESSION,
						jsstring, 0, jsstring.length(), 0, null);
				if (edit != null) {
					edit.apply(doc);
					jsstring = doc.get();
				}
			} catch (org.eclipse.jface.text.BadLocationException e) {}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return jsstring;
	}

}
