package planificador;

import java.util.List;

/**
 * Round Robin: cola FIFO y un quantum máximo por turno.
 *
 * Elige igual que FCFS (la cabeza de la cola), pero desaloja al proceso cuando
 * ha usado {@code quantum} unidades seguidas. El bucle común de
 * {@link Planificador} se encarga de las reglas 3 (el que llega en t entra en
 * la cola antes que el desalojado en t) y 4 (si nadie espera, sigue en CPU).
 */
public class PlanificadorRR extends Planificador {

    private final int quantum;

    public PlanificadorRR(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException("El quantum debe ser mayor que cero: " + quantum);
        }
        this.quantum = quantum;
    }

    @Override
    public String getNombre() {
        return "Round Robin (q=" + quantum + ")";
    }

    @Override
    protected Proceso elegir(List<Proceso> listos) {
        return listos.get(0);
    }

    @Override
    protected boolean agotaTurno(int usadoEnTurno) {
        return usadoEnTurno >= quantum;
    }

    @Override
    protected String motivoDesalojo() {
        return "agota el quantum";
    }

    public int getQuantum() {
        return quantum;
    }
}
