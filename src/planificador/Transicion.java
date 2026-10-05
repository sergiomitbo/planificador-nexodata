package planificador;

/**
 * Una línea de la traza de estados: en el instante {@code t}, el proceso pasa
 * de {@code origen} a {@code destino} por el {@code motivo} indicado.
 *
 * Caso especial (regla 4 de Round Robin): si {@code origen == destino ==
 * EJECUCION}, no hay transición; el proceso sigue en la CPU con un quantum
 * nuevo. Se guarda para que la traza deje constancia de la decisión.
 */
public record Transicion(int t, String proceso, EstadoProceso origen, EstadoProceso destino, String motivo) {

    public boolean esContinuacion() {
        return origen == destino;
    }
}
