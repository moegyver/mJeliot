/*
 * JavaTokenMarker.java - Java token marker
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jeliot_jedit.tokenmarker;

import org.syntax.jeliot_jedit.KeywordMap;

/**
 * Java token marker.
 *
 * @author Slava Pestov
 * @version $Id: JavaTokenMarker.java,v 1.1 2005/10/05 12:06:20 jeliot Exp $
 */
public class JavaTokenMarker extends CTokenMarker
{
	public JavaTokenMarker()
	{
		super(false,getKeywords());
	}

	public static KeywordMap getKeywords()
	{
		if(javaKeywords == null)
		{
			javaKeywords = new KeywordMap(false);
			javaKeywords.add("package",Token.KEYWORD_2);
			javaKeywords.add("import",Token.KEYWORD_2);
			javaKeywords.add("byte",Token.KEYWORD_3);
			javaKeywords.add("char",Token.KEYWORD_3);
			javaKeywords.add("short",Token.KEYWORD_3);
			javaKeywords.add("int",Token.KEYWORD_3);
			javaKeywords.add("long",Token.KEYWORD_3);
			javaKeywords.add("float",Token.KEYWORD_3);
			javaKeywords.add("double",Token.KEYWORD_3);
			javaKeywords.add("boolean",Token.KEYWORD_3);
			javaKeywords.add("void",Token.KEYWORD_3);
			javaKeywords.add("class",Token.KEYWORD_3);
			javaKeywords.add("interface",Token.KEYWORD_3);
			javaKeywords.add("abstract",Token.KEYWORD_1);
			javaKeywords.add("final",Token.KEYWORD_1);
			javaKeywords.add("private",Token.KEYWORD_1);
			javaKeywords.add("protected",Token.KEYWORD_1);
			javaKeywords.add("public",Token.KEYWORD_1);
			javaKeywords.add("static",Token.KEYWORD_1);
			javaKeywords.add("synchronized",Token.KEYWORD_1);
			javaKeywords.add("native",Token.KEYWORD_1);
			javaKeywords.add("volatile",Token.KEYWORD_1);
			javaKeywords.add("transient",Token.KEYWORD_1);
			javaKeywords.add("break",Token.KEYWORD_1);
			javaKeywords.add("case",Token.KEYWORD_1);
			javaKeywords.add("continue",Token.KEYWORD_1);
			javaKeywords.add("default",Token.KEYWORD_1);
			javaKeywords.add("do",Token.KEYWORD_1);
			javaKeywords.add("else",Token.KEYWORD_1);
			javaKeywords.add("for",Token.KEYWORD_1);
			javaKeywords.add("if",Token.KEYWORD_1);
			javaKeywords.add("instanceof",Token.KEYWORD_1);
			javaKeywords.add("new",Token.KEYWORD_1);
			javaKeywords.add("return",Token.KEYWORD_1);
			javaKeywords.add("switch",Token.KEYWORD_1);
			javaKeywords.add("while",Token.KEYWORD_1);
			javaKeywords.add("throw",Token.KEYWORD_1);
			javaKeywords.add("try",Token.KEYWORD_1);
			javaKeywords.add("catch",Token.KEYWORD_1);
			javaKeywords.add("extends",Token.KEYWORD_1);
			javaKeywords.add("finally",Token.KEYWORD_1);
			javaKeywords.add("implements",Token.KEYWORD_1);
			javaKeywords.add("throws",Token.KEYWORD_1);
			javaKeywords.add("this",Token.LITERAL_2);
			javaKeywords.add("null",Token.LITERAL_2);
			javaKeywords.add("super",Token.LITERAL_2);
			javaKeywords.add("true",Token.LITERAL_2);
			javaKeywords.add("false",Token.LITERAL_2);
		}
		return javaKeywords;
	}

	// private members
	private static KeywordMap javaKeywords;
}
