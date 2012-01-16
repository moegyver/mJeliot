/*
 * CCTokenMarker.java - C++ token marker
 * Copyright (C) 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jeliot_jedit.tokenmarker;

import org.syntax.jeliot_jedit.KeywordMap;

/**
 * C++ token marker.
 *
 * @author Slava Pestov
 * @version $Id: CCTokenMarker.java,v 1.1 2005/10/05 12:06:21 jeliot Exp $
 */
public class CCTokenMarker extends CTokenMarker
{
	public CCTokenMarker()
	{
		super(true,getKeywords());
	}

	public static KeywordMap getKeywords()
	{
		if(ccKeywords == null)
		{
			ccKeywords = new KeywordMap(false);

			ccKeywords.add("and", Token.KEYWORD_3);
			ccKeywords.add("and_eq", Token.KEYWORD_3);
			ccKeywords.add("asm", Token.KEYWORD_2);         //
			ccKeywords.add("auto", Token.KEYWORD_1);        //
			ccKeywords.add("bitand", Token.KEYWORD_3);
			ccKeywords.add("bitor", Token.KEYWORD_3);
			ccKeywords.add("bool",Token.KEYWORD_3);
			ccKeywords.add("break", Token.KEYWORD_1);	   //
			ccKeywords.add("case", Token.KEYWORD_1);		   //
			ccKeywords.add("catch", Token.KEYWORD_1);
			ccKeywords.add("char", Token.KEYWORD_3);		   //
			ccKeywords.add("class", Token.KEYWORD_3);
			ccKeywords.add("compl", Token.KEYWORD_3);
			ccKeywords.add("const", Token.KEYWORD_1);	   //
			ccKeywords.add("const_cast", Token.KEYWORD_3);
			ccKeywords.add("continue", Token.KEYWORD_1);	   //
			ccKeywords.add("default", Token.KEYWORD_1);	   //
			ccKeywords.add("delete", Token.KEYWORD_1);
			ccKeywords.add("do",Token.KEYWORD_1);           //
			ccKeywords.add("double" ,Token.KEYWORD_3);	   //
			ccKeywords.add("dynamic_cast", Token.KEYWORD_3);
			ccKeywords.add("else", 	Token.KEYWORD_1);	   //
			ccKeywords.add("enum",  Token.KEYWORD_3);	   //
			ccKeywords.add("explicit", Token.KEYWORD_1);			
			ccKeywords.add("export", Token.KEYWORD_2);
			ccKeywords.add("extern", Token.KEYWORD_2);	   //
			ccKeywords.add("false", Token.LITERAL_2);
			ccKeywords.add("float", Token.KEYWORD_3);	   //
			ccKeywords.add("for", Token.KEYWORD_1);		   //
			ccKeywords.add("friend", Token.KEYWORD_1);			
			ccKeywords.add("goto", Token.KEYWORD_1);        //
			ccKeywords.add("if", Token.KEYWORD_1);		   //
			ccKeywords.add("inline", Token.KEYWORD_1);
			ccKeywords.add("int", Token.KEYWORD_3);		   //
			ccKeywords.add("long", Token.KEYWORD_3);		   //
			ccKeywords.add("mutable", Token.KEYWORD_3);
			ccKeywords.add("namespace", Token.KEYWORD_2);
			ccKeywords.add("new", Token.KEYWORD_1);
			ccKeywords.add("not", Token.KEYWORD_3);
			ccKeywords.add("not_eq", Token.KEYWORD_3);
			ccKeywords.add("operator", Token.KEYWORD_3);
			ccKeywords.add("or", Token.KEYWORD_3);
			ccKeywords.add("or_eq", Token.KEYWORD_3);
			ccKeywords.add("private", Token.KEYWORD_1);
			ccKeywords.add("protected", Token.KEYWORD_1);
			ccKeywords.add("public", Token.KEYWORD_1);
			ccKeywords.add("register", Token.KEYWORD_1);
			ccKeywords.add("reinterpret_cast", Token.KEYWORD_3);
			ccKeywords.add("return", Token.KEYWORD_1);      //
			ccKeywords.add("short", Token.KEYWORD_3);	   //
			ccKeywords.add("signed", Token.KEYWORD_3);	   //
			ccKeywords.add("sizeof", Token.KEYWORD_1);	   //
			ccKeywords.add("static", Token.KEYWORD_1);	   //
			ccKeywords.add("static_cast", Token.KEYWORD_3);
			ccKeywords.add("struct", Token.KEYWORD_3);	   //
			ccKeywords.add("switch", Token.KEYWORD_1);	   //
			ccKeywords.add("template", Token.KEYWORD_3);
			ccKeywords.add("this", Token.LITERAL_2);
			ccKeywords.add("throw", Token.KEYWORD_1);
			ccKeywords.add("true", Token.LITERAL_2);
			ccKeywords.add("try", Token.KEYWORD_1);
			ccKeywords.add("typedef", Token.KEYWORD_3);	   //
			ccKeywords.add("typeid", Token.KEYWORD_3);
			ccKeywords.add("typename", Token.KEYWORD_3);
			ccKeywords.add("union", Token.KEYWORD_3);	   //
			ccKeywords.add("unsigned", Token.KEYWORD_3);	   //
			ccKeywords.add("using", Token.KEYWORD_2);
			ccKeywords.add("virtual", Token.KEYWORD_1);
			ccKeywords.add("void", Token.KEYWORD_1);		   //
			ccKeywords.add("volatile", Token.KEYWORD_1);	   //
			ccKeywords.add("wchar_t", Token.KEYWORD_3);
			ccKeywords.add("while", Token.KEYWORD_1);	   //
			ccKeywords.add("xor", Token.KEYWORD_3);
			ccKeywords.add("xor_eq", Token.KEYWORD_3);            

			// non ANSI keywords
			ccKeywords.add("NULL", Token.LITERAL_2);
		}
		return ccKeywords;
	}

	// private members
	private static KeywordMap ccKeywords;
}
