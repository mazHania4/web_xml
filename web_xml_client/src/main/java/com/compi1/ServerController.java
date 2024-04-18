package com.compi1;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ServerController {

    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public String executeQuery(String query){
        return readReportsResponse(makeRequest("reports", query));
    }

    public String executeActions(String actions){
        return readActionsResponse(makeRequest("actions", actions));
    }

    private String makeRequest(String url, String msg) {
        StringBuilder resp = new StringBuilder();
        try {
            startConnection();
            String header = "POST /"+url+"/"+ msg +" HTTP/1.1 \n\rHost:localhost\n\r";
            byte[] byteHeader = header.getBytes(StandardCharsets.ISO_8859_1);
            out.write(byteHeader,0,byteHeader.length);
            out.flush();
            byte[] buf = new byte[4096];
            int read;
            while ((read = in.read(buf)) != -1) {
                resp.append(new String(buf, 0, read, StandardCharsets.ISO_8859_1));
            }
            stopConnection();
        } catch (IOException e) {
            resp = new StringBuilder("(!) Failed to establish connection with server");
        }
        return resp.toString();
    }

    private void startConnection() throws IOException {
        clientSocket = new Socket("localhost", 2000);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    private void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    private String readReportsResponse(String xml){
        try {
            Node node = parseXML(xml);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                return switch (element.getTagName()) {
                    case "report" -> "(*) Reporte: " + element.getTextContent();
                    case "error" -> "(!) Error: " + element.getTextContent();
                    default -> "(!) Respuesta del servidor no reconocida";
                };
            }
            return "Error al interpretar respuesta del servidor";
        } catch (Exception e) {
            return "Error al interpretar respuesta del servidor";
        }
    }

    private Node parseXML(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new org.xml.sax.InputSource(new StringReader(xml)));
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("*");
        return nodeList.item(0);
    }

    private String readActionsResponse(String xml){
        try {
            Node node = parseXML(xml);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                return switch (element.getTagName()) {
                    case "confirmation" -> ">> Completado: " + element.getTextContent();
                    case "error" -> "(!) Error: " + element.getTextContent();
                    default -> "(!) Respuesta del servidor no reconocida";
                };
            }
            return "Error al interpretar respuesta del servidor";
        } catch (Exception e) {
            return "Error al interpretar respuesta del servidor";
        }
    }
}