package org.mJeliot.helpers;

public final class StringFunctions {

	public static String escape(String unescapedString) {
		return unescapedString.replace("&", "&amp;").replace("\n", "\\n").replace(">", "&gt;").replace("<", "&lt;");
	}
	public static String unescape(String escapedString) {
		return escapedString.replace("&gt;", ">").replace("&lt;","<").replace("\\n", "\n").replace("&amp;", "&");
	}
}
