import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodigoIntermedio {

    private static List<String> instrucciones = new ArrayList<>();
    private static int contadorTemporales = 0;
    private static HashMap<String, Integer> contadoresEtiquetas = new HashMap<>();

    public static void emitir(String instruccion) {
        instrucciones.add(instruccion);
    }

    public static String nuevoTemporal() {
        return "t" + (++contadorTemporales);
    }

    
    public static String nuevaEtiqueta(String prefijo) {
        int contador = contadoresEtiquetas.getOrDefault(prefijo, 0) + 1;
        contadoresEtiquetas.put(prefijo, contador);
        return prefijo + "_" + contador;
    }

    
    public static String nuevaEtiqueta() {
        return nuevaEtiqueta("L");
    }

    public static List<String> getInstrucciones() {
        return instrucciones;
    }

    public static void limpiar() {
        instrucciones.clear();
        contadorTemporales = 0;
        contadoresEtiquetas.clear();
    }
}