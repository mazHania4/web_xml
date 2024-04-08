package com.compi1.parsers;
import java_cup.runtime.*;

%%

%public
%class ActionsLexer
%cup
%line
%column
%char

whitespace = [ \t]+
newline = [\n\r]+
quoteValue = \"[^\n\r]*\"
bracketValue = "["[^\[\n\r]*"]"

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
    return symbol(ActionsParserSym.EOF);
%eofval}

%%

    // NOMBRES DE ACCIONES
    "\"NUEVO_SITIO_WEB\"" { return symbol(ActionsParserSym.NEW_SITE); }
    "\"BORRAR_SITIO_WEB\"" { return symbol(ActionsParserSym.DELETE_SITE); }
    "\"NUEVA_PAGINA\"" { return symbol(ActionsParserSym.NEW_PAGE); }
    "\"MODIFICAR_PAGINA\"" { return symbol(ActionsParserSym.MODIFY_PAGE); }
    "\"BORRAR_PAGINA\"" { return symbol(ActionsParserSym.DELETE_PAGE); }
    "\"AGREGAR_COMPONENTE\"" { return symbol(ActionsParserSym.ADD_COMPONENT); }
    "\"BORRAR_COMPONENTE\"" { return symbol(ActionsParserSym.DELETE_COMPONENT); }
    "\"MODIFICAR_COMPONENTE\"" { return symbol(ActionsParserSym.MODIFY_COMPONENT);  }
     // NOMBRES DE PARAMETROS
    "\"ID\"" { return symbol(ActionsParserSym.ID); }
    "\"USUARIO_CREACION\"" { return symbol(ActionsParserSym.CREATION_USER); }
    "\"FECHA_CREACION\"" { return symbol(ActionsParserSym.CREATION_DATE); }
    "\"FECHA_MODIFICACION\"" { return symbol(ActionsParserSym.MODIFICATION_DATE); }
    "\"USUARIO_MODIFICACION\"" { return symbol(ActionsParserSym.MODIFICATION_USER); }
    "\"TITULO\"" { return symbol(ActionsParserSym.PARAM_TITLE); }
    "\"SITIO\"" { return symbol(ActionsParserSym.SITE); }
    "\"PADRE\"" { return symbol(ActionsParserSym.PARENT); } // tambien puede ser nombre de atributo
    "\"PAGINA\"" { return symbol(ActionsParserSym.PARAM_PAGE); }
    "\"CLASE\"" { return symbol(ActionsParserSym.CLASS); }
     // NOMBRES DE ATRIBUTOS COMPONENTES
    "\"TEXTO\"" { return symbol(ActionsParserSym.TEXT); }
    "\"ALINEACION\"" { return symbol(ActionsParserSym.ALIGNMENT); }
    "\"COLOR\"" { return symbol(ActionsParserSym.COLOR); }
    "\"ORIGEN\"" { return symbol(ActionsParserSym.SOURCE); }
    "\"ALTURA\"" { return symbol(ActionsParserSym.HEIGHT); }
    "\"ANCHO\"" { return symbol(ActionsParserSym.WIDTH); }
    "\"ETIQUETAS\"" { return symbol(ActionsParserSym.COMP_TAGS); }

    // NOMBRES DE ETIQUETAS XML
    "<acciones>" { return symbol(ActionsParserSym.ACTIONS_OP); }
    "</acciones>" { return symbol(ActionsParserSym.ACTIONS_CL); }
    "accion" { return symbol(ActionsParserSym.ACTION); }
    "<parametros>" { return symbol(ActionsParserSym.PARAMS_OP); }
    "</parametros>" { return symbol(ActionsParserSym.PARAMS_CL); }
    "parametro" { return symbol(ActionsParserSym.PARAM); }
    "<etiquetas>" { return symbol(ActionsParserSym.TAGS_OP); }
    "</etiquetas>" { return symbol(ActionsParserSym.TAGS_CL); }
    "etiqueta" { return symbol(ActionsParserSym.TAG); }
    "<atributos>" { return symbol(ActionsParserSym.ATTRIBUTES_OP); }
    "</atributos>" { return symbol(ActionsParserSym.ATTRIBUTES_CL); }
    "atributo" { return symbol(ActionsParserSym.ATTRIBUTE); }
    "nombre" { return symbol(ActionsParserSym.NAME); }
    "valor" { return symbol(ActionsParserSym.VALUE); }
    // SIMBOLOS
    "=" { return symbol(ActionsParserSym.EQUALS); }
    "<" { return symbol(ActionsParserSym.LESS_THAN); }
    "/" { return symbol(ActionsParserSym.SLASH); }
    ">" { return symbol(ActionsParserSym.MORE_THAN); }
    {quoteValue} { return symbol(ActionsParserSym.QUOTE_VALUE, yytext()); }
    {bracketValue} { return symbol(ActionsParserSym.BRACKET_VALUE, yytext()); }
    {whitespace} { }
    {newline} { }
    [^] { throw new Error("Illegal string: '"+ yytext() + "' at line: " + yyline+1 + ", col:" + yycolumn+1); }
