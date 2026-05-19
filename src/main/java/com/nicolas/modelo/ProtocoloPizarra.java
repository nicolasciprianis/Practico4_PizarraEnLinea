package com.nicolas.modelo;

import org.apache.logging.log4j.*;
import java.io.*;
import java.net.*;

public class ProtocoloPizarra implements Runnable {

    private static final Logger logger = LogManager.getRootLogger();

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
        Socket socket = new Socket(InetAddress.getByName(host), ConfigPizarra.PUERTO);
        return new ProtocoloPizarra(socket, modelo);
    }

    @Override
    public void run() {
        try {
            String msg = leer();
            if (ConfigPizarra.CMD_HOLA.equals(msg)) enviar(ConfigPizarra.CMD_OK);

            msg = leer();
            if (msg.matches(ConfigPizarra.REGEX_LISTA)) {
                int cantidad = Integer.parseInt(msg.split("\\s+")[1]);
                recibirFiguras(cantidad);
                enviar(ConfigPizarra.CMD_OK);
            }

            enviarLista();
            leer();

            modelo.setProtocoloActivo(this);
            modelo.notificarConectado();

            leerMensajes();

        } catch (IOException e) {
            logger.error("Error servidor: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    public void ejecutarComoCliente() throws IOException {
        try {
            enviar(ConfigPizarra.CMD_HOLA);
            leer();

            enviarLista();
            leer();

            String msg = leer();
            if (msg.matches(ConfigPizarra.REGEX_LISTA)) {
                int cantidad = Integer.parseInt(msg.split("\\s+")[1]);
                recibirFiguras(cantidad);
                enviar(ConfigPizarra.CMD_OK);
            }

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

            if (msg.matches(ConfigPizarra.REGEX_FIGURA)) {
                String datos  = msg.substring("FIGURA ".length());
                Figura figura = Figura.parsear(datos, PizarraModelo.COLOR);
                modelo.agregarFiguraRemota(figura);
                enviar(ConfigPizarra.CMD_OK);

            } else if (ConfigPizarra.CMD_LIMPIAR.equals(msg)) {
                modelo.limpiarPantallaLocal();
                enviar(ConfigPizarra.CMD_OK);

            } else if (ConfigPizarra.CMD_CHAU.equals(msg)) {
                enviar(ConfigPizarra.CMD_OK);
                modelo.notificarDesconectado();
                break;
            }
        }
    }

    private void enviarLista() {
        enviar(ConfigPizarra.CMD_LISTA + " " + modelo.getFiguras().tamano());
        for (Figura f : modelo.getFiguras()) {
            enviar("FIGURA " + f.aProtocolo());
        }
    }

    private void recibirFiguras(int cantidad) throws IOException {
        for (int i = 0; i < cantidad; i++) {
            String msg = leer();
            if (msg.matches(ConfigPizarra.REGEX_FIGURA)) {
                String datos  = msg.substring("FIGURA ".length());
                Figura figura = Figura.parsear(datos, PizarraModelo.COLOR);
                modelo.agregarFiguraRemota(figura);
            } else {
                logger.warn("Figura invalida recibida: " + msg);
            }
        }
    }

    public synchronized void enviarFigura(Figura figura) {
        enviar("FIGURA " + figura.aProtocolo());
    }

    public synchronized void enviarLimpiar() {
        enviar(ConfigPizarra.CMD_LIMPIAR);
    }

    public void enviarChau() {
        enviar(ConfigPizarra.CMD_CHAU);
    }

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