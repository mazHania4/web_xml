
#! /bin/bash
echo "COMPILING ACTIONS PARSER"
echo "-JFLEX"
jflex ActionsLexer.flex
mv ActionsLexer.java ../main/java/com/compi1/parsers/ActionsLexer.java

echo "-CUP"
java -jar /home/hania/java-cup-11b.jar ActionsParser.cup
mv ActionsParser.java ../main/java/com/compi1/parsers/ActionsParser.java
mv ActionsParserSym.java ../main/java/com/compi1/parsers/ActionsParserSym.java
