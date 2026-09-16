package pixelbot.components.editor.syntax;

import javax.swing.text.*;

public class NonModifiableEditorKit extends ScriptEditorKit {
	private static final long serialVersionUID = 6714009155349212710L;

	@Override
	public Document createDefaultDocument() {
		return new NonModifiableDocument();
	}
	public class NonModifiableDocument extends DefaultStyledDocument {
		private static final long serialVersionUID = 6498015679335822986L;
		private byte[] data = null;

		public byte[] getData() {
			return this.data;
		}

		public void setData(byte[] value) {
			this.data = value;
		}
	}
//	public class NonModifiableDocument extends ScriptDocument {
//		private static final long serialVersionUID = 5494085389487673552L;
//
//		private byte[] data = null;
//
//		public NonModifiableDocument() {
//			super();
//		}
//
//		@Override
//		protected IScanner getScanner() {
//			return new IScanner() {
//				@Override
//				public void setSource(char[] source) {}
//
//				@Override
//				public int getTokenNameEOF() {
//					return 0;
//				}
//
//				@Override
//				public int getTokenNameEOL() {
//					return 0;
//				}
//
//				@Override
//				public int[] getTokenNameCOMMENTs() {
//					return new int[] { this.getTokenNameEOL() };
//				}
//
//				@Override
//				public int getNextToken() {
//					return 0;
//				}
//
//				@Override
//				public int[] getLineEnds() {
//					return new int[] {};
//				}
//
//				@Override
//				public int getCurrentTokenStartPosition() {
//					return 0;
//				}
//
//				@Override
//				public int getCurrentTokenEndPosition() {
//					return 0;
//				}
//
//				@Override
//				public String getCurrentTokenSource() {
//					return "";
//				}
//			};
//		}
//
//		@Override
//		public void format() {}
//
//		public byte[] getData() {
//			return this.data;
//		}
//
//		public void setData(byte[] value) {
//			this.data = value;
//		}
//
//	}

}
