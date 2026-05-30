import java.util.ArrayList;
import java.util.List;

public class CodigoIntermedio {

    private static List<String> instrucciones = new ArrayList<>();
    private static int contadorTemporales = 0;
    private static int contadorEtiquetas = 0;

    public static void emitir(String instruccion) {
        instrucciones.add(instruccion);
    }

    public static String nuevoTemporal() {
        return "t" + (++contadorTemporales);
    }

    public static String nuevaEtiqueta() {
        return "L" + (++contadorEtiquetas);
    }

    public static List<String> getInstrucciones() {
        return instrucciones;
    }

    public static void limpiar() {
        instrucciones.clear();
        contadorTemporales = 0;
        contadorEtiquetas  = 0;
    }
}