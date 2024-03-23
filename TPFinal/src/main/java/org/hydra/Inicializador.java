package org.hydra;

import org.hydra.beans.Segmento;

import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Clase que se encarga de inicializar y lanzar los hilos de los disparadores en la simulación de la Red de Petri.
 */
public class Inicializador {

    private final AdminMonitor monitor;
    private final Segmento[] segmentos;
    private final ProcesosModelados procesosModelados;
    private final List<Thread> disparadores;

    /**
     * Constructor de la clase.
     *
     * @param monitor monitor del sistema
     * @param segmentos Array de segmentos de la RdP
     * @param procesosModelados proceso modelado de la RdP
     */
    public Inicializador(AdminMonitor monitor, Segmento[] segmentos, ProcesosModelados procesosModelados) {
        // Se asigna el monitor que viene como parametro a la variable local
        this.monitor = monitor;

        // Se asignan los segmentos que vienen como parametro a la variable local
        this.segmentos = segmentos;

        // Se asignan los procesos modelados que vienen como parametro a la variable global
        this.procesosModelados = procesosModelados;

        // Se crea y se recolecta los disparadores para cada segmento
        this.disparadores = Arrays.stream(segmentos)
                .flatMap(this::createShooters)
                .collect(Collectors.toList());
    }

    /**
     * A partir de un objeto de la clase Segmento recibido como parámetro, crea una cantidad definida
     * (cantidad máxima de hilos del segmento) de objetos Runnable (Disparador).
     * Luego utiliza estos objetos como parámetro para la creación de objetos Thread, a los cuales se les
     * asigna un nombre en función del segmento y su orden de creación.
     *
     * @param segmento Segmento particular para el que se quieren crear los disparadores
     * @return Stream de Threads que contiene los hilos creados para el segmento específico
     */
    private Stream<Thread> createShooters(Segmento segmento) {
        // Se crea un flujo de enteros desde 0 hasta el numero de hilos del segmento, se crea
        // un nuevo disparo disparador para este segmento y se le asigna un nombre unico al
        // hilo basado en el segmento y el indice del hilo
        return IntStream.range(0, segmento.getNroHilo()).mapToObj(i -> {
            Runnable shooter = new Disparador(this.monitor, segmento.getTransiciones(), this.procesosModelados);
            return new Thread(shooter, String.format("S%sN%s", segmento, i));
        });
    }

    /**
     * Este método inicia todos los hilos sin especificar un tiempo límite de ejecución.
     *
     * @throws InterruptedException Excepción por interrupción
     */
    public void start() throws InterruptedException {
        // Se inician todos los hilos generados
        this.disparadores.parallelStream().forEach(Thread::start);
    }

    /**
     * Este método interrumpe todos los hilos.
     *
     * @throws InterruptedException Excepción por interrupción
     */
    public void finish() throws InterruptedException {
        // Se interrumpen todos los hilos generados
        this.disparadores.parallelStream().forEach(Thread::interrupt);

        try {
            // Se espera hasta que todos los hilos terminen
            for(Thread shooter : this.disparadores){
                shooter.join();
            }
        }
        catch (InterruptedException e) {
            // Se imprime el error
            e.printStackTrace();
        }

        // Se imprime por consola que el hilo se interrumpio y los tokens de la red
        System.out.println("El hilo ha sido interrumpido");
        System.out.println(this.procesosModelados.getRDP().getTokens());

        // Se declaran las transiciones finales
        int[] finaltransiciones = {1,2,3,4,5,6,7,9,10,11};

        // Se obtiene el tamaño total de las transiciones
        int tamanio = this.procesosModelados.getRDP().getTotaltransiciones()-2;

        // Se setea en false que se llego al fin
        boolean fin = false;

        // Se realiza un bucle hasta que terminen todas las transiciones
        while (!fin) {
            // Se supone que hemos terminado hasta que encontremos una transición que disparar
            fin = true;

            for(int i=0; i<tamanio; i++){
                // Se verifica si se puede disparar una transicion
                if(this.procesosModelados.getRDP().disparo(finaltransiciones[i], true)) {
                    // Se realiza la tarea despues de disparar la transicion
                    this.procesosModelados.realizarTarea(finaltransiciones[i]);

                    // Se setea en false porque aun no se termino
                    fin = false;
                }
            }
        }

        // Se imprime por consola los tokens finales, el contador de transiciones y que la ejecucion del programa finalizo
        System.out.printf("Tokens finales %s\n", this.procesosModelados.getRDP().getTokens());
        System.out.printf("Contador final de transiciones %s\n", Arrays.toString(this.procesosModelados.getContadorDisparoTransiciones()));
        System.out.println("La ejecucion del programa ha finalizado");
    }
}
