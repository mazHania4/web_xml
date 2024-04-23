package com.compi1.controller;

import com.compi1.model.actions.Action;
import com.compi1.model.stats.Report;
import com.compi1.parsers.ActionsLexer;
import com.compi1.parsers.ActionsParser;
import com.compi1.parsers.StatsLexer;
import com.compi1.parsers.StatsParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
                if (firstLine != null){
                    System.out.println("Received request: " + firstLine);
                    if (firstLine.startsWith("GET")) {
                        handleGET(firstLine, out);
                    } else if (firstLine.startsWith("POST")) {
                        StringBuilder request = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            if ("~~".equals(line)) break;
                            request.append(line).append("\n");
                        }
                        String[] parts = request.toString().split("~");
                        if (parts.length == 2) {
                            String content = parts[1].trim();
                            handlePOST(content, firstLine, out);
                        }
                    }
                    out.flush();
                    out.close();
                    in.close();
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGET(String reqLine, PrintWriter out){
        String url = reqLine.split(" ")[1];
        String[] urlParts = url.split("/");
        if (urlParts.length>3){ out.println("HTTP/1.1 400 Bad Request"); return; }
        String site;
        String content = "";
        if (urlParts.length==0) {
            content = htmlCtr.getSiteList();
        } else {
            site = urlParts[1];
            if (!files.siteIdExists(site)) { out.println("HTTP/1.1 404 NOT_FOUND "); return; }
            try {
                String page = urlParts.length > 2 ? urlParts[2] : site+"Index";
                if (page.equals("index")) page = site +"Index";
                if (!files.pageIdExists(page)) { out.println("HTTP/1.1 404 NOT_FOUND "); return; }
                content = htmlCtr.parsePage(page, site);
                files.addVisit(site, page);
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
                        System.out.println("\tresponse:" + response);
                        out.println("<confirmation>"+response+"</confirmation>");
                        out.println();
                    }
                } catch(Exception | Error e){
                    System.out.println("\tresponse:" + e.getMessage());
                    out.println("<error>"+e.getMessage()+"</error>");
                    out.println();
                }
            }
            case "reports"->{
                try {
                    StatsParser parser = new StatsParser(new StatsLexer(new StringReader(content)));
                    Report report = (Report) parser.parse().value;
                        String response = new ReportsController(files).of(report);
                        System.out.println("\tresponse:" + response);
                        out.println("<report>"+response+"</report>");
                        out.println();
                } catch(Exception | Error e){
                    System.out.println("\tresponse:" + e.getMessage());
                    out.println("<error>"+e.getMessage()+"</error>");
                    out.println();
                }
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
