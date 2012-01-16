/*
 * EiffelTokenMarker.java - Eiffel token marker
 * Copyright (C) 1999 Slava Pestov
 * Copyright (C) 1999 Artur Biesiadowski
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jeliot_jedit.tokenmarker;

import org.syntax.jeliot_jedit.*;

import javax.swing.text.Segment;

/**
 * Eiffel token Marker.
 *
 * @author Artur Biesiadowski
 */
public class EiffelTokenMarker extends TokenMarker
{

	public EiffelTokenMarker()
	{
		this.keywords = getKeywords();
	}

	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		lastOffset = offset;
		lastKeyword = offset;
		int length = line.count + offset;
		boolean backslash = false;

loop:		for(int i = offset; i < length; i++)
		{
			int i1 = (i+1);

			char c = array[i];
			if(c == '%')
			{
				backslash = !backslash;
				continue;
			}

			switch(token)
			{
			case Token.NULL:
				switch(c)
				{
				case '"':
					doKeyword(line,i,c);
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL_1;
						lastOffset = lastKeyword = i;
					}
					break;
				case '\'':
					doKeyword(line,i,c);
					if(backslash)
						backslash = false;
					else
					{
						addToken(i - lastOffset,token);
						token = Token.LITERAL_2;
						lastOffset = lastKeyword = i;
					}
					break;
				case ':':
					if(lastKeyword == offset)
					{
						if(doKeyword(line,i,c))
							break;
						backslash = false;
						addToken(i1 - lastOffset,Token.LABEL_1);
						lastOffset = lastKeyword = i1;
					}
					else if(doKeyword(line,i,c))
						break;
					break;
				case '-':
					backslash = false;
					doKeyword(line,i,c);
					if(length - i > 1)
					{
						switch(array[i1])
						{
						case '-':
							addToken(i - lastOffset,token);
							addToken(length - i,Token.COMMENT_1);
							lastOffset = lastKeyword = length;
							break loop;
						}
					}
					break;
				default:
					backslash = false;
					if(!Character.isLetterOrDigit(c)
						&& c != '_')
						doKeyword(line,i,c);
					break;
				}
				break;
			case Token.COMMENT_1:
			case Token.COMMENT_2:
				throw new RuntimeException("Wrong eiffel parser state");
			case Token.LITERAL_1:
				if(backslash)
					backslash = false;
				else if(c == '"')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			case Token.LITERAL_2:
				if(backslash)
					backslash = false;
				else if(c == '\'')
				{
					addToken(i1 - lastOffset,Token.LITERAL_1);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			default:
				throw new InternalError("Invalid state: "
					+ token);
			}
		}

		if(token == Token.NULL)
			doKeyword(line,length,'\0');

		switch(token)
		{
		case Token.LITERAL_1:
		case Token.LITERAL_2:
			addToken(length - lastOffset,Token.INVALID_1);
			token = Token.NULL;
			break;
		case Token.KEYWORD_2:
			addToken(length - lastOffset,token);
			if(!backslash)
				token = Token.NULL;
		default:
			addToken(length - lastOffset,token);
			break;
		}

		return token;
	}

	public static KeywordMap getKeywords()
	{
		if(eiffelKeywords == null)
		{
			eiffelKeywords = new KeywordMap(true);
			eiffelKeywords.add("alias", Token.KEYWORD_1);
			eiffelKeywords.add("all", Token.KEYWORD_1);
			eiffelKeywords.add("and", Token.KEYWORD_1);
			eiffelKeywords.add("as", Token.KEYWORD_1);
			eiffelKeywords.add("check", Token.KEYWORD_1);
			eiffelKeywords.add("class", Token.KEYWORD_1);
			eiffelKeywords.add("creation", Token.KEYWORD_1);
			eiffelKeywords.add("debug", Token.KEYWORD_1);
			eiffelKeywords.add("deferred", Token.KEYWORD_1);
			eiffelKeywords.add("do", Token.KEYWORD_1);
			eiffelKeywords.add("else",Token.KEYWORD_1);
			eiffelKeywords.add("elseif", Token.KEYWORD_1);
			eiffelKeywords.add("end", Token.KEYWORD_1);
			eiffelKeywords.add("ensure", Token.KEYWORD_1);
			eiffelKeywords.add("expanded", Token.KEYWORD_1);
			eiffelKeywords.add("export", Token.KEYWORD_1);
			eiffelKeywords.add("external", Token.KEYWORD_1);
			eiffelKeywords.add("feature", Token.KEYWORD_1);
			eiffelKeywords.add("from", Token.KEYWORD_1);
			eiffelKeywords.add("frozen", Token.KEYWORD_1);
			eiffelKeywords.add("if", Token.KEYWORD_1);
			eiffelKeywords.add("implies",Token.KEYWORD_1);
			eiffelKeywords.add("indexing", Token.KEYWORD_1);
			eiffelKeywords.add("infix", Token.KEYWORD_1);
			eiffelKeywords.add("inherit", Token.KEYWORD_1);
			eiffelKeywords.add("inspect", Token.KEYWORD_1);
			eiffelKeywords.add("invariant", Token.KEYWORD_1);
			eiffelKeywords.add("is", Token.KEYWORD_1);
			eiffelKeywords.add("like", Token.KEYWORD_1);
			eiffelKeywords.add("local", Token.KEYWORD_1);
			eiffelKeywords.add("loop", Token.KEYWORD_1);
			eiffelKeywords.add("not", Token.KEYWORD_1);
			eiffelKeywords.add("obsolete", Token.KEYWORD_1);
			eiffelKeywords.add("old",Token.KEYWORD_1);
			eiffelKeywords.add("once", Token.KEYWORD_1);
			eiffelKeywords.add("or", Token.KEYWORD_1);
			eiffelKeywords.add("prefix", Token.KEYWORD_1);
			eiffelKeywords.add("redefine", Token.KEYWORD_1);
			eiffelKeywords.add("rename", Token.KEYWORD_1);
			eiffelKeywords.add("require", Token.KEYWORD_1);
			eiffelKeywords.add("rescue", Token.KEYWORD_1);
			eiffelKeywords.add("retry", Token.KEYWORD_1);
			eiffelKeywords.add("select", Token.KEYWORD_1);
			eiffelKeywords.add("separate", Token.KEYWORD_1);
			eiffelKeywords.add("then",Token.KEYWORD_1);
			eiffelKeywords.add("undefine", Token.KEYWORD_1);
			eiffelKeywords.add("until", Token.KEYWORD_1);
			eiffelKeywords.add("variant", Token.KEYWORD_1);
			eiffelKeywords.add("when", Token.KEYWORD_1);
			eiffelKeywords.add("xor", Token.KEYWORD_1);

			eiffelKeywords.add("current",Token.LITERAL_2);
			eiffelKeywords.add("false",Token.LITERAL_2);
			eiffelKeywords.add("precursor",Token.LITERAL_2);
			eiffelKeywords.add("result",Token.LITERAL_2);
			eiffelKeywords.add("strip",Token.LITERAL_2);
			eiffelKeywords.add("true",Token.LITERAL_2);
			eiffelKeywords.add("unique",Token.LITERAL_2);
			eiffelKeywords.add("void",Token.LITERAL_2);

		}
		return eiffelKeywords;
	}

	// private members
	private static KeywordMap eiffelKeywords;

	private boolean cpp;
	private KeywordMap keywords;
	private int lastOffset;
	private int lastKeyword;

	private boolean doKeyword(Segment line, int i, char c)
	{
		int i1 = i+1;
		boolean klassname = false;

		int len = i - lastKeyword;
		byte id = keywords.lookup(line,lastKeyword,len);
		if ( id == Token.NULL )
		{
			klassname = true;
			for ( int at = lastKeyword; at < lastKeyword + len; at++ )
			{
				char ch = line.array[at];
				if ( ch != '_' && !Character.isUpperCase(ch) )
				{
					klassname = false;
					break;
				}
			}
			if ( klassname )
				id = Token.KEYWORD_3;
		}

		if(id != Token.NULL)
		{
			if(lastKeyword != lastOffset)
				addToken(lastKeyword - lastOffset,Token.NULL);
			addToken(len,id);
			lastOffset = i;
		}
		lastKeyword = i1;
		return false;
	}
}
