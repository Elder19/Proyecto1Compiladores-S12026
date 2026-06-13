import java.util.ArrayList;
import java.util.List;

public class ArgsFuncion {
    public List<String> tipos = new ArrayList<>();
    public List<String> valores = new ArrayList<>();

    public void agregar(String tipo, String valor) {
        tipos.add(tipo);
        valores.add(valor);
    }

    public void agregarTodos(ArgsFuncion otros) {
        tipos.addAll(otros.tipos);
        valores.addAll(otros.valores);
    }

    public int cantidad() {
        return tipos.size();
    }
}