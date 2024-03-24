package org.yusacetin.xox;
import javax.swing.*;
import java.awt.*;
public class XOXMain {
    Canvas canvas;
    JFrame frame;

    void init(){
        frame = new JFrame("Loading...");
        canvas = new Canvas(frame);
        Container cont = frame.getContentPane();
        cont.setLayout(null);
        cont.add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(canvas.width, canvas.height);
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(550, 500));
        frame.setVisible(true);
        canvas.paintComponent(canvas.getGraphics());
        canvas.initMenu();
        canvas.checkCompTurn();
    }

    public static void main(String[] args){
        new XOXMain().init();
    }
}
