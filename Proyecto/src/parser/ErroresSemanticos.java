import java.util.ArrayList;
import java.util.List;

public class ErroresSemanticos {

    public static List<String> errores = new ArrayList<>();

    public static void agregar(String mensaje) {
        errores.add(mensaje);
    }

    public static boolean hayErrores() {
        return !errores.isEmpty();
    }

    public static void imprimir() {
        System.out.println("\n[ ERRORES SEMÁNTICOS ]");
        System.out.println("──────────────────────────────────────────────────────");

        if (errores.isEmpty()) {
            System.out.println(" Sin errores semánticos.");
        } else {
            for (String error : errores) {
                System.out.println(error);
            }
        }
    }

    public static void limpiar() {
        errores.clear();
    }

    public static List<String> obtenerErrores() {
        return errores;
    }
}