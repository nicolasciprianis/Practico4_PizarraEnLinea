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
        String[] p = linea.split(" ");
        return switch (p[0]) {
            case "CUADRADO" -> new Cuadrado(int_(p[1]), int_(p[2]), int_(p[3]), int_(p[4]), color);
            case "CIRCULO"  -> new Circulo (int_(p[1]), int_(p[2]), int_(p[3]), color);
            case "LINEA"    -> new Linea   (int_(p[1]), int_(p[2]), int_(p[3]), int_(p[4]), color);
            default         -> null;
        };
    }

    private static int int_(String s) { return Integer.parseInt(s); }
}