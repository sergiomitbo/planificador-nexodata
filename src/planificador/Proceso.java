package planificador;

/**
 * Un proceso tal y como lo ve el planificador de corto plazo.
 *
 * Los datos que vienen del fichero (nombre, llegada, ráfaga y posición en el
 * fichero) son inmutables. Lo que cambia durante una simulación (tiempo
 * restante, estado, primer uso de CPU y fin) se reinicia en cada copia, de modo
 * que cada algoritmo trabaja sobre sus propias copias y los datos originales
 * nunca se alteran.
 */
public class Proceso {

    private final String nombre;
    private final int llegada;
    private final int rafaga;
    /** Posición en el fichero (0, 1, 2...). Desempata llegadas simultáneas. */
    private final int orden;

    private int restante;
    private EstadoProceso estado;
    /** Instante en que usa la CPU por primera vez; -1 si aún no la ha usado. */
    private int inicio;
    /** Instante en que completa la ráfaga; -1 si aún no ha terminado. */
    private int fin;

    public Proceso(String nombre, int llegada, int rafaga, int orden) {
        this.nombre = nombre;
        this.llegada = llegada;
        this.rafaga = rafaga;
        this.orden = orden;
        this.restante = rafaga;
        this.estado = EstadoProceso.NUEVO;
        this.inicio = -1;
        this.fin = -1;
    }

    /** Copia "limpia": mismos datos del fichero, estado de simulación reiniciado. */
    public Proceso copia() {
        return new Proceso(nombre, llegada, rafaga, orden);
    }

    // --- Operaciones que usa el simulador -------------------------------

    public void cambiarEstado(EstadoProceso nuevo) {
        this.estado = nuevo;
    }

    /** Ejecuta una unidad de tiempo de CPU. */
    public void ejecutarUnaUnidad() {
        if (restante <= 0) {
            throw new IllegalStateException(nombre + " no tiene ráfaga pendiente");
        }
        restante--;
    }

    public boolean haTerminadoRafaga() {
        return restante == 0;
    }

    public void registrarInicio(int t) {
        if (inicio < 0) {
            inicio = t;
        }
    }

    public void registrarFin(int t) {
        fin = t;
    }

    // --- Métricas (válidas cuando el proceso ha terminado) ---------------

    /** Retorno = fin − llegada. */
    public int getRetorno() {
        return fin - llegada;
    }

    /** Espera = retorno − ráfaga (tiempo total en la cola de listos). */
    public int getEspera() {
        return getRetorno() - rafaga;
    }

    /** Respuesta = primer uso de CPU − llegada. */
    public int getRespuesta() {
        return inicio - llegada;
    }

    // --- Getters ---------------------------------------------------------

    public String getNombre() { return nombre; }
    public int getLlegada() { return llegada; }
    public int getRafaga() { return rafaga; }
    public int getOrden() { return orden; }
    public int getRestante() { return restante; }
    public EstadoProceso getEstado() { return estado; }
    public int getInicio() { return inicio; }
    public int getFin() { return fin; }

    @Override
    public String toString() {
        return nombre + "(llegada=" + llegada + ", ráfaga=" + rafaga + ", restante=" + restante
                + ", estado=" + estado + ")";
    }
}
