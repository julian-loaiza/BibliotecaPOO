import javax.swing.*;
import java.awt.*;

public class Biblioteca {
    private Biblioteca biblioteca;
    private JTextArea textArea;
    private ElementoBiblioteca[] elementos;

    public void Biblioteca() {
        biblioteca = new Biblioteca();
        inicializarDatos();
        crearInterfaz();
    }

    private void inicializarDatos() {
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
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Barra lateral
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(3, 1, 10, 10));
        sidebar.setBackground(new Color(60, 63, 65));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JButton mostrarElementosButton = new JButton("Mostrar Elementos");
        JButton agregarElementoButton = new JButton("Agregar Elemento");
        JButton prestarElementoButton = new JButton("Prestar Elemento");

        estilizarBoton(mostrarElementosButton);
        estilizarBoton(agregarElementoButton);
        estilizarBoton(prestarElementoButton);

        sidebar.add(mostrarElementosButton);
        sidebar.add(agregarElementoButton);
        sidebar.add(prestarElementoButton);

        frame.add(sidebar, BorderLayout.WEST);

        // Área de texto principal
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Acciones de los botones
        mostrarElementosButton.addActionListener(e -> mostrarElementos());
        agregarElementoButton.addActionListener(e -> mostrarFormularioAgregar(frame));
        prestarElementoButton.addActionListener(e -> mostrarFormularioPrestar(frame));

        frame.setVisible(true);
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(new Color(75, 110, 175));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void mostrarElementos() {
        textArea.setText("Elementos en la biblioteca:\n");
        textArea.append(biblioteca.mostrarTodosLosElementos());
    }

    public String mostrarTodosLosElementos() {
        StringBuilder resultado = new StringBuilder();
        for (ElementoBiblioteca elemento : elementos) { // Asegúrate de que 'elementos' sea una lista o array válido.
            resultado.append(elemento.toString()).append("\n");
        }
        return resultado.toString();
    }
    private void mostrarFormularioAgregar(JFrame frame) {
        JDialog dialog = new JDialog(frame, "Agregar Elemento", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel tipoLabel = new JLabel("Tipo:");
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Libro", "Revista", "DVD"});
        JLabel tituloLabel = new JLabel("Título:");
        JTextField tituloField = new JTextField();
        JLabel autorLabel = new JLabel("Autor:");
        JTextField autorField = new JTextField();
        JLabel anioLabel = new JLabel("Año:");
        JTextField anioField = new JTextField();
        JLabel paginasDuracionLabel = new JLabel("Páginas/Duración:");
        JTextField paginasDuracionField = new JTextField();
        JLabel generoLabel = new JLabel("Género:");
        JTextField generoField = new JTextField();

        JButton agregarButton = new JButton("Agregar");
        agregarButton.addActionListener(e -> {
            String tipo = (String) tipoCombo.getSelectedItem();
            String titulo = tituloField.getText();
            String autor = autorField.getText();
            int anio = Integer.parseInt(anioField.getText());
            int paginasDuracion = Integer.parseInt(paginasDuracionField.getText());
            String genero = generoField.getText();

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
            JOptionPane.showMessageDialog(dialog, "Elemento agregado con éxito.");
            dialog.dispose();
        });

        dialog.add(tipoLabel);
        dialog.add(tipoCombo);
        dialog.add(tituloLabel);
        dialog.add(tituloField);
        dialog.add(autorLabel);
        dialog.add(autorField);
        dialog.add(anioLabel);
        dialog.add(anioField);
        dialog.add(paginasDuracionLabel);
        dialog.add(paginasDuracionField);
        dialog.add(generoLabel);
        dialog.add(generoField);
        dialog.add(new JLabel());
        dialog.add(agregarButton);

        dialog.setVisible(true);
    }

    private void mostrarFormularioPrestar(JFrame frame) {
        JDialog dialog = new JDialog(frame, "Prestar Elemento", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel tituloLabel = new JLabel("Título:");
        JTextField tituloField = new JTextField();
        JButton prestarButton = new JButton("Prestar");

        prestarButton.addActionListener(e -> {
            String titulo = tituloField.getText();
            boolean prestado = biblioteca.prestarElemento(titulo);
            if (prestado) {
                JOptionPane.showMessageDialog(dialog, "Elemento prestado con éxito.");
            } else {
                JOptionPane.showMessageDialog(dialog, "Elemento no encontrado o ya prestado.");
            }
            dialog.dispose();
        });

        dialog.add(tituloLabel);
        dialog.add(tituloField);
        dialog.add(new JLabel());
        dialog.add(prestarButton);

        dialog.setVisible(true);
    }

    public boolean prestarElemento(String titulo) {
        for (ElementoBiblioteca elemento : elementos) { // Itera sobre 'elementos', no sobre un String.
            if (elemento.getTitulo().equalsIgnoreCase(titulo) && !elemento.estaPrestado()) {
                elemento.prestar();
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BibliotecaGUI::new);
    }

    // Método para registrar un elemento en la biblioteca
    public void registrarElemento(ElementoBiblioteca elementoBiblioteca) {
        if (elementos == null) {
            elementos = new ElementoBiblioteca[0];
        }
        ElementoBiblioteca[] nuevosElementos = new ElementoBiblioteca[elementos.length + 1];
        System.arraycopy(elementos, 0, nuevosElementos, 0, elementos.length);
        nuevosElementos[elementos.length] = elementoBiblioteca;
        elementos = nuevosElementos;
    }

    public void mostrarElementosDisponibles() {
        StringBuilder resultado = new StringBuilder();
        for (ElementoBiblioteca elemento : elementos) {
            if (!elemento.estaPrestado()) {
                resultado.append(elemento.toString()).append("\n");
            }
        }
        textArea.setText(resultado.toString());
    }
}