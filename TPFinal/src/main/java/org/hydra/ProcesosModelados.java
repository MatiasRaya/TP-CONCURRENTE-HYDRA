package org.hydra;


import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase modela la actividad que se realiza en las plazas de la RdP. Además, cuenta con
 *  un contador de disparos por transición que será util para las estadísticas de la red.
 */
public class ProcesosModelados {

    private final RDP rdp;
    private final List<List<Integer>> plazasTransiciones;
    private final Politicas politicas;
    private final int[] contadorDisparoTransiciones; /*Contador de disparadores por transición*/
    private Integer counterTotal = 0;

    /**
     * Constructor de la clase.
     *
     * @param rdp RdP del sistema.
     * @param plazasTransiciones Listado de lista de transiciones que representan cada camino productivo de la RdP.
     */
    public ProcesosModelados(RDP rdp, List<List<Integer>> plazasTransiciones) {
        this.rdp = rdp;
        this.plazasTransiciones = plazasTransiciones;
        this.politicas = new Politicas(this);
        contadorDisparoTransiciones = new int[rdp.getTotaltransiciones()];
    }

    /**
     * Se incrementa el contador del disparo de la transicion
     *
     * @param transicion Transición de entrada a la plaza a simular.
     * @throws RuntimeException Excepción manejada en Disparador
     */
    public void realizeTask(int transicion) throws RuntimeException {
        contadorDisparoTransiciones[transicion]++;
    }

    /**
     * Retorna la RdP asociada.
     *
     * @return rdp
     */
    public RDP getRDP() {
        return rdp;
    }

    /**
     * Retorna las transiciones de los caminos de producción de la RdP.
     *
     * @return plazasTransiciones
     */
    public List<List<Integer>> getPlazasTransiciones() {
        return plazasTransiciones;
    }

    /**
     * Retorna las políticas aplicadas.
     *
     * @return politicas
     */
    public Politicas getPolitics() {
        return politicas;
    }

    /**
     * Retorna el contador de disparadores de transiciones
     * @return contadorDisparoTransiciones
     */
    public int[] getContadorDisparoTransiciones() {
        return contadorDisparoTransiciones;
    }

}
