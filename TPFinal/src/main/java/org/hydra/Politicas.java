package org.hydra;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Esta clase es encargada de tomar las decisiones que darán mayor o menor prioridad de disparos
 *  a las transiciones. Se busca que todas las transiciones sean disparadas equitativamente.
 */
public class Politicas {

    private final ProcesosModelados procesoModelado;

    /**
     * Constructor de la clase.
     *
     * @param procesoModelado proceso modelado por la RdP
     */
    public Politicas(ProcesosModelados procesoModelado) {
        this.procesoModelado = procesoModelado;
    }

    /**
     * Devuelve una y solo una transición para ser disparada. El método de decisión es, a partir de todas las
     *  transiciones disponibles para disparar (transicionesDisponibles) se elige la que tenga menor cantidad de disparos
     *  históricos (valor representado en el contador de procesoModelado). En caso de haber más de una con la misma
     *  cantidad de disparos, se elige la primera.
     *
     * @param transicionesDisponibles transiciones disponibles para disparar
     * @return privilegedAction
     */
    public int getDisparoPrioritario(List<Integer> transicionesDisponibles) {
        int[] acciones = procesoModelado.getContadorDisparoTransiciones();

        List<List<Integer>> base = procesoModelado.getPlazasTransiciones();

        List<Integer> accionesPrivilegiadas = base.stream()
                .sorted(Comparator.comparingInt(piecetransiciones -> acciones[piecetransiciones.get(1)]))
                .flatMap(Collection::stream)
                .filter(transicionesDisponibles::contains)
                .collect(Collectors.toList());

        System.out.println("Disparos: " + Arrays.toString(acciones));
        System.out.println("Disponible: " + transicionesDisponibles);
        System.out.println("Resultado " + accionesPrivilegiadas);
        return accionesPrivilegiadas.get(0);
    }
}
