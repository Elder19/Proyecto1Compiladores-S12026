public class InfoMatriz {
    public int filas;
    public int columnas;
    public boolean error;

    public InfoMatriz(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.error = false;
    }

    public InfoMatriz(int filas, int columnas, boolean error) {
        this.filas = filas;
        this.columnas = columnas;
        this.error = error;
    }
}