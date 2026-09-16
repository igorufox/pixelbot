package pixelbot.components.table;

import java.io.Serializable;
import java.net.*;

import pixelbot.misc.Tools;

public class Link implements Serializable {
	private static final long serialVersionUID = 3413970952090709565L;

	private URI uri;
	private String text;

	private Link(URI uri, String text) {
		if (uri == null) {
			throw new NullPointerException();
		}
		if (text == null)
			text = "";
		this.uri = uri;
		this.text = text;
	}

	public Link(URL url, String text) {
		this(Link.toUri(url), text);
	}

	private static URI toUri(URL url) {
		try {
			return Link.normalizeURL(url).toURI();
		} catch (Exception ex) {}
		return null;
	}

	public static URL normalizeURL(URL url) {
		try {
			StringBuffer result = new StringBuffer();
			result.append(url.getProtocol());
			result.append(":");
			if (url.getAuthority() != null && url.getAuthority().length() > 0) {
				result.append("//");
				result.append(url.getAuthority());
			}
			if (url.getPath() != null) {
				result.append(url.getPath());
			}
			if (url.getQuery() != null) {
				result.append('?');

				result.append(Tools.generateQueryString(Tools.parseQueryString(url.getQuery())));

			}
			if (url.getRef() != null) {
				result.append("#");
				result.append(url.getRef());
			}
			return new URL(result.toString());

		} catch (Exception e) {}
		return null;
	}

	public URI getURI() {
		return this.uri;
	}

	@Override
	public String toString() {
		return this.text;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Link) {
			Link other_link = (Link) other;
			return this.uri.equals(other_link.uri) && this.text.equals(other_link.text);
		} else {
			return super.equals(other);
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
