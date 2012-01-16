/*
 * JavaScriptTokenMarker.java - JavaScript token marker
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jeliot_jedit.tokenmarker;

import org.syntax.jeliot_jedit.KeywordMap;

/**
 * JavaScript token marker.
 *
 * @author Slava Pestov
 * @version $Id: JavaScriptTokenMarker.java,v 1.1 2005/10/05 12:06:20 jeliot Exp $
 */
public class JavaScriptTokenMarker extends CTokenMarker
{
	public JavaScriptTokenMarker()
	{
		super(false,getKeywords());
	}

	public static KeywordMap getKeywords()
	{
		if(javaScriptKeywords == null)
		{
			javaScriptKeywords = new KeywordMap(false);
			javaScriptKeywords.add("function",Token.KEYWORD_3);
			javaScriptKeywords.add("var",Token.KEYWORD_3);
			javaScriptKeywords.add("else",Token.KEYWORD_1);
			javaScriptKeywords.add("for",Token.KEYWORD_1);
			javaScriptKeywords.add("if",Token.KEYWORD_1);
			javaScriptKeywords.add("in",Token.KEYWORD_1);
			javaScriptKeywords.add("new",Token.KEYWORD_1);
			javaScriptKeywords.add("return",Token.KEYWORD_1);
			javaScriptKeywords.add("while",Token.KEYWORD_1);
			javaScriptKeywords.add("with",Token.KEYWORD_1);
			javaScriptKeywords.add("break",Token.KEYWORD_1);
			javaScriptKeywords.add("case",Token.KEYWORD_1);
			javaScriptKeywords.add("continue",Token.KEYWORD_1);
			javaScriptKeywords.add("default",Token.KEYWORD_1);
			javaScriptKeywords.add("false",Token.LABEL_1);
			javaScriptKeywords.add("this",Token.LABEL_1);
			javaScriptKeywords.add("true",Token.LABEL_1);
		}
		return javaScriptKeywords;
	}

	// private members
	private static KeywordMap javaScriptKeywords;
}
