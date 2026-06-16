import java.util.ArrayList;
import java.util.List;

public class InfoMatriz {
    public int filas;
    public int columnas;
    public boolean error;
    public List valores;

    public InfoMatriz(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.error = false;
        this.valores = new ArrayList();
    }

    public InfoMatriz(int filas, int columnas, boolean error) {
        this.filas = filas;
        this.columnas = columnas;
        this.error = error;
        this.valores = new ArrayList();
    }

    public InfoMatriz(int filas, int columnas, boolean error, List valores) {
        this.filas = filas;
        this.columnas = columnas;
        this.error = error;
        this.valores = valores;
    }
}