import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Biblioteca {
    private List<ElementoBiblioteca> elementos;

    public Biblioteca() {
        elementos = new ArrayList<>();
    }

    public void registrarElemento(ElementoBiblioteca elemento) {
        elementos.add(elemento);
        System.out.println(elemento.getTipo() + " registrado en la biblioteca.");
    }

    public List<ElementoBiblioteca> buscarPorTitulo(String titulo) {
        return elementos.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public String mostrarTodosLosElementos() {
        StringBuilder resultado = new StringBuilder();
        for (ElementoBiblioteca elemento : elementos) {
            resultado.append(elemento.toString()).append("\n");
        }
        return resultado.toString();
    }

    public void mostrarElementosDisponibles() {
        elementos.stream()
                .filter(e -> !e.estaPrestado())
                .forEach(ElementoBiblioteca::mostrarDetalles);
    }

    public boolean prestarElemento(String titulo) {
        for (ElementoBiblioteca elemento : elementos) {
            if (elemento.getTitulo().equalsIgnoreCase(titulo) && !elemento.estaPrestado()) {
                elemento.prestar();
                return true;
            }
        }
        return false;
    }
}