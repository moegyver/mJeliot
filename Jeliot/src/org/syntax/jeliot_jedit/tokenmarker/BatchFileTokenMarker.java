/*
 * BatchFileTokenMarker.java - Batch file token marker
 * Copyright (C) 1998, 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jeliot_jedit.tokenmarker;

import org.syntax.jeliot_jedit.*;

import javax.swing.text.Segment;

/**
 * Batch file token marker.
 *
 * @author Slava Pestov
 * @version $Id: BatchFileTokenMarker.java,v 1.1 2005/10/05 12:06:20 jeliot Exp $
 */
public class BatchFileTokenMarker extends TokenMarker
{
	public byte markTokensImpl(byte token, Segment line, int lineIndex)
	{
		char[] array = line.array;
		int offset = line.offset;
		int lastOffset = offset;
		int length = line.count + offset;

		if(SyntaxUtilities.regionMatches(true,line,offset,"rem"))
		{
			addToken(line.count,Token.COMMENT_1);
			return Token.NULL;
		}

loop:		for(int i = offset; i < length; i++)
		{
			int i1 = (i+1);

			switch(token)
			{
			case Token.NULL:
				switch(array[i])
				{
				case '%':
					addToken(i - lastOffset,token);
					lastOffset = i;
					if(length - i <= 3 || array[i+2] == ' ')
					{
						addToken(2,Token.KEYWORD_2);
						i += 2;
						lastOffset = i;
					}
					else
						token = Token.KEYWORD_2;
					break;
				case '"':
					addToken(i - lastOffset,token);
					token = Token.LITERAL_1;
					lastOffset = i;
					break;
				case ':':
					if(i == offset)
					{
						addToken(line.count,Token.LABEL_1);
						lastOffset = length;
						break loop;
					}
					break;
				case ' ':
					if(lastOffset == offset)
					{
						addToken(i - lastOffset,Token.KEYWORD_1);
						lastOffset = i;
					}
					break;
				}
				break;
			case Token.KEYWORD_2:
				if(array[i] == '%')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = i1;
				}
				break;
			case Token.LITERAL_1:
				if(array[i] == '"')
				{
					addToken(i1 - lastOffset,token);
					token = Token.NULL;
					lastOffset = i1;
				}
				break;
			default:
				throw new InternalError("Invalid state: " + token);
			}
		}

		if(lastOffset != length)
		{
			if(token != Token.NULL)
				token = Token.INVALID_1;
			else if(lastOffset == offset)
				token = Token.KEYWORD_1;
			addToken(length - lastOffset,token);
		}
		return Token.NULL;
	}

	public boolean supportsMultilineTokens()
	{
		return false;
	}
}
