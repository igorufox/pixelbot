package pixelbot.script.parser.general;

import java.util.Collection;

public interface IScriptCompiler {

	public class CompiledUnit {
		private String className;
		private byte[] data;

		public CompiledUnit(String className, byte[] data) {
			this.className = className;
			this.data = data;
		}

		public String getClassName() {
			return this.className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public byte[] getData() {
			return this.data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

	}

	Collection<CompiledUnit> compile(String src_name, byte[] source);
}
