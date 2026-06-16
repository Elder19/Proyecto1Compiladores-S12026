public class Resultado {
    public String valor;
    public String tipo;
    public String literal;
    public String variableModificada;

    public Resultado(String valor, String tipo) {
        this.valor = valor;
        this.tipo = tipo;
        this.literal = valor;
        this.variableModificada = null;
    }

    public Resultado(String valor, String tipo, String literal) {
        this.valor = valor;
        this.tipo = tipo;
        this.literal = literal;
        this.variableModificada = null;
    }

    @Override
    public String toString() {
        return valor;
    }
}