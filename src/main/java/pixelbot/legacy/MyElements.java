package pixelbot.legacy;

import java.io.*;
import java.util.*;

import pixelbot.elements.*;

public class MyElements implements Serializable {
	static final long serialVersionUID = 67346L;
	private Hashtable<String, pixelbot.elements.ElementDescriptor> ht = new Hashtable<String, pixelbot.elements.ElementDescriptor>();

	Object readResolve() {
		Elements elems = new Elements();
		elems.putAll(this.ht);
		return elems;
	}
}
