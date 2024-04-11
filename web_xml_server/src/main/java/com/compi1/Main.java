package com.compi1;
import com.compi1.model.actions.Action;
import com.compi1.model.actions.Attr;
import com.compi1.model.sites.Alignment;
import com.compi1.model.sites.Component;
import com.compi1.model.sites.ComponentType;
import com.compi1.model.sites.Page;
import com.compi1.parsers.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws IOException {
        List<Component> comps = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        List<Page> pages = new ArrayList<>();
        comps.add(new Component(ComponentType.TITLE, "title", Alignment.CENTER, "", "", 10, 10,"p", tags));
        Page page = new Page("id", "theTitle", comps, pages);
        FileOutputStream fileOutputStream;

        {
            try {
                fileOutputStream = new FileOutputStream("/home/hania/Desktop/serialized/save.ser");
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(page);
                objectOutputStream.close();

                FileInputStream fileInputStream = new FileInputStream("/home/hania/Desktop/serialized/save.ser");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Page readPage = (Page) objectInputStream.readObject();
                System.out.println(readPage);
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(":(");
            }
        }

        Action.builder().attributes((List<Attr>) null).build();
        ActionsParser parser = new ActionsParser(new ActionsLexer(new StringReader("""
                <accion nombre="AGREGAR_COMPONENTE">
                    <parametros>
                        <parametro nombre="ID">
                            [comp-1]
                        </parametro>
                        <parametro nombre="PAGINA">
                            [productos]
                        </parametro>
                        <parametro nombre="CLASE">
                            [TITULO]
                        </parametro>
                    </parametros>
                    <atributos>
                        <atributo nombre="TEXTO">
                            [Este es el texto que aparece en el titulo :) ]
                        </atributo>
                        <atributo nombre="COLOR">
                            [#5A5A5A]
                        </atributo>
                    </atributos>
                </accion>
                """)));
        Action s;
        try{
            List<Action> result = (List<Action>) parser.parse().value;
            s = result.getFirst();
            System.out.println("\n"+s.toString());

        }catch (Exception e){e.printStackTrace();}
        System.out.println("Bye!");
    }
}