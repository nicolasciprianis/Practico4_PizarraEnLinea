package com.nicolas.modelo;

import java.awt.*;

public class Cuadrado extends Figura {

    private int ancho, alto;

    public Cuadrado(int x, int y, int ancho, int alto, Color color) {
        super(x, y, color);
        this.ancho = ancho;
        this.alto  = alto;
    }

    @Override
    public void dibujar(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, ancho, alto);
        g.drawRect(x, y, ancho, alto);
    }

    @Override
    public String aProtocolo() {
        return "CUADRADO " + x + " " + y + " " + ancho + " " + alto;
    }
}