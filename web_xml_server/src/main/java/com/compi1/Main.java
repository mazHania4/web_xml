package com.compi1;
import com.compi1.parsers.*;

import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws IOException {
        ActionsLexer lexer = new ActionsLexer(new StringReader("\"ID\""));
        lexer.next_token();
        System.out.println("Hello world!");
    }
}