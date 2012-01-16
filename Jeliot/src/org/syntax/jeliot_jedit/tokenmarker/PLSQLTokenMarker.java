/*
 * PLSQLTokenMarker.java - Oracle PL/SQL token marker
 * Copyright (C) 2002 Oliver Henning
 * 
 * adapted from:
 * plsqlTokenMarker.java - Transact-SQL token marker
 * Copyright (C) 1999 mike dillon
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

package org.syntax.jeliot_jedit.tokenmarker;

import org.syntax.jeliot_jedit.KeywordMap;

/**
 * Oracle PL-SQL token marker.
 *
 * @author oliver henning
 * @version $Id: PLSQLTokenMarker.java,v 1.1 2005/10/05 12:06:20 jeliot Exp $
 */
public class PLSQLTokenMarker extends SQLTokenMarker
{
	// public members
	public PLSQLTokenMarker()
	{
		super(getKeywordMap(), true);
	}

	public static KeywordMap getKeywordMap()
	{
		if (plsqlKeywords == null) {
			plsqlKeywords = new KeywordMap(true);
			addKeywords();
			addDataTypes();
			addSystemFunctions();
			addOperators();
	  		addSystemStoredProcedures();
			addSystemTables();
		}
		return plsqlKeywords;
	}	

	private static void addKeywords()
	{
		plsqlKeywords.add("ABORT",Token.KEYWORD_1);
		plsqlKeywords.add("ACCESS",Token.KEYWORD_1);
		plsqlKeywords.add("ADD",Token.KEYWORD_1);
		plsqlKeywords.add("ALTER",Token.KEYWORD_1);
		plsqlKeywords.add("ARRAY",Token.KEYWORD_1);
		plsqlKeywords.add("ARRAY_LEN",Token.KEYWORD_1);
		plsqlKeywords.add("AS",Token.KEYWORD_1);
		plsqlKeywords.add("ASC",Token.KEYWORD_1);
		plsqlKeywords.add("ASSERT",Token.KEYWORD_1);
		plsqlKeywords.add("ASSIGN",Token.KEYWORD_1);
		plsqlKeywords.add("AT",Token.KEYWORD_1);
		plsqlKeywords.add("AUDIT",Token.KEYWORD_1);
		plsqlKeywords.add("AUTHORIZATION",Token.KEYWORD_1);
		plsqlKeywords.add("AVG",Token.KEYWORD_1);
		plsqlKeywords.add("BASE_TABLE",Token.KEYWORD_1);
		plsqlKeywords.add("BEGIN",Token.KEYWORD_1);
		plsqlKeywords.add("BODY",Token.KEYWORD_1);
		plsqlKeywords.add("CASE",Token.KEYWORD_1);
		plsqlKeywords.add("CHAR",Token.KEYWORD_1);
		plsqlKeywords.add("CHAR_BASE",Token.KEYWORD_1);
		plsqlKeywords.add("CHECK",Token.KEYWORD_1);
		plsqlKeywords.add("CLOSE",Token.KEYWORD_1);
		plsqlKeywords.add("CLUSTER",Token.KEYWORD_1);
		plsqlKeywords.add("CLUSTERS",Token.KEYWORD_1);
		plsqlKeywords.add("COLAUTH",Token.KEYWORD_1);
		plsqlKeywords.add("COLUMN",Token.KEYWORD_1);
		plsqlKeywords.add("COMMENT",Token.KEYWORD_1);
		plsqlKeywords.add("COMMIT",Token.KEYWORD_1);
		plsqlKeywords.add("COMPRESS",Token.KEYWORD_1);
		plsqlKeywords.add("CONSTANT",Token.KEYWORD_1);
		plsqlKeywords.add("CONSTRAINT",Token.KEYWORD_1);
		plsqlKeywords.add("COUNT",Token.KEYWORD_1);
		plsqlKeywords.add("CREATE",Token.KEYWORD_1);
		plsqlKeywords.add("CURRENT",Token.KEYWORD_1);
		plsqlKeywords.add("CURRVAL",Token.KEYWORD_1);
		plsqlKeywords.add("CURSOR",Token.KEYWORD_1);
		plsqlKeywords.add("DATABASE",Token.KEYWORD_1);
		plsqlKeywords.add("DATA_BASE",Token.KEYWORD_1);
		plsqlKeywords.add("DATE",Token.KEYWORD_1);
		plsqlKeywords.add("DBA",Token.KEYWORD_1);
		plsqlKeywords.add("DEBUGOFF",Token.KEYWORD_1);
		plsqlKeywords.add("DEBUGON",Token.KEYWORD_1);
		plsqlKeywords.add("DECLARE",Token.KEYWORD_1);
		plsqlKeywords.add("DEFAULT",Token.KEYWORD_1);
		plsqlKeywords.add("DEFINITION",Token.KEYWORD_1);
		plsqlKeywords.add("DELAY",Token.KEYWORD_1);
		plsqlKeywords.add("DELETE",Token.KEYWORD_1);
		plsqlKeywords.add("DESC",Token.KEYWORD_1);
		plsqlKeywords.add("DIGITS",Token.KEYWORD_1);
		plsqlKeywords.add("DISPOSE",Token.KEYWORD_1);
		plsqlKeywords.add("DISTINCT",Token.KEYWORD_1);
		plsqlKeywords.add("DO",Token.KEYWORD_1);
		plsqlKeywords.add("DROP",Token.KEYWORD_1);
		plsqlKeywords.add("DUMP",Token.KEYWORD_1);
		plsqlKeywords.add("ELSE",Token.KEYWORD_1);
		plsqlKeywords.add("ELSIF",Token.KEYWORD_1);
		plsqlKeywords.add("END",Token.KEYWORD_1);
		plsqlKeywords.add("ENTRY",Token.KEYWORD_1);
		plsqlKeywords.add("EXCEPTION",Token.KEYWORD_1);
		plsqlKeywords.add("EXCEPTION_INIT",Token.KEYWORD_1);
		plsqlKeywords.add("EXCLUSIVE",Token.KEYWORD_1);
		plsqlKeywords.add("EXIT",Token.KEYWORD_1);
		plsqlKeywords.add("FALSE",Token.KEYWORD_1);
		plsqlKeywords.add("FETCH",Token.KEYWORD_1);
		plsqlKeywords.add("FILE",Token.KEYWORD_1);
		plsqlKeywords.add("FOR",Token.KEYWORD_1);
		plsqlKeywords.add("FORM",Token.KEYWORD_1);
		plsqlKeywords.add("FROM",Token.KEYWORD_1);
		plsqlKeywords.add("FUNCTION",Token.KEYWORD_1);
		plsqlKeywords.add("GENERIC",Token.KEYWORD_1);
		plsqlKeywords.add("GOTO",Token.KEYWORD_1);
		plsqlKeywords.add("GRANT",Token.KEYWORD_1);
		plsqlKeywords.add("GREATEST",Token.KEYWORD_1);
		plsqlKeywords.add("GROUP",Token.KEYWORD_1);
		plsqlKeywords.add("HAVING",Token.KEYWORD_1);
		plsqlKeywords.add("IDENTIFIED",Token.KEYWORD_1);
		plsqlKeywords.add("IDENTITYCOL",Token.KEYWORD_1);
		plsqlKeywords.add("IF",Token.KEYWORD_1);
		plsqlKeywords.add("IMMEDIATE",Token.KEYWORD_1);
		plsqlKeywords.add("INCREMENT",Token.KEYWORD_1);
		plsqlKeywords.add("INDEX",Token.KEYWORD_1);
		plsqlKeywords.add("INDEXES",Token.KEYWORD_1);
		plsqlKeywords.add("INDICATOR",Token.KEYWORD_1);
		plsqlKeywords.add("INITIAL",Token.KEYWORD_1);
		plsqlKeywords.add("INSERT",Token.KEYWORD_1);
		plsqlKeywords.add("INTERFACE",Token.KEYWORD_1);
		plsqlKeywords.add("INTO",Token.KEYWORD_1);
		plsqlKeywords.add("IS",Token.KEYWORD_1);
		plsqlKeywords.add("LEAST",Token.KEYWORD_1);
		plsqlKeywords.add("LEVEL",Token.KEYWORD_1);
		plsqlKeywords.add("LIMITED",Token.KEYWORD_1);
		plsqlKeywords.add("LOCK",Token.KEYWORD_1);
		plsqlKeywords.add("LONG",Token.KEYWORD_1);
		plsqlKeywords.add("LOOP",Token.KEYWORD_1);
		plsqlKeywords.add("MAX",Token.KEYWORD_1);
		plsqlKeywords.add("MAXEXTENTS",Token.KEYWORD_1);
		plsqlKeywords.add("MIN",Token.KEYWORD_1);
		plsqlKeywords.add("MINUS",Token.KEYWORD_1);
		plsqlKeywords.add("MLSLABEL",Token.KEYWORD_1);
		plsqlKeywords.add("MOD",Token.KEYWORD_1);
		plsqlKeywords.add("MORE",Token.KEYWORD_1);
		plsqlKeywords.add("NEW",Token.KEYWORD_1);
		plsqlKeywords.add("NEXTVAL",Token.KEYWORD_1);
		plsqlKeywords.add("NOAUDIT",Token.KEYWORD_1);
		plsqlKeywords.add("NOCOMPRESS",Token.KEYWORD_1);
		plsqlKeywords.add("NOWAIT",Token.KEYWORD_1);
		plsqlKeywords.add("NULL",Token.KEYWORD_1);
		plsqlKeywords.add("NUMBER_BASE",Token.KEYWORD_1);
		plsqlKeywords.add("OF",Token.KEYWORD_1);
		plsqlKeywords.add("OFFLINE",Token.KEYWORD_1);
		plsqlKeywords.add("ON",Token.KEYWORD_1);
		plsqlKeywords.add("OFF",Token.KEYWORD_1);
		plsqlKeywords.add("ONLINE",Token.KEYWORD_1);
		plsqlKeywords.add("OPEN",Token.KEYWORD_1);
		plsqlKeywords.add("OPTION",Token.KEYWORD_1);
		plsqlKeywords.add("ORDER",Token.KEYWORD_1);
		plsqlKeywords.add("OTHERS",Token.KEYWORD_1);
		plsqlKeywords.add("OUT",Token.KEYWORD_1);
		plsqlKeywords.add("PACKAGE",Token.KEYWORD_1);
		plsqlKeywords.add("PARTITION",Token.KEYWORD_1);
		plsqlKeywords.add("PCTFREE",Token.KEYWORD_1);
		plsqlKeywords.add("PRAGMA",Token.KEYWORD_1);
		plsqlKeywords.add("PRIVATE",Token.KEYWORD_1);
		plsqlKeywords.add("PRIVILEGES",Token.KEYWORD_1);
		plsqlKeywords.add("PROCEDURE",Token.KEYWORD_1);
		plsqlKeywords.add("PUBLIC",Token.KEYWORD_1);
		plsqlKeywords.add("QUOTED_IDENTIFIER",Token.KEYWORD_1);
		plsqlKeywords.add("RAISE",Token.KEYWORD_1);
		plsqlKeywords.add("RANGE",Token.KEYWORD_1);
		plsqlKeywords.add("RECORD",Token.KEYWORD_1);
		plsqlKeywords.add("REF",Token.KEYWORD_1);
		plsqlKeywords.add("RELEASE",Token.KEYWORD_1);
		plsqlKeywords.add("REMR",Token.KEYWORD_1);
		plsqlKeywords.add("RENAME",Token.KEYWORD_1);
		plsqlKeywords.add("RESOURCE",Token.KEYWORD_1);
		plsqlKeywords.add("RETURN",Token.KEYWORD_1);
		plsqlKeywords.add("REVERSE",Token.KEYWORD_1);
		plsqlKeywords.add("REVOKE",Token.KEYWORD_1);
		plsqlKeywords.add("ROLLBACK",Token.KEYWORD_1);
		plsqlKeywords.add("ROW",Token.KEYWORD_1);
		plsqlKeywords.add("ROWLABEL",Token.KEYWORD_1);
		plsqlKeywords.add("ROWNUM",Token.KEYWORD_1);
		plsqlKeywords.add("ROWS",Token.KEYWORD_1);
		plsqlKeywords.add("ROWTYPE",Token.KEYWORD_1);
		plsqlKeywords.add("RUN",Token.KEYWORD_1);
		plsqlKeywords.add("SAVEPOINT",Token.KEYWORD_1);
		plsqlKeywords.add("SCHEMA",Token.KEYWORD_1);
		plsqlKeywords.add("SELECT",Token.KEYWORD_1);
		plsqlKeywords.add("SEPERATE",Token.KEYWORD_1);
		plsqlKeywords.add("SESSION",Token.KEYWORD_1);
		plsqlKeywords.add("SET",Token.KEYWORD_1);
		plsqlKeywords.add("SHARE",Token.KEYWORD_1);
		plsqlKeywords.add("SPACE",Token.KEYWORD_1);
		plsqlKeywords.add("SQL",Token.KEYWORD_1);
		plsqlKeywords.add("SQLCODE",Token.KEYWORD_1);
		plsqlKeywords.add("SQLERRM",Token.KEYWORD_1);
		plsqlKeywords.add("STATEMENT",Token.KEYWORD_1);
		plsqlKeywords.add("STDDEV",Token.KEYWORD_1);
		plsqlKeywords.add("SUBTYPE",Token.KEYWORD_1);
		plsqlKeywords.add("SUCCESSFULL",Token.KEYWORD_1);
		plsqlKeywords.add("SUM",Token.KEYWORD_1);
		plsqlKeywords.add("SYNONYM",Token.KEYWORD_1);
		plsqlKeywords.add("SYSDATE",Token.KEYWORD_1);
		plsqlKeywords.add("TABAUTH",Token.KEYWORD_1);
		plsqlKeywords.add("TABLE",Token.KEYWORD_1);
		plsqlKeywords.add("TABLES",Token.KEYWORD_1);
		plsqlKeywords.add("TASK",Token.KEYWORD_1);
		plsqlKeywords.add("TERMINATE",Token.KEYWORD_1);
		plsqlKeywords.add("THEN",Token.KEYWORD_1);
		plsqlKeywords.add("TO",Token.KEYWORD_1);
		plsqlKeywords.add("TRIGGER",Token.KEYWORD_1);
		plsqlKeywords.add("TRUE",Token.KEYWORD_1);
		plsqlKeywords.add("TYPE",Token.KEYWORD_1);
		plsqlKeywords.add("UID",Token.KEYWORD_1);
		plsqlKeywords.add("UNION",Token.KEYWORD_1);
		plsqlKeywords.add("UNIQUE",Token.KEYWORD_1);
		plsqlKeywords.add("UPDATE",Token.KEYWORD_1);
		plsqlKeywords.add("UPDATETEXT",Token.KEYWORD_1);
		plsqlKeywords.add("USE",Token.KEYWORD_1);
		plsqlKeywords.add("USER",Token.KEYWORD_1);
		plsqlKeywords.add("USING",Token.KEYWORD_1);
		plsqlKeywords.add("VALIDATE",Token.KEYWORD_1);
		plsqlKeywords.add("VALUES",Token.KEYWORD_1);
		plsqlKeywords.add("VARIANCE",Token.KEYWORD_1);
		plsqlKeywords.add("VIEW",Token.KEYWORD_1);
		plsqlKeywords.add("VIEWS",Token.KEYWORD_1);
		plsqlKeywords.add("WHEN",Token.KEYWORD_1);
		plsqlKeywords.add("WHENEVER",Token.KEYWORD_1);
		plsqlKeywords.add("WHERE",Token.KEYWORD_1);
		plsqlKeywords.add("WHILE",Token.KEYWORD_1);
		plsqlKeywords.add("WITH",Token.KEYWORD_1);
		plsqlKeywords.add("WORK",Token.KEYWORD_1);
		plsqlKeywords.add("WRITE",Token.KEYWORD_1);
		plsqlKeywords.add("XOR",Token.KEYWORD_1);
		
		plsqlKeywords.add("ABS",Token.KEYWORD_2);
		plsqlKeywords.add("ACOS",Token.KEYWORD_2);
		plsqlKeywords.add("ADD_MONTHS",Token.KEYWORD_2);
		plsqlKeywords.add("ASCII",Token.KEYWORD_2);
		plsqlKeywords.add("ASIN",Token.KEYWORD_2);
		plsqlKeywords.add("ATAN",Token.KEYWORD_2);
		plsqlKeywords.add("ATAN2",Token.KEYWORD_2);
		plsqlKeywords.add("CEIL",Token.KEYWORD_2);
		plsqlKeywords.add("CHARTOROWID",Token.KEYWORD_2);
		plsqlKeywords.add("CHR",Token.KEYWORD_2);
		plsqlKeywords.add("CONCAT",Token.KEYWORD_2);
		plsqlKeywords.add("CONVERT",Token.KEYWORD_2);
		plsqlKeywords.add("COS",Token.KEYWORD_2);
		plsqlKeywords.add("COSH",Token.KEYWORD_2);
		plsqlKeywords.add("DECODE",Token.KEYWORD_2);
		plsqlKeywords.add("DEFINE",Token.KEYWORD_2);
		plsqlKeywords.add("FLOOR",Token.KEYWORD_2);
		plsqlKeywords.add("HEXTORAW",Token.KEYWORD_2);
		plsqlKeywords.add("INITCAP",Token.KEYWORD_2);
		plsqlKeywords.add("INSTR",Token.KEYWORD_2);
		plsqlKeywords.add("INSTRB",Token.KEYWORD_2);
		plsqlKeywords.add("LAST_DAY",Token.KEYWORD_2);
		plsqlKeywords.add("LENGTH",Token.KEYWORD_2);
		plsqlKeywords.add("LENGTHB",Token.KEYWORD_2);
		plsqlKeywords.add("LN",Token.KEYWORD_2);
		plsqlKeywords.add("LOG",Token.KEYWORD_2);
		plsqlKeywords.add("LOWER",Token.KEYWORD_2);
		plsqlKeywords.add("LPAD",Token.KEYWORD_2);
		plsqlKeywords.add("LTRIM",Token.KEYWORD_2);
		plsqlKeywords.add("MOD",Token.KEYWORD_2);
		plsqlKeywords.add("MONTHS_BETWEEN",Token.KEYWORD_2);
		plsqlKeywords.add("NEW_TIME",Token.KEYWORD_2);
		plsqlKeywords.add("NEXT_DAY",Token.KEYWORD_2);
		plsqlKeywords.add("NLSSORT",Token.KEYWORD_2);
		plsqlKeywords.add("NSL_INITCAP",Token.KEYWORD_2);
		plsqlKeywords.add("NLS_LOWER",Token.KEYWORD_2);
		plsqlKeywords.add("NLS_UPPER",Token.KEYWORD_2);
		plsqlKeywords.add("NVL",Token.KEYWORD_2);
		plsqlKeywords.add("POWER",Token.KEYWORD_2);
		plsqlKeywords.add("RAWTOHEX",Token.KEYWORD_2);
		plsqlKeywords.add("REPLACE",Token.KEYWORD_2);
		plsqlKeywords.add("ROUND",Token.KEYWORD_2);
		plsqlKeywords.add("ROWIDTOCHAR",Token.KEYWORD_2);
		plsqlKeywords.add("RPAD",Token.KEYWORD_2);
		plsqlKeywords.add("RTRIM",Token.KEYWORD_2);
		plsqlKeywords.add("SIGN",Token.KEYWORD_2);
		plsqlKeywords.add("SOUNDEX",Token.KEYWORD_2);
		plsqlKeywords.add("SIN",Token.KEYWORD_2);
		plsqlKeywords.add("SINH",Token.KEYWORD_2);
		plsqlKeywords.add("SQRT",Token.KEYWORD_2);
		plsqlKeywords.add("SUBSTR",Token.KEYWORD_2);
		plsqlKeywords.add("SUBSTRB",Token.KEYWORD_2);
		plsqlKeywords.add("TAN",Token.KEYWORD_2);
		plsqlKeywords.add("TANH",Token.KEYWORD_2);
		plsqlKeywords.add("TO_CHAR",Token.KEYWORD_2);
		plsqlKeywords.add("TO_DATE",Token.KEYWORD_2);
		plsqlKeywords.add("TO_MULTIBYTE",Token.KEYWORD_2);
		plsqlKeywords.add("TO_NUMBER",Token.KEYWORD_2);
		plsqlKeywords.add("TO_SINGLE_BYTE",Token.KEYWORD_2);
		plsqlKeywords.add("TRANSLATE",Token.KEYWORD_2);
		plsqlKeywords.add("TRUNC",Token.KEYWORD_2);
		plsqlKeywords.add("UPPER",Token.KEYWORD_2);
		
		plsqlKeywords.add("VERIFY",Token.KEYWORD_1);
		plsqlKeywords.add("SERVEROUTPUT",Token.KEYWORD_1);
		plsqlKeywords.add("PAGESIZE",Token.KEYWORD_1);
		plsqlKeywords.add("LINESIZE",Token.KEYWORD_1);
		plsqlKeywords.add("ARRAYSIZE",Token.KEYWORD_1);
		plsqlKeywords.add("DBMS_OUTPUT",Token.KEYWORD_1);
		plsqlKeywords.add("PUT_LINE",Token.KEYWORD_1);
		plsqlKeywords.add("ENABLE",Token.KEYWORD_1);

	}

	private static void addDataTypes()
	{

		plsqlKeywords.add("binary",Token.KEYWORD_1);
		plsqlKeywords.add("bit",Token.KEYWORD_1);
		plsqlKeywords.add("blob",Token.KEYWORD_1);
		plsqlKeywords.add("boolean",Token.KEYWORD_1);
		plsqlKeywords.add("char",Token.KEYWORD_1);
		plsqlKeywords.add("character",Token.KEYWORD_1);
		plsqlKeywords.add("DATE",Token.KEYWORD_1);
		plsqlKeywords.add("datetime",Token.KEYWORD_1);
		plsqlKeywords.add("DEC",Token.KEYWORD_1);
 		plsqlKeywords.add("decimal",Token.KEYWORD_1);
		plsqlKeywords.add("DOUBLE PRECISION",Token.KEYWORD_1);
		plsqlKeywords.add("float",Token.KEYWORD_1);
		plsqlKeywords.add("image",Token.KEYWORD_1);
		plsqlKeywords.add("int",Token.KEYWORD_1);
		plsqlKeywords.add("integer",Token.KEYWORD_1);
		plsqlKeywords.add("money",Token.KEYWORD_1);
		plsqlKeywords.add("name",Token.KEYWORD_1);
		plsqlKeywords.add("NATURAL",Token.KEYWORD_1);
		plsqlKeywords.add("NATURALN",Token.KEYWORD_1);
		plsqlKeywords.add("NUMBER",Token.KEYWORD_1);
		plsqlKeywords.add("numeric",Token.KEYWORD_1);
		plsqlKeywords.add("nchar",Token.KEYWORD_1);
		plsqlKeywords.add("nvarchar",Token.KEYWORD_1);
		plsqlKeywords.add("ntext",Token.KEYWORD_1);
		plsqlKeywords.add("pls_integer",Token.KEYWORD_1);
		plsqlKeywords.add("POSITIVE",Token.KEYWORD_1);
		plsqlKeywords.add("POSITIVEN",Token.KEYWORD_1);
		plsqlKeywords.add("RAW",Token.KEYWORD_1);
		plsqlKeywords.add("real",Token.KEYWORD_1);
		plsqlKeywords.add("ROWID",Token.KEYWORD_1);
		plsqlKeywords.add("SIGNTYPE",Token.KEYWORD_1);
		plsqlKeywords.add("smalldatetime",Token.KEYWORD_1);
		plsqlKeywords.add("smallint",Token.KEYWORD_1);
		plsqlKeywords.add("smallmoney",Token.KEYWORD_1);
		plsqlKeywords.add("text",Token.KEYWORD_1);
		plsqlKeywords.add("timestamp",Token.KEYWORD_1);
		plsqlKeywords.add("tinyint",Token.KEYWORD_1);
		plsqlKeywords.add("uniqueidentifier",Token.KEYWORD_1);
		plsqlKeywords.add("UROWID",Token.KEYWORD_1);
		plsqlKeywords.add("varbinary",Token.KEYWORD_1);
		plsqlKeywords.add("varchar",Token.KEYWORD_1);
		plsqlKeywords.add("varchar2",Token.KEYWORD_1);


	}

	private static void addSystemFunctions()
	{
		plsqlKeywords.add("SYSDATE",Token.KEYWORD_2);

	}

	private static void addOperators()
	{
		plsqlKeywords.add("ALL",Token.OPERATOR_1);
		plsqlKeywords.add("AND",Token.OPERATOR_1);
		plsqlKeywords.add("ANY",Token.OPERATOR_1);
		plsqlKeywords.add("BETWEEN",Token.OPERATOR_1);
		plsqlKeywords.add("BY",Token.OPERATOR_1);
		plsqlKeywords.add("CONNECT",Token.OPERATOR_1);
		plsqlKeywords.add("EXISTS",Token.OPERATOR_1);
		plsqlKeywords.add("IN",Token.OPERATOR_1);
		plsqlKeywords.add("INTERSECT",Token.OPERATOR_1);
		plsqlKeywords.add("LIKE",Token.OPERATOR_1);
		plsqlKeywords.add("NOT",Token.OPERATOR_1);
		plsqlKeywords.add("NULL",Token.OPERATOR_1);
		plsqlKeywords.add("OR",Token.OPERATOR_1);
		plsqlKeywords.add("START",Token.OPERATOR_1);
		plsqlKeywords.add("UNION",Token.OPERATOR_1);
		plsqlKeywords.add("WITH",Token.OPERATOR_1);

	}

	private static void addSystemStoredProcedures()
	{
		plsqlKeywords.add("sp_add_agent_parameter",Token.KEYWORD_3);
	}

	private static void addSystemTables()
	{
		plsqlKeywords.add("backupfile",Token.KEYWORD_3);
	}

	private static KeywordMap plsqlKeywords;
}
