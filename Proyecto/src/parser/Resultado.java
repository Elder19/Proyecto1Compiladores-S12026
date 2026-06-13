/** Se utiliza para representar el resultado de una expresion
 * ya sea el valor asociado a la expresion o el tipo de datos
 */
public class Resultado {
    // puede ser un literal: identificador o un temporal (5, x, t1)
    public String valor;
    // Es el tipo del dato: float, int, bool, error, etc
    public String tipo;
    public String literal;

   public Resultado(String valor, String tipo) {
    this.valor = valor;
    this.tipo = tipo;
    this.literal = valor;
}
    public Resultado(String valor, String tipo, String literal) {
    this.valor = valor;
    this.tipo = tipo;
    this.literal = literal;
}

    // importante: toString devuelve el valor
    // para que CUP lo use correctamente
    @Override
    public String toString() {
        return valor;
    }
}