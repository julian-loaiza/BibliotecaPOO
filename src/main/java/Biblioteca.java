import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.sql.SQLException;

// Clase Biblioteca para gestionar los elementos´
public class Biblioteca {
    private List<ElementoBiblioteca> elementos;
    private final ElementoBibliotecaDAO elementoDAO;

    // Constructor
    public Biblioteca() {
        elementos = new ArrayList<>();
        elementoDAO = new ElementoBibliotecaDAO();
    }

    // Método para registrar un elemento en la biblioteca
    public void registrarElemento(ElementoBiblioteca elemento) {
        elementos.add(elemento);
        System.out.println(elemento.getTipo() + " registrado en la biblioteca.");

        try {
            // Persiste el elemento al momento de registrarlo para no perder datos al cerrar la app.
            elementoDAO.guardarElemento(elemento);
            System.out.println(elemento.getTipo() + " guardado en base de datos.");
        } catch (SQLException e) {
            System.err.println("No se pudo guardar en base de datos: " + e.getMessage());
        }
    }

    // Método para buscar elementos por título
    public List<ElementoBiblioteca> buscarPorTitulo(String titulo) {
        return elementos.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Método para mostrar todos los elementos
    public void mostrarTodosLosElementos() {
        for (ElementoBiblioteca elemento : elementos) {
            elemento.mostrarDetalles();
            System.out.println("-------------------");
        }
    }

    // Método para mostrar elementos disponibles
    public void mostrarElementosDisponibles() {
        elementos.stream()
                .filter(e -> !e.estaPrestado())
                .forEach(ElementoBiblioteca::mostrarDetalles);
    }
}
