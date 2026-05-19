package com.nicolas.modelo;

import java.awt.*;

public abstract class Figura {

    protected int   x, y;
    protected Color color;

    public Figura(int x, int y, Color color) {
        this.x     = x;
        this.y     = y;
        this.color = color;
    }

    public abstract void   dibujar(Graphics g);
    public abstract String aProtocolo();

    public static Figura parsear(String linea, Color color) {

        if (linea.matches("^CUADRADO\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+$")) {
            String[] p = linea.split("\\s+");
            return new Cuadrado(
                    Integer.parseInt(p[1]),
                    Integer.parseInt(p[2]),
                    Integer.parseInt(p[3]),
                    Integer.parseInt(p[4]),
                    color);
        }

        if (linea.matches("^CIRCULO\\s+\\d+\\s+\\d+\\s+\\d+$")) {
            String[] p = linea.split("\\s+");
            return new Circulo(
                    Integer.parseInt(p[1]),
                    Integer.parseInt(p[2]),
                    Integer.parseInt(p[3]),
                    color);
        }

        if (linea.matches("^LINEA\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+$")) {
            String[] p = linea.split("\\s+");
            return new Linea(
                    Integer.parseInt(p[1]),
                    Integer.parseInt(p[2]),
                    Integer.parseInt(p[3]),
                    Integer.parseInt(p[4]),
                    color);
        }

        return null;
    }
}