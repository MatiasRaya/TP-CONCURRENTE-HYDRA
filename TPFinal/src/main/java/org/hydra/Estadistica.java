package org.hydra;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Estadistica {
    private long timeStart;
    private long timeStop;
    private final List<List<Integer>> tInvariantes;
    private final List<Integer> tInvarianteIncompleto = new ArrayList<Integer>();
    private final Map<List<Integer>, Integer> tInvariantesContador = new HashMap<>();
    private final Object notificador = new Object();

    /**
     * Constructor de la clase Estadistica.
     *
     * @param plazasTransiciones Lista de listas de enteros que representan los t-invariantes de la red.
     */
    public Estadistica(List<List<Integer>> plazasTransiciones) {
        // Se asigna la listas de listas de enteros a la variable tInvariantes
        this.tInvariantes = plazasTransiciones;

        // Se imprime cada lista de enteros en la lista de listas
        for (List<Integer> lista : tInvariantes) {
            System.out.print("[ ");
            for (Integer elemento : lista) {
                System.out.print(elemento + " ");
            }
            System.out.println("]");
        }

        // Se inicializa el mapa tInvariatesContador con los t-invariantes y un contador inicial de 0 para cada uno
        this.tInvariantes.forEach(tInvariante -> this.tInvariantesContador.put(tInvariante, 0));
    }

    /**
     * Se setea el tiempo de inicio del programa
     */
    public void setTimeStart() {
        // Se almacena la hora actual del sistema en milisegundos
        this.timeStart = System.currentTimeMillis();
    }

    /**
     * Se setea el tiempo de finalizacion del programa
     */
    public void setTimeStop() {
        // Se almacena la hora actual del sistema en milisegundos
        this.timeStop = System.currentTimeMillis();
    }

    /**
     * Imprime por pantalla diferentes estadísticas relacionadas con la ejecución del programa.
     */
    public void imprimirEstadisticas() {
        // Se imprime por pantalla el tiempo total que se demoro la ejecucion en milisegundos y segundos
        System.out.printf("Tiempo total de ejecucion: %d ms\n", this.timeStop - this.timeStart);
        System.out.printf("Tiempo total de ejecucion: %d s\n", (this.timeStop - this.timeStart) / 1000);

        // Se imprime el contador de cada t-invariante
        this.tInvariantesContador.forEach((tInvariante, contador) -> {
            System.out.printf("Invariante %s: %d\n", tInvariante, contador);
        });

        // Se calcula e imprime la cantidad total de disparos
        System.out.printf("Cantidad total de disparos: %d\n", this.tInvariantesContador.values().stream().mapToInt(x -> x.intValue()).sum());
    }

    /**
     * Incrementa el contador asociado al t-invariante incompleto especificado.
     *
     * @param transicion La transición incompleta que se agrega al t-invariante.
     */
    public void incrementarContador(Integer transicion) {
        // Se agrega la transicion a la lista tInvarianteIncompleto
        this.tInvarianteIncompleto.add(transicion);

        // Se itera sobre los t-invariantes para encontrar si alguno se ha completado
        for (List<Integer> lista : tInvariantes) {
            // Se verifica si la la lista tInvarianteIncompleto contiene todos los t-invariantes de ese momento
            if (this.tInvarianteIncompleto.containsAll(lista)) {
                // Se remueven los elementos de la lista incompleta
                lista.forEach(elemento -> this.tInvarianteIncompleto.remove(this.tInvarianteIncompleto.indexOf(elemento)));

                // Se incrementa el contador del t-invariante completo
                this.tInvariantesContador.merge(lista, 1, Integer::sum);

                // Se verifica si se alcanzo el limite de transiciones
                if (this.llegoTInvarianteLimite()) {
                    // Se sincroniza en el objeto notificador para realizar una notificación
                    synchronized (this.notificador) {
                        // Se notifica al objeto notificador
                        this.notificador.notify();
                    }
                }
            }
        }
    }

    /**
     * Verifica si se ha alcanzado el límite del t-invariante (1000 disparos).
     *
     * @return true si se alcanzó el límite, false de lo contrario.
     */
    private boolean llegoTInvarianteLimite() {
        // Se calcula y almacena la cantidad total de disparos
        int contador = this.tInvariantesContador.values().stream().mapToInt(x -> x.intValue()).sum();

        // Se retorna si el contador alcanzo el valor de 1000
        return contador == 1000;
    }

    /**
     * Funcion que espera a que se tengan los 1000 invariantes
     */
    public void esperar1000TInvariantes() throws InterruptedException {
        // Bloque de codigo que espera a que se reciba una notificacion
        synchronized (notificador) {
            try {
                // Se espera hasta que otro hilo llame a notificador para salir de aca
                notificador.wait();
            }
            catch (InterruptedException e) {
                // Se imprime la interrupcion
                e.printStackTrace();
            }
        }
    }
}
