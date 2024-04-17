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
quotedValue = \"[^\n\r\"]*[\w]*\"
bracketValue = "["[^\[]*"]"

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
    {quotedValue} {
        String val = yytext();
        val = yytext().substring(1, val.length()-1);
        switch (val){
            // NOMBRES DE ACCIONES
            case "NUEVO_SITIO_WEB": return symbol(ActionsParserSym.NEW_SITE);
            case "BORRAR_SITIO_WEB": return symbol(ActionsParserSym.DELETE_SITE);
            case "NUEVA_PAGINA": return symbol(ActionsParserSym.NEW_PAGE);
            case "MODIFICAR_PAGINA": return symbol(ActionsParserSym.MODIFY_PAGE);
            case "BORRAR_PAGINA": return symbol(ActionsParserSym.DELETE_PAGE);
            case "AGREGAR_COMPONENTE": return symbol(ActionsParserSym.ADD_COMPONENT);
            case "BORRAR_COMPONENT": return symbol(ActionsParserSym.DELETE_COMPONENT);
            case "MODIFICAR_COMPONENTE":  return symbol(ActionsParserSym.MODIFY_COMPONENT);
            // NOMBRES DE PARAMETROS
            case "ID": return symbol(ActionsParserSym.ID);
            case "USUARIO_CREACION": return symbol(ActionsParserSym.CREATION_USER);
            case "FECHA_CREACION": return symbol(ActionsParserSym.CREATION_DATE);
            case "FECHA_MODIFICACION": return symbol(ActionsParserSym.MODIFICATION_DATE);
            case "USUARIO_MODIFICACION": return symbol(ActionsParserSym.MODIFICATION_USER);
            case "TITULO": return symbol(ActionsParserSym.PARAM_TITLE);
            case "SITIO": return symbol(ActionsParserSym.SITE);
            case "PADRE": return symbol(ActionsParserSym.PARENT); // tambien puede ser nombre de atributo
            case "PAGINA": return symbol(ActionsParserSym.PARAM_PAGE);
            case "CLASE": return symbol(ActionsParserSym.CLASS);
            // NOMBRES DE ATRIBUTOS COMPONENTES
            case "TEXTO": return symbol(ActionsParserSym.TEXT);
            case "ALINEACION": return symbol(ActionsParserSym.ALIGNMENT);
            case "COLOR": return symbol(ActionsParserSym.COLOR);
            case "ORIGEN": return symbol(ActionsParserSym.SOURCE);
            case "ALTURA": return symbol(ActionsParserSym.HEIGHT);
            case "ANCHO": return symbol(ActionsParserSym.WIDTH);
            case "ETIQUETAS": return symbol(ActionsParserSym.COMP_TAGS);
            default: return symbol(ActionsParserSym.QUOTE_VALUE, val);
        }
    }
    {bracketValue} {
          String val = yytext();
          val = val.substring(1, val.length()-1);
          return symbol(ActionsParserSym.BRACKET_VALUE, val); }
    {whitespace} { }
    {newline} { }
    [^] { throw new Error("Illegal string: '"+ yytext() + "' at line: " + yyline+1 + ", col:" + yycolumn+1); }
