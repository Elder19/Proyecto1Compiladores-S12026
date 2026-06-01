
public class Resultado {
    public String valor;
    public String tipo;

    public Resultado(String valor, String tipo) {
        this.valor = valor;
        this.tipo  = tipo;
    }

    // importante: toString devuelve el valor
    // para que CUP lo use correctamente
    @Override
    public String toString() {
        return valor;
    }
}