package com.nicolas.modelo;

import java.util.Iterator;

public class Lista<E> implements Iterable<E> {

    private Nodo<E> primero;
    private int tamano;

    public Lista() {
        primero = null;
        tamano  = 0;
    }

    public void insertar(E o) {
        Nodo<E> nuevo = new Nodo<>(o);
        nuevo.setSiguiente(primero);
        primero = nuevo;
        tamano++;
    }

    public void limpiar() {
        primero = null;
        tamano  = 0;
    }

    public int tamano() { return tamano; }

    @Override
    public Iterator<E> iterator() { return new IteradorLista<>(this); }

    static class Nodo<E> {
        private E contenido;
        private Nodo<E> siguiente;

        Nodo(E o) { contenido = o; siguiente = null; }

        E getContenido()             { return contenido; }
        Nodo<E> getSiguiente()       { return siguiente; }
        void setSiguiente(Nodo<E> n) { this.siguiente = n; }
    }

    static class IteradorLista<E> implements Iterator<E> {
        private Nodo<E> actual;

        IteradorLista(Lista<E> lista) { actual = lista.primero; }

        @Override public boolean hasNext() { return actual != null; }

        @Override
        public E next() {
            E o = actual.getContenido();
            actual = actual.getSiguiente();
            return o;
        }
    }
}