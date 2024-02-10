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
        matrizFlujo = MatrixUtils.createRealMatrix(fluxMatrixData);
        tokens = MatrixUtils.createRealVector(initialTokens);
    }

    /**
     * Este método retorna un vector que representa a la transición pasada como parámetro. El vector
     *  contiene todos ceros y un uno en la posición correspondiente a la transición. El tamaño del vector se calcula
     *  en función de la cantidad total de transiciones (cantidad de columnas de la matriz).
     * @param i transición a representar
     * @return datosTransicion
     */
    private RealVector getTransicion(int i) {
        double[] datosTransicion = new double[getTotaltransiciones()];
        Arrays.fill(datosTransicion, 0);
        datosTransicion[i] = 1;
        return MatrixUtils.createRealVector(datosTransicion);
    }

    /**
     * Realiza el disparo de transiciones (en exclusión mutua, ya que se llama desde adentro del monitor).
     *  Primero obtiene las transiciones sensibilizadas y actualiza el vector de sensibilizado. Luego utiliza un método
     *  definido en dicho vector, para saber si la transición puede ser disparada o no.
     *  Si la transición a disparar está sensibilizada entonces la dispara, actualiza el vector de marcado con
     *  la ecuación fundamental e imprime un mensaje en pantalla (también lo guarda en el log).
     *  Finalmente, actualiza los tiempos relacionados con las transiciones.
     *
     * @param transicion transición a disparar
     * @param finalShots disparadores finales para ajustar la red
     * @return valor boolean de disparo
     */
    public boolean disparo(int transicion, boolean finalShots) {
        vectorSensibilizado.setSensibilizar(getSensibilizadas()); /*Actualiza las transiciones sensibilizadas*/

        if(vectorSensibilizado.estaSensibilizada(transicion, finalShots)) { /*Transición sensibilizada, se dispara*/
            tokens = tokens.add(matrizFlujo.operate(getTransicion(transicion))); /*Actualiza marcado*/
            String message = String.format("%s. Disparador %s disparo T%s", /*log*/
                    System.nanoTime(), Thread.currentThread().getName(), (transicion+1));
            LOG.info(message);
            vectorSensibilizado.actualizarTiempoEspera(getSensibilizadas()); /*Actualiza tiempoEsperas*/
            return true;
        } else { /*Transición no sensibilizada, no se dispara*/
            return false;
        }
    }

    /**
     * Este método retorna una lista con todas las transiciones sensibilizadas al momento de la
     *  llamada al método. Para verificar si una transición está sensibilizada, verifica que la secuencia de disparo
     *  de dicha transición (vector con todos ceros y un uno en la transición, generado con el método getTransicion)
     *  sea una secuencia válida. Utiliza la ecuación fundamental y comprueba que no haya tokens menores a cero.
     *
     * @return sensibilizado
     */
    public List<Integer> getSensibilizadas() {
        List<Integer> sensibilizado = new ArrayList<>();
        for(int transicion = 0; transicion < getTotaltransiciones(); transicion++) {
            RealVector proximoToken = tokens.add(matrizFlujo.operate(getTransicion(transicion)));
            if(!Arrays.stream(proximoToken.getData()).filter(val -> val < 0).findAny().isPresent()) {
                sensibilizado.add(transicion);
            }
        }
        return sensibilizado;
    }

    /**
     * Retorna la cantidad total de transiciones de la RdP. Calculadas a partir de la cantidad
     *  de columnas de la matriz de flujo.
     *
     * @return totaltransiciones
     */
    public int getTotaltransiciones() {
        return matrizFlujo.getColumnDimension();
    }

    /**
     * Proporciona a la red una instancia del vector de sensibilizados.
     * @param vectorSensibilizado vector de sensibilizados
     */
    public void setVectorSensibilizado(VectorSensibilizado vectorSensibilizado) {
        this.vectorSensibilizado = vectorSensibilizado;
    }

    /**
     * Retorna el vector de marcado de la red.
     * @return vector de marcado de la red
     */
    public RealVector getTokens() {
        return tokens;
    }
}
