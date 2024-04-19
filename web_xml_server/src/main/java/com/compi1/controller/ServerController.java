package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.parsers.ActionsLexer;
import com.compi1.parsers.ActionsParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerController {

    private final HTMLWriter htmlCtr;
    private final FilesController files;
    private final ActionsController actionsCtr;


    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(2000);
            System.out.println("Server listening on port 2000...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String firstLine = in.readLine();
                System.out.print("Received request: "+firstLine);
                StringBuilder request = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    if ("~~".equals(line)) break;
                    request.append(line).append("\n");
                }
                String[] parts = request.toString().split("~");
                if (parts.length == 2) {
                    String content = parts[1];
                    if (firstLine.startsWith("GET")) {
                        handleGET(firstLine, out);
                    } else if (firstLine.startsWith("POST")) {
                        handlePOST(content, firstLine, out);
                    }
                } else {
                    out.println("HTTP/1.1 400 Bad Request");
                    out.println();
                }
                out.flush();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGET(String reqLine, PrintWriter out){
        String url = reqLine.split(" ")[1].replace('$', 'S').replace('_', 'Z').replace('-', 'H');;
        String[] urlParts = url.split("/");
        if (urlParts.length>3){ out.println("HTTP/1.1 400 Bad Request"); return; }
        String site = urlParts[1];
        String content = "";
        if (site.isEmpty()) {
            content = htmlCtr.getSiteList();
        } else {
            if (!files.siteIdExists(site)) { out.println("HTTP/1.1 404 NOT_FOUND "); return; }
            try {
                String page = urlParts.length > 2 ? urlParts[2] : site+"_index";
                if (!files.pageIdExists(page)) { out.println("HTTP/1.1 404 NOT_FOUND "); return; }
                content = htmlCtr.parsePage(page, site);
            }catch (RuntimeException e){
                out.println("HTTP/1.1 412 PRECONDITION_FAILED");
            }
        }
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();
        out.println(content);
    }

    private void handlePOST(String content, String firstLine, PrintWriter out) {
        String[] urlParts = firstLine.split(" ")[1].split("/");
        if (urlParts.length>2){ out.println("HTTP/1.1 400 Bad Request"); return; }
        switch (urlParts[1]){
            case "actions"->{
                try {
                    ActionsParser parser = new ActionsParser(new ActionsLexer(new StringReader(content)));
                    List<Action> actions = (List<Action>) parser.parse().value;
                    for (Action a: actions) {
                        StringBuilder response = new StringBuilder();
                        actionsCtr.execute(a);
                        response.append(a.getType()).append("\n");
                        System.out.println("response:" + response);
                        out.println("<confirmation>"+response+"</confirmation>");
                        out.println();
                    }
                } catch(Exception e){
                    out.println("<error>"+e.getMessage()+"</error>");
                    out.println();
                }
            }
            case "reports"->{

            }
            default -> out.println("HTTP/1.1 400 Bad Request");
        }
    }

    public ServerController(FilesController files) {
        htmlCtr = new HTMLWriter(files);
        this.files = files;
        actionsCtr = new ActionsController(files);
    }
}
