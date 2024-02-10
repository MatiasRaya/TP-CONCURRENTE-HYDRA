package org.hydra;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Esta clase representa la cola de espera en la cual los hilos se estarán bloqueados a la espera
 *  de una señal que les indique que el recurso fue liberado y pueden disparar la transición. Esta cola tendrá un
 *  listado de colas que representan cada transición.
 */
public class Colas {

    private final List<ColaTransiciones> listaSemaforosTransiciones = new ArrayList<>();

    /**
     * Constructor de la clase. Crea las listas de transiciones particulares.
     *
     * @param totalTransiciones número total de transiciones
     */
    public Colas(int totalTransiciones) {
        /* Permits=0, para que siempre se bloquee al intentar hacer un acquire */
        IntStream.range(0, totalTransiciones)
                .forEach(i -> listaSemaforosTransiciones.add(new ColaTransiciones(i, 0)));
    }

    /**
     * Ejecuta el acquire de una lista de transiciones particular que es pasada como
     * parámetro.
     *
     * @param transicion indica en que cola se debe colocar el hilo en espera
     */
    public void acquire(int transicion) {
        try {
            listaSemaforosTransiciones.get(transicion).acquire();
        } catch (InterruptedException e) {
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
        listaSemaforosTransiciones.get(transicion).release();
    }

    /**
     * Devuelve un listado con las transiciones que tienen hilos esperando por ella.
     * Es decir
     * hilos que realizaron el acquire de la lista y fueron bloqueados.
     *
     * @return waitingtransiciones
     */
    public List<Integer> getTransicionesEspera() {
        return listaSemaforosTransiciones.stream()
                .filter(Semaphore::hasQueuedThreads)
                .map(ColaTransiciones::getTransicion)
                .collect(Collectors.toList());
    }

}
