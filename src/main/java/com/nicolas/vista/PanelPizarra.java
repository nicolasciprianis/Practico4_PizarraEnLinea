package com.nicolas.vista;

import com.nicolas.modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class PanelPizarra extends JPanel implements PropertyChangeListener {

    private PizarraModelo modelo;
    private String  tipoFigura    = "CUADRADO";
    private int     tamaño        = 50;
    private boolean esperando     = false;
    private int     lineX1, lineY1;
    private boolean trazandoLinea = false;

    public PanelPizarra(PizarraModelo modelo) {
        this.modelo = modelo;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(650, 500));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (tipoFigura.equals("LINEA")) {
                    lineX1 = e.getX();
                    lineY1 = e.getY();
                    trazandoLinea = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (tipoFigura.equals("LINEA") && trazandoLinea) {
                    modelo.agregarFigura(
                            new Linea(lineX1, lineY1, e.getX(), e.getY(), PizarraModelo.COLOR));
                    trazandoLinea = false;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (tipoFigura.equals("CUADRADO")) {
                    modelo.agregarFigura(
                            new Cuadrado(e.getX(), e.getY(), tamaño, tamaño, PizarraModelo.COLOR));
                } else if (tipoFigura.equals("CIRCULO")) {
                    modelo.agregarFigura(
                            new Circulo(e.getX(), e.getY(), tamaño, PizarraModelo.COLOR));
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Figura f : modelo.getFiguras()) f.dibujar(g);

        if (esperando) {
            g.setColor(new Color(200, 200, 200, 200));
            g.fillRoundRect(getWidth() / 2 - 130, getHeight() / 2 - 35, 260, 70, 15, 15);
            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Esperando conexion...", getWidth() / 2 - 105, getHeight() / 2 + 8);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals("ESPERANDO"))    esperando = true;
        if (prop.equals("CONECTADO"))    esperando = false;
        if (prop.equals("DESCONECTADO")) esperando = false;
        SwingUtilities.invokeLater(this::repaint);
    }

    public void setTipoFigura(String tipo) { this.tipoFigura = tipo; }
    public void setTamaño(int t)           { this.tamaño = t; }
}