/*
 * IDLTokenMarker.java - IDL token marker
 * Copyright (C) 1999 Slava Pestov
 * Copyright (C) 1999 Juha Lindfors
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jeliot_jedit.tokenmarker;

import org.syntax.jeliot_jedit.KeywordMap;

/**
 * IDL token marker.
 *
 * @author Slava Pestov
 * @author Juha Lindfors
 * @version $Id: IDLTokenMarker.java,v 1.1 2005/10/05 12:06:21 jeliot Exp $
 */
public class IDLTokenMarker extends CTokenMarker
{
	public IDLTokenMarker()
	{
		super(true,getKeywords());
	}

	public static KeywordMap getKeywords()
	{
		if(idlKeywords == null)
		{
			idlKeywords = new KeywordMap(false);

			idlKeywords.add("any",      Token.KEYWORD_3);
			idlKeywords.add("attribute",Token.KEYWORD_1);
			idlKeywords.add("boolean",  Token.KEYWORD_3);
			idlKeywords.add("case",     Token.KEYWORD_1);
			idlKeywords.add("char",     Token.KEYWORD_3);
			idlKeywords.add("const",    Token.KEYWORD_1);
			idlKeywords.add("context",  Token.KEYWORD_1);
			idlKeywords.add("default",  Token.KEYWORD_1);
			idlKeywords.add("double",   Token.KEYWORD_3);
			idlKeywords.add("enum",     Token.KEYWORD_3);
			idlKeywords.add("exception",Token.KEYWORD_1);
			idlKeywords.add("FALSE",    Token.LITERAL_2);
			idlKeywords.add("fixed",    Token.KEYWORD_1);
			idlKeywords.add("float",    Token.KEYWORD_3);
			idlKeywords.add("in",       Token.KEYWORD_1);
			idlKeywords.add("inout",    Token.KEYWORD_1);
			idlKeywords.add("interface",Token.KEYWORD_1);
			idlKeywords.add("long",     Token.KEYWORD_3);
			idlKeywords.add("module",   Token.KEYWORD_1);
			idlKeywords.add("Object",   Token.KEYWORD_3);
			idlKeywords.add("octet",    Token.KEYWORD_3);
			idlKeywords.add("oneway",   Token.KEYWORD_1);
			idlKeywords.add("out",      Token.KEYWORD_1);
			idlKeywords.add("raises",   Token.KEYWORD_1);
			idlKeywords.add("readonly", Token.KEYWORD_1);
			idlKeywords.add("sequence", Token.KEYWORD_3);
			idlKeywords.add("short",    Token.KEYWORD_3);
			idlKeywords.add("string",   Token.KEYWORD_3);
			idlKeywords.add("struct",   Token.KEYWORD_3);
			idlKeywords.add("switch",   Token.KEYWORD_1);
			idlKeywords.add("TRUE",     Token.LITERAL_2);
			idlKeywords.add("typedef",  Token.KEYWORD_3);
			idlKeywords.add("unsigned", Token.KEYWORD_3);
			idlKeywords.add("union",    Token.KEYWORD_3);
			idlKeywords.add("void",     Token.KEYWORD_3);
			idlKeywords.add("wchar",    Token.KEYWORD_3);
			idlKeywords.add("wstring",  Token.KEYWORD_3);
		}
		return idlKeywords;
	}

	// private members
	private static KeywordMap idlKeywords;
}
