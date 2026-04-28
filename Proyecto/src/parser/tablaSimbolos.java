import java.util.*;

public class TablaSimbolos {

    public static class Simbolo {
        public String nombreToken;
        public String tipoSimbolo;
        public String fila;
        public String columna;

        public Simbolo(String nombreToken, String tipoSimbolo, String fila, String columna) {
            this.nombreToken = nombreToken;
            this.tipoSimbolo = tipoSimbolo;
            this.fila = fila;
            this.columna = columna;
        }
    }

    private static LinkedHashMap<String, List<Simbolo>> tablas = new LinkedHashMap<>();
    private static Deque<String> scopes = new ArrayDeque<>();
    private static Map<String, Integer> contadores = new HashMap<>();

    public static void entrarScope(String nombre) {
        String padre = scopes.isEmpty() ? "" : scopes.peek();

        String claveBase = nombre + (padre.isEmpty() ? "" : "_" + padre);

        int n = contadores.getOrDefault(claveBase, 0) + 1;
        contadores.put(claveBase, n);

        String clave = claveBase;
        if (n > 1) {
            clave = nombre  + n+(padre.isEmpty() ? "" : "_" + padre);
        }

        scopes.push(clave);
        tablas.put(clave, new ArrayList<>());
    }

    public static void salirScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    public static String scopeActual() {
        return scopes.isEmpty() ? "global" : scopes.peek();
    }

    public static void agregar(String nombreToken, String tipoSimbolo, String fila, String columna) {
        if (tipoSimbolo.equals("LLAMADA")) return;  

        String scope = scopeActual();

        if (!tablas.containsKey(scope)) {
            tablas.put(scope, new ArrayList<>());
        }

        tablas.get(scope).add(
            new Simbolo(nombreToken, tipoSimbolo, fila, columna)
        );
    }

    public static void limpiar() {
        tablas.clear();
        scopes.clear();
        contadores.clear();
    }

    public static void imprimir() {
        for (Map.Entry<String, List<Simbolo>> entry : tablas.entrySet()) {
            System.out.println("\nScope: [ " + entry.getKey() + " ]");
            System.out.println("─".repeat(60));

            System.out.printf("%-20s %-18s %-10s %-10s%n",
                    "TOKEN", "TIPO", "FILA", "COLUMNA");

            System.out.println("─".repeat(60));

            if (entry.getValue().isEmpty()) {
                System.out.println("(sin identificadores)");
            } else {
                for (Simbolo s : entry.getValue()) {
                    System.out.printf("%-20s %-18s %-10s %-10s%n",
                            s.nombreToken,
                            s.tipoSimbolo,
                            s.fila,
                            s.columna);
                }
            }
        }
    }
}