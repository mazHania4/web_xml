package com.compi1;
import com.compi1.controller.ActionsController;
import com.compi1.model.actions.Action;
import com.compi1.parsers.*;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {


    public static void main(String[] args) throws IOException {
        ActionsController actionsCtr = new ActionsController();
        try (FileReader fr = new FileReader("/home/hania/Desktop/compi1/web_xml/web_xml_server/src/main/resources/actionsTest.xml")) {
            BufferedReader br = new BufferedReader(fr);
            String xml = br.lines().collect(Collectors.joining("\n"));
            ActionsParser parser = new ActionsParser(new ActionsLexer(new StringReader(xml)));
            List<Action> actions = (List<Action>) parser.parse().value;
            for (Action a: actions) {
                System.out.println("\n" + a.toString());
                actionsCtr.execute(a);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Bye!");
    }
}