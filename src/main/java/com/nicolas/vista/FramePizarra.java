package com.nicolas.vista;

import com.nicolas.modelo.*;
import org.apache.logging.log4j.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FramePizarra extends JFrame {

    private static final Logger logger = LogManager.getRootLogger();

    private PizarraModelo modelo;

    public FramePizarra() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Pizarra en Red");

        modelo = new PizarraModelo();
        PanelPizarra panel = new PanelPizarra(modelo);

        modelo.addObserver(panel);

        setJMenuBar(crearMenu());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel,               BorderLayout.CENTER);
        getContentPane().add(crearToolbar(panel),  BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel crearToolbar(PanelPizarra panel) {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setBorder(BorderFactory.createEtchedBorder());
        toolbar.setPreferredSize(new Dimension(70, 0));

        JToggleButton btnCuadrado = boton("□", "CUADRADO", panel);
        JToggleButton btnCirculo  = boton("○", "CIRCULO",  panel);
        JToggleButton btnLinea    = boton("/", "LINEA",    panel);

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(btnCuadrado);
        grupo.add(btnCirculo);
        grupo.add(btnLinea);
        btnCuadrado.setSelected(true);

        toolbar.add(Box.createVerticalStrut(10));
        toolbar.add(btnCuadrado);
        toolbar.add(Box.createVerticalStrut(5));
        toolbar.add(btnCirculo);
        toolbar.add(Box.createVerticalStrut(5));
        toolbar.add(btnLinea);
        toolbar.add(Box.createVerticalGlue());

        JLabel lbl = new JLabel("Toolbar", SwingConstants.CENTER);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        toolbar.add(lbl);
        toolbar.add(Box.createVerticalStrut(5));

        return toolbar;
    }

    private JToggleButton boton(String texto, String tipo, PanelPizarra panel) {
        JToggleButton btn = new JToggleButton(texto);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(55, 55));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btn.addActionListener(e -> panel.setTipoFigura(tipo));
        return btn;
    }

    private JMenuBar crearMenu() {
        JMenuBar menuBar     = new JMenuBar();
        JMenu    menuArchivo = new JMenu("Archivo");

        JMenuItem itemServidor = new JMenuItem("Comenzar Servidor");
        JMenuItem itemConectar = new JMenuItem("Conectar");
        JMenuItem itemLimpiar  = new JMenuItem("Limpiar Pantalla");
        JMenuItem itemSalir    = new JMenuItem("Salir");

        itemServidor.addActionListener(e -> comenzarServidor());
        itemConectar.addActionListener(e -> conectar());
        itemLimpiar.addActionListener(e  -> modelo.limpiarPantalla());
        itemSalir.addActionListener(e    -> System.exit(0));

        menuArchivo.add(itemServidor);
        menuArchivo.add(itemConectar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemLimpiar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        menuBar.add(menuArchivo);
        return menuBar;
    }

    private void comenzarServidor() {
        modelo.notificarEsperando();
        new Thread(() -> {
            try {
                ServerSocket srv = new ServerSocket(ProtocoloPizarra.PUERTO);
                logger.info("Servidor escuchando en puerto " + ProtocoloPizarra.PUERTO);
                Socket clt = srv.accept();
                logger.info("Cliente conectado");
                ProtocoloPizarra protocolo = ProtocoloPizarra.crearParaServidor(clt, modelo);
                new Thread(protocolo).start();
                srv.close();
            } catch (IOException e) {
                logger.error("Error servidor: " + e.getMessage());
            }
        }).start();
    }

    private void conectar() {
        String ip = JOptionPane.showInputDialog(this, "Conectar a servidor:", "localhost");
        if (ip == null || ip.trim().isEmpty()) return;

        new Thread(() -> {
            try {
                ProtocoloPizarra protocolo = ProtocoloPizarra.crearParaCliente(ip.trim(), modelo);
                protocolo.ejecutarComoCliente();
            } catch (IOException e) {
                logger.error("Error cliente: " + e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FramePizarra::new);
    }
}