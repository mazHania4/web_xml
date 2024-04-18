package com.compi1;

import com.compi1.view.WindowPnl;

import javax.swing.*;

public class Main {
    public static void main(String [] args) {
        JFrame window = new JFrame();
        window.setBounds(300,300,800,800);
        window.setTitle("WEB XML");
        window.add(new WindowPnl());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
