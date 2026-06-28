# Proyecto 3 - Compiladores e Intérpretes

**Instituto Tecnológico de Costa Rica**
**Ingeniería en Computación**
**Curso:** Compiladores e Intérpretes
**Grupo:** 60
**Semestre:** I Semestre 2026

## Estudiantes

* Elder León
* Quiriat Mata

---

## Descripción del proyecto

Este proyecto corresponde a la tercera fase del desarrollo de un compilador para un lenguaje imperativo ligero. En esta etapa se implementa la **generación de código destino en MIPS**, tomando como base el código intermedio de tres direcciones generado en el Proyecto II.

El compilador conserva las fases anteriores de análisis léxico, sintáctico y semántico, y posteriormente traduce los programas válidos hacia un archivo `.asm`, el cual puede ser ejecutado y verificado en **QtSpim**.

El lenguaje fuente permite trabajar con tipos de datos primitivos, estructuras de control, funciones, expresiones aritméticas, operaciones lógicas, arreglos y generación de código intermedio.

---

## Objetivo general

Desarrollar una fase de generación de código destino en MIPS a partir del código intermedio de tres direcciones, manteniendo la relación semántica entre el código fuente original y el código ensamblador generado.

---

## Alcance del proyecto

El sistema permite:

* Leer archivos fuente escritos en el lenguaje definido.
* Realizar análisis léxico mediante JFlex.
* Realizar análisis sintáctico mediante CUP.
* Ejecutar validaciones semánticas sobre el código fuente.
* Reportar errores léxicos, sintácticos y semánticos.
* Generar código intermedio de tres direcciones.
* Traducir el código intermedio hacia instrucciones MIPS.
* Generar un archivo `.asm` ejecutable en QtSpim.
* Manejar estructuras básicas como asignaciones, expresiones, ciclos, condicionales, funciones y operaciones especiales.

---

## Tecnologías utilizadas

* Java
* JFlex
* CUP
* MIPS Assembly
* QtSpim
* Visual Studio Code

---

## Requisitos de ejecución

Para compilar y ejecutar el proyecto se requiere:

1. Tener instalado Java JDK.
2. Tener Visual Studio Code o una terminal disponible.
3. Contar con las librerías necesarias dentro de la carpeta `lib`:

   * `jflex.jar`
   * `java-cup-runtime.jar`
4. Tener acceso a la carpeta principal del proyecto.
5. Tener instalado QtSpim para ejecutar el código MIPS generado.

---

## Estructura general del proyecto

```text
PROYECTO1COMPILADORES-S12026/
│
├── Gramatica/
│   ├── Gramatica.bnf
│   └── Gramaticacompletaentxt.txt
│
├── Proyecto/
│   ├── EjemplosPruebas/
│   │   ├── Ejemplo.txt
│   │   └── tokens_Ejemplo.txt
│   │
│   └── generated/
│       ├── Codigo3D_Ejemplo.txt
│       └── Ejemplo.asm
│
├── lib/
│   ├── jflex.jar
│   └── java-cup-runtime.jar
│
├── src/
│   ├── app/
│   │   └── Main.java
│   │
│   ├── lexer/
│   │   ├── Lexer.flex
│   │   └── Lexer.java
│   │
│   └── parser/
│       ├── Parser.cup
│       ├── Parser.java
│       ├── MyParser.java
│       ├── CodigoIntermedio.java
│       ├── GeneradorMIPS.java
│       ├── ErroresSemanticos.java
│       ├── Resultado.java
│       ├── ArgsFuncion.java
│       └── InfoMatriz.java
│
└── README.md
```

### Descripción de carpetas

| Carpeta / archivo           | Descripción                                                                                    |
| --------------------------- | ---------------------------------------------------------------------------------------------- |
| `Gramatica/`                | Contiene la gramática del lenguaje en formato BNF y texto.                                     |
| `Proyecto/EjemplosPruebas/` | Contiene los archivos fuente utilizados para probar el compilador.                             |
| `Proyecto/generated/`       | Contiene los archivos generados por el compilador, como el código intermedio y el código MIPS. |
| `lib/`                      | Contiene las librerías necesarias para compilar y ejecutar el proyecto.                        |
| `src/app/`                  | Contiene la clase principal del programa.                                                      |
| `src/lexer/`                | Contiene el analizador léxico generado con JFlex.                                              |
| `src/parser/`               | Contiene el parser, las clases semánticas, el código intermedio y el generador MIPS.           |
| `README.md`                 | Archivo de documentación principal del repositorio.                                            |


## Compilación del proyecto

Desde la carpeta principal del proyecto, abrir una terminal y ejecutar:

```bash
javac -cp ".;lib/jflex.jar;lib/java-cup-runtime.jar" -sourcepath src -d out src/app/Main.java
```

Este comando compila el proyecto y coloca los archivos `.class` generados dentro de la carpeta `out`.

---

## Ejecución del proyecto

Después de compilar, ejecutar el proyecto con el siguiente comando:

```bash
java -cp "out;lib/jflex.jar;lib/java-cup-runtime.jar" app.Main
```

> Nota: Los comandos anteriores están escritos para Windows.
> En Linux o macOS se debe cambiar `;` por `:` en el classpath.


---

## Uso del proyecto

1. Abrir la carpeta del proyecto en Visual Studio Code.
2. Entrar a la carpeta `ejemplosPruebas`.
3. Crear o pegar un archivo fuente de prueba.
4. Ejecutar el compilador desde la terminal.
5. El sistema analizará los archivos fuente disponibles.
6. Si el código no contiene errores, se generará el archivo MIPS correspondiente.
7. Si existen errores, el compilador mostrará el tipo de error, fila y columna donde se detectó.

Los archivos con prefijo `_tokens` no se toman como archivos fuente para la generación principal.

---

## Ejecución del código MIPS en QtSpim

Para comprobar el código generado:

1. Abrir QtSpim.
2. Seleccionar la opción:

```text
File > Load File
```

3. Buscar el archivo `.asm` generado por el proyecto.
4. Cargar el archivo en QtSpim.
5. Verificar que no aparezcan errores en la consola inferior.
6. Presionar el botón `Run`.
7. Revisar la salida del programa en la consola de QtSpim.

El archivo generado debe contener una estructura similar a:

```asm
.data
    newline: .asciiz "\n"

.text
.globl main
```

---

## Descripción del problema

El proyecto aborda la generación de código destino para un lenguaje imperativo ligero orientado a sistemas empotrados. La fase desarrollada toma como entrada el código intermedio producido por el compilador y lo traduce a instrucciones MIPS.

El reto principal consiste en conservar el significado del programa original, administrando correctamente instrucciones, variables, expresiones, etiquetas, saltos, funciones y estructuras de control dentro del lenguaje ensamblador.

---

## Funcionalidades principales

| Funcionalidad       | Descripción                                                                       |
| ------------------- | --------------------------------------------------------------------------------- |
| Análisis léxico     | Reconoce tokens válidos del lenguaje mediante JFlex.                              |
| Análisis sintáctico | Valida la estructura del programa mediante CUP.                                   |
| Análisis semántico  | Verifica tipos, declaraciones, uso de variables, funciones y errores de contexto. |
| Código intermedio   | Genera instrucciones de tres direcciones como representación intermedia.          |
| Generación MIPS     | Traduce el código intermedio hacia instrucciones MIPS.                            |
| Manejo de errores   | Reporta errores léxicos, sintácticos y semánticos.                                |
| Ejecución en QtSpim | Permite verificar el archivo `.asm` generado.                                     |

---

## Análisis de resultados

| Área evaluada                | Estado                       | Análisis del resultado                                                                                                                                                                                                                                                                                                                                |
| ---------------------------- | ---------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Integración del compilador   | Alcanzado                    | Se logró mantener la base del compilador y utilizarla como punto de partida para la generación de código destino en MIPS.                                                                                                                                                                                                                             |
| Generación de código destino | Alcanzado                    | El sistema genera código MIPS con una estructura básica válida, incluyendo secciones de datos, texto, instrucciones, etiquetas y saltos.                                                                                                                                                                                                              |
| Operaciones básicas          | Alcanzado                    | Se logró traducir asignaciones, operaciones aritméticas, impresiones, lecturas y expresiones simples del lenguaje fuente hacia código MIPS.                                                                                                                                                                                                           |
| Estructuras de control       | Alcanzado                    | Se logró representar el flujo del programa mediante etiquetas, saltos condicionales y saltos incondicionales en MIPS.                                                                                                                                                                                                                                 |
| Funciones y retornos         | Parcialmente alcanzado       | Se cubrieron casos básicos de funciones y retornos. Sin embargo, algunos escenarios más complejos requieren un manejo más completo de pila, registros y valores de retorno.                                                                                                                                                                           |
| Manejo de scopes             | Parcialmente alcanzado (80%) | Se logró manejar parcialmente los scopes. Sin embargo, se identificó un caso en el que variables con el mismo nombre en distintos alcances pueden sobrescribir su valor. Este error no fue detectado en la etapa anterior y no se corrigió por falta de tiempo, ya que implicaba realizar ajustes en el archivo CUP y en las validaciones semánticas. |

---

## Resultado general

El proyecto logró cumplir con el objetivo principal de generar código destino MIPS a partir del código intermedio producido por el compilador.

La implementación permite traducir programas válidos del lenguaje fuente hacia código ensamblador y comprobar su ejecución en QtSpim. Aunque existen oportunidades de mejora en escenarios avanzados, la solución desarrollada cubre las funcionalidades principales esperadas para esta fase del compilador.

##

---

## Algoritmos principales

### Traducción de código intermedio a MIPS

El generador recorre las instrucciones de tres direcciones y traduce cada una hacia una o más instrucciones MIPS. Para esto identifica el tipo de instrucción y aplica la conversión correspondiente.

Entre las instrucciones reconocidas se encuentran:

* Declaraciones de variables.
* Asignaciones.
* Operaciones aritméticas.
* Lecturas y escrituras.
* Etiquetas.
* Saltos condicionales.
* Saltos incondicionales.
* Llamadas a funciones.
* Retornos.

---

### Traducción de expresiones

Cuando se encuentra una asignación o expresión, el generador determina el tipo de operación involucrada. Dependiendo del caso, genera instrucciones para enteros, flotantes, comparaciones o expresiones lógicas.

El proceso general consiste en:

1. Identificar el destino de la asignación.
2. Analizar la expresión del lado derecho.
3. Determinar si se trata de un literal, variable, llamada a función u operación.
4. Cargar los operandos en registros.
5. Generar la instrucción MIPS correspondiente.
6. Guardar el resultado en memoria o en el registro adecuado.

---

### Manejo de funciones

Para las funciones se administra un marco de activación utilizando la pila. Esto permite guardar información importante como registros, parámetros y valores de retorno.

De forma general, el proceso incluye:

* Crear el inicio de la función.
* Guardar registros necesarios.
* Reservar espacio para variables locales.
* Procesar parámetros.
* Ejecutar el cuerpo de la función.
* Retornar el valor correspondiente.
* Restaurar el estado anterior antes de volver al llamador.

---

### Manejo de estructuras de control

Las estructuras como condicionales y ciclos se traducen mediante etiquetas y saltos. El generador crea etiquetas para representar los puntos de entrada, salida y repetición dentro del flujo del programa.

Ejemplo general:

```text
if_false condicion goto etiqueta_else
instrucciones del bloque verdadero
goto etiqueta_fin
etiqueta_else:
instrucciones del bloque falso
etiqueta_fin:
```

Este esquema permite representar en MIPS el comportamiento lógico del código fuente.

---

### Manejo de potencia

Como MIPS no cuenta con una instrucción directa para calcular potencia, se utilizan subrutinas de apoyo. Estas realizan multiplicaciones repetidas hasta completar el exponente indicado.

Se contemplan casos para:

* Potencia con enteros.
* Potencia con flotantes.

---

## Estado de la entrega

El proyecto se considera en estado **Muy Bueno**, ya que cumple con la funcionalidad principal de generación de código MIPS y conserva las fases previas del compilador. Sin embargo, se identifican detalles pendientes en casos avanzados, principalmente en scopes y funciones complejas.

##

 **Compiladores e Intérpretes**, Escuela de Ingeniería en Computación, Instituto Tecnológico de Costa Rica.
