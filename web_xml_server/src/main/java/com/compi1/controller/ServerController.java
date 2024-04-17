package com.compi1.controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerController {

    private final HTMLWriter htmlCtr;
    private final FilesController files;


    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket(2000);
            System.out.println("Server listening on port 2000...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                String requestLine = in.readLine();
                System.out.println("Received request: " + requestLine);
                if (requestLine.startsWith("GET")) {
                    handleGET(requestLine, out);
                } else if (requestLine.startsWith("POST")) {
                    handlePOST(out);
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
        if (urlParts.length>3){ out.println("HTTP/1.1 400 BAD_REQUEST "); return; }
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

    private void handlePOST(PrintWriter out) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println();
        out.println("Received POST request with XML data");
    }

    public ServerController(FilesController files) {
        htmlCtr = new HTMLWriter(files);
        this.files = files;
    }
}
