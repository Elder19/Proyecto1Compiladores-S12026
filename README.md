#  Gramática BNF 
##  Estudiantes
- Elder León  
- Quiriat Mata  

---

##  Descripción

Diseño e implementación de una **gramática BNF** para un lenguaje imperativo, la cual será utilizada como base para el desarrollo de un **analizador léxico y sintáctico** utilizando JFlex y CUP.

El proyecto contempla la validación de estructuras del lenguaje, así como el reconocimiento de expresiones, funciones y sentencias mediante el proceso de compilación.

---

##  Alcance del Proyecto

El sistema a desarrollar deberá ser capaz de:

- Leer y procesar archivos fuente escritos en el lenguaje definido  
- Realizar análisis léxico mediante JFlex  
- Realizar análisis sintáctico mediante CUP  
- Validar que el código fuente cumpla con la gramática definida  
- Detectar y reportar errores léxicos y sintácticos  
- Generar salida con los tokens identificados  
- Gestionar información mediante tablas de símbolos  

---



## Manual de Usuario – Ejecución del Analizador Léxico y Sintáctico

el usuario debe contar con:

- Java JDK instalado correctamente
- Acceso a una terminal (PowerShell o CMD)
- Proyecto configurado con la siguiente estructura

```text
Proyecto/
├── lib/
│   ├── jflex.jar
│   ├── java-cup.jar
│   └── java-cup-runtime.jar
├── generated/
├── src/
│   ├── lexer/
│   │   └── Lexer.flex
│   ├── parser/
│   │   └── Parser.cup
│   └── app/
│       └── Main.java

Paso 1: Ubicarse en la carpeta del proyecto
*   cd "RUTA_DEL_PROYECTO\Proyecto"
Paso 2: Generar el Analizador Léxico (JFlex)
    -- java -jar .\lib\jflex.jar -d generated .\src\lexer\Lexer.flex
Paso 3: Generar el Analizador Sintáctico (CUP)
    --java -jar .\lib\java-cup.jar -destdir generated -parser Parser -symbols Sym .\src\parser\Parser.cup
