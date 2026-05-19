package com.nicolas.modelo;

import org.apache.logging.log4j.*;
import java.awt.*;
import java.beans.*;

public class PizarraModelo {

    private static final Logger logger = LogManager.getRootLogger();

    public static final Color COLOR = Color.BLACK;

    private Lista<Figura>         figuras;
    private ProtocoloPizarra      protocoloActivo;
    private PropertyChangeSupport observado;

    public PizarraModelo() {
        figuras   = new Lista<>();
        observado = new PropertyChangeSupport(this);
    }

    public void addObserver(PropertyChangeListener listener) {
        observado.addPropertyChangeListener(listener);
    }

    public void agregarFigura(Figura figura) {
        figuras.insertar(figura);
        logger.info("Figura agregada: " + figura.aProtocolo());
        if (protocoloActivo != null) {
            protocoloActivo.enviarFigura(figura);
        }
        observado.firePropertyChange("CAMBIO", null, this);
    }

    public void agregarFiguraRemota(Figura figura) {
        figuras.insertar(figura);
        logger.info("Figura remota: " + figura.aProtocolo());
        observado.firePropertyChange("CAMBIO", null, this);
    }


    public void limpiarPantalla() {
        figuras.limpiar();
        if (protocoloActivo != null) {
            protocoloActivo.enviarLimpiar();
        }
        observado.firePropertyChange("CAMBIO", null, this);
    }

    public void limpiarPantallaLocal() {
        figuras.limpiar();
        observado.firePropertyChange("CAMBIO", null, this);
    }

    public void setProtocoloActivo(ProtocoloPizarra protocolo) {
        this.protocoloActivo = protocolo;
    }

    public void notificarEsperando() {
        observado.firePropertyChange("ESPERANDO", null, this);
    }

    public void notificarConectado() {
        logger.info("Conexion establecida");
        observado.firePropertyChange("CONECTADO", null, this);
    }

    public void notificarDesconectado() {
        protocoloActivo = null;
        logger.info("Conexion cerrada");
        observado.firePropertyChange("DESCONECTADO", null, this);
    }

    public void desconectar() {
        if (protocoloActivo == null) return;
        protocoloActivo.enviarChau();
        notificarDesconectado();
    }

    public Lista<Figura> getFiguras() { return figuras; }
}