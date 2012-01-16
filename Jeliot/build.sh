#!/bin/sh
# -----------------------------------------------------------------------------
# build.sh - Unix Build Script for DynamicJava
#
# $Id: build.sh,v 1.1.1.1 2003/05/05 17:10:38 amoreno Exp $
# -----------------------------------------------------------------------------

# ----- Verify and Set Required Environment Variables -------------------------
   
if [ "$ANT_HOME" = "" ] ; then
  ANT_HOME=.
fi

if [ "$JAVA_HOME" = "" ] ; then
  echo You must set JAVA_HOME to point at your Java Development Kit installation
  exit 1
fi

# ----- Set Up The Runtime Classpath ------------------------------------------

CP=$JAVA_HOME/lib/tools.jar:$ANT_HOME/lib/build/ant_1_2.jar:./lib/build/crimson.jar:./lib/build/jaxp.jar
 
# ----- Execute The Requested Build -------------------------------------------

$JAVA_HOME/bin/java $ANT_OPTS -classpath $CP org.apache.tools.ant.Main -Dant.home=$ANT_HOME $*

