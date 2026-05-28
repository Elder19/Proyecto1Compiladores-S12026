import java.util.*;

public class TablaSimbolos {

    public static class Simbolo {
        public String nombreToken;
        public String tipoSimbolo;
        public String fila;
        public String columna;
        public String tipoDato;

        public Simbolo(String nombreToken, String tipoSimbolo, String fila, String columna, String tipoDato) {
            this.nombreToken = nombreToken;
            this.tipoSimbolo = tipoSimbolo;
            this.fila = fila;
            this.columna = columna;
            this.tipoDato = tipoDato;
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
            clave = nombre + n + (padre.isEmpty() ? "" : "_" + padre);
        }

        scopes.push(clave);
        tablas.putIfAbsent(clave, new ArrayList<>());
    }

    public static void salirScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    public static String scopeActual() {
        return scopes.isEmpty() ? "global" : scopes.peek();
    }

    public static boolean existeEnScopeActual(String nombreToken) {
        String scope = scopeActual();

        if (!tablas.containsKey(scope)) {
            return false;
        }

        for (Simbolo s : tablas.get(scope)) {
            if (s.nombreToken.equals(nombreToken)) {
                return true;
            }
        }

        return false;
    }

    public static Simbolo buscar(String nombreToken) {
        for (String scope : scopes) {
            if (tablas.containsKey(scope)) {
                for (Simbolo s : tablas.get(scope)) {
                    if (s.nombreToken.equals(nombreToken)) {
                        return s;
                    }
                }
            }
        }

        if (tablas.containsKey("global")) {
            for (Simbolo s : tablas.get("global")) {
                if (s.nombreToken.equals(nombreToken)) {
                    return s;
                }
            }
        }

        return null;
    }

    public static Simbolo buscarFuncion(String nombreToken) {
        if (!tablas.containsKey("global")) {
            return null;
        }

        for (Simbolo s : tablas.get("global")) {
            if (s.nombreToken.equals(nombreToken) && s.tipoSimbolo.equals("FUNCION")) {
                return s;
            }
        }

        return null;
    }

    public static void agregar(String nombreToken, String tipoSimbolo, String fila, String columna, String tipoDato) {
        if ("LLAMADA".equals(tipoSimbolo)) {
            return;
        }

        String scope = scopeActual();

        if (!tablas.containsKey(scope)) {
            tablas.put(scope, new ArrayList<>());
        }

        if (existeEnScopeActual(nombreToken)) {
            ErroresSemanticos.agregar(
                "Error semántico en fila " + fila +
                ", columna " + columna +
                ": el identificador '" + nombreToken +
                "' ya fue declarado en el scope '" + scope + "'."
            );
            return;
        }

        tablas.get(scope).add(
            new Simbolo(nombreToken, tipoSimbolo, fila, columna, tipoDato)
        );
    }

    public static void limpiar() {
        tablas.clear();
        scopes.clear();
        contadores.clear();
    }

    public static void imprimir() {
        System.out.println("\n[ TABLA DE SÍMBOLOS ]");

        if (tablas.isEmpty()) {
            System.out.println("(tabla vacía)");
            return;
        }

        for (Map.Entry<String, List<Simbolo>> entry : tablas.entrySet()) {
            System.out.println("\nScope: [ " + entry.getKey() + " ]");
            System.out.println("─".repeat(70));

            System.out.printf(
                "%-20s %-18s %-12s %-10s %-10s%n",
                "TOKEN", "TIPO", "TIPO_DATO", "FILA", "COLUMNA"
            );

            System.out.println("─".repeat(70));

            if (entry.getValue().isEmpty()) {
                System.out.println("(sin identificadores)");
            } else {
                for (Simbolo s : entry.getValue()) {
                    System.out.printf(
                        "%-20s %-18s %-12s %-10s %-10s%n",
                        s.nombreToken,
                        s.tipoSimbolo,
                        s.tipoDato,
                        s.fila,
                        s.columna
                    );
                }
            }
        }
    }
}