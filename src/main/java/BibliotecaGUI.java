import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BibliotecaGUI {
    private Biblioteca biblioteca;

    public BibliotecaGUI() {
        biblioteca = new Biblioteca();
        inicializarDatos();
        crearInterfaz();
    }

    private void inicializarDatos() {
        // Agregar algunos elementos iniciales
        Libro libro1 = new Libro("Cien Años de Soledad", "Gabriel García Márquez", 1967, 432, "Realismo Mágico");
        Revista revista1 = new Revista("National Geographic", "National Geographic Society", 2023, 245, "Ciencia y Naturaleza");
        DVD dvd1 = new DVD("Inception", "Christopher Nolan", 2010, 148, "Ciencia Ficción");

        libro1.registrarLibro(biblioteca);
        revista1.registrarRevista(biblioteca);
        dvd1.registrarDVD(biblioteca);
    }

    private void crearInterfaz() {
        JFrame frame = new JFrame("Biblioteca");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Área de texto para mostrar los elementos
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel();
        JButton mostrarElementosButton = new JButton("Mostrar Elementos");
        JButton agregarElementoButton = new JButton("Agregar Elemento");
        JButton prestarElementoButton = new JButton("Prestar Elemento");

        buttonPanel.add(mostrarElementosButton);
        buttonPanel.add(agregarElementoButton);
        buttonPanel.add(prestarElementoButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Acción para mostrar elementos
        mostrarElementosButton.addActionListener(e -> {
            textArea.setText("Elementos en la biblioteca:\n");
            textArea.append(biblioteca.mostrarTodosLosElementos());
        });

        // Acción para agregar un elemento
        agregarElementoButton.addActionListener(e -> {
            String tipo = JOptionPane.showInputDialog(frame, "Tipo de elemento (Libro, Revista, DVD):");
            String titulo = JOptionPane.showInputDialog(frame, "Título:");
            String autor = JOptionPane.showInputDialog(frame, "Autor:");
            int anio = Integer.parseInt(JOptionPane.showInputDialog(frame, "Año:"));
            int paginasDuracion = Integer.parseInt(JOptionPane.showInputDialog(frame, "Páginas/Duración:"));
            String genero = JOptionPane.showInputDialog(frame, "Género:");

            if (tipo.equalsIgnoreCase("Libro")) {
                Libro libro = new Libro(titulo, autor, anio, paginasDuracion, genero);
                libro.registrarLibro(biblioteca);
            } else if (tipo.equalsIgnoreCase("Revista")) {
                Revista revista = new Revista(titulo, autor, anio, paginasDuracion, genero);
                revista.registrarRevista(biblioteca);
            } else if (tipo.equalsIgnoreCase("DVD")) {
                DVD dvd = new DVD(titulo, autor, anio, paginasDuracion, genero);
                dvd.registrarDVD(biblioteca);
            }
            JOptionPane.showMessageDialog(frame, "Elemento agregado con éxito.");
        });

        // Acción para prestar un elemento
        prestarElementoButton.addActionListener(e -> {
            String titulo = JOptionPane.showInputDialog(frame, "Título del elemento a prestar:");
            boolean prestado = biblioteca.prestarElemento(titulo);
            if (prestado) {
                JOptionPane.showMessageDialog(frame, "Elemento prestado con éxito.");
            } else {
                JOptionPane.showMessageDialog(frame, "Elemento no encontrado o ya prestado.");
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BibliotecaGUI::new);
    }
}