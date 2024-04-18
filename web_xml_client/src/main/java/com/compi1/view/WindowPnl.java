package com.compi1.view;

import com.compi1.ServerController;

import java.awt.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.undo.UndoManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

public class WindowPnl extends JPanel {
    private ServerController server;
    private int tabCounter = 0;
    private boolean tabExists = false;
    private final JTabbedPane tPane;
    private final ArrayList<JTextPane> textAreaList;
    private final ArrayList<JScrollPane> scrollPnList;
    private final ArrayList<UndoManager> listManager;
    private final ArrayList<File> filesList;
    private final JPopupMenu textAreaMenu;
    private final JTextArea resultArea;
    private final JMenuItem save, undo, redo; //menu items that can be disabled and enabled again
    private static final Color bgColor = new Color(81, 101, 136);
    private static final Color menuColor = new Color(83, 95, 117);
    private static final Color buttonColor = new Color(195, 214, 254);
    private static final Color querysColor = new Color(110, 130, 165);
    private static final Color editColor = new Color(167, 186, 224);
    private static final Font font = new Font(Font.MONOSPACED, Font.ITALIC, 12);
    public WindowPnl() {
        server = new ServerController();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        JPanel mainPnl = new JPanel(new BorderLayout());
        mainPnl.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPnl.setBackground(bgColor);
        //----------- Menu ------------------------------------
        JMenuBar menu = new JMenuBar();
        menu.setBackground(menuColor);
        JMenu file = new JMenu("Archivo");
        file.setForeground(Color.WHITE);
        file.setBackground(menuColor);
        JMenu edit = new JMenu("Editar");
        edit.setForeground(Color.WHITE);
        edit.setBackground(menuColor);
        menu.add(file);
        menu.add(edit);
        //---------------- FILE MENU ---------------
        JMenuItem newFile = new JMenuItem("Nuevo archivo");
        newFile.setForeground(Color.WHITE);
        newFile.setBackground(menuColor);
        newFile.addActionListener(e -> {
            createTab();
            if(tabExists) enableItems();
        });
        JMenuItem open = new JMenuItem("Abrir archivo");
        open.setForeground(Color.WHITE);
        open.setBackground(menuColor);
        open.addActionListener(e -> {
            createTab();
            openFile();
        });
        save = new JMenuItem("Guardar archivo");
        save.addActionListener(e -> saveFile());
        save.setBackground(menuColor);
        save.setForeground(Color.WHITE);
        file.add(open);
        file.add(newFile);
        file.add(save);
        //------------------ EDIT MENU ----------------
        undo = new JMenuItem("Deshacer");
        undo.setBackground(menuColor);
        undo.setForeground(Color.WHITE);
        undo.addActionListener(e -> editUndo());
        redo = new JMenuItem("Rehacer");
        redo.setBackground(menuColor);
        redo.setForeground(Color.WHITE);
        redo.addActionListener(e -> editRedo());
        edit.add(undo);
        edit.add(redo);
        disableItems();
        add(menu, BorderLayout.NORTH);
        //----------- Edition Area --------------------------
        tPane = new JTabbedPane();
        filesList = new ArrayList<>();
        textAreaList = new ArrayList<>();
        scrollPnList = new ArrayList<>();
        listManager = new ArrayList<>();
        mainPnl.add(tPane, BorderLayout.CENTER);
        //----------- Query & Results Area --------------------------
        JPanel southPnl = new JPanel(new GridLayout(2, 1, 0, 10));
        southPnl.setPreferredSize(new Dimension(780, 300));
        southPnl.setBorder(new EmptyBorder(10,0,0,0));
        southPnl.setBackground(bgColor);
        JPanel queryPnl = new JPanel(new BorderLayout());
        JTextArea queryArea = new JTextArea();
        queryArea.setMargin(new Insets(5,5,5,5));
        queryArea.setBackground(querysColor);
        queryArea.setForeground(Color.black);
        queryArea.setFont(font);
        queryArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    resultArea.setText(server.executeQuery(queryArea.getText().trim()));
                    queryArea.setText("");
                }
            }
        });
        JLabel queryLbl = new JLabel("Consola de consultas:");
        queryLbl.setOpaque(true);
        queryLbl.setBackground(bgColor);
        queryLbl.setForeground(Color.black);
        queryLbl.setBorder(new EmptyBorder(0, 10, 5, 0));
        queryPnl.add(queryLbl, BorderLayout.NORTH);
        queryPnl.add(new JScrollPane(queryArea), BorderLayout.CENTER);
        southPnl.add(queryPnl);
        JPanel resultsPnl = new JPanel(new BorderLayout());
        resultArea = new JTextArea();
        resultArea.setMargin(new Insets(5,5,5,5));
        resultArea.setBackground(querysColor);
        resultArea.setForeground(Color.black);
        resultArea.setEditable(false);
        resultArea.setFont(font);
        JLabel resultsLbl = new JLabel("Resultados:");
        resultsLbl.setOpaque(true);
        resultsLbl.setBorder(new EmptyBorder(0, 10, 5, 0));
        resultsLbl.setBackground(bgColor);
        resultsLbl.setForeground(Color.black);
        resultsPnl.add(resultsLbl, BorderLayout.NORTH);
        resultsPnl.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        southPnl.add(resultsPnl);
        mainPnl.add(southPnl, BorderLayout.SOUTH);
        //----------- Run & Close Buttons --------------------------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(bgColor);
        JButton sendButton = new JButton("Run");
        sendButton.setBackground(buttonColor);
        sendButton.setToolTipText("Enviar al servidor para ejecutar");
        sendButton.addActionListener(e -> runXML() );
        buttonPanel.add(sendButton);
        JButton closeButton = new JButton(" X ");
        closeButton.setBackground(buttonColor);
        closeButton.setToolTipText("Cerrar pestaña actual");
        closeButton.addActionListener(e -> closeTab());
        buttonPanel.add(closeButton);
        mainPnl.add(buttonPanel, BorderLayout.NORTH);
        //------------------ Popup Menu on the text areas  --------------------------------------
        textAreaMenu = new JPopupMenu();
        JMenuItem cut = new JMenuItem("Cortar");
        JMenuItem copy = new JMenuItem("Copiar");
        JMenuItem paste = new JMenuItem("Pegar");
        cut.addActionListener(new DefaultEditorKit.CutAction());
        copy.addActionListener(new DefaultEditorKit.CopyAction());
        paste.addActionListener(new DefaultEditorKit.PasteAction());
        textAreaMenu.add(cut);
        textAreaMenu.add(copy);
        textAreaMenu.add(paste);
        add(mainPnl, BorderLayout.CENTER);
    }

    private void closeTab() {
        int selectedI = tPane.getSelectedIndex();
        if(selectedI != -1) {
            scrollPnList.get(tPane.getSelectedIndex()).setRowHeader(null);
            tPane.remove(selectedI);
            textAreaList.remove(selectedI);
            scrollPnList.remove(selectedI);
            listManager.remove(selectedI);
            filesList.remove(selectedI);
            tabCounter--;
            if(tPane.getSelectedIndex() == -1) {
                tabExists = false;
                disableItems();
            }
        }
    }

    private void runXML() {
        int selectedI = tPane.getSelectedIndex();
        if(selectedI != -1) {
            String actions = textAreaList.get(selectedI).getText().trim();
            if (!actions.isEmpty()) resultArea.setText(server.executeActions(actions));
        }
    }


    public void createTab() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BorderLayout());
        filesList.add(new File(""));
        textAreaList.add(new JTextPane());
        scrollPnList.add(new JScrollPane(textAreaList.get(tabCounter)));
        listManager.add(new UndoManager());
        textAreaList.get(tabCounter).getDocument().addUndoableEditListener(listManager.get(tabCounter));
        textAreaList.get(tabCounter).setComponentPopupMenu(textAreaMenu);
        pnl.add(scrollPnList.get(tabCounter), BorderLayout.CENTER);
        tPane.addTab("title", pnl);
        scrollPnList.get(tabCounter).setRowHeaderView(new TextLineNumber( textAreaList.get(tabCounter) ));
        tPane.setSelectedIndex(tabCounter);
        textAreaList.get(tabCounter).selectAll();
        textAreaList.get(tabCounter).setForeground(Color.black);
        textAreaList.get(tabCounter).setBackground(editColor);
        textAreaList.get(tabCounter).setFont(font);
        tabExists = true;
        tabCounter++;

    }

    private void saveFile(){
        JFileChooser fileChooser = new JFileChooser();
        int opc = fileChooser.showSaveDialog(null);
        if(opc == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            filesList.set(tPane.getSelectedIndex(), file);
            tPane.setTitleAt(tPane.getSelectedIndex(), file.getName());
            try {
                FileWriter fw = new FileWriter(filesList.get(tPane.getSelectedIndex()).getPath());
                String text = textAreaList.get(tPane.getSelectedIndex()).getText();
                for(int i = 0; i<text.length(); i++) {
                    fw.write(text.charAt(i));
                }
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

    private void openFile(){
        JFileChooser fileSelector = new JFileChooser();
        fileSelector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int res = fileSelector.showOpenDialog(textAreaList.get(tPane.getSelectedIndex()));
        if (res == JFileChooser.APPROVE_OPTION) {
            if(tabExists) enableItems();
            try {
                boolean pathExists = false;
                for (int i = 0; i < tPane.getTabCount(); i++) {
                    File f = fileSelector.getSelectedFile();
                    if (filesList.get(i).getPath().equals(f.getPath()))
                        pathExists = true;
                }
                if (!pathExists) {
                    File file = fileSelector.getSelectedFile();
                    filesList.set(tPane.getSelectedIndex(), file);
                    FileReader content = new FileReader( filesList.get(tPane.getSelectedIndex()).getPath());
                    BufferedReader buffer = new BufferedReader(content);
                    String linea = "";
                    String title = filesList.get(tPane.getSelectedIndex()).getName();
                    tPane.setTitleAt(tPane.getSelectedIndex(), title);
                    while(linea != null) {
                        linea = buffer.readLine();
                        if(linea !=null) append(linea+"\n", textAreaList.get(tPane.getSelectedIndex()));
                    }
                }else {
                    //if the file is already open select the tab
                    for(int i = 0; i<tPane.getTabCount(); i++) {
                        File f = fileSelector.getSelectedFile();
                        if(filesList.get(i).getPath().equals(f.getPath())) {
                            tPane.setSelectedIndex(i);
                            textAreaList.remove(tPane.getTabCount()-1);
                            scrollPnList.remove(tPane.getTabCount()-1);
                            filesList.remove(tPane.getTabCount()-1);
                            tPane.remove(tPane.getTabCount()-1);
                            tabCounter--;
                            break;
                        }
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }else {//if the Cancel button is clicked, delete the empty panel created
            int selected = tPane.getSelectedIndex();
            if(selected != -1) {
                textAreaList.remove(tPane.getTabCount()-1);
                scrollPnList.remove(tPane.getTabCount()-1);
                filesList.remove(tPane.getTabCount()-1);
                tPane.remove(tPane.getTabCount()-1);
                tabCounter--;
            }
        }
    }

    private void editUndo() {
        if(listManager.get(tPane.getSelectedIndex()).canUndo()) listManager.get(tPane.getSelectedIndex()).undo();
    }
    private void editRedo() {
        if(listManager.get(tPane.getSelectedIndex()).canRedo()) listManager.get(tPane.getSelectedIndex()).redo();
    }

    private void append(String linea, JTextPane textPn) {
        try {
            Document doc = textPn.getDocument();
            doc.insertString(doc.getLength(), linea, null);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    private void enableItems() {
        save.setEnabled(true);
        undo.setEnabled(true);
        redo.setEnabled(true);
    }

    private void disableItems() {
        save.setEnabled(false);
        undo.setEnabled(false);
        redo.setEnabled(false);
    }
}
