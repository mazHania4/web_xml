package com.compi1;
import com.compi1.model.Action;
import com.compi1.model.Attr;
import com.compi1.parsers.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
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