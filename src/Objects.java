package org.yusacetin.xox;
import java.awt.*;

class RectArea {
    Point borderTL;
    Point borderBR;
    Point borderTR;
    Point borderBL;
    Point[] topLine;
    Point[] rightLine;
    Point[] bottomLine;
    Point[] leftLine;

    int width;
    int height;
    int xleft;
    int ytop;
    int xright;
    int ybottom;

    boolean constHeight = false;
    boolean constWidth = false;

    public RectArea(Point tl, Point br){
        borderTL = tl;
        borderBR = br;
        initializeVars();
    }

    void initializeVars(){
        borderTR = new Point(borderBR.x, borderTL.y);
        borderBL = new Point(borderTL.x, borderBR.y);

        topLine = new Point[]{borderTL, borderTR};
        rightLine = new Point[]{borderTR, borderBR};
        bottomLine = new Point[]{borderBL, borderBR};
        leftLine = new Point[]{borderTL, borderBL};

        if (!constWidth) {
            width = borderBR.x - borderBL.x;
        }
        if (!constHeight) {
            height = borderBR.y - borderTR.y;
        }

        xleft = borderBL.x;
        xright = xleft + width;
        ytop = borderTR.y;
        ybottom = ytop + height;
    }

    Point getCenter(){
        return new Point((borderTR.x + borderTL.x)/2, (borderTR.y + borderBR.y)/2);
    }

    void drawBorders(Graphics2D gtd){
        if (gtd != null){
            Stroke bkpStroke = gtd.getStroke();
            BasicStroke bst = new BasicStroke((float)(3), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND);
            gtd.setStroke(bst);
            float lw = bst.getLineWidth();

            gtd.setColor(Color.RED);
            gtd.drawLine(topLine[0].x, topLine[0].y, topLine[1].x-(int)lw, topLine[1].y);
            gtd.drawLine(rightLine[0].x-(int)lw, rightLine[0].y, rightLine[1].x-(int)lw, rightLine[1].y-(int)lw);
            gtd.drawLine(bottomLine[0].x, bottomLine[0].y-(int)lw, bottomLine[1].x-(int)lw, bottomLine[1].y-(int)lw);
            gtd.drawLine(leftLine[0].x, leftLine[0].y, leftLine[1].x, leftLine[1].y-(int)lw);
            gtd.setStroke(bkpStroke);
            //Call repaint from main function
        }
    }

    boolean isInside(Point p){
        if (p.x <= this.borderTR.x && p.x >= this.borderTL.x){
            return p.y <= borderBR.y && p.y >= borderTR.y;
        }
        return false;
    }

}

class StringArea extends RectArea{
    Point startpt;
    String text;

    public StringArea(Point tl, Point br, String settext, Graphics2D gtd){
        super(tl, br);
        text = settext;
        initializeTextStartPoint(gtd);
    }

    void initializeTextStartPoint(Graphics2D gtd){
        FontMetrics metrics = gtd.getFontMetrics();
        int x = xleft + (width - metrics.stringWidth(text)) / 2;
        int y = ytop + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        //System.out.println(x+" "+y);
        //System.out.println(ytop);
        startpt = new Point(x,y);
    }

    void drawText(Graphics2D gtd){
        if (gtd != null) {
            //Paint bkppaint = gtd.getPaint();
            //gtd.setPaint(paint);
            //gtd.setFont(font);
            initializeTextStartPoint(gtd);
            gtd.drawString(text, startpt.x, startpt.y);
            //gtd.setPaint(bkppaint);
        }
    }
}

class Point{
    int x;
    int y;
    public Point(int setx, int sety){
        this.x = setx;
        this.y = sety;
    }
}
