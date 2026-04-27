import java.util.*

public class TablaSimbolos{
   
    public static class simbolo{

        public string nombreToken; 
        public string tipoSimbolo;
        public string fila; 
        public columna; 
        public Simbolo(String nombre, String categoria, String linea, String columna) {
           this.nombre    = nombre;
            this.categoria = categoria;
            this.linea     = linea;
            this.columna   = columna;
        }
    }
    private static LinkedHashMap<String, List<Simbolo>> tablas   = new LinkedHashMap<>();
    private static Deque<String>                        scopes   = new ArrayDeque<>();
    // private static Map<String, Integer>                 contadores = new HashMap<>();
    
    public static void Scopes (String nombre){
        String scopePadre = scopes.isEmpty()? "": scopes.peek();
        String clave = nombre + (padre.isEmpty() ? "" : "_" + padre);
        int n = contadores.getOrDefault(clave, 0) + 1;
        contadores.put(clave, n);
        if (n > 1) clave = nombre + n + (padre.isEmpty() ? "" : "_" + padre);

        scopes.push(clave);
        tablas.put(clave, new ArrayList<>());
    }
    public static void salirScope() {
        if (!scopes.isEmpty()) scopes.pop();
    }

    public static String scopeActual() {
        return scopes.isEmpty() ? "global" : scopes.peek();
    }

    public static void agregar(String nombre, String categoria,
                            String linea, String columna) {

        String scope = scopeActual();

      
        if (!tablas.containsKey(scope)) {
            tablas.put(scope, new ArrayList<>());
        }

        // Agrega el símbolo al scope actual
        tablas.get(scope).add(
            new Simbolo(nombre, categoria, linea, columna)
        );
    }

    public static void limpiar() {
        tablas.clear();
        scopes.clear();
        contadores.clear();
    }

     public static void imprimir() {
    
        for (Map.Entry<String, List<Simbolo>> entry : tablas.entrySet()) {
            System.out.println("\n  Scope: [ " + entry.getKey() + " ]");
            System.out.println("  " + "─".repeat(54));
            System.out.printf("  %-20s %-18s %-7s %-7s%n",
                "NOMBRE", "CATEGORA", "LNEA", "COLUMNA");
            System.out.println("  " + "─".repeat(54));

            if (entry.getValue().isEmpty()) {
                System.out.println("  (sin identificadores)");
            } else {
                for (Simbolo s : entry.getValue()) {
                    System.out.printf("  %-20s %-18s %-7s %-7s%n",
                        s.nombre, s.categoria, s.linea, s.columna);
                }
            }
        }
    }
}





