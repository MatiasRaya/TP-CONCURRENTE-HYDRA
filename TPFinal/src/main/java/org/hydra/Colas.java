package org.hydra;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Clase que gestiona las colas de espera para las transiciones.
 */

public class Colas {

    private final List<ColaTransiciones> listaSemaforosTransiciones = new ArrayList<>();

    /**
     * Constructor de la clase. Crea las listas de transiciones particulares.
     *
     * @param totalTransiciones número total de transiciones
     */
    public Colas(int totalTransiciones) {
        // Se inicializan las colas de espera para cada transición
        IntStream.range(0, totalTransiciones)
                .forEach(i -> this.listaSemaforosTransiciones.add(new ColaTransiciones(i, 0)));
    }

    /**
     * Ejecuta el acquire de una lista de transiciones particular que es pasada como
     * parámetro.
     *
     * @param transicion indica en qué cola se debe colocar el hilo en espera
     */
    public void acquire(int transicion) {
        try {
            // Se ejecuta el acquire en la cola de espera específica para la transición indicada
            this.listaSemaforosTransiciones.get(transicion).acquire();
        }
        catch (InterruptedException e) {
            // Se lanza una RuntimeException si se produce una interrupción durante la espera
            throw new RuntimeException(e);
        }
    }

    /**
     * Ejecuta el release de una lista de transiciones particular que es pasada como
     * parámetro.
     *
     * @param transicion indica de que cola se debe despertar un hilo
     */
    public void release(int transicion) {
        // Se ejecuta el release en la cola de espera específica para la transición indicada
        this.listaSemaforosTransiciones.get(transicion).release();
    }

    /**
     * Devuelve un listado con las transiciones que tienen hilos esperando por ella.
     * Es decir, hilos que realizaron el acquire de la lista y fueron bloqueados.
     *
     * @return Lista de transiciones con hilos en espera
     */

    public List<Integer> getTransicionesEspera() {
        // Se filtran las colas de espera para obtener aquellas que tienen hilos en espera
        return this.listaSemaforosTransiciones.stream()
                .filter(Semaphore::hasQueuedThreads)
                .map(ColaTransiciones::getTransicion)
                .collect(Collectors.toList());
    }
}
