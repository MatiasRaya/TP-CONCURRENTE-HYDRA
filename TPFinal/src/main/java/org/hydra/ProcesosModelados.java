package org.hydra;

import java.util.List;

/**
 * Clase que modela los procesos de la Red de Petri.
 */
public class ProcesosModelados {

    private final RDP rdp;
    private final List<List<Integer>> plazasTransiciones;
    private final Politicas politicas;
    private final int[] contadorDisparoTransiciones;

    /**
     * Constructor de la clase.
     *
     * @param rdp RdP del sistema.
     * @param plazasTransiciones Listado de lista de transiciones que representan cada camino productivo de la RdP.
     */
    public ProcesosModelados(RDP rdp, List<List<Integer>> plazasTransiciones) {
        // Se almacena la RdP pasada como parametro en la variable global
        this.rdp = rdp;

        // Se almacenan los t-invariantes en la variable global
        this.plazasTransiciones = plazasTransiciones;

        // Se inicializa la variable global de la clase Politicas
        this.politicas = new Politicas(this);

        // Se inicializa el tamaño del contador con la cantidad total de transiciones
        this.contadorDisparoTransiciones = new int[rdp.getTotaltransiciones()];
    }

    /**
     * Realiza una tarea específica en el contexto de la simulación de una Red de Petri.
     *
     * @param transicion La transición que se va a realizar.
     * @throws RuntimeException Si ocurre algún error durante la ejecución de la tarea.
     */
    public void realizarTarea(int transicion) throws RuntimeException {
        // Incrementa el contador de disparo de la transición especificada
        this.contadorDisparoTransiciones[transicion]++;
    }

    /**
     * Retorna la Red de Petri asociada a esta instancia de ProcesosModelados.
     *
     * @return La Red de Petri asociada.
     */
    public RDP getRDP() {
        // Retorna la instancia de la Red de Petri asociada
        return this.rdp;
    }

    /**
     * Retorna la lista de transiciones de los caminos de producción de la Red de Petri asociada a esta instancia de ProcesosModelados.
     *
     * @return La lista de transiciones de los caminos de producción.
     */
    public List<List<Integer>> getPlazasTransiciones() {
        // Retorna la lista de transiciones de los caminos de producción
        return this.plazasTransiciones;
    }

    /**
     * Retorna las políticas aplicadas en la simulación de la Red de Petri asociada a esta instancia de ProcesosModelados.
     *
     * @return Las políticas aplicadas.
     */
    public Politicas getPolitica() {
        // Retorna las políticas aplicadas en la simulación
        return this.politicas;
    }

    /**
     * Retorna el contador de disparos de transiciones en la simulación de la Red de Petri asociada a esta instancia de ProcesosModelados.
     *
     * @return El arreglo que contiene el contador de disparos de transiciones.
     */
    public int[] getContadorDisparoTransiciones() {
        // Retorna el contador de disparos de transiciones
        return this.contadorDisparoTransiciones;
    }
}
