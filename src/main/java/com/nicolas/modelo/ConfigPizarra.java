package com.nicolas.modelo;

public class ConfigPizarra {

    public static final int PUERTO = 5000;

    public static final String CMD_HOLA    = "HOLA";
    public static final String CMD_OK      = "OK";
    public static final String CMD_CHAU    = "CHAU";
    public static final String CMD_LIMPIAR = "LIMPIAR";
    public static final String CMD_LISTA   = "LISTA";

    public static final String REGEX_FIGURA =
            "^FIGURA (CUADRADO|CIRCULO|LINEA)(\\s+\\d+)+$";

    public static final String REGEX_LISTA =
            "^LISTA\\s+\\d+$";
}