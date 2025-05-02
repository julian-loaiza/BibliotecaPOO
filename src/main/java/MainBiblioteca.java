// Clase principal para demostrar el uso
public class MainBiblioteca {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();

        // Crear elementos
        Libro libro1 = new Libro("Cien Años de Soledad", "Gabriel García Márquez", 1967, 432, "Realismo Mágico");
        Revista revista1 = new Revista("National Geographic", "National Geographic Society", 2023, 245, "Ciencia y Naturaleza");
        DVD dvd1 = new DVD("Inception", "Christopher Nolan", 2010, 148, "Ciencia Ficción");

        Libro libro2 = new Libro("Aprendiendo Java", "Pepito Perez", 1967, 432, "Programación");

        // Norbey Gutierrez
        Libro libroNorbey = new Libro("El Principito", "Antoine de Saint-Exupéry", 1943, 96, "Fabula");
        DVD dvdNorbey = new DVD("Time", "Time USA, LLC", 2024, 80, "Noticias");
        Revista revistaNorbey = new Revista("Parasite", "Bong Joon-ho", 2019, 132, "Drama/Thriller");

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

        libro1.registrarLibro(biblioteca);
        libro2.registrarLibro(biblioteca);
        revista1.registrarRevista(biblioteca);
        dvd1.registrarDVD(biblioteca);
        libroOscar.registrarLibro(biblioteca);
        revistaOscar.registrarRevista(biblioteca);
        dvdOscar.registrarDVD(biblioteca);

        // Mostrar todos los elementos
        System.out.println("Elementos en la biblioteca:");
        biblioteca.mostrarTodosLosElementos();

        // Prestar un elemento
        libro1.prestar();
        dvdOscar.prestar();

        // Mostrar elementos disponibles
        System.out.println("\nElementos disponibles:");
        biblioteca.mostrarElementosDisponibles();
    }
}