package com.nicolas.modelo;

import java.awt.*;

public class Linea extends Figura {

    private int x2, y2;

    public Linea(int x1, int y1, int x2, int y2, Color color) {
        super(x1, y1, color);
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void dibujar(Graphics g) {
        g.setColor(color);
        ((Graphics2D) g).setStroke(new BasicStroke(2));
        g.drawLine(x, y, x2, y2);
    }

    @Override
    public String aProtocolo() {
        return "LINEA " + x + " " + y + " " + x2 + " " + y2;
    }
}