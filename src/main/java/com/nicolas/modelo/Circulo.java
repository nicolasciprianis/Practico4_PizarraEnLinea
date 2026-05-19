package com.nicolas.modelo;

import java.awt.*;

public class Circulo extends Figura {

    private int diametro;

    public Circulo(int x, int y, int diametro, Color color) {
        super(x, y, color);
        this.diametro = diametro;
    }

    @Override
    public void dibujar(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, diametro, diametro);
        g.drawOval(x, y, diametro, diametro);
    }

    @Override
    public String aProtocolo() {
        return "CIRCULO " + x + " " + y + " " + diametro;
    }
}