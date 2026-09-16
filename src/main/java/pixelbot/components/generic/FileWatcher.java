package pixelbot.components.generic;

import java.io.*;
import java.util.*;

import javax.swing.event.*;

public class FileWatcher extends TimerTask {
	public interface FileModificationEventListener extends EventListener {
		public void onFileModification(FileModificationEvent event);
	}

	public class FileModificationEvent {
		public File file;

		public FileModificationEvent(File value) {
			this.file = value;
		}
	}

	private class Watch {
		protected Watch(File file) {
			this.file = file;
			this.timeStamp = this.file.lastModified();
		}

		protected File file;
		protected long timeStamp;
	}

	private List<Watch> watches = new ArrayList<FileWatcher.Watch>();
	private Timer timer = null;
	protected EventListenerList listenerList = new EventListenerList();

	public FileWatcher() {}

	public void addWatch(File file) {
		synchronized (this.watches) {
			this.watches.add(new Watch(file));

			if (this.timer == null) {
				// repeat the check every second
				try {
					this.timer = new Timer("FileWatcher", true);
					this.timer.schedule(this, new Date(), 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void removeWatch(File file) {
		synchronized (this.watches) {
			for (Watch w : this.watches) {
				if (w.file.equals(file)) {
					this.watches.remove(w);
					break;
				}
			}
//			if (this.watches.size() == 0 && this.timer != null) {
//				this.timer.cancel();
//				this.timer = null;
//			}
		}
	}

	@Override
	public final void run() {
		synchronized (this.watches) {
			for (Watch w : this.watches) {
				long timeStamp = w.file.lastModified();

				if (w.timeStamp != timeStamp) {
					w.timeStamp = timeStamp;
					this.fireFileModificationEvent(w.file);
				}
			}
		}
	}

	public void stop() {
		this.timer.cancel();
	}

	public void addFileModificationEvent(FileModificationEventListener e) {
		this.listenerList.add(FileModificationEventListener.class, e);
	}

	public void removeFileModificationEvent(FileModificationEventListener e) {
		this.listenerList.remove(FileModificationEventListener.class, e);
	}

	public void fireFileModificationEvent(File file) {
		Object[] listeners = this.listenerList.getListenerList();
		FileModificationEvent e = new FileModificationEvent(file);
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == FileModificationEventListener.class) {
				FileModificationEventListener listener = ((FileModificationEventListener) listeners[i + 1]);
				listener.onFileModification(e);
			}
		}
	}
}
