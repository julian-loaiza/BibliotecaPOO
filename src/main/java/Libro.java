// Clase Libro que hereda de ElementoBiblioteca
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Libro extends ElementoBiblioteca {
    private int numeroPaginas;
    private String genero;

    // Constructor
    public Libro(String titulo, String autor, int anioPublicacion, int numeroPaginas, String genero) {
        super(titulo, autor, anioPublicacion);
        this.numeroPaginas = numeroPaginas;
        this.genero = genero;
    }

    // Método específico de Libro
    public void registrarLibro(Biblioteca biblioteca) {
        // Lógica específica para registrar un libro si es necesario
        agregarABiblioteca(biblioteca);
        System.out.println("Libro registrado con detalles adicionales.");
    }

    // Implementación del método abstracto mostrarDetalles()
    @Override
    public void mostrarDetalles() {
        System.out.println("Libro: " + titulo);
        System.out.println("Autor: " + autor);
        System.out.println("Año de Publicación: " + anioPublicacion);
        System.out.println("Número de Páginas: " + numeroPaginas);
        System.out.println("Género: " + genero);
        System.out.println("Estado: " + (prestado ? "Prestado" : "Disponible"));
    }

    // Implementación del método abstracto getTipo()
    @Override
    public String getTipo() {
        return "Libro";
    }

    // Getters específicos de Libro
    public int getNumeroPaginas() {
        return numeroPaginas;
    }

    public String getGenero() {
        return genero;
    }
}


