package org.yusacetin.xox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class Canvas extends JComponent {
    double curstroke = 8;
    final Color lightDrawingColor = Color.decode("#FAFAFA");
    final Color darkDrawingColor = Color.decode("#0F0F0F");
    Color fgColor = lightDrawingColor;
    Color bgColor = darkDrawingColor;
    Image image;
    int width = 550;
    int height = 500;

    final int AUTH_GAP = 80;
    final int BACKBTN_HEIGHT = 50;
    final int BACKBTN_WIDTH = 75;

    final int DARK_THEME = 0;
    final int LIGHT_THEME = 1;
    int currentTheme = DARK_THEME;

    final int PvP = 0;
    final int XvsComp = 1;
    final int OvsComp = 2;
    final int CvC = 3;

    int gametype = PvP;
    final long computerPlayDelay = 150;

    int turn = 1;
    boolean freeze = false;
    boolean gameover = false;
    boolean tie = true;

    final int menu = 1;
    final int gameplay = 2;
    int screen = 1;

    final boolean drawRectBorders = false;

    int lastWinner = 0;
    int lastr1 = -1;
    int lastr2 = -1;

    final int MIN_WIDTH = 550;
    final int MIN_HEIGHT = 500;

    String[][] strings = {
            {"X vs Computer", "X vs Bilgisayar"}, //0
            {"Computer vs O", "Bilgisayar vs O"}, //1
            {"Player vs Player", "Oyuncu vs Oyuncu"}, //2
            {"GAME OVER", "OYUN BİTTİ"}, //3
            {"Click anywhere to replay", "Tekrar oynamak için herhangi bir yere tıklayın"}, //4
            {"TR", "EN"}, //5
            {"O WINS", "O KAZANDI"}, //6
            {"COMPUTER WINS", "BİLGİSAYAR KAZANDI"}, //7
            {"Author: İ. Yuşa Çetin", "Yazar: İ. Yuşa Çetin"}, //8
            {"Change Theme", "Tema Değiştir"}, //9
            {"COMPUTER", "BİLGİSAYAR"}, //10
            {"WINS", "KAZANDI"}, //11
            {"Back", "Geri"}, //12
            {"Computer vs Computer", "Bilgisayar vs Bilgisayar"}, //13
            {"Tic-Tac-Toe", "XOX"} //14
    };

    int sxvc = 0;
    int scvo = 1;
    int spvp = 2;
    int sgameover = 3;
    int sretry = 4;
    int slang = 5;
    //int sowins = 6;
    //int scwins = 7;
    int sauth = 8;
    int stheme = 9;
    int scomp = 10;
    int swins = 11;
    int sback = 12;
    //int scvc = 13;
    int stitle = 14;

    int en = 0;
    int tr = 1;
    int lang = en;

    ArrayList<Integer> regions = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0));
    ArrayList<RectArea> regionareas = new ArrayList<>();

    Graphics2D gtd;
    JFrame frame;
    int clickX, clickY;

    boolean strAreasInitiated = false;
    boolean gameAreasInitialized = false;

    StringArea pvparea;
    StringArea xvcarea;
    StringArea cvoarea;
    StringArea autharea;
    StringArea themearea;
    StringArea langarea;

    StringArea backarea;

    boolean sizeRedraw = false;

    ArrayList<StringArea> areas = new ArrayList<>();

    public Canvas(JFrame getframe){
        setSize(width, height);
        //setDoubleBuffered(false);
        frame = getframe;
        frame.setTitle(strings[stitle][lang]);
        //rPvP = new RectArea(new Point(0,50), new Point(getWidth(), getHeight()/3));
        //clickY <= 2*getHeight()/3 - 50 & clickY >= getHeight()/3
        //rXvC = new RectArea(new Point(0, getHeight()/3), new Point(getWidth(), 2*getHeight()/3-50));
        //clickY <= getHeight() - 120 & clickY >= 2*getHeight()/3 - 50
        //rCvO = new RectArea(new Point(0, 2*getHeight()/3-50), new Point(getWidth() , getHeight()-120));
        //clickY <= height & clickX <= 170 & clickY >= height-60
        //rRefreshTheme = new RectArea(new Point(0, getHeight()-80), new Point(170, getHeight()-12));
        //clickY <= height & clickX >= width-80 & clickY >= height-60
        //rChLang = new RectArea(new Point(getWidth()-80, getHeight()-80), new Point(getWidth(), getHeight()-12));
        //clickY <= getHeight() & clickY >= getHeight()-110 & clickX > 170 & clickX < height-100
        //rAuthor = new RectArea(new Point(170, getHeight()-110), new Point(getHeight()-100, getHeight()-12));
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                clickX = e.getX();
                clickY = e.getY();
                if (screen == gameplay){
                    boolean getclick = true;
                    if ((gametype == XvsComp & turn == 2) | (gametype == OvsComp & turn == 1) | (gametype == CvC)) {
                        getclick = false;
                    }
                    if (backarea.isInside(new Point(clickX, clickY))){
                        reconfigMenuItems(); //initMenu();
                    }else if (getclick | gameover) {
                        if (!freeze) {
                            playTurn();
                        } else {
                            resetVars();
                            clear();
                            initRegionsAndAxesAndDrawThem();
                            checkCompTurn();
                        }
                    }
                }else if (screen == menu){
                    clickButton();
                }
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                //System.out.println("Before size: "+screen);
                sizeRedraw = true;
                //if (frame.getContentPane().getSize().width < MIN_WIDTH){setSize(MIN_WIDTH, getHeight());}
                //if (frame.getContentPane().getSize().height < MIN_HEIGHT){setSize(getWidth(), MIN_HEIGHT);}
                /*boolean wexceed = frame.getContentPane().getSize().width < MIN_WIDTH;
                boolean hexceed = frame.getContentPane().getSize().height < MIN_HEIGHT;
                if (wexceed){
                    if (hexceed){
                        setSize(MIN_WIDTH, MIN_HEIGHT);
                        frame.getContentPane().setSize(MIN_WIDTH, MIN_HEIGHT+frame.getInsets().top);
                    }else{
                        setSize(MIN_WIDTH, frame.getContentPane().getSize().height);
                        frame.getContentPane().setSize(MIN_WIDTH, frame.getContentPane().getSize().height+frame.getInsets().top);
                    }
                }
                if (hexceed){
                    if (wexceed){
                        setSize(MIN_WIDTH, MIN_HEIGHT);
                        frame.getContentPane().setSize(MIN_WIDTH, MIN_HEIGHT+frame.getInsets().top);
                    }else{
                        setSize(frame.getContentPane().getSize().width, MIN_HEIGHT);
                        frame.getContentPane().setSize(frame.getContentPane().getSize().width, MIN_HEIGHT+frame.getInsets().top);
                    }
                }
                if (!hexceed && !wexceed){
                    setSize(frame.getContentPane().getSize());
                }*/
                setSize(frame.getContentPane().getSize());
                //System.out.println("After size: "+screen);
                if (screen == menu) {
                    reconfigMenuItems();
                }
                //System.out.println("RESIZED");
            }
        });
    }

    void resetVars(){
        turn = 1;
        freeze = false;
        gameover = false;
        tie = true;
        regions = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0));
    }

    void playTurn(){
        if ((gametype == PvP)|(gametype == XvsComp & turn == 1)|(gametype == OvsComp & turn == 2)) {
            int region = 0;
            boolean play = true;
            if (regionareas.get(0).isInside(new Point(clickX, clickY))) {
                region = 1;
            }else if (regionareas.get(3).isInside(new Point(clickX, clickY))) {
                region = 4;
            }else if (regionareas.get(6).isInside(new Point(clickX, clickY))) {
                region = 7;
            }else if (regionareas.get(1).isInside(new Point(clickX, clickY))) {
                region = 2;
            }else if (regionareas.get(4).isInside(new Point(clickX, clickY))) {
                region = 5;
            }else if (regionareas.get(7).isInside(new Point(clickX, clickY))) {
                region = 8;
            }else if (regionareas.get(2).isInside(new Point(clickX, clickY))) {
                region = 3;
            }else if (regionareas.get(5).isInside(new Point(clickX, clickY))) {
                region = 6;
            }else if (regionareas.get(8).isInside(new Point(clickX, clickY))) {
                region = 9;
            }
            if (backarea.isInside(new Point(clickX, clickY))){
                play = false;
                screen = menu;
                initMenu();
            }
            if (region > 0 & play) {
                if (regions.get(region - 1) == 0) {
                    if (turn == 1) {
                        playX(region);
                    } else if (turn == 2) {
                        playO(region);
                    }
                    switchTurns();
                }
            }
        }else{
            if (turn == 1){
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        computerPlaysX();
                        switchTurns();
                    }
                };
                timer.schedule(task, computerPlayDelay);
            }else if (turn == 2){
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        computerPlaysO();
                        switchTurns();
                    }
                };
                timer.schedule(task, computerPlayDelay);
            }
            //switchTurns();
        }
    }

    void checkCompTurn(){
        if (isItComputersTurn()){
            playTurn();
        }
    }

    boolean isItComputersTurn() {
        return gametype == XvsComp & turn == 2 || gametype == OvsComp & turn == 1 || gametype == CvC;
    }

    void switchTurns(){
        check();
        isGameOver();
        if (!gameover){
            if (turn == 1){
                turn = 2;
            }else if (turn == 2){
                turn = 1;
            }
            if (isItComputersTurn() & !freeze){
                playTurn();
            }
        }else{
            if (tie) {
                drawGameOver();
            }else{
                drawClickToRestart();
            }
        }
    }

    void isGameOver(){
        boolean isit = true;
        for (int i: regions){
            if (i == 0){
                isit = false;
                break;
            }
        }
        if (isit){gameover = true;}
        if (gameover){
            freeze = true;
        }
    }

    void drawClickToRestart(){
        if (gtd != null) {
            gtd.setPaint(fgColor);
            gtd.setFont(new Font("sans-serif", Font.PLAIN, 25));
            gtd.drawString(strings[sretry][lang], 5, 30);
        }
    }

    void drawGameOver(){
        if (gtd != null){
            gtd.setPaint(Color.RED);
            gtd.setFont(new Font("sans-serif", Font.BOLD, 75));
            gtd.drawString(strings[sgameover][lang], getWidth()/2-gtd.getFontMetrics().stringWidth(strings[sgameover][lang])/2, getHeight()/2);
            drawClickToRestart();
            repaint();
        }
    }

    int getTurn(){
        int turn = 0;
        for (int i: regions){
            if (i != 0){
                turn++;
            }
        }
        return turn;
    }

    void computerPlaysX() {
        int turn = getTurn();
        if (getNextMove(1, true).size() > 0){
            playX(getNextMove(1, true).get(0)+1);
        }else if (getNextMove(1, false).size() > 0){
            playX(getNextMove(1, false).get(0)+1);
        } else{
            if (turn == 0){
                int[] cases = {1,3,7,9};
                playX(choice(makeList(cases)));
            }else if (turn == 2){
                if (regions.get(4) == 2){
                    if (regions.get(0) == 1){
                        playX(9);
                    }else if (regions.get(2) == 1){
                        playX(7);
                    }else if (regions.get(6) == 1){
                        playX(3);
                    }else if (regions.get(8) == 1){
                        playX(1);
                    }
                }else{
                    if (regions.get(0) == 1 | regions.get(8) == 1){
                        if (regions.get(2) == 2 | regions.get(6) == 2){
                            if (regions.get(2) == 2){
                                playX(7);
                            }else if (regions.get(6) == 2){
                                playX(3);
                            }
                        }else if (regions.get(0) == 2 | regions.get(8) == 2){
                            int[] cases = {3, 7};
                            playX(choice(makeList(cases)));
                        }else if (regions.get(5) == 2 | regions.get(7) == 2){
                            if (regions.get(0) == 1) {
                                int[] cases = {3, 7};
                                playX(choice(makeList(cases)));
                            }else if (regions.get(8) == 1){
                                if (regions.get(5) == 2) {
                                    playX(7);
                                }else if (regions.get(7) == 2){
                                    playX(3);
                                }
                            }
                        }else if (regions.get(1) == 2 | regions.get(3) == 2){
                            if (regions.get(8) == 1) {
                                int[] cases = {3, 7};
                                playX(choice(makeList(cases)));
                            }else if (regions.get(0) == 1){
                                if (regions.get(1) == 2) {
                                    playX(7);
                                }else if (regions.get(3) == 2){
                                    playX(3);
                                }
                            }
                        }
                    }else if (regions.get(2) == 1 | regions.get(6) == 1){
                        if (regions.get(0) == 2 | regions.get(8) == 2){
                            ArrayList<Integer> cases = new ArrayList<>();
                            if (regions.get(0) == 0) {
                                cases.add(1);
                            }
                            if (regions.get(8) == 0){
                                cases.add(9);
                            }
                            playX(choice(cases));
                        }else if (regions.get(6) == 2 | regions.get(2) == 2){
                            int[] cases = {1, 9};
                            playX(choice(makeList(cases)));
                        }else if (regions.get(1) == 2 | regions.get(5) == 2){
                            if (regions.get(2) == 1){
                                if (regions.get(1) == 2){
                                    playX(9);
                                }else if (regions.get(5) == 2){
                                    playX(1);
                                }
                            }else if (regions.get(6) == 1){
                                int[] cases = {1, 9};
                                playX(choice(makeList(cases)));
                            }
                        }else if (regions.get(3) == 2 | regions.get(7) == 2){
                            if (regions.get(2) == 1){
                                int[] cases = {1,9};
                                playX(choice(makeList(cases)));
                            }else if (regions.get(6) == 1){
                                if (regions.get(3) == 2){
                                    playX(9);
                                }else if (regions.get(7) == 2){
                                    playX(1);
                                }
                            }
                        }
                    }
                }
            }else if (turn == 4){
                if (regions.get(0) == 1 & regions.get(2) == 1){
                    if ((regions.get(3) == 2 | regions.get(6) == 2)  && regions.get(8) == 0){
                        playX(9);
                    }else if ((regions.get(5) == 2 | regions.get(8) == 2)  && regions.get(6) == 0){
                        playX(7);
                    }else {
                        int[] cases = {7, 9};
                        playX(choice(makeList(cases)));
                    }
                }else if (regions.get(2) == 1 & regions.get(8) == 1){
                    if ((regions.get(0) == 2 | regions.get(1) == 2) && regions.get(6) == 0){
                        playX(7);
                    }else if ((regions.get(6) == 2 | regions.get(7) == 2) && regions.get(0) == 0){
                        playX(1);
                    }else{
                        int[] cases = {1, 7};
                        playX(choice(makeList(cases)));
                    }
                }else if (regions.get(8) == 1 & regions.get(6) == 1){
                    if ((regions.get(0) == 2 | regions.get(3) == 2) && regions.get(2) == 0){
                        playX(3);
                    }else if ((regions.get(2) == 2 | regions.get(5) == 2) && regions.get(0) == 0){
                        playX(1);
                    }else{
                        int[] cases = {1, 3};
                        playX(choice(makeList(cases)));
                    }
                }else if (regions.get(6) == 1 & regions.get(0) == 1){
                    if ((regions.get(1) == 2 | regions.get(2) == 2) && regions.get(8) == 0){
                        playX(9);
                    }else if ((regions.get(7) == 2 | regions.get(8) == 2) && regions.get(2) == 0){
                        playX(3);
                    }else {
                        int[] cases = {3, 9};
                        playX(choice(makeList(cases)));
                    }
                }
            }else{
                playX(zeroChoice(regions));
            }
        }
    }

    void computerPlaysO() {
        int turn = getTurn();
        if (getNextMove(2, true).size() > 0){
            playO(getNextMove(2, true).get(0)+1);
        }else {
            if (turn == 1) {
                if (regions.get(4) == 0) {
                    playO(5);
                } else {
                    ArrayList<Integer> cases = new ArrayList<>();
                    if (regions.get(0) == 0) {
                        cases.add(1);
                    }
                    if (regions.get(2) == 0) {
                        cases.add(3);
                    }
                    if (regions.get(6) == 0) {
                        cases.add(7);
                    }
                    if (regions.get(8) == 0) {
                        cases.add(9);
                    }
                    playO(choice(cases));
                }
            } else if (turn == 3) {
                if (regions.get(4) == 2) {
                    if (getNextMove(2, false).size() != 0) {
                        playO(getNextMove(2, false).get(0) + 1);
                    } else {
                        if (regions.get(1) == 1 & regions.get(5) == 1) {
                            playO(3);
                        }else if (regions.get(1) == 1 & regions.get(3) == 1) {
                            playO(1);
                        }else if (regions.get(7) == 1 & regions.get(5) == 1) {
                            playO(9);
                        }else if (regions.get(7) == 1 & regions.get(3) == 1){
                            playO(7);
                        }else if ((regions.get(0) == 1 & regions.get(8) == 1) | (regions.get(2) == 1 & regions.get(6) == 1)) {
                            ArrayList<Integer> cases = new ArrayList<>(Arrays.asList(2, 8, 4, 6));
                            playO(choice(cases));
                        } else if (regions.get(0) == 1 | regions.get(8) == 1) {
                            ArrayList<Integer> cases = new ArrayList<>();
                            if (regions.get(2) == 0 & (regions.get(3) != 1)&(regions.get(7) != 1)){cases.add(3);}
                            if (regions.get(6) == 0 & (regions.get(1) != 1)&(regions.get(5) != 1)){cases.add(7);}
                            playO(choice(cases));
                        } else if (regions.get(2) == 1 | regions.get(6) == 1) {
                            ArrayList<Integer> cases = new ArrayList<>();
                            if (regions.get(0) == 0 & (regions.get(5) != 1)&(regions.get(7) != 1)){cases.add(1);}
                            if (regions.get(8) == 0 & (regions.get(1) != 1)&(regions.get(3) != 1)){cases.add(9);}
                            playO(choice(cases));
                        }else {
                            playO(zeroChoice(regions));
                        }
                    }
                }else if (regions.get(0) == 1 | regions.get(8) == 1 && getNextMove(2, false).size() == 0){
                    int[] cases = {3, 7};
                    playO(choice(makeList(cases)));
                }else if (regions.get(2) == 1 | regions.get(6) == 1 && getNextMove(2, false).size() == 0){
                    int[] cases = {1, 9};
                    playO(choice(makeList(cases)));
                }else {
                    if (getNextMove(2, false).size() != 0) {
                        playO(getNextMove(2, false).get(0) + 1);
                    } else {
                        playO(zeroChoice(regions));
                    }
                }
            } else {
                if (getNextMove(2, false).size() != 0) {
                    playO(getNextMove(2, false).get(0) + 1);
                } else {
                    playO(zeroChoice(regions));
                }
            }
        }
    }

    ArrayList<Integer> makeList(int[] tomake){
        ArrayList<Integer> mylist = new ArrayList<>();
        for (int i: tomake){
            mylist.add(i);
        }
        return mylist;
    }

    int choice(ArrayList<Integer> thelist){
        Random random = new Random();
        int chosen = random.nextInt(thelist.size());
        return thelist.get(chosen);
    }

    int zeroChoice(ArrayList<Integer> thelist){
        Random random = new Random();
        int chosen = 1;
        boolean init = false;
        while (!init | thelist.get(chosen-1) != 0){
            chosen = random.nextInt(thelist.size())+1;
            init = true;
        }
        return chosen;
    }

    ArrayList<Integer> getNextMove(int pid, boolean win){
        ArrayList<Integer> wins = new ArrayList<>();
        int targetid;
        if (win){
            targetid = pid;
        }else {
            if (pid == 1) {
                targetid = 2;
            } else {
                targetid = 1;
            }
        }
        for (int i=0; i<regions.size(); i++) {
            ArrayList<Integer> copy = new ArrayList<>(regions);
            if (regions.get(i) == 0){
                copy.set(i, targetid);
                if (copy.get(0).equals(copy.get(1)) & copy.get(1).equals(copy.get(2)) & !copy.get(0).equals(0)) {
                    int[] tocheck = {0,1,2};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(3).equals(copy.get(4)) & copy.get(4).equals(copy.get(5)) & !copy.get(3).equals(0)) {
                    int[] tocheck = {3,4,5};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(6).equals(copy.get(7)) & copy.get(7).equals(copy.get(8)) & !copy.get(6).equals(0)) {
                    int[] tocheck = {6,7,8};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(0).equals(copy.get(3)) & copy.get(3).equals(copy.get(6)) & !copy.get(0).equals(0)) {
                    int[] tocheck = {0,3,6};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(1).equals(copy.get(4)) & copy.get(4).equals(copy.get(7)) & !copy.get(1).equals(0)) {
                    int[] tocheck = {1,4,7};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(2).equals(copy.get(5)) & copy.get(5).equals(copy.get(8)) & !copy.get(2).equals(0)) {
                    int[] tocheck = {2,5,8};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(3).equals(copy.get(4)) & copy.get(4).equals(copy.get(5)) & !copy.get(0).equals(0)) {
                    int[] tocheck = {3,4,5};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(0).equals(copy.get(4)) & copy.get(4).equals(copy.get(8)) & !copy.get(8).equals(0)) {
                    int[] tocheck = {0,4,8};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                } else if (copy.get(2).equals(copy.get(4)) & copy.get(4).equals(copy.get(6)) & !copy.get(6).equals(0)) {
                    int[] tocheck = {2, 4, 6};
                    for (int ii : tocheck) {
                        if (copy.get(ii).equals(targetid) & regions.get(ii) == 0) {
                            if (!wins.contains(ii))
                                wins.add(ii);
                        }
                    }
                }
            }
        }
        return wins;
    }

    void check(){
        int winner = 0;
        int r1 = 0;
        int r2 = 0;
        if (regions.get(0).equals(regions.get(1)) & regions.get(1).equals(regions.get(2)) & !regions.get(0).equals(0)){
            winner = regions.get(0);
            r1 = 0; r2 = 2;
        }else if (regions.get(3).equals(regions.get(4)) & regions.get(4).equals(regions.get(5)) & !regions.get(3).equals(0)){
            winner = regions.get(3);
            r1 = 3; r2 = 5;
        }else if (regions.get(6).equals(regions.get(7)) & regions.get(7).equals(regions.get(8)) & !regions.get(6).equals(0)){
            winner = regions.get(6);
            r1 = 6; r2 = 8;
        }else if (regions.get(0).equals(regions.get(3)) & regions.get(3).equals(regions.get(6)) & !regions.get(0).equals(0)){
            winner = regions.get(0);
            r1 = 0; r2 = 6;
        }else if (regions.get(1).equals(regions.get(4)) & regions.get(4).equals(regions.get(7)) & !regions.get(1).equals(0)){
            winner = regions.get(1);
            r1 = 1; r2 = 7;
        }else if (regions.get(2).equals(regions.get(5)) & regions.get(5).equals(regions.get(8)) & !regions.get(2).equals(0)){
            winner = regions.get(2);
            r1 = 2; r2 = 8;
        }else if (regions.get(3).equals(regions.get(4)) & regions.get(4).equals(regions.get(5)) & !regions.get(0).equals(0)){
            winner = regions.get(3);
            r1 = 3; r2 = 5;
        }else if (regions.get(0).equals(regions.get(4)) & regions.get(4).equals(regions.get(8)) & !regions.get(8).equals(0)){
            winner = regions.get(8);
            r1 = 0; r2 = 8;
        }else if (regions.get(2).equals(regions.get(4)) & regions.get(4).equals(regions.get(6)) & !regions.get(6).equals(0)){
            winner = regions.get(6);
            r1 = 2; r2 = 6;
        }
        if (winner != 0){
            r1++; r2++;
            lastWinner = winner; lastr1 = r1; lastr2 = r2;
            drawWinningLine(r1, r2);
            drawWinnerText(winner);
            freeze = true;
            gameover = true;
            tie = false;
        }
    }

    void drawWinningLine(int r1, int r2){
        int goback = 25;
        int center1x = regionareas.get(r1-1).getCenter().x; //getCenter(r1)[0];
        int center1y = regionareas.get(r1-1).getCenter().y; //getCenter(r1)[1];
        int center2x = regionareas.get(r2-1).getCenter().x; //getCenter(r2)[0];
        int center2y = regionareas.get(r2-1).getCenter().y; //getCenter(r2)[1];
        if ((r1 == 1 & r2 == 7)|(r1 == 2 & r2 == 8)|(r1 == 3 & r2 == 9)){
            center1y -= goback;
            center2y += goback;
        }else if ((r1 == 1 & r2 == 3)|(r1 == 4 & r2 == 6)|(r1 == 7 & r2 == 9)){
            center1x -= goback;
            center2x += goback;
        }else if (r1 == 1 & r2 == 9){
            center1x -= goback;
            center1y -= goback;
            center2x += goback;
            center2y += goback;
        }else if (r1 == 3 & r2 == 7){
            center1x += goback;
            center1y -= goback;
            center2x -= goback;
            center2y += goback;
        }
        if (gtd != null){
            Stroke bkpStroke = gtd.getStroke();
            gtd.setStroke(new BasicStroke((float)(12), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            gtd.setColor(Color.RED);
            gtd.drawLine(center1x, center1y, center2x, center2y);
            repaint();
            gtd.setStroke(bkpStroke);
        }
    }

    void drawWinnerText(int winner){
        if (gtd != null){
            String winnersymbol;
            if (winner == 1){
                if (gametype == OvsComp) {
                    winnersymbol = strings[scomp][lang];
                }else {
                    winnersymbol = "X";
                }
            }else{
                if (gametype == XvsComp){
                    winnersymbol = strings[scomp][lang];
                }else {
                    winnersymbol = "O";
                }
            }
            String toDraw = winnersymbol+" "+strings[swins][lang];
            int size;
            gtd.setPaint(Color.decode("#0028f9"));
            if (toDraw.length() < 7){
                size = 120;
            }else if (toDraw.length() < 10){
                size = 80;
            }
            else if (toDraw.length() < 15){
                size = 55;
            }else if (toDraw.length() < 18){
                size = 52;
            }else{
                size = 48;
            }

            /*Color curcolor = gtd.getColor();
            Color transcolor = new Color(curbg.getRed(), curbg.getGreen(), curbg.getBlue(), 180);
            gtd.setPaint(transcolor);
            gtd.fillRect(0, 0, getSize().width, getSize().height);
            gtd.setPaint(curcolor);*/


            gtd.setFont(new Font("sans-serif", Font.BOLD, size));
            gtd.drawString(toDraw, getWidth()/2-gtd.getFontMetrics().stringWidth(toDraw)/2, getHeight()/2+gtd.getFontMetrics().getHeight()/3);
            drawClickToRestart();
            repaint();
        }
    }

    /*int[] getCenter(int region){
        int centerx;
        int centery;
        if (region == 1 | region == 2 | region == 3){
            centery = marginV + blocksize/2;
        }else if (region == 4 | region == 5 | region == 6){
            centery = marginV + blocksize + axiss + blocksize/2;
        }else{
            centery = marginV + 2*blocksize + 2*axiss + blocksize/2;
        }

        if (region == 1 | region == 4 | region == 7){
            centerx = marginH + blocksize/2;
        }else if (region == 2 | region == 5 | region == 8){
            centerx = marginH + blocksize + axiss + blocksize/2;
        }else{
            centerx = marginH + 2*blocksize + 2*axiss + blocksize/2;
        }
        return new int[]{centerx, centery};
    }*/

    void playX(int region){
        regions.set(region-1, 1);
        drawX(region);
    }

    void drawX(int region){
        int l = 50;
        //int cross1x = getCenter(region)[0]-l/2;
        //int cross1y = getCenter(region)[1]-l/2;
        int cross1x = regionareas.get(region-1).getCenter().x-l/2;
        int cross1y = regionareas.get(region-1).getCenter().y-l/2;
        if (gtd != null){
            if (gtd.getColor() != fgColor){gtd.setColor(fgColor);}
            gtd.drawLine(cross1x, cross1y, cross1x+l, cross1y+l);
            gtd.drawLine(cross1x+l, cross1y, cross1x, cross1y+l);
            repaint();
        }
    }

    void drawO(int region){
        //int centerx = getCenter(region)[0];
        //int centery = getCenter(region)[1];
        int centerx = regionareas.get(region-1).getCenter().x;
        int centery = regionareas.get(region-1).getCenter().y;
        int radius = 40;
        int iradius = 30;
        if (gtd != null){
            if (gtd.getColor() != fgColor){gtd.setColor(fgColor);}
            gtd.fillOval(centerx-radius, centery-radius, 2*radius, 2*radius);
            gtd.setColor(bgColor);
            gtd.fillOval(centerx-iradius, centery-iradius, 2*iradius, 2*iradius);
            repaint();
        }
    }

    void playO(int region){
        regions.set(region-1, 2);
        drawO(region);
    }

    public void paintComponent(Graphics g){
        //System.out.println("called");
        if(image == null | sizeRedraw){
            //System.out.println("ALSO CALLED");
            image = createImage(getSize().width, getSize().height);
            gtd = (Graphics2D)image.getGraphics();
            gtd.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gtd.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            gtd.setStroke(new BasicStroke((float)(curstroke), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
            gtd.setPaint(fgColor);
            clear();
        }
        g.drawImage(image, 0, 0, null);
        if (sizeRedraw){
            if (screen == menu) {
                reconfigMenuItems();
            }else if (screen == gameplay){
                redrawGameOnResize();
            }
            sizeRedraw = false;
        }
    }

    void reconfigMenuItems(){
        if (strAreasInitiated) {
            int optionsSpan = Math.round(getHeight() - AUTH_GAP);
            pvparea.text = strings[spvp][lang];
            pvparea.borderBR = new Point(getWidth(), optionsSpan/3);pvparea.initializeVars();

            xvcarea.text = strings[sxvc][lang];
            xvcarea.borderTL = new Point(0, pvparea.borderBR.y);
            xvcarea.borderBR = new Point(getWidth(), optionsSpan*2/3);xvcarea.initializeVars();

            cvoarea.text = strings[scvo][lang];
            cvoarea.borderTL = new Point(0, xvcarea.borderBR.y);
            cvoarea.borderBR = new Point(getWidth(), optionsSpan);cvoarea.initializeVars();

            langarea.text = strings[slang][lang];
            langarea.borderBR = new Point(getWidth(), getHeight());
            langarea.borderTL = new Point(langarea.borderBR.x-langarea.width, getHeight()-langarea.height);
            langarea.initializeVars();

            autharea.text = strings[sauth][lang];
            autharea.borderBR = new Point(langarea.xleft, getHeight());
            autharea.borderTL = new Point(themearea.xright, getHeight()-autharea.height);
            autharea.initializeVars();

            themearea.text = strings[stheme][lang];
            themearea.borderBR = new Point(themearea.xright, getHeight());
            themearea.borderTL = new Point(0, getHeight()-themearea.height);
            themearea.initializeVars();

            clear();
            initMenu();
            //System.out.println(getSize().width);
        }
    }

    void initMenu(){
        if (!strAreasInitiated) {
            int optionsSpan = getHeight() - AUTH_GAP;
            pvparea = new StringArea(new Point(0, 0), new Point(getWidth(), optionsSpan/3), strings[spvp][lang], gtd);
            xvcarea = new StringArea(new Point(0, pvparea.borderBR.y), new Point(getWidth(), optionsSpan*2/3), strings[sxvc][lang], gtd);
            cvoarea = new StringArea(new Point(0, xvcarea.borderBR.y), new Point(getWidth(), optionsSpan), strings[scvo][lang], gtd);
            themearea = new StringArea(new Point(0, getHeight() - 80), new Point(170, getHeight()), strings[stheme][lang], gtd);
            langarea = new StringArea(new Point(getWidth() - 80, getHeight() - 80), new Point(getWidth(), getHeight()), strings[slang][lang], gtd);
            autharea = new StringArea(new Point(170, getHeight() - AUTH_GAP), new Point(langarea.xleft, getHeight()), strings[sauth][lang], gtd);
            autharea.constHeight = true;
            langarea.constHeight = true; langarea.constWidth = true;
            themearea.constWidth = true; themearea.constHeight = true;

            //areas = {pvparea, xvcarea, cvoarea, autharea, themearea, langarea};
            areas.add(pvparea);
            areas.add(xvcarea);
            areas.add(cvoarea);
            areas.add(autharea);
            areas.add(langarea);
            areas.add(themearea);
            strAreasInitiated = true;
        }
        screen = menu;
        clear();
        resetVars();
        drawMenuOptions();
        if (drawRectBorders) {
            pvparea.drawBorders(gtd);
            xvcarea.drawBorders(gtd);
            cvoarea.drawBorders(gtd);
            themearea.drawBorders(gtd);
            langarea.drawBorders(gtd);
            autharea.drawBorders(gtd);
            gtd.setPaint(fgColor);
        }
        repaint();
    }

    void drawMenuOptions(){
        if (gtd != null){
            gtd.setPaint(fgColor);
            gtd.setFont(new Font("sans-serif", Font.BOLD, 40));
            //for(StringArea area: areas){area.initializeTextStartPoint(gtd);}
            /*pvparea.initializeTextStartPoint(gtd);
            xvcarea.initializeTextStartPoint(gtd);
            cvoarea.initializeTextStartPoint(gtd);*/
            /*gtd.drawString(strings[spvp][lang], getWidth()/2-gtd.getFontMetrics().stringWidth(strings[spvp][lang])/2, 150-gtd.getFontMetrics().getHeight()/3);
            gtd.drawString(strings[sxvc][lang], getWidth()/2-gtd.getFontMetrics().stringWidth(strings[sxvc][lang])/2, 300);
            gtd.drawString(strings[scvo][lang], getWidth()/2-gtd.getFontMetrics().stringWidth(strings[scvo][lang])/2, 450);*/
            pvparea.drawText(gtd);
            //rCvO = new RectArea(new Point(0, 2*getHeight()/3-50), new Point(getWidth() , getHeight()-120));
            //rRefreshTheme = new RectArea(new Point(0, getHeight()-80), new Point(170, getHeight()-12));
            //rChLang = new RectArea(new Point(getWidth()-80, getHeight()-80), new Point(getWidth(), getHeight()-12));
            //rAuthor = new RectArea(new Point(170, getHeight()-110), new Point(getHeight()-100, getHeight()-12));
            xvcarea.drawText(gtd);
            cvoarea.drawText(gtd);

            gtd.setFont(new Font("sans-serif", Font.PLAIN, 20));
            //for(StringArea area: areas){area.initializeTextStartPoint(gtd);}
            //autharea.initializeTextStartPoint(gtd);
            //gtd.drawString(strings[sauth][lang], getWidth()/2-gtd.getFontMetrics().stringWidth(strings[sauth][lang])/2, 550);
            autharea.drawText(gtd);

            gtd.setFont(new Font("sans-serif", Font.PLAIN, 18));
            //for(StringArea area: areas){area.initializeTextStartPoint(gtd);}
            //themearea.initializeTextStartPoint(gtd);
            //langarea.initializeTextStartPoint(gtd);
            //gtd.drawString(strings[stheme][lang], 20, height-20);
            //gtd.drawString(strings[slang][lang], width-50, height-20);
            themearea.drawText(gtd);
            langarea.drawText(gtd);

            repaint();
        }
    }

    void refreshTheme(){
        currentTheme = (currentTheme == LIGHT_THEME) ? DARK_THEME:LIGHT_THEME;
        bgColor = (currentTheme == LIGHT_THEME) ? lightDrawingColor:darkDrawingColor;
        fgColor = (currentTheme == LIGHT_THEME) ? darkDrawingColor:lightDrawingColor;
        initMenu();
    }

    void clickButton(){
        if (pvparea.isInside(new Point(clickX, clickY))){
            gametype = PvP;
            initGame();
        }else if (xvcarea.isInside(new Point(clickX, clickY))){
            gametype = XvsComp;
            initGame();
        }else if (cvoarea.isInside(new Point(clickX, clickY))){
            gametype = OvsComp;
            initGame();
        }else if (themearea.isInside(new Point(clickX, clickY))){
            refreshTheme();
        }else if (langarea.isInside(new Point(clickX, clickY))){
            changeLanguage();
            reconfigMenuItems();
        }
    }

    void changeLanguage(){
        if (lang == en){
            lang = tr;
            frame.setTitle(strings[stitle][lang]);
        }else if (lang == tr){
            lang = en;
            frame.setTitle(strings[stitle][lang]);
        }else{
            lang = en;
            frame.setTitle(strings[stitle][lang]);
        }
    }

    void initGameAreas(){
        if (!gameAreasInitialized){
            Font bkpfont = gtd.getFont();
            gtd.setFont(new Font("sans-serif", Font.PLAIN, 30));
            backarea = new StringArea(new Point(0, getHeight()-BACKBTN_HEIGHT), new Point(BACKBTN_WIDTH, getHeight()), strings[sback][lang], gtd);
            backarea.constHeight = true; backarea.constWidth = true;
            gtd.setFont(bkpfont);
        }
    }

    void initRegionsAndAxesAndDrawThem(){
        regionareas.clear();
        int baseLength = (getWidth() > getHeight()) ? getHeight():getWidth();
        int sqdimen = baseLength*4/5;
        int hbm = (getWidth() - sqdimen)/2; // Horizontal Box Margin
        int vbm = (getHeight() - sqdimen)/2; // Vertical Box Margin
        if (vbm < 45){
            vbm = 45; // To avoid overlapping with the retry text
        }
        int al = sqdimen/16; // Axis Length
        int boxSpaceTotal = sqdimen -2*al;
        int bd = boxSpaceTotal/3; // Box Dimension
        for (int y: new int[] {0,1,2}) {
            for (int x : new int[]{0, 1, 2}) {
                regionareas.add(new RectArea(new Point(hbm+bd*x+al*x, vbm+bd*y+al*y), new Point(hbm+bd*x+al*x+bd, vbm+bd*y+al*y+bd)));
            }
        }
        backarea.borderTL = new Point(0, getHeight()-backarea.height);
        backarea.borderBR = new Point(backarea.width, getHeight());
        backarea.initializeVars();
        if (gtd != null) {
            gtd.setColor(fgColor);
            gtd.setPaint(fgColor);
            /*gtd.fillRect(marginH, marginV+blocksize, axisl, axiss);
            gtd.fillRect(marginH, marginV+2*blocksize+axiss, axisl, axiss);
            gtd.fillRect(marginH+blocksize, marginV, axiss, axisl);
            gtd.fillRect(marginH + 2*blocksize+axiss, marginV, axiss, axisl);*/

            gtd.fillRect(hbm, vbm+bd,   sqdimen, al);
            gtd.fillRect(hbm, vbm+2*bd+al,   sqdimen, al);

            gtd.fillRect(hbm+bd, vbm,   al, sqdimen);
            gtd.fillRect(hbm+bd*2+al, vbm,   al,sqdimen);

            gtd.setFont(new Font("sans-serif", Font.PLAIN, 30));
            //gtd.drawString(strings[sback][lang], 10, 585);
            //backarea.initializeTextStartPoint(gtd);
            backarea.drawText(gtd);
            if (drawRectBorders){
                backarea.drawBorders(gtd);
                for (RectArea ra: regionareas){
                    ra.drawBorders(gtd);
                }
                gtd.setPaint(fgColor);
            }
            /*if (gametype == CvC) {
                gtd.setFont(gtd.getFont().deriveFont(Font.PLAIN, 20));
                gtd.drawString(strings[scvc][lang], width - 20 - gtd.getFontMetrics().stringWidth(strings[scvc][lang]), 50);
            }*/
            repaint();
        }
    }

    /*void initDrawing(){
        drawAxis();
        if (drawRectBorders){
            for (RectArea ra: regionareas){
                ra.drawBorders(gtd);
            }
        }
    }*/

    void redrawGameOnResize(){
        initRegionsAndAxesAndDrawThem();
        for (int i=0; i<regions.size(); i++){
            if (regions.get(i) == 1){
                drawX(i+1);
            }else if (regions.get(i) == 2){
                drawO(i+1);
            }
        }
        backarea.borderBR = new Point(backarea.xright, getHeight());
        backarea.borderTL = new Point(0, getHeight()-backarea.height);
        backarea.initializeVars();

        if (gameover){
            drawClickToRestart();
            if (freeze & !tie){
                drawWinningLine(lastr1, lastr2);
                drawWinnerText(lastWinner);
            }
            if (tie){ drawGameOver();}
        }
    }

    void initGame(){
        screen = gameplay;
        clear();
        initGameAreas();
        resetVars();
        initRegionsAndAxesAndDrawThem();
        checkCompTurn();
    }

    void clear(){
        if (gtd != null) {
            Color curcolor = gtd.getColor();
            gtd.setPaint(bgColor);
            gtd.fillRect(0, 0, getSize().width, getSize().height);
            //System.out.println(getSize());
            gtd.setPaint(curcolor);
            repaint();
        }
    }
}