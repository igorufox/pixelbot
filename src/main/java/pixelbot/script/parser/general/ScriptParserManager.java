package pixelbot.script.parser.general;

import java.awt.event.*;
import java.util.*;
import java.util.Map.Entry;

import pixelbot.script.general.ScriptManager;
import pixelbot.script.parser.general.ScriptParserManager.ScriptParserList;

public class ScriptParserManager implements Iterable<Map.Entry<String, ScriptParserList>> {
	protected ScriptManager manager = null;

	public ScriptParserManager(ScriptManager manager) {
		this.manager = manager;
	}

	private Map<String, ScriptParserList> scripts = new HashMap<String, ScriptParserList>();

	public class ScriptParserList implements ItemListener {
		private List<IScriptParser> parsers = new ArrayList<IScriptParser>();
		private int selected = 0;

		public ScriptParserList() {}

		public void add(IScriptParser parser) {
			this.parsers.add(parser);
		}

		public IScriptParser getActive() {
			return this.parsers.get(this.selected);
		}

		public Collection<String> getList() {
			ArrayList<String> result = new ArrayList<String>();
			for (IScriptParser sp : this.parsers) {
				result.add(sp.getName());
			}
			return result;
		}

		public List<IScriptParser> asList() {
			return Collections.unmodifiableList(this.parsers);
		}

		public IScriptParser find(String id) {
			if (id != null) {
				for (IScriptParser parser : this.parsers) {
					if (id.equals(parser.getId())) {
						return parser;
					}
				}
			}
			return null;
		}

		private int find(Object o) {
			if (o == null)
				return 0;
			int i = 0;
			for (IScriptParser parser : this.parsers) {
				if (parser == o) {
					return i;
				}
				++i;
			}
			return 0;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				this.selected = this.find(e.getItem());
				ScriptParserManager.this.manager.scopeReparse();
			}
		}
	}

	public void addScriptProvider(IScriptParser parser) {
		ScriptParserList spl = this.scripts.get(parser.getType());
		if (spl == null) {
			spl = new ScriptParserList();
			this.scripts.put(parser.getType(), spl);
		}
		spl.add(parser);
	}

	public IScriptParser get(String type) {
		return this.scripts.get(type).getActive();
	}

	@Override
	public Iterator<Map.Entry<String, ScriptParserList>> iterator() {
		List<Map.Entry<String, ScriptParserList>> result = new ArrayList<Map.Entry<String, ScriptParserList>>();
		result.addAll(this.scripts.entrySet());
		Collections.sort(result, new Comparator<Map.Entry<String, ScriptParserList>>() {

			@Override
			public int compare(Entry<String, ScriptParserList> o1,
					Entry<String, ScriptParserList> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		return result.iterator();
	}

}