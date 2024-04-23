package com.compi1.parsers;
import java_cup.runtime.*;

%%

%public
%class StatsLexer
%cup
%line
%column
%char
%ignorecase

whitespace = [ \t]+
newline = [\n\r]+
quotedValue = \"[^\n\r\"]*[\w]*\"

%{
    StringBuffer stringBuffer = new StringBuffer();

    private  Symbol symbol(int type){
        return new Symbol(type, yyline+1, yycolumn+1);
    }
    private  Symbol symbol(int type, Object value){
        return new Symbol(type, yyline+1, yycolumn+1, value);
    }
%}

%eofval{
    return symbol(StatsParserSym.EOF);
%eofval}

%%
    "consultar" { return symbol(StatsParserSym.CONSULT); }
    "visitas_sitio" { return symbol(StatsParserSym.SITE_VISITS); }
    "visitas_pagina" { return symbol(StatsParserSym.PAGE_VISITS); }
    "paginas_populares" { return symbol(StatsParserSym.POPULAR_PAGES); }
    "componente" { return symbol(StatsParserSym.COMPONENT); }
    "," { return symbol(StatsParserSym.COMMA); }
    ";" { return symbol(StatsParserSym.SEMICOLON); }
    "TITULO" { return symbol(StatsParserSym.TITLE); }
    "PARAGRAPH" { return symbol(StatsParserSym.PARAGRAPH); }
    "IMAGEN" { return symbol(StatsParserSym.IMG); }
    "VIDEO" { return symbol(StatsParserSym.VID); }
    "MENU" { return symbol(StatsParserSym.MENU); }
    "TODOS" { return symbol(StatsParserSym.ALL); }
    {quotedValue} {
        String val = yytext();
        val = yytext().substring(1, val.length()-1);
        return symbol(StatsParserSym.PATH, val);
    }
    {whitespace} { }
    {newline} { }
    [^] { throw new Error("Illegal string: '"+ yytext() + "' at line: " + yyline+1 + ", col:" + yycolumn+1); }
