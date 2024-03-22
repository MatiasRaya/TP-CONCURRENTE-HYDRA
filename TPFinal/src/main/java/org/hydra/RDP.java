package org.hydra;

import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

import org.apache.log4j.Logger;

import org.hydra.beans.VectorSensibilizado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Esta clase modela la RdP del sistema a representar.
 */
public class RDP {

    private static final Logger LOG = Logger.getLogger(RDP.class);
    private final RealMatrix matrizFlujo;
    private RealVector tokens;
    private VectorSensibilizado vectorSensibilizado;

    /**
     * Constructor de la clase.
     *
     * @param fluxMatrixData matriz de flujo de incidencia de la RdP
     * @param initialTokens marcado inicial de la RdP
     */
    public RDP(double[][] fluxMatrixData, double[] initialTokens) {
        // Se almacena la matriz de flujo que viene como parametro en la variable global
        matrizFlujo = MatrixUtils.createRealMatrix(fluxMatrixData);

        // Se alamcenan los tokens que vienen como parametro en la variable global
        tokens = MatrixUtils.createRealVector(initialTokens);
    }

    /**
     * Este método devuelve un vector de transición que representa una transición específica en la red de Petri.
     * El vector de transición tiene un valor de 1 en la posición correspondiente a la transición especificada,
     * indicando que esa transición está habilitada, y 0 en todas las demás posiciones.
     *
     * @param i Índice de la transición para la cual se creará el vector de transición
     * @return Vector de transición creado
     */
    private RealVector getTransicion(int i) {
        // Se crea un array para almacenar los datos del vector de transicion
        double[] datosTransicion = new double[getTotaltransiciones()];

        // Se rellena el array con 0
        Arrays.fill(datosTransicion, 0);

        // Se establece el valor de 1 en la posicion correspondiente a la transicion especificiada
        datosTransicion[i] = 1;

        // Se crea y devuelve un vector real con los datos proporcionados
        return MatrixUtils.createRealVector(datosTransicion);
    }

    /**
     * Este método intenta disparar una transición en la red de Petri. Si la transición está sensibilizada,
     * se dispara, actualizando el marcado y registrando el evento en el registro. Si la transición no está
     * sensibilizada, no se dispara y el método devuelve false.
     *
     * @param transicion Índice de la transición que se intentará disparar
     * @param finalShots Indica si se trata de los disparos finales en la simulación
     * @return true si la transición se disparó exitosamente, false si la transición no estaba sensibilizada
     */
    public boolean disparo(int transicion, boolean finalShots) {
        // Se actualiza el vector de las transiciones sensibilizadas
        this.vectorSensibilizado.setSensibilizar(getSensibilizadas());

        boolean retval;

        // Se verifica si la transicion esta sensibilizada
        if(this.vectorSensibilizado.estaSensibilizada(transicion, finalShots)) {
            // Se actualiza el marcaco
            this.tokens = this.tokens.add(this.matrizFlujo.operate(getTransicion(transicion)));

            // Se crea el String para registrar el evento de disparo
            String message = String.format("%s. Disparador %s disparo T%s",
                    System.currentTimeMillis(), Thread.currentThread().getName(), (transicion+1));

            // Se registra el evento de disparo
            LOG.info(message);

            // Se actualizan los tiempos de espera
            vectorSensibilizado.actualizarTiempoEspera(getSensibilizadas());

            // Se setea el valor a retornar en true
            retval = true;
        }
        else {
            // Se setea el valor a retornar en false
            retval = false;
        }

        // Se retorna el valor del disparo
        return retval;
    }

    /**
     * Este método devuelve una lista de índices de transiciones que están sensibilizadas en la red de Petri
     * en función del marcado actual y la matriz de flujo.
     *
     * @return Lista de índices de transiciones sensibilizadas
     */
    public List<Integer> getSensibilizadas() {
        // Se crea una nueva lista
        List<Integer> sensibilizado = new ArrayList<>();

        for(int transicion = 0; transicion < getTotaltransiciones(); transicion++) {
            // Se calcula cual es el proximo marcado
            RealVector proximoToken = tokens.add(matrizFlujo.operate(getTransicion(transicion)));

            // Se verifica que no haya ningun valor negativo en el arreglo
            if(!Arrays.stream(proximoToken.getData()).filter(val -> val < 0).findAny().isPresent()) {
                // Se almacena el valor de la transicion en la lista de sensibilizada
                sensibilizado.add(transicion);
            }
        }

        // Se devuelve la lista de transiciones sensibilizadas
        return sensibilizado;
    }

    /**
     * Este método devuelve el número total de transiciones en la red de Petri.
     *
     * @return Número total de transiciones
     */
    public int getTotaltransiciones() {
        // Se devuelve la dimenson de columnas de la matriz de flujo, que representa el numero total de transiciones
        return matrizFlujo.getColumnDimension();
    }

    /**
     * Este método establece el objeto VectorSensibilizado asociado a la red de Petri.
     *
     * @param vectorSensibilizado Objeto VectorSensibilizado a establecer
     */
    public void setVectorSensibilizado(VectorSensibilizado vectorSensibilizado) {
        // Se asigna el objeto vectorSensibilizado pasado coo parametro a la variable global
        this.vectorSensibilizado = vectorSensibilizado;
    }

    /**
     * Este método devuelve el vector de marcado actual de la red de Petri.
     *
     * @return Vector de marcado actual
     */
    public RealVector getTokens() {
        // Se retorna el tokens de marcado
        return this.tokens;
    }
}
