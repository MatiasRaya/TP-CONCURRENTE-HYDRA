package org.hydra;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clase que implementa políticas para la selección de transiciones a disparar en una Red de Petri.
 */
public class Politicas {

    private final ProcesosModelados procesoModelado;

    /**
     * Constructor de la clase.
     *
     * @param procesoModelado proceso modelado por la RdP
     */
    public Politicas(ProcesosModelados procesoModelado) {
        // Se almacena el proceso modelado del parametro en la variable global
        this.procesoModelado = procesoModelado;
    }

    /**
     * Devuelve una y solo una transición para ser disparada. El método de decisión es, a partir de todas las
     * transiciones disponibles para disparar (transicionesDisponibles) se elige la que tenga menor cantidad de disparos
     * históricos (valor representado en el contador de procesoModelado). En caso de haber más de una con la misma
     * cantidad de disparos, se elige la primera.
     *
     * @param transicionesDisponibles Transiciones disponibles para disparar
     * @return Transición seleccionada para disparar
     */
    public int getDisparoPrioritario(List<Integer> transicionesDisponibles) {
        // Se obtiene el contador de disparos por transicion del proceso modelado
        int[] acciones = procesoModelado.getContadorDisparoTransiciones();

        // Se obtiene la matriz de t-invariantes
        List<List<Integer>> base = procesoModelado.getPlazasTransiciones();

        // Se aplica la politica de seleccion de transicion prioriza, donde se aplana la lista de transiciones, se
        // obtienen las transiciones disponibles y se recolectan las transiciones con prioridad
        List<Integer> accionesPrivilegiadas = base.stream()
                .sorted(Comparator.comparingInt(piecetransiciones -> acciones[piecetransiciones.get(1)]))
                .flatMap(Collection::stream)
                .filter(transicionesDisponibles::contains)
                .collect(Collectors.toList());

        // Se imprime por consola los disparos, los disponibles y el resultado
        System.out.println("Disparos: " + Arrays.toString(acciones));
        System.out.println("Disponible: " + transicionesDisponibles);
        System.out.println("Resultado " + accionesPrivilegiadas);

        // Se retorna el primer disparo que cumpla la condicion
        return accionesPrivilegiadas.get(0);
    }
}
