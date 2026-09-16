package pixelbot.components.editor.syntax;

import java.awt.*;

import org.eclipse.jdt.core.compiler.ITerminalSymbols;

public class StyleProvider {
	private static final Font d = new Font("Arial", Font.PLAIN, 12);
	private static final Font b = new Font("Arial", Font.BOLD, 12);
	private static final Font i = new Font("Arial", Font.ITALIC, 12);

	public Font getFont(int type) {
		switch (type) {
		case ITerminalSymbols.TokenNamepublic:
		case ITerminalSymbols.TokenNameprivate:
		case ITerminalSymbols.TokenNameprotected:
		case ITerminalSymbols.TokenNamenull:
		case ITerminalSymbols.TokenNamereturn:
		case ITerminalSymbols.TokenNamethis:
		case ITerminalSymbols.TokenNamefalse:
		case ITerminalSymbols.TokenNametrue:
		case ITerminalSymbols.TokenNamethrow:
		case ITerminalSymbols.TokenNametry:
		case ITerminalSymbols.TokenNamecatch:
		case ITerminalSymbols.TokenNamefinally:
		case ITerminalSymbols.TokenNameif:
		case ITerminalSymbols.TokenNameelse:
		case ITerminalSymbols.TokenNameswitch:
		case ITerminalSymbols.TokenNamecase:
		case ITerminalSymbols.TokenNamefor:
		case ITerminalSymbols.TokenNamewhile:
		case ITerminalSymbols.TokenNamedo:
		case ITerminalSymbols.TokenNamebreak:
		case ITerminalSymbols.TokenNamecontinue:
		case ITerminalSymbols.TokenNamepackage:
		case ITerminalSymbols.TokenNameimport:
		case ITerminalSymbols.TokenNameclass:
		case ITerminalSymbols.TokenNameextends:
		case ITerminalSymbols.TokenNameimplements:
		case ITerminalSymbols.TokenNamestatic:
		case ITerminalSymbols.TokenNamefinal:
		case ITerminalSymbols.TokenNameint:
		case ITerminalSymbols.TokenNamevoid:
		case ITerminalSymbols.TokenNameboolean:
			return b;
		case ITerminalSymbols.TokenNameIdentifier:
			return i;
		default:
			return d;
		}
	}

	public Color getColor(int type) {
		switch (type) {
		case ITerminalSymbols.TokenNameCOMMENT_LINE:
		case ITerminalSymbols.TokenNameCOMMENT_BLOCK:
			return new Color(0x3F7F5F);
		case ITerminalSymbols.TokenNamepublic:
		case ITerminalSymbols.TokenNameprivate:
		case ITerminalSymbols.TokenNameprotected:
		case ITerminalSymbols.TokenNamenull:
		case ITerminalSymbols.TokenNamereturn:
		case ITerminalSymbols.TokenNamethis:
		case ITerminalSymbols.TokenNamefalse:
		case ITerminalSymbols.TokenNametrue:
		case ITerminalSymbols.TokenNamethrow:
		case ITerminalSymbols.TokenNametry:
		case ITerminalSymbols.TokenNamecatch:
		case ITerminalSymbols.TokenNamefinally:
		case ITerminalSymbols.TokenNameif:
		case ITerminalSymbols.TokenNameelse:
		case ITerminalSymbols.TokenNameswitch:
		case ITerminalSymbols.TokenNamecase:
		case ITerminalSymbols.TokenNamefor:
		case ITerminalSymbols.TokenNamewhile:
		case ITerminalSymbols.TokenNamedo:
		case ITerminalSymbols.TokenNamebreak:
		case ITerminalSymbols.TokenNamecontinue:
		case ITerminalSymbols.TokenNamepackage:
		case ITerminalSymbols.TokenNameimport:
		case ITerminalSymbols.TokenNameclass:
		case ITerminalSymbols.TokenNameextends:
		case ITerminalSymbols.TokenNameimplements:
		case ITerminalSymbols.TokenNamestatic:
		case ITerminalSymbols.TokenNamefinal:
		case ITerminalSymbols.TokenNameint:
		case ITerminalSymbols.TokenNamevoid:
		case ITerminalSymbols.TokenNameboolean:
			return new Color(0x7F0055);
		case ITerminalSymbols.TokenNameCOMMENT_JAVADOC:
			return new Color(0x3F5FBF);
		case ITerminalSymbols.TokenNameStringLiteral:
		case ITerminalSymbols.TokenNameCharacterLiteral:
			return new Color(0x2A00FF);
		default:
			return Color.black;
		}
	}
}
