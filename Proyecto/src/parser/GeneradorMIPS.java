import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

// Clase para generar código MIPS a partir de código intermedio
public class GeneradorMIPS {
    // Lista de instrucciones de código intermedio
    private List<String> instrucciones;
    private StringBuilder data;
    private StringBuilder text;

    // Mapa de variables a sus offsets en el stack
    private HashMap<String, Integer> offsetVars;
    private int offsetActual;

    // tipo de cada variable/temporal ("int" o "float")
    private HashMap<String, String> tipoVars;

    // arreglos en .data
    private HashMap<String, String> etiquetaArreglo; // nombre -> "arr_nombre"
    private HashMap<String, int[]> dimensionesArreglo; // nombre -> {filas, columnas}
    private HashMap<String, String> tipoArreglos;
    private HashMap<String, String> etiquetasCadenas = new HashMap<>();
    // contador de cadenas para generar etiquetas únicas
    private int contadorCadenas = 0;
    private int contadorLabelsMips = 0;
    // contador de temporales para generar etiquetas únicas
    private java.util.List<String> parametrosPendientes = new java.util.ArrayList<>();// contador de parametros
    private int indiceParametroActual = 0; // funciones

    private HashMap<String, String> tipoRetornoFunciones = new HashMap<>();
    private String funcionActualMips = "";
    private int contadorEtiquetas = 0;

    /* Genera una nueva etiqueta única para instrucciones MIPS */
    private String nuevaEtiquetaMips(String base) {
        return base + "_" + (contadorLabelsMips++);
    }
    /*
     * Constructor de la clase GeneradorMIPS se encarga de inicializar las
     * estructuras de datos necesarias
     * para la generación de código MIPS a partir de una lista de instrucciones de
     * código intermedio.
     */

    public GeneradorMIPS(List<String> instrucciones) {
        this.instrucciones = instrucciones;
        this.data = new StringBuilder();
        this.text = new StringBuilder();
        this.offsetVars = new HashMap<>();
        this.tipoVars = new HashMap<>();
        this.etiquetaArreglo = new HashMap<>();
        this.dimensionesArreglo = new HashMap<>();
        this.offsetActual = 0;
        this.tipoArreglos = new HashMap<>();
    }

    /*
     * metodo para emitir subrutinas de potencia se encarga de generar el código
     * MIPS para las subrutinas de
     * cálculo de potencia, tanto para enteros como para flotantes
     */
    private void emitirSubrutinasPotencia() {

        // ── Potencia entera ───────────────────────────────────────
        // función __potencia_int: calcula la potencia de un número entero base elevado
        // a un exponente entero.
        text.append("\n__potencia_int:\n");
        text.append("    addiu $sp, $sp, -8\n");
        text.append("    sw $ra, 4($sp)\n");
        text.append("    sw $fp, 0($sp)\n");
        text.append("    move $fp, $sp\n");
        text.append("    li $v0, 1\n");
        text.append("    ble $a1, $zero, __pot_int_fin\n");
        text.append("__pot_int_loop:\n");
        text.append("    mul $v0, $v0, $a0\n");
        text.append("    addiu $a1, $a1, -1\n");
        text.append("    bgt $a1, $zero, __pot_int_loop\n");
        text.append("__pot_int_fin:\n");
        text.append("    move $sp, $fp\n");
        text.append("    lw $fp, 0($sp)\n");
        text.append("    lw $ra, 4($sp)\n");
        text.append("    addiu $sp, $sp, 8\n");
        text.append("    jr $ra\n");

        // función __potencia_float: calcula la potencia de un número flotante base
        // elevado a un exponente entero.
        text.append("\n__potencia_float:\n");
        text.append("    addiu $sp, $sp, -8\n");
        text.append("    sw $ra, 4($sp)\n");
        text.append("    sw $fp, 0($sp)\n");
        text.append("    move $fp, $sp\n");
        text.append("    li $t9, 0x3f800000\n");
        text.append("    mtc1 $t9, $f0\n");
        text.append("    cvt.w.s $f14, $f14\n");
        text.append("    mfc1 $t0, $f14\n");
        text.append("    ble $t0, $zero, __pot_float_fin\n");
        text.append("__pot_float_loop:\n");
        text.append("    mul.s $f0, $f0, $f12\n");
        text.append("    addiu $t0, $t0, -1\n");
        text.append("    bgt $t0, $zero, __pot_float_loop\n");
        text.append("__pot_float_fin:\n");
        text.append("    move $sp, $fp\n");
        text.append("    lw $fp, 0($sp)\n");
        text.append("    lw $ra, 4($sp)\n");
        text.append("    addiu $sp, $sp, 8\n");
        text.append("    jr $ra\n");
    }

    /*
     * funcion para generar el código MIPS a partir de las instrucciones de código
     * intermedio y escribirlo en un archivo de salida.
     */
    public void generar(String rutaSalida) throws IOException {
        data.append(".data\n");
        data.append("    newline: .asciiz \"\\n\"\n");
        text.append("\n.text\n");
        text.append(".globl main\n");
        if (usaPotencia()) {
            emitirSubrutinasPotencia();
        }

        // Primer paso: registrar arreglos en .data
        // Recorre la lista de instrucciones y registra los arreglos en la sección .data
        // del código MIPS.
        for (String instruccion : instrucciones) {
            String inst = instruccion.trim();
            if (inst.startsWith("var int[] ") || inst.startsWith("var float[] ")) {
                registrarArregloEnData(inst);
            }
        }
        // Segundo paso: traducir instrucciones a MIPS
        // Recorre la lista de instrucciones nuevamente y traduce cada instrucción a
        // código MIPS.
        for (String instruccion : instrucciones) {
            String inst = instruccion.trim();

            traducir(inst);
        }

        PrintWriter writer = new PrintWriter(new FileWriter(rutaSalida));
        writer.print(data.toString());
        writer.print(text.toString());
        writer.close();
    }

    // funcion para registrar un arreglo en la sección .data del código MIPS
    // Recibe una instrucción de declaración de arreglo y extrae la información
    // necesaria para
    // generar la sección .data correspondiente en el código MIPS.
    // ejemplo: var int[] arr 3,4 lo divide en partes y obtiene el tipo base, nombre
    // y dimensiones del arreglo.
    private void registrarArregloEnData(String inst) {
        String[] partes = inst.split(" ");
        String tipoBase = partes[1].replace("[]", "");
        String nombre = partes[2];
        String[] dims = partes[3].split(",");
        int filas = Integer.parseInt(dims[0].trim());
        int columnas = Integer.parseInt(dims[1].trim());

        String etiqueta = "arr_" + nombre;
        int totalBytes = filas * columnas * 4;

        etiquetaArreglo.put(nombre, etiqueta);
        dimensionesArreglo.put(nombre, new int[] { filas, columnas });
        tipoArreglos.put(nombre, tipoBase);

        data.append("    .align 2\n");
        data.append("    " + etiqueta + ": .space " + totalBytes + "\n");
    }

    // funcion para traducir una instrucción de código intermedio a código MIPS
    // Recibe una instrucción de código intermedio y genera el código MIPS
    // correspondiente.
    // Maneja diferentes tipos de instrucciones, como declaraciones de funciones,
    // etiquetas, asignaciones, operaciones aritméticas y lógicas, llamadas a
    // funciones
    private void traducir(String inst) {
        // declaración de función: func tipo nombre
        if (inst.startsWith("func ") && inst.endsWith(":")) {
            String encabezado = inst.substring("func ".length(), inst.length() - 1).trim();
            String tipoFuncion = "int";
            String nombreFuncion = encabezado;
            String[] partes = encabezado.split("\\s+");

            if (partes.length == 2) {
                tipoFuncion = partes[0].trim();
                nombreFuncion = partes[1].trim();
            }
            // Guardar el tipo de retorno de la función y reiniciar el frame actual
            funcionActualMips = nombreFuncion;
            tipoRetornoFunciones.put(nombreFuncion, tipoFuncion);

            indiceParametroActual = 0;
            reiniciarFrameActual();
            // Generar el código MIPS para la declaración de la función
            // incluyendo la reserva de espacio en el stack y el almacenamiento de los
            // registros $ra y $fp
            text.append("\n").append(nombreFuncion).append(":\n");
            text.append("    addiu $sp, $sp, -8\n");
            text.append("    sw $ra, 4($sp)\n");
            text.append("    sw $fp, 0($sp)\n");
            text.append("    move $fp, $sp\n");

            return;
        }

        // etiqueta: nombre:
        // Maneja etiquetas en el código intermedio y genera el código MIPS
        // correspondiente.
        //
        if (inst.endsWith(":")) {
            String etiqueta = inst.substring(0, inst.length() - 1).trim();

            if (etiqueta.equals("main")) {
                funcionActualMips = "main";
                indiceParametroActual = 0;
                reiniciarFrameActual();

                text.append("\nmain:\n");
                text.append("    move $fp, $sp\n");
            } else if (etiqueta.equals("main_end")) {
                text.append("\nmain_end:\n");
                text.append("    li $v0, 10\n");
                text.append("    syscall\n");
            } else {
                text.append("\n").append(etiqueta).append(":\n");
            }
            return;
        }
        // definición de parámetro
        // Maneja la definición de parámetros de funciones y genera el código MIPS
        // correspondiente.
        if (inst.startsWith("param_def ")) {
            escribirParamDef(inst);
            return;
        }
        // fin de función: end func
        // Maneja el final de una función y genera el código MIPS correspondiente para
        if (inst.startsWith("end func ")) {
            return;
        }
        // Maneja las instrucciones de impresión y lectura de variables
        if (inst.startsWith("print ")) {
            escribirPrint(inst);
            return;
        }
        // Maneja las instrucciones de lectura de variables
        if (inst.startsWith("read ")) {
            escribirRead(inst);
            return;
        }
        // Maneja las instrucciones de salida y entrada de datos
        if (inst.startsWith("cout ")) {
            escribirCout(inst);
            return;
        }
        // Maneja las instrucciones de entrada de datos
        if (inst.startsWith("cin ")) {
            escribirCin(inst);
            return;
        }
        // Maneja las instrucciones de retorno de funciones
        if (inst.startsWith("if_true ")) {
            emitirIfTrue(inst);
            return;
        }
        // Maneja las instrucciones de retorno de funciones
        if (inst.startsWith("return ")) {
            escribirReturn(inst);
            return;
        }
        // etiqueta para manejar saltos incondicionales en el código intermedio y
        // generar el código MIPS correspondiente.
        if (inst.startsWith("goto ")) {
            String etiqueta = inst.substring("goto ".length()).trim();
            text.append("    j ").append(etiqueta).append("\n");
            return;
        }
        // etiqueta para maneja if false en el código intermedio y genera el código MIPS
        // correspondiente.
        if (inst.startsWith("if_false ")) {
            emitirIfFalse(inst);
            return;
        }

        // q Maneja la declaración de variables simples y arreglos, generando el código
        // MIPS correspondiente para reservar espacio en el stack y almacenar los
        // valores de las variables.
        if (inst.startsWith("var int[] ") || inst.startsWith("var float[] ")) {
            return; // ya está en .data
        }

        // Maneja la declaración de variables simples, generando el código MIPS
        // LO HACE RESERVANDO ESPACIO EN EL STACK Y ALMACENANDO LOS VALORES DE LAS
        // VARIABLES.
        if (inst.startsWith("var ")) {
            String[] partes = inst.split(" ");
            String tipo = partes[1];
            String nombre = partes[2];
            offsetActual -= 4;
            offsetVars.put(nombre, offsetActual);
            tipoVars.put(nombre, tipo);
            text.append("    addiu $sp, $sp, -4  # " + nombre + " (" + tipo + ") @ " + offsetActual + "($fp)\n");
            return;
        }

        // Maneja la escritura en arreglos, generando el código MIPS correspondiente
        // para calcular la dirección del elemento del arreglo y almacenar el valor en
        // esa posición.
        if (inst.matches("^\\w+\\[.+\\]\\[.+\\]\\s*=\\s*.+$") && !inst.contains("==")) {
            int posEq = inst.indexOf(" = ");
            String ladoIzq = inst.substring(0, posEq).trim();// obtiene el lado izquierdo de la asignación
            String fuente = inst.substring(posEq + 3).trim();
            String nombreArr = ladoIzq.substring(0, ladoIzq.indexOf('['));
            String resto = ladoIzq.substring(ladoIzq.indexOf('['));// obtiene los índices del arreglo
            int c1 = resto.indexOf(']');
            String idxI = resto.substring(1, c1).trim();// obtiene el índice i del arreglo
            String idxJ = resto.substring(resto.indexOf('[', c1) + 1, resto.lastIndexOf(']')).trim();// obtiene el
                                                                                                     // índice j del
                                                                                                     // arreglo
            emitirEscrituraArreglo(nombreArr, idxI, idxJ, fuente);
            return;
        }

        // Maneja la lectura de arreglos, generando el código MIPS correspondiente
        if (inst.matches("^\\w+\\s*=\\s*\\w+\\[.+\\]\\[.+\\]$") && !inst.contains("==")) {
            int posEq = inst.indexOf(" = ");
            String dest = inst.substring(0, posEq).trim();// obtiene el lado izquierdo de la asignación
            String ladoDer = inst.substring(posEq + 3).trim();
            String nombreArr = ladoDer.substring(0, ladoDer.indexOf('['));
            String resto = ladoDer.substring(ladoDer.indexOf('['));
            int c1 = resto.indexOf(']');
            String idxI = resto.substring(1, c1).trim();
            String idxJ = resto.substring(resto.indexOf('[', c1) + 1, resto.lastIndexOf(']')).trim();
            emitirLecturaArreglo(nombreArr, idxI, idxJ, dest);
            return;
        }
        // Maneja las instrucciones de retorno de funciones, generando el código MIPS me
        // diante la función escribirReturn.
        if (inst.startsWith("return ")) {
            escribirReturn(inst);
            return;
        }
        // Maneja la definición de parámetros de funciones, generando el código MIPS
        // mediante la función escribirParamDef.
        if (inst.startsWith("param_def ")) {
            escribirParamDef(inst);
            return;
        }

        // Maneja las asignaciones y operaciones aritméticas y lógicas, generando el
        // código MIPS correspondiente para cargar los valores de las variables,
        // realizar las operaciones y almacenar los resultados.
        if (inst.contains(" = ")) {
            String[] partes = inst.split(" = ", 2);// divide la instrucción en dos partes: el lado izquierdo y el lado
                                                   // derecho de la asignación
            String dest = partes[0].trim();
            String fuente = partes[1].trim();
            // ── asignación de string literal: t1 = "Hola" ─────────
            // usa esCadenaLiteral para verificar si la fuente es un literal de cadena y, en
            // caso afirmativo, obtiene una etiqueta única para la cadena y genera el código
            // MIPS correspondiente para cargar la dirección de la cadena en un registro y
            // almacenarla en la variable destino ejemplo: t1 = "Hola" se traduce a la
            // etiqueta str_0 y se carga en $t0 y se guarda en t1.
            if (esCadenaLiteral(fuente)) {
                String etiqueta = obtenerEtiquetaCadena(fuente);

                tipoVars.put(dest, "string");
                etiquetasCadenas.put(dest, etiqueta);

                text.append("    la $t0, ").append(etiqueta).append("\n");
                guardarInt("$t0", dest);

                return;
            }
            // ── llamada a función: t1 = call f(a, b)
            if (fuente.startsWith("call ")) {
                Escribir_llamada(fuente, dest);// llama a la función Escribir_llamada para generar el código MIPS
                                               // correspondiente a la llamada a función
                return;
            }
            // asignación de variable: t1 = t2

            if (tipoVars.containsKey(fuente)) {
                String tipoFuente = tipoVars.get(fuente);
                tipoVars.put(dest, tipoFuente);
                // Maneja la asignación de variables, generando el código MIPS correspondiente
                // para cargar el valor de la variable fuente en un registro y almacenarlo en la
                // variable destino.
                // Dependiendo del tipo de la variable fuente (float o int), se utilizan
                // diferentes registros y funciones para cargar y guardar los valores.
                if ("float".equals(tipoFuente)) {
                    cargarFloat(fuente, "$f0");
                    guardarFloat("$f0", dest);
                } else {
                    // int, bool, char y string se copian con registros enteros.
                    // En string se copia la dirección.
                    cargarInt(fuente, "$t0");
                    guardarInt("$t0", dest);
                }

                return;
            }
            // ── NOT lógico: t1 = !a, t1 = $a ─────────
            if (fuente.startsWith("!") || fuente.startsWith("$")) {
                String valor = fuente.substring(1).trim();
                escribirNot(valor, dest);// llama a la función escribirNot para generar el código MIPS correspondiente a
                                         // la operación NOT lógico
                return;
            }

            // ── operadores lógicos binarios: &&, ||, @, #
            for (String opLog : new String[] { " && ", " || ", " @ ", " # " }) {
                if (fuente.contains(opLog)) {
                    String[] ops = fuente.split(Pattern.quote(opLog), 2);
                    String izq = ops[0].trim();
                    String der = ops[1].trim();
                    escribirLogicoBinario(izq, opLog.trim(), der, dest);
                    return;
                }
            }

            // ── comparación relacional: t1 = a > b, t1 = a == b, etc.
            for (String opRel : new String[] { " >= ", " <= ", " == ", " != ", " > ", " < " }) {
                if (fuente.contains(opRel)) {
                    String[] ops = fuente.split(Pattern.quote(opRel), 2);
                    String izq = ops[0].trim();
                    String der = ops[1].trim();
                    String operador = opRel.trim();
                    boolean flt = esFloat(izq) || esFloat(der);
                    if (flt) {
                        EscribirComparacionFloat(izq, operador, der, dest);
                    } else {
                        cargarInt(izq, "$t0");
                        cargarInt(der, "$t1");
                        switch (operador) {
                            case ">":
                                text.append("    sgt $t2, $t0, $t1\n");
                                break;
                            case "<":
                                text.append("    slt $t2, $t0, $t1\n");
                                break;
                            case ">=":
                                text.append("    sge $t2, $t0, $t1\n");
                                break;
                            case "<=":
                                text.append("    sle $t2, $t0, $t1\n");
                                break;
                            case "==":
                                text.append("    seq $t2, $t0, $t1\n");
                                break;
                            case "!=":
                                text.append("    sne $t2, $t0, $t1\n");
                                break;
                        }
                        tipoVars.put(dest, "int");
                        guardarInt("$t2", dest);
                    }
                    return;
                }
            }

            // ── operación aritmética binaria: +, -, *, /, %, ^
            for (String opArit : new String[] { " + ", " - ", " * ", " / ", " % ", " ^ " }) {
                if (fuente.contains(opArit)) {
                    String[] ops = fuente.split(Pattern.quote(opArit), 2);
                    String izq = ops[0].trim();
                    String der = ops[1].trim();
                    boolean flt = esFloat(izq) || esFloat(der); // si alguno de los operandos es float, se realiza
                                                                // la operación como flotante
                    tipoVars.put(dest, flt ? "float" : "int");

                    // caso especial potencia
                    if (opArit.trim().equals("^")) {// si el operador es ^, se llama a la subrutina de potencia
                                                    // correspondiente según el tipo de los operandos
                        if (flt) {
                            cargarFloat(izq, "$f12");
                            cargarFloat(der, "$f14");
                            text.append("    jal __potencia_float\n");
                            guardarFloat("$f0", dest);
                        } else {
                            cargarInt(izq, "$a0");
                            cargarInt(der, "$a1");
                            text.append("    jal __potencia_int\n");
                            guardarInt("$v0", dest);
                        }
                        return;
                    }

                    if (flt) {// si alguno de los operandos es float, se realiza la operación como flotante
                        cargarFloat(izq, "$f0");
                        cargarFloat(der, "$f1");
                        String mips;
                        switch (opArit.trim()) {
                            case "+":
                                mips = "add.s";// suma flotante
                                break;
                            case "-":
                                mips = "sub.s";// resta flotante
                                break;
                            case "*":
                                mips = "mul.s";// multiplicación flotante
                                break;
                            default:
                                mips = "div.s";// división flotante
                                break;
                        }
                        text.append("    ").append(mips).append(" $f2, $f0, $f1\n");// realiza la operación flotante
                                                                                    // y guarda el resultado en $f2
                        guardarFloat("$f2", dest);
                    } else {
                        cargarInt(izq, "$t0");
                        cargarInt(der, "$t1");
                        switch (opArit.trim()) {
                            case "/":
                                text.append("    div $t0, $t1\n    mflo $t2\n");
                                break;
                            case "%":
                                text.append("    div $t0, $t1\n    mfhi $t2\n");
                                break;
                            case "+":
                                text.append("    add $t2, $t0, $t1\n");
                                break;
                            case "-":
                                text.append("    sub $t2, $t0, $t1\n");
                                break;
                            default:
                                text.append("    mul $t2, $t0, $t1\n");
                                break;
                        }
                        guardarInt("$t2", dest);
                    }
                    return;
                }
            }

            // ── asignación simple: dest = fuente
            // (solo se llega aquí si NINGÚN operador lógico, relacional ni
            // aritmético matcheó en los bucles anteriores)
            boolean flt = esFloat(fuente);
            if (!tipoVars.containsKey(dest)) {
                tipoVars.put(dest, flt ? "float" : "int");
            }
            if ("float".equals(tipoVars.get(dest))) {
                cargarFloat(fuente, "$f0");
                guardarFloat("$f0", dest);
            } else {
                cargarInt(fuente, "$t0");
                guardarInt("$t0", dest);
            }
            return;
        }
        // Maneja la definición de parámetros pendientes, agregándolos a la lista de
        // parámetros pendientes para su posterior procesamiento
        // ademas, si la instrucción comienza con "param ", se extrae el valor del
        // parámetro y se agrega a la lista de parámetros pendientes.
        if (inst.startsWith("param "))

        {
            String valor = inst.substring("param ".length()).trim();
            parametrosPendientes.add(valor);
            return;
        }

    }

    // if true metodo para trabajar la etiqueta if true

    private void emitirIfTrue(String inst) {
        String resto = inst.substring("if_true ".length()).trim();

        int posGoto = resto.lastIndexOf(" goto ");

        if (posGoto == -1) {
            text.append("    # ERROR if_true mal formado: ").append(inst).append("\n");
            return;
        }

        String condicion = resto.substring(0, posGoto).trim(); // guarda el valor de la condicion
        String etiqueta = resto.substring(posGoto + " goto ".length()).trim(); // guarda el valor de la etiqueta a la
                                                                               // que se va a saltar

        cargarInt(condicion, "$t0"); // metodo para cargar el valor a un registro.

        text.append("    bne $t0, $zero, ")
                .append(etiqueta)
                .append("\n");
    }

    /// if false metodo para trabajar la etiqueta if false
    // Recibe una instrucción de código intermedio que representa un salto
    // condicional
    // y genera el código MIPS correspondiente para realizar el salto si la
    // condición es falsa.
    private void emitirIfFalse(String inst) {

        String resto = inst.substring("if_false ".length()).trim();// toma la intruccion despues del if false

        int posGoto = resto.lastIndexOf(" goto ");

        if (posGoto == -1) {
            text.append("    # ERROR if_false mal formado: ").append(inst).append("\n");// si no encuentra la palabra
                                                                                        // goto, imprime un mensaje de
                                                                                        // error
            return;
        }

        String condicion = resto.substring(0, posGoto).trim();// guarda el valor de la condicion
        String etiqueta = resto.substring(posGoto + " goto ".length()).trim();// guarda el valor de la etiqueta a la que
                                                                              // se va a saltar

        cargarInt(condicion, "$t0");// metodo para cargar el valor a un registro.

        text.append("    beq $t0, $zero, ").append(etiqueta).append("\n");
    }

    // ── Arreglos ──────────────────────────────────────────────
    // calcula la dirección de un elemento de un arreglo en memoria
    private void calcularDireccionArreglo(String nombreArr, String idxI, String idxJ) {
        int columnas = dimensionesArreglo.get(nombreArr)[1];// obtiene el número de columnas del arreglo a partir del
                                                            // mapa dimensionesArreglo
        String label = etiquetaArreglo.get(nombreArr); // obtiene la etiqueta del arreglo a partir del mapa
                                                       // etiquetaArreglo

        text.append("    la $t8, " + label + "\n"); // carga la dirección base del arreglo en $t8
        cargarInt(idxI, "$t6");// carga el índice i en $t6
        cargarInt(idxJ, "$t7");// carga el índice j en $t7
        text.append("    li $t5, " + columnas + "\n");// carga el número de columnas en $t5
        text.append("    mul $t5, $t6, $t5\n");// multiplica el índice i por el número de columnas para obtener el
                                               // desplazamiento en filas
        text.append("    add $t5, $t5, $t7\n");// suma el desplazamiento en filas con el índice j para obtener el
                                               // desplazamiento total
        text.append("    sll $t5, $t5, 2\n");// multiplica el desplazamiento total por 4 (tamaño de un entero) para
                                             // obtener el desplazamiento en bytes
        text.append("    add $t8, $t8, $t5\n");// suma la dirección base del arreglo con el desplazamiento en bytes para
                                               // obtener la dirección final del elemento del arreglo
    }

    // Maneja la escritura en arreglos, generando el código MIPS correspondiente
    // para calcular la dirección del elemento del arreglo y almacenar el valor en
    // esa posición.
    private void emitirEscrituraArreglo(String arr, String idxI, String idxJ, String fuente) {// calcula la dirección
                                                                                              // del elemento del
                                                                                              // arreglo y la almacena
                                                                                              // en $t8
        calcularDireccionArreglo(arr, idxI, idxJ);// llama a la función calcularDireccionArreglo para calcular la
                                                  // dirección del elemento del arreglo y almacenarla en $t8
        if ("float".equals(tipoArreglos.get(arr))) {
            cargarFloat(fuente, "$f0");
            text.append("    s.s $f0, 0($t8)\n");
        } else {
            cargarInt(fuente, "$t0");
            text.append("    sw $t0, 0($t8)\n");
        }
    }

    // Maneja la lectura de arreglos, generando el código MIPS correspondiente
    private void emitirLecturaArreglo(String arr, String idxI, String idxJ, String dest) {// calcula la dirección del
                                                                                          // elemento del arreglo y la
                                                                                          // almacena en $t8
        calcularDireccionArreglo(arr, idxI, idxJ);
        boolean flt = "float".equals(tipoArreglos.get(arr));
        tipoVars.put(dest, flt ? "float" : "int");
        if (flt) {
            text.append("    l.s $f0, 0($t8)\n");
            guardarFloat("$f0", dest);
        } else {
            text.append("    lw $t0, 0($t8)\n");
            guardarInt("$t0", dest);
        }
    }

    // ── Carga / guardado int ──────────────────────────────────
    // Maneja la carga de valores enteros, booleanos, caracteres y literales de
    // cadena en registros MIPS.
    private void cargarInt(String val, String reg) {
        // esCadenaLiteral(val) verifica si el valor es un literal de cadena y, en caso
        // afirmativo, obtiene una etiqueta única para la cadena y genera el código MIPS
        // correspondiente para cargar la dirección de la cadena en un registro.
        if (esCadenaLiteral(val)) {
            String etiqueta = obtenerEtiquetaCadena(val);
            text.append("    la ").append(reg).append(", ").append(etiqueta).append("\n");
            // si el valor es "true", se carga el valor 1 en el registro, y si es "false",
            // se carga el valor 0.
        } else if ("true".equals(val)) {
            text.append("    li ").append(reg).append(", 1\n");
        } else if ("false".equals(val)) {
            text.append("    li ").append(reg).append(", 0\n");
        } // si el valor es un literal de carácter, se obtiene el código ASCII del
          // carácter y se carga en el registro.
        else if (esCharLiteral(val)) {
            text.append("    li ").append(reg).append(", ")
                    .append(obtenerAsciiChar(val))
                    .append("\n");
        }
        // si el valor es un literal exponencial entero, se emite el código para manejar
        // la exponencial en base 10.
        else if (esExponencialEnteroLiteral(val)) {
            emitirExponencialBase10(val, reg);
        } else if (esEnteroLiteral(val)) {
            text.append("    li ").append(reg).append(", ").append(val).append("\n");
        } else {// si el valor es una variable, se obtiene su offset en el stack y se carga en
                // el registro.
            int off = obtenerOffset(val);
            text.append("    lw ").append(reg).append(", ")
                    .append(off)
                    .append("($fp)\n");
        }
    }

    // Maneja la guardado de valores enteros, booleanos, caracteres y literales de
    // cadena en registros MIPS.
    private void guardarInt(String reg, String dest) {
        int off = obtenerOffset(dest);
        text.append("    sw " + reg + ", " + off + "($fp)\n");// guarda el valor del registro en la dirección de memoria
                                                              // correspondiente a la variable destino
    }

    // ── Carga / guardado float ────────────────────────────────
    // Maneja la carga de valores flotantes en registros MIPS.
    private void cargarFloat(String val, String reg) {
        if (esFraccionLiteral(val)) {// si el valor es un literal de fracción, se evalúa la fracción y se convierte a
                                     // su representación en bits de punto flotante, luego se carga en el registro.
            float valor = evaluarFraccionLiteral(val);
            int bits = Float.floatToIntBits(valor);

            text.append("    li $t9, 0x")// se encarga el valor de la fracción en $t9 como un entero de 32 bits y luego
                                         // se mueve a un registro de punto flotante.
                    .append(Integer.toHexString(bits))
                    .append("\n");

            text.append("    mtc1 $t9, ") // mueve el valor de $t9 a un registro de punto flotante
                    .append(reg)
                    .append("\n");

            return;
        }
        // si el valor es un literal de punto flotante, se convierte a su representación
        // en bits de punto flotante y
        if (esFloatLiteral(val)) {
            float valor = Float.parseFloat(val);
            int bits = Float.floatToIntBits(valor);

            text.append("    li $t9, 0x")
                    .append(Integer.toHexString(bits))
                    .append("\n");

            text.append("    mtc1 $t9, ")
                    .append(reg)
                    .append("\n");

            return;
        }
        // si el valor es una variable, se obtiene su offset en el stack y se carga en
        // el registro de punto flotante.
        int off = obtenerOffset(val);
        text.append("    l.s ")
                .append(reg)
                .append(", ")
                .append(off)
                .append("($fp)\n");
    }

    // se usa para verificar si un valor es un literal de fracción, que tiene la
    // forma "numerador//denominador", donde numerador y denominador son enteros
    // opcionalmente con signo negativo.
    private boolean esFraccionLiteral(String val) {
        return val != null && val.matches("-?\\d+//-?\\d+");
    }

    // Maneja la guardado de valores flotantes en registros MIPS.
    private void guardarFloat(String reg, String dest) {
        int off = obtenerOffset(dest);
        text.append("    s.s " + reg + ", " + off + "($fp)\n");
    }

    // Reinicia el frame actual, limpiando los mapas de offset y tipo de variables,
    // y estableciendo el offset actual a cero. Esto se utiliza al inicio de una
    // nueva función para preparar el entorno de ejecución.
    private void reiniciarFrameActual() {
        offsetVars.clear();
        tipoVars.clear();
        offsetActual = 0;
    }

    // Obtiene el offset de una variable en el stack. Si la variable no tiene un
    // offset asignado, se decrementa el offset actual en 4 bytes (tamaño de una
    // palabra) y se asigna a la variable. Además, se genera el código MIPS para
    // ajustar el puntero de pila ($sp) hacia abajo para reservar espacio para la
    // variable.
    private int obtenerOffset(String nombre) {
        if (!offsetVars.containsKey(nombre)) {
            offsetActual -= 4;
            offsetVars.put(nombre, offsetActual);
            text.append("    addiu $sp, $sp, -4\n");
        }
        return offsetVars.get(nombre);
    }

    // ── Helpers de tipo ───────────────────────────────────────
    // Verifica si un valor es de tipo float, ya sea un literal de punto flotante o
    // una variable de tipo float.
    private boolean esFloat(String val) {
        if (esFloatLiteral(val))
            return true;
        if (esFraccionLiteral(val))
            return true;
        return "float".equals(tipoVars.get(val));
    }

    // Evalúa un literal de fracción en la forma "numerador//denominador" y devuelve
    // su valor como un número flotante.
    private float evaluarFraccionLiteral(String s) {
        String[] partes = s.split("//");

        float numerador = Float.parseFloat(partes[0]);
        float denominador = Float.parseFloat(partes[1]);

        return numerador / denominador;
    }
    // Verifica si un valor es un literal de entero, devolviendo true si puede ser
    // parseado como un entero, y false en caso contrario.

    private boolean esEnteroLiteral(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Verifica si un valor es un literal de entero en notación exponencial,
    // devolviendo true si puede ser parseado como un entero, y false en caso
    // contrario.
    private boolean esFloatLiteral(String s) {
        try {
            Double.parseDouble(s);
            return s.contains(".");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Verifica si un valor es un literal de cadena, devolviendo true si comienza y
    // termina con comillas dobles.
    private boolean esCadenaLiteral(String s) {
        return s != null && s.length() >= 2 &&
                s.startsWith("\"") && s.endsWith("\"");
    }

    // Verifica si un valor es un literal de carcter, devolviendo true si comienza y
    // termina con comillas simples.
    private boolean esCharLiteral(String s) {
        return s != null && s.length() >= 3 &&
                s.startsWith("'") && s.endsWith("'");
    }

    // obtiene el código ASCII de un carácter literal, devolviendo el valor entero
    // correspondiente al carácter entre comillas simples.
    private int obtenerAsciiChar(String literal) {

        if (literal.length() >= 3) {
            return (int) literal.charAt(1);
        }

        return 0;
    }

    // Obtiene una etiqueta única para un literal de cadena. Si el literal ya tiene
    // una etiqueta asignada, se devuelve esa etiqueta. De lo contrario, se genera
    // una nueva etiqueta, se almacena en el mapa de etiquetas y se agrega la
    // definición de la cadena al segmento de datos del código MIPS.
    private String obtenerEtiquetaCadena(String literal) {
        if (etiquetasCadenas.containsKey(literal)) {
            return etiquetasCadenas.get(literal);
        }

        String etiqueta = "str_" + contadorCadenas++;
        etiquetasCadenas.put(literal, etiqueta);

        data.append("    ")
                .append(etiqueta)
                .append(": .asciiz ")
                .append(literal)
                .append("\n");

        return etiqueta;
    }

    // Maneja la comparación de valores flotantes, generando el código MIPS
    // correspondiente para realizar la comparación y almacenar el resultado en una
    // variable destino. Dependiendo del operador de comparación, se utilizan
    // instrucciones MIPS específicas para comparar los valores flotantes cargados
    // en los registros $f0 y $f1. El resultado de la comparación se almacena en el
    // registro $t2, que luego se guarda en la variable destino.
    private void EscribirComparacionFloat(String izq, String op, String der, String dest) {
        String lTrue = nuevaEtiquetaMips("cmp_float_true");
        String lFin = nuevaEtiquetaMips("cmp_float_fin");

        cargarFloat(izq, "$f0");
        cargarFloat(der, "$f1");

        switch (op) {
            case "<":
                text.append("    c.lt.s $f0, $f1\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case "<=":
                text.append("    c.le.s $f0, $f1\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case ">":
                text.append("    c.lt.s $f1, $f0\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case ">=":
                text.append("    c.le.s $f1, $f0\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case "==":
                text.append("    c.eq.s $f0, $f1\n");
                text.append("    bc1t ").append(lTrue).append("\n");
                break;

            case "!=":
                text.append("    c.eq.s $f0, $f1\n");
                text.append("    bc1f ").append(lTrue).append("\n");
                break;
        }

        text.append("    li $t2, 0\n");
        text.append("    j ").append(lFin).append("\n");

        text.append(lTrue).append(":\n");
        text.append("    li $t2, 1\n");

        text.append(lFin).append(":\n");

        tipoVars.put(dest, "int");
        guardarInt("$t2", dest);
    }

    // Maneja las operaciones logicas binarias, generando el codigo MIPS
    // correspondiente para realizar la operación lógica entre dos operandos y
    // almacenar el resultado en una variable destino. Dependiendo del operador
    // lógico, se utilizan instrucciones MIPS específicas para realizar la operacion
    // AND o OR entre los valores cargados en los registros $t0 y $t1. El resultado
    // de la operacion se almacena en el registro $t2, que luego se guarda en la
    // variable destino.
    private void escribirLogicoBinario(String izq, String operador, String der, String dest) {

        cargarBoolComoInt(izq, "$t0");
        cargarBoolComoInt(der, "$t1");

        text.append("    sne $t0, $t0, $zero\n");
        text.append("    sne $t1, $t1, $zero\n");

        switch (operador) {
            case "&&":
            case "@":
                text.append("    and $t2, $t0, $t1\n");
                break;

            case "||":
            case "#":
                text.append("    or $t2, $t0, $t1\n");
                break;
        }

        tipoVars.put(dest, "bool");
        guardarInt("$t2", dest);
    }

    // Maneja la operación NOT lógico, generando el código MIPS correspondiente para
    // realizar la negación logica de un valor y almacenar el resultado en una
    // variable destino. Dependiendo del valor de la fuente, se carga como un entero
    // (1 para true, 0 para false) en el registro $t0. Luego, se utiliza la
    // instrucción "seq" para comparar el valor cargado con cero y almacenar el
    // resultado de la negación lógica en el registro $t2. Finalmente, se guarda el
    // resultado en la variable destino.
    private void escribirNot(String fuente, String dest) {

        cargarBoolComoInt(fuente, "$t0");

        text.append("    seq $t2, $t0, $zero\n");

        tipoVars.put(dest, "bool");
        guardarInt("$t2", dest);
    }

    // Maneja la carga de valores booleanos como enteros, generando el código MIPS
    // correspondiente para cargar el valor booleano en un registro como un entero
    // (1 para true, 0 para false). Dependiendo del valor de la fuente, se carga el
    // valor correspondiente en el registro especificado. Si el valor es "true", se
    // carga 1; si es "false", se carga 0; de lo contrario, se llama a la función
    // cargarInt para cargar el valor como un entero normal.
    private void cargarBoolComoInt(String val, String reg) {
        if ("true".equals(val)) {
            text.append("    li ").append(reg).append(", 1\n");
        } else if ("false".equals(val)) {
            text.append("    li ").append(reg).append(", 0\n");
        } else {
            cargarInt(val, reg);
        }
    }

    // Maneja la escritura de llamadas a funciones, generando el código MIPS
    // correspondiente para preparar los parámetros, realizar la llamada a la
    // función y almacenar el valor de retorno en una variable destino. Se procesan
    // los parámetros pendientes, cargándolos en los registros adecuados según su
    // tipo (entero o flotante). Luego, se realiza la llamada a la función
    // utilizando la instrucción "jal". Después de la llamada, se determina el tipo
    // de retorno de la función y se guarda el valor de retorno en la variable
    // destino correspondiente.
    private void Escribir_llamada(String fuente, String dest) {
        String resto = fuente.substring("call ".length()).trim();
        String[] partes = resto.split(",", 2);
        String nombreFuncion = partes[0].trim();

        String[] regsInt = { "$a0", "$a1", "$a2", "$a3" };
        String[] regsFloat = { "$f12", "$f14", "$f16", "$f18" };

        int contInt = 0, contFloat = 0;

        for (String param : parametrosPendientes) {
            if (esFloat(param)) {
                if (contFloat < regsFloat.length)
                    cargarFloat(param, regsFloat[contFloat++]);
            } else {
                if (contInt < regsInt.length)
                    cargarInt(param, regsInt[contInt++]);
            }
        }

        parametrosPendientes.clear();
        text.append("    jal ").append(nombreFuncion).append("\n");

        String tipoRetorno = tipoRetornoFunciones.getOrDefault(nombreFuncion, "int");
        tipoVars.put(dest, tipoRetorno);

        if ("float".equals(tipoRetorno)) {
            guardarFloat("$f0", dest);
        } else {
            guardarInt("$v0", dest);
        }
    }

    // Maneja la escritura de instrucciones de retorno, generando el código MIPS
    // correspondiente para cargar el valor de retorno en el registro adecuado y
    // salir de la función. Se determina el tipo del valor de retorno y se carga en
    // el registro correspondiente ($f0 para flotantes, $v0 para enteros). Luego, se
    // emite el epílogo de la función para restaurar el estado del stack y regresar
    // al llamador.
    private void escribirReturn(String inst) {
        String valor = inst.substring("return ".length()).trim();
        String tipo = obtenerTipoValor(valor);

        if (funcionActualMips != null && !funcionActualMips.isEmpty() && !"main".equals(funcionActualMips)) {
            tipoRetornoFunciones.put(funcionActualMips, tipo);
        }

        if ("float".equals(tipo)) {
            cargarFloat(valor, "$f0");
            cerrarFuncionConRetorno();
        } else {
            cargarInt(valor, "$v0");
            cerrarFuncionConRetorno();
        }
    }

    // Maneja el cierre de una función con retorno, generando el código MIPS

    private void cerrarFuncionConRetorno() {
        text.append("    move $sp, $fp\n");
        text.append("    lw $fp, 0($sp)\n");
        text.append("    lw $ra, 4($sp)\n");
        text.append("    addiu $sp, $sp, 8\n");
        text.append("    jr $ra\n");
    }

    // Maneja la escritura de definiciones de parámetros, generando el código MIPS
    // correspondiente para almacenar los parámetros en el stack y mantener un
    // registro del offset y tipo de cada parámetro. Se verifica que la instrucción
    // esté bien formada y que no se exceda el número máximo de parámetros
    // permitidos.
    private void escribirParamDef(String inst) {
        String[] partes = inst.split("\\s+");
        if (partes.length < 3) {
            text.append("    # ERROR param_def mal formado: ").append(inst).append("\n");
            return;
        }

        String tipo = partes[1].trim();
        String nombre = partes[2].trim();

        String[] regsInt = { "$a0", "$a1", "$a2", "$a3" };// registros para parámetros enteros
        String[] regsFloat = { "$f12", "$f14", "$f16", "$f18" };// registros para parámetros flotantes

        if (indiceParametroActual >= regsInt.length) {
            text.append("    # ERROR: demasiados parametros para ").append(nombre).append("\n");
            return;
        }

        offsetActual -= 4;
        offsetVars.put(nombre, offsetActual);
        tipoVars.put(nombre, tipo);
        // Se genera el código MIPS para ajustar el puntero de pila ($sp) hacia abajo
        // para reservar espacio para el parámetro \
        // y se indica el tipo y nombre del parámetro junto con su offset en el stack.
        text.append("    addiu $sp, $sp, -4  # parametro ")
                .append(nombre).append(" (").append(tipo)
                .append(") @ ").append(offsetActual).append("($fp)\n");

        if ("float".equals(tipo)) {
            text.append("    s.s ")
                    .append(regsFloat[indiceParametroActual])
                    .append(", ").append(offsetActual).append("($fp)\n");
        } else {
            text.append("    sw ")
                    .append(regsInt[indiceParametroActual])
                    .append(", ").append(offsetActual).append("($fp)\n");
        }

        indiceParametroActual++;
    }

    // Maneja la escritura de instrucciones de entrada y salida, generando el código
    // MIPS correspondiente para realizar operaciones de lectura y escritura en la
    // consola. Dependiendo del tipo de dato, se utilizan diferentes servicios del
    // sistema para imprimir o leer valores
    private void escribirCout(String inst) {
        String valor = inst.substring("cout ".length()).trim();
        String tipo = tipoVars.get(valor);

        if (esCadenaLiteral(valor)) {
            String etiqueta = obtenerEtiquetaCadena(valor);
            text.append("    la $a0, ").append(etiqueta).append("\n");
            text.append("    li $v0, 4\n");
            text.append("    syscall\n");
        }

        else if ("string".equals(tipo)) {
            cargarInt(valor, "$a0");
            text.append("    li $v0, 4\n");
            text.append("    syscall\n");
        }

        else if (esCharLiteral(valor)) {
            int ascii = obtenerAsciiChar(valor);
            text.append("    li $a0, ").append(ascii).append("\n");
            text.append("    li $v0, 11\n");
            text.append("    syscall\n");
        }

        else if ("float".equals(tipo) || esFloatLiteral(valor)) {
            cargarFloat(valor, "$f12");
            text.append("    li $v0, 2\n");
            text.append("    syscall\n");
        }

        else if ("char".equals(tipo)) {
            cargarInt(valor, "$a0");
            text.append("    li $v0, 11\n");
            text.append("    syscall\n");
        }
        // por defecto, se asume que el valor es un entero o booleano y se utiliza el
        // servicio
        else {
            cargarInt(valor, "$a0");
            text.append("    li $v0, 1\n");
            text.append("    syscall\n");
        }

        text.append("    la $a0, newline\n");
        text.append("    li $v0, 4\n");
        text.append("    syscall\n");
    }

    // Maneja la escritura de instrucciones de entrada, generando el codigo MIPS
    // correspondiente para leer valores desde la consola y almacenarlos en
    // variables
    private void escribirCin(String inst) {
        String destino = inst.substring("cin ".length()).trim();
        String tipo = tipoVars.get(destino);

        if ("float".equals(tipo)) {
            text.append("    li $v0, 6\n");
            text.append("    syscall\n");
            guardarFloat("$f0", destino);
        } else if ("char".equals(tipo)) {
            text.append("    li $v0, 12\n");
            text.append("    syscall\n");
            guardarInt("$v0", destino);
        } else { // por defecto, se asume que el valor es un entero
            text.append("    li $v0, 5\n");
            text.append("    syscall\n");
            guardarInt("$v0", destino);
        }
    }

    // Maneja la escritura de instrucciones de impresión, generando el código MIPS
    private void escribirPrint(String inst) {
        String valor = inst.substring("print ".length()).trim();

        // Reutiliza el método de cout
        escribirCout("cout " + valor);

    }

    // Maneja la escritura de instrucciones de lectura, generando el código MIPS
    private void escribirRead(String inst) {
        String destino = inst.substring("read ".length()).trim();

        // Reutiliza el método de cin
        escribirCin("cin " + destino);
    }

    // Obtiene el tipo de un valor, ya sea un literal o una variable, devolviendo el
    // tipo correspondiente como una cadena. Se verifica si el valor es un literal
    // de cadena, flotante,
    private String obtenerTipoValor(String valor) {
        if (esCadenaLiteral(valor)) {
            return "string";
        }

        if (esFloatLiteral(valor)) {
            return "float";
        }

        if (esCharLiteral(valor)) {
            return "char";
        }

        if ("true".equals(valor) || "false".equals(valor)) {
            return "bool";
        }

        return tipoVars.getOrDefault(valor, "int");
    }

    // Verifica si un valor es un literal de entero en notacion exponencial,
    // devolviendo true si puede ser parseado como un entero, y false en caso
    // contrario.
    private boolean esExponencialEnteroLiteral(String val) {
        if (val == null)
            return false;

        String s = val.toLowerCase();

        if (!s.contains("e"))
            return false;

        String[] partes = s.split("e");

        if (partes.length != 2)
            return false;

        try {
            Integer.parseInt(partes[0]);
            Integer.parseInt(partes[1]);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Maneja la emision de código MIPS para calcular el valor de una expresion
    // exponencial en base 10, dado un literal
    // en notacion exponencial (por ejemplo, "3e4")
    private void emitirExponencialBase10(String val, String regDestino) {
        String[] partes = val.toLowerCase().split("e");

        int coeficiente = Integer.parseInt(partes[0]);
        int exponente = Integer.parseInt(partes[1]);

        String regBase = "$t6";
        String regExp = "$t7";

        String lblInicio = "exp10_loop_" + contadorEtiquetas++;
        String lblFin = "exp10_end_" + contadorEtiquetas++;

        text.append("    li ").append(regDestino).append(", ").append(coeficiente).append("\n");
        text.append("    li ").append(regBase).append(", 10\n");
        text.append("    li ").append(regExp).append(", ").append(exponente).append("\n");
        // Genera un bucle para multiplicar el coeficiente por 10 elevado al exponente
        // hasta que el exponente llegue a cero. Se utiliza la instrucción "beq" para
        // verificar si el exponente es cero y saltar al final del bucle si es así.
        // Dentro del bucle, se multiplica el valor actual en regDestino por 10
        // (almacenado en regBase)
        text.append(lblInicio).append(":\n");
        text.append("    beq ").append(regExp).append(", $zero, ").append(lblFin).append("\n");
        text.append("    mul ").append(regDestino).append(", ").append(regDestino).append(", ").append(regBase)
                .append("\n");
        text.append("    addi ").append(regExp).append(", ").append(regExp).append(", -1\n");
        text.append("    j ").append(lblInicio).append("\n");

        text.append(lblFin).append(":\n");
    }

    // Verifica si alguna instrucción en la lista de instrucciones utiliza la
    // operación de potencia (^)
    private boolean usaPotencia() {
        for (String instruccion : instrucciones) {
            String inst = instruccion.trim();

            // Busca operaciones tipo: t1 = a ^ b
            if (inst.contains(" ^ ")) {
                return true;
            }
        }
        return false;
    }
}