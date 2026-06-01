public class TipoInfo {
    public String tipo;
    public String op;
    public int linea;
    public int columna;

    public TipoInfo(String tipo, String op, int linea, int columna) {
        this.tipo   = tipo;
        this.op     = op;
        this.linea  = linea;
        this.columna = columna;
    }

    // Constructor sin operador (para resultados finales)
    public TipoInfo(String tipo, int linea, int columna) {
        this(tipo, "", linea, columna);
    }

    public boolean esVacio()  { return "vacio".equals(tipo); }
    public boolean esError()  { return "error".equals(tipo); }

    public static final TipoInfo VACIO = new TipoInfo("vacio", "", 0, 0);
    public static final TipoInfo ERROR = new TipoInfo("error", "", 0, 0);
}