package com.nicolas.modelo;

import org.apache.logging.log4j.*;
import java.io.*;
import java.net.*;

public class ProtocoloPizarra implements Runnable {

    private static final Logger logger = LogManager.getRootLogger();
    public  static final int    PUERTO = 5000;

    private final Socket         socket;
    private final PrintWriter    salida;
    private final BufferedReader entrada;
    private final PizarraModelo  modelo;

    private ProtocoloPizarra(Socket socket, PizarraModelo modelo) throws IOException {
        this.socket  = socket;
        this.modelo  = modelo;
        this.salida  = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        this.entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
    }

    public static ProtocoloPizarra crearParaServidor(Socket socket, PizarraModelo modelo)
            throws IOException {
        return new ProtocoloPizarra(socket, modelo);
    }

    public static ProtocoloPizarra crearParaCliente(String host, PizarraModelo modelo)
            throws IOException {
        Socket socket = new Socket(InetAddress.getByName(host), PUERTO);
        return new ProtocoloPizarra(socket, modelo);
    }

    @Override
    public void run() {
        try {
            String msg = leer();
            if ("HOLA".equals(msg)) enviar("OK");

            msg = leer();
            if ("LISTA".equals(msg)) {
                enviarLista();
                leer();
            }

            enviar("LISTA");
            recibirLista();
            enviar("OK");

            modelo.setProtocoloActivo(this);
            modelo.notificarConectado();

            leerMensajes();

        } catch (IOException e) {
            logger.error("Error protocolo servidor: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    public void ejecutarComoCliente() throws IOException {
        try {
            enviar("HOLA");
            leer();

            enviar("LISTA");
            recibirLista();
            enviar("OK");

            leer();
            enviarLista();
            leer();

            modelo.setProtocoloActivo(this);
            modelo.notificarConectado();

            leerMensajes();

        } finally {
            cerrarConexion();
        }
    }

    private void leerMensajes() throws IOException {
        while (true) {
            String msg = leer();
            if (msg == null) break;

            if ("FIGURA".equals(msg)) {
                enviar("OK");
                Figura figura = Figura.parsear(leer(), PizarraModelo.COLOR);
                modelo.agregarFiguraRemota(figura);
                enviar("OK");

            } else if ("CHAU".equals(msg)) {
                enviar("OK");
                modelo.notificarDesconectado();
                break;
            }
        }
    }

    private void enviarLista() {
        enviar(String.valueOf(modelo.getFiguras().tamano()));
        for (Figura f : modelo.getFiguras()) {
            enviar(f.aProtocolo());
        }
    }

    private void recibirLista() throws IOException {
        int count = Integer.parseInt(leer());
        for (int i = 0; i < count; i++) {
            Figura figura = Figura.parsear(leer(), PizarraModelo.COLOR);
            modelo.agregarFiguraRemota(figura);
        }
    }

    public synchronized void enviarFigura(Figura figura) {
        enviar("FIGURA");
        enviar(figura.aProtocolo());
    }

    public void enviarChau() { enviar("CHAU"); }

    private String leer() throws IOException {
        String msg = entrada.readLine();
        logger.info("<<< " + msg);
        return msg;
    }

    private void enviar(String msg) {
        logger.info(">>> " + msg);
        salida.println(msg);
    }

    private void cerrarConexion() {
        try { socket.close(); } catch (IOException e) { logger.error(e.getMessage()); }
    }
}