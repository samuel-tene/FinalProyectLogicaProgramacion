import java.util.Scanner;

public class Biblioteca {

    // ---------- Constantes (reglas del negocio) ----------
    static final int MAX_LIBROS = 15;
    static final int MAX_PRESTAMOS = 2;
    static final int MULTA_POR_DIA = 500;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Arreglos paralelos: la misma posicion representa el mismo libro
        int[] codigos = new int[MAX_LIBROS];
        String[] titulos = new String[MAX_LIBROS];
        boolean[] disponible = new boolean[MAX_LIBROS];
        String[] lectorActual = new String[MAX_LIBROS];

        int totalLibros = 0;   // contador de libros registrados
        int totalMultas = 0;   // acumulador de multas cobradas
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEnteroValidado("Elige una opcion: ", 0, 6);

            switch (opcion) {
                case 1:
                    totalLibros = registrarLibro(codigos, titulos, disponible, lectorActual, totalLibros);
                    break;
                case 2:
                    prestarLibro(codigos, disponible, lectorActual, totalLibros);
                    break;
                case 3:
                    totalMultas += devolverLibro(codigos, disponible, lectorActual, totalLibros);
                    break;
                case 4:
                    consultarLibro(codigos, titulos, disponible, lectorActual, totalLibros);
                    break;
                case 5:
                    int filtro = leerEnteroValidado("1. Disponibles  2. Prestados: ", 1, 2);
                    listarLibros(codigos, titulos, disponible, lectorActual, totalLibros, filtro == 1);
                    break;
                case 6:
                    System.out.println("Total de multas recaudadas: $" + totalMultas);
                    break;
                case 0:
                    System.out.println("Hasta luego!");
                    break;
            }
        } while (opcion != 0);
    }

    // Menu y lectura validada 

    static void mostrarMenu() {
        System.out.println("\n===== BIBLIOTECA =====");
        System.out.println("1. Registrar libro");
        System.out.println("2. Prestar libro");
        System.out.println("3. Devolver libro");
        System.out.println("4. Consultar libro por codigo");
        System.out.println("5. Listar libros");
        System.out.println("6. Ver total de multas");
        System.out.println("0. Salir");
    }

    //Pide un entero y repite hasta que sea un numero dentro de [min, max].
    static int leerEnteroValidado(String mensaje, int min, int max) {
        int valor = 0;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                valor = Integer.parseInt(linea);
                if (valor >= min && valor <= max) {
                    valido = true;
                } else {
                    System.out.println("Error: ingresa un numero entre " + min + " y " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: debes ingresar un numero entero.");
            }
        }
        return valor;
    }

    // Pide un texto y repite hasta que no este vacio. 
    static String leerTextoNoVacio(String mensaje) {
        String texto = "";
        while (texto.isEmpty()) {
            System.out.print(mensaje);
            texto = sc.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("Error: el texto no puede estar vacio.");
            }
        }
        return texto;
    }

    // ---------- Modulos del negocio ----------
     //Devuelve la posicion del codigo en el arreglo, o -1 si no existe. 
    static int buscarLibro(int[] codigos, int total, int codigo) {
        for (int i = 0; i < total; i++) {
            if (codigos[i] == codigo) {
                return i;
            }
        }
        return -1;
    }

    // Registra un libro y devuelve el nuevo total
    static int registrarLibro(int[] codigos, String[] titulos, boolean[] disponible,
                              String[] lectorActual, int total) {
        if (total >= MAX_LIBROS) {
            System.out.println("Error: la biblioteca esta llena (maximo " + MAX_LIBROS + " libros).");
            return total;
        }
        int codigo = leerEnteroValidado("Codigo del libro (1-9999): ", 1, 9999);
        if (buscarLibro(codigos, total, codigo) != -1) {
            System.out.println("Error: ya existe un libro con ese codigo.");
            return total;
        }
        String titulo = leerTextoNoVacio("Titulo del libro: ");

        codigos[total] = codigo;
        titulos[total] = titulo;
        disponible[total] = true;
        lectorActual[total] = "";
        System.out.println("Libro registrado correctamente.");
        return total + 1;
    }

    // Cuenta cuantos libros tiene prestados un lector
    static int contarPrestamosLector(String[] lectorActual, int total, String lector) {
        int contador = 0;
        for (int i = 0; i < total; i++) {
            if (lectorActual[i].equalsIgnoreCase(lector)) {
                contador++;
            }
        }
        return contador;
    }

    static void prestarLibro(int[] codigos, boolean[] disponible, String[] lectorActual, int total) {
        if (total == 0) {
            System.out.println("Error: no hay libros registrados.");
            return;
        }
        int codigo = leerEnteroValidado("Codigo del libro a prestar: ", 1, 9999);
        int pos = buscarLibro(codigos, total, codigo);
        if (pos == -1) {
            System.out.println("Error: el libro no existe.");
            return;
        }
        if (!disponible[pos]) {
            System.out.println("Error: el libro ya esta prestado a " + lectorActual[pos] + ".");
            return;
        }
        String lector = leerTextoNoVacio("Nombre del lector: ");
        if (contarPrestamosLector(lectorActual, total, lector) >= MAX_PRESTAMOS) {
            System.out.println("Error: " + lector + " ya tiene " + MAX_PRESTAMOS + " libros prestados.");
            return;
        }
        disponible[pos] = false;
        lectorActual[pos] = lector;
        System.out.println("Prestamo registrado a " + lector + ".");
    }

    static int calcularMulta(int diasRetraso) {
        return diasRetraso * MULTA_POR_DIA;
    }

    // Registra la devolucion y devuelve la multa cobrada 
    static int devolverLibro(int[] codigos, boolean[] disponible, String[] lectorActual, int total) {
        if (total == 0) {
            System.out.println("Error: no hay libros registrados.");
            return 0;
        }
        int codigo = leerEnteroValidado("Codigo del libro a devolver: ", 1, 9999);
        int pos = buscarLibro(codigos, total, codigo);
        if (pos == -1) {
            System.out.println("Error: el libro no existe.");
            return 0;
        }
        if (disponible[pos]) {
            System.out.println("Error: ese libro no esta prestado.");
            return 0;
        }
        int dias = leerEnteroValidado("Dias de retraso (0 si no hubo): ", 0, 365);
        int multa = calcularMulta(dias);

        disponible[pos] = true;
        lectorActual[pos] = "";
        System.out.println("Devolucion registrada. Multa: $" + multa);
        return multa;
    }

    static void consultarLibro(int[] codigos, String[] titulos, boolean[] disponible,
                               String[] lectorActual, int total) {
        int codigo = leerEnteroValidado("Codigo a consultar: ", 1, 9999);
        int pos = buscarLibro(codigos, total, codigo);
        if (pos == -1) {
            System.out.println("El libro no existe.");
        } else if (disponible[pos]) {
            System.out.println(titulos[pos] + " -> DISPONIBLE");
        } else {
            System.out.println(titulos[pos] + " -> PRESTADO a " + lectorActual[pos]);
        }
    }

    //Lista los libros disponibles (true) o los prestados (false)
    static void listarLibros(int[] codigos, String[] titulos, boolean[] disponible,
                             String[] lectorActual, int total, boolean mostrarDisponibles) {
        int encontrados = 0;
        System.out.println(mostrarDisponibles ? "--- Libros disponibles ---" : "--- Libros prestados ---");
        for (int i = 0; i < total; i++) {
            if (disponible[i] == mostrarDisponibles) {
                if (mostrarDisponibles) {
                    System.out.println(codigos[i] + " | " + titulos[i]);
                } else {
                    System.out.println(codigos[i] + " | " + titulos[i] + " | Lector: " + lectorActual[i]);
                }
                encontrados++;
            }
        }
        if (encontrados == 0) {
            System.out.println("(ninguno)");
        }
    }
}