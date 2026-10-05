package planificador;

/**
 * Error en el contenido del fichero de procesos. El mensaje indica la línea
 * y qué es lo que está mal, para que el usuario pueda corregirlo.
 */
public class ErrorFormatoException extends Exception {

    private static final long serialVersionUID = 1L;

    public ErrorFormatoException(String mensaje) {
        super(mensaje);
    }
}
