package pixelbot.prefs;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;

/**
 * Preferences implementation that stores to a user-defined file. See FilePreferencesFactory.
 * 
 * @author David Croft (<a href="http://www.davidc.net">www.davidc.net</a>)
 * @version $Id: FilePreferences.java 283 2009-06-18 17:06:58Z david $
 */
public class FilePreferences extends AbstractPreferences {
	private static final Logger log = Logger.getLogger(FilePreferences.class.getName());

	private Map<String, String> root;
	private Map<String, FilePreferences> children;
	private boolean isRemoved = false;

	public FilePreferences(AbstractPreferences parent, String name) {
		super(parent, name);

		log.finest("Instantiating node " + name);

		this.root = new TreeMap<String, String>();
		this.children = new TreeMap<String, FilePreferences>();

		try {
			sync();
		} catch (BackingStoreException e) {
			log.log(Level.SEVERE, "Unable to sync on creation of node " + name, e);
		}
	}

	@Override
	protected void putSpi(String key, String value) {
		this.root.put(key, value);
		try {
			flush();
		} catch (BackingStoreException e) {
			log.log(Level.SEVERE, "Unable to flush after putting " + key, e);
		}
	}

	@Override
	protected String getSpi(String key) {
		return this.root.get(key);
	}

	@Override
	protected void removeSpi(String key) {
		this.root.remove(key);
		try {
			flush();
		} catch (BackingStoreException e) {
			log.log(Level.SEVERE, "Unable to flush after removing " + key, e);
		}
	}

	@Override
	protected void removeNodeSpi() throws BackingStoreException {
		this.isRemoved = true;
		flush();
	}

	@Override
	protected String[] keysSpi() throws BackingStoreException {
		return this.root.keySet().toArray(new String[this.root.keySet().size()]);
	}

	@Override
	protected String[] childrenNamesSpi() throws BackingStoreException {
		return this.children.keySet().toArray(new String[this.children.keySet().size()]);
	}

	@Override
	protected FilePreferences childSpi(String name) {
		FilePreferences child = this.children.get(name);
		if (child == null || child.isRemoved()) {
			child = new FilePreferences(this, name);
			this.children.put(name, child);
		}
		return child;
	}

	@Override
	protected void syncSpi() throws BackingStoreException {
		if (isRemoved())
			return;

		final File file = FilePreferencesFactory.getPreferencesFile();

		if (!file.exists())
			return;

		synchronized (file) {
			Properties p = new Properties();
			try {
				try (FileInputStream fis = new FileInputStream(file)) {
					p.load(fis);
				}

				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();

				final Enumeration<?> pnen = p.propertyNames();
				while (pnen.hasMoreElements()) {
					String propKey = (String) pnen.nextElement();
					if (propKey.startsWith(path)) {
						String subKey = propKey.substring(path.length());
						// Only load immediate descendants
						if (subKey.indexOf('.') == -1) {
							this.root.put(subKey, p.getProperty(propKey));
						}
					}
				}
			} catch (IOException e) {
				throw new BackingStoreException(e);
			}
		}
	}

	private void getPath(StringBuilder sb) {
		final FilePreferences parent = (FilePreferences) parent();
		if (parent == null)
			return;

		parent.getPath(sb);
		sb.append(name()).append('.');
	}

	@Override
	protected void flushSpi() throws BackingStoreException {
		final File file = FilePreferencesFactory.getPreferencesFile();

		synchronized (file) {
			Properties p = new Properties();
			try {

				StringBuilder sb = new StringBuilder();
				getPath(sb);
				String path = sb.toString();

				if (file.exists()) {
					try (FileInputStream fis = new FileInputStream(file)) {
						p.load(fis);
					}

					List<String> toRemove = new ArrayList<String>();

					// Make a list of all direct children of this node to be
					// removed
					final Enumeration<?> pnen = p.propertyNames();
					while (pnen.hasMoreElements()) {
						String propKey = (String) pnen.nextElement();
						if (propKey.startsWith(path)) {
							String subKey = propKey.substring(path.length());
							// Only do immediate descendants
							if (subKey.indexOf('.') == -1) {
								toRemove.add(propKey);
							}
						}
					}

					// Remove them now that the enumeration is done with
					for (String propKey : toRemove) {
						p.remove(propKey);
					}
				}

				// If this node hasn't been removed, add back in any values
				if (!this.isRemoved) {
					for (String s : this.root.keySet()) {
						p.setProperty(path + s, this.root.get(s));
					}
				}
				try (FileOutputStream fos = new FileOutputStream(file)) {
					p.store(fos, "FilePreferences");
				}
			} catch (IOException e) {
				throw new BackingStoreException(e);
			}
		}
	}
}