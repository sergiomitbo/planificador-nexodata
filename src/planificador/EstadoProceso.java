package planificador;

/**
 * Estados del modelo de cinco estados visto en clase.
 *
 * <pre>
 *  NUEVO --admitido--> LISTO --elegido--> EJECUCION --termina--> TERMINADO
 *                        ^  <--desalojado--   |
 *                        |                    | espera E/S
 *                        +---- BLOQUEADO <----+
 * </pre>
 *
 * El simulador nunca usa BLOQUEADO porque los trabajos de los ficheros solo
 * tienen ráfaga de CPU (no hacen E/S), pero el estado forma parte del modelo.
 */
public enum EstadoProceso {
    NUEVO,
    LISTO,
    EJECUCION,
    BLOQUEADO,
    TERMINADO
}
