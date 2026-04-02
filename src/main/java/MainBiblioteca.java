// Clase principal para demostrar el uso
public class MainBiblioteca {
    public static void main(String[] args) {
        long usuarioDemo01 = 1L;
        long usuarioDemo02 = 2L;

        try {
            // Inicializa tablas si no existen antes de registrar elementos.
            DatabaseConnection.initializeDatabase();

            // Crea o reutiliza usuarios para operaciones con FK en prestamos/reservas.
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDemo01 = usuarioDAO.ensureUsuarioId(1L, "usuario_demo_01");
            usuarioDemo02 = usuarioDAO.ensureUsuarioId(2L, "usuario_demo_02");
        } catch (Exception e) {
            System.err.println("No se pudo inicializar la base de datos: " + e.getMessage());
        }

        Biblioteca biblioteca = new Biblioteca();

        // Crear elementos
        Libro libro1 = new Libro("Cien Años de Soledad", "Gabriel García Márquez", 1967, 432, "Realismo Mágico");
        Revista revista1 = new Revista("National Geographic", "National Geographic Society", 2023, 245, "Ciencia y Naturaleza");
        DVD dvd1 = new DVD("Inception", "Christopher Nolan", 2010, 148, "Ciencia Ficción");

        Libro libro2 = new Libro("Aprendiendo Java", "Pepito Perez", 1967, 432, "Programación");

        Libro libro3 = new Libro("El Principito", "Antoine de Saint-Exupéry", 1943, 96, "Fabula");
        DVD dvd3 = new DVD("Time", "Time USA, LLC", 2024, 80, "Noticias");
        Revista revista3 = new Revista("Parasite", "Bong Joon-ho", 2019, 132, "Drama/Thriller");

        // Ruben Toro
        Libro libroR01 =  new Libro("El tunel", "Ernesto Sábato", 1948,104,"Narrativo-Novela");
        Revista revistaR01 = new Revista("Cosmo","Carl Sagan", 1995,230,"Ciencia y Naturaleza");
        DVD dvdR01 = new DVD("Cosmos y Galaxias","Carl Sagan", 1998,120,"Ciencia y Naturaleza");

                
        // Variables Oscar
        Libro libroOscar = new Libro("Carrie", "Stephen King", 1974, 280, "Terror");
        Revista revistaOscar = new Revista("Nat GEO: Depredadores", "National Geographic", 2024, 2, "Naturaleza");
        DVD dvdOscar = new DVD("El señor de los anillos", "Peter Jackson", 2001, 557, "Fantasía");

        Libro libro03 = new Libro("El llano en llamas", "Juan Rulfo", 1956, 250, "Literario");
        Revista revista03 = new Revista("Nat GEO: Depredadores", "National Geographic", 2024, 2, "Naturaleza");
        DVD dvd03 = new DVD("El señor de los anillos", "Peter Jackson", 2001, 557, "Fantasía");


        // Varible Santi
        Libro librosanti = new Libro("Harry Potter y la piedra filosofal", "J. K. Rowling", 1997, 264, "Alta fantasia");
        Revista revistasanti = new Revista("Otaku Bunka", "panini", 2024, 30, "anime");
        DVD dvdsanti = new DVD("Naruto Shippuden Box 1", "Masashi Kishimoto", 2023, 120, "anime");

        //Variables felho
        Libro librofelho = new Libro("El señor de los anillos", "Alan Lee", 2019, 1272, "Fantasia");
        Revista revistafelho = new Revista("Soy motero", "Soymotero.ne5", 2023, 830, "Motocicletas");
        DVD dvdfelho = new DVD("Interstellar", "Christopher Nolan", 2014, 169, "Ciencia ficción épica / Drama espacial");

        // Agregar elementos a la biblioteca usando métodos específicos

        // Registrar todos los elementos creados para persistir en todas las tablas.
        libro1.registrarLibro(biblioteca);
        libro2.registrarLibro(biblioteca);
        libro3.registrarLibro(biblioteca);
        libroR01.registrarLibro(biblioteca);
        libroOscar.registrarLibro(biblioteca);
        libro03.registrarLibro(biblioteca);
        librosanti.registrarLibro(biblioteca);
        librofelho.registrarLibro(biblioteca);

        revista1.registrarRevista(biblioteca);
        revista3.registrarRevista(biblioteca);
        revistaR01.registrarRevista(biblioteca);
        revistaOscar.registrarRevista(biblioteca);
        revista03.registrarRevista(biblioteca);
        revistasanti.registrarRevista(biblioteca);
        revistafelho.registrarRevista(biblioteca);

        dvd1.registrarDVD(biblioteca);
        dvd3.registrarDVD(biblioteca);
        dvdR01.registrarDVD(biblioteca);
        dvdOscar.registrarDVD(biblioteca);
        dvd03.registrarDVD(biblioteca);
        dvdsanti.registrarDVD(biblioteca);
        dvdfelho.registrarDVD(biblioteca);

        // Mostrar todos los elementos
        System.out.println("Elementos en la biblioteca:");
        biblioteca.mostrarTodosLosElementos();

        // Prestar un elemento
        libro1.prestar(usuarioDemo01);
        dvdOscar.prestar(usuarioDemo02);

        // Registrar reservas de ejemplo para poblar la tabla reservas.
        revista1.reservar(usuarioDemo01, "usuario_demo_01");
        revista3.reservar(usuarioDemo02, "usuario_demo_02");

        // Mostrar elementos disponibles
        System.out.println("\nElementos disponibles:");
        biblioteca.mostrarElementosDisponibles();
    }
}