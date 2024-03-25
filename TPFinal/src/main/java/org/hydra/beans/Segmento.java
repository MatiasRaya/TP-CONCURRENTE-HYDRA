package org.hydra.beans;

import java.util.Iterator;

/**
 * Representa los segmentos definidos de la red de Petri. Estos segmentos
 *  son sectores específicos de la RdP que fijan las responsabilidades de los hilos. Es decir
 *  cada hilo puede disparar las transiciones que corresponden a su segmento asignado.
 */
public class Segmento {

    private final String nombre;
    private final int nroHilo;
    private final Iterator<Integer> transiciones;

    /**
     * Constructor de la clase.
     *
     * @param nroHilo Número máximo de hilos del segmento
     * @param transiciones Transiciones que pertenecen al segmento
     * @param nombre Nombre del segmento, representado con una letra mayúscula, "A","B",...
     */
    public Segmento(int nroHilo, Iterator<Integer> transiciones, String nombre) {
        // Se almacena el parametro del numero de hilos en su repsectiva variable global
        this.nroHilo = nroHilo;

        // Se almacena el parametro de transiciones en su repsectiva variable global
        this.transiciones = transiciones;

        // Se almacena el parametro del nombre en su repsectiva variable global
        this.nombre = nombre;
    }

    /**
     * Retorna la cantidad máxima de hilos del segmento.
     *
     * @return nroHilo
     */
    public int getNroHilo() {
        // Se retorna la cantidad de hilos
        return this.nroHilo;
    }

    /**
     * Retorna las transiciones pertenecientes al segmento.
     *
     * @return transiciones
     */
    public Iterator<Integer> getTransiciones() {
        // Se retornan las transiciones pertenecientes al segmento
        return this.transiciones;
    }

    /**
     * Retorna el nombre del segmento.
     *
     * @return nombre
     */
    @Override
    public String toString() {
        // Se retorna el nombre del segmento
        return this.nombre;
    }

}
