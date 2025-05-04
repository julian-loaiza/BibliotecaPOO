import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaUI extends JFrame {
        private Biblioteca biblioteca;
        private JTabbedPane tabbedPane;
        private JTable elementosTable;
        private DefaultTableModel elementosTableModel;
        private JTextField buscarTextField;
        private JComboBox<String> tipoComboBox;
        private JButton buscarButton;
        private JButton prestarButton;
        private JButton devolverButton;
        private JButton agregarButton;

        public BibliotecaUI() {
            biblioteca = new Biblioteca();
            inicializarDatos(); // Cargar datos iniciales
            configurarVentana();
            inicializarComponentes();
            actualizarTablaElementos();
        }

        private void inicializarDatos() {
            // Cargar algunos datos de muestra
            Libro libro1 = new Libro("Cien Años de Soledad", "Gabriel García Márquez", 1967, 432, "Realismo Mágico");
            Revista revista1 = new Revista("National Geographic", "National Geographic Society", 2023, 245, "Ciencia y Naturaleza");
            DVD dvd1 = new DVD("Inception", "Christopher Nolan", 2010, 148, "Ciencia Ficción");

            libro1.registrarLibro(biblioteca);
            revista1.registrarRevista(biblioteca);
            dvd1.registrarDVD(biblioteca);
        }

        private void configurarVentana() {
            setTitle("Sistema de Gestión de Biblioteca");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
        }

        private void inicializarComponentes() {
            // Panel principal con pestañas
            tabbedPane = new JTabbedPane();

            // Pestaña de Catálogo
            JPanel catalogoPanel = crearPanelCatalogo();
            tabbedPane.addTab("Catálogo", catalogoPanel);

            // Pestaña de Agregar Nuevo Elemento
            JPanel agregarPanel = crearPanelAgregar();
            tabbedPane.addTab("Agregar Elemento", agregarPanel);

            // Pestaña de Préstamos y Devoluciones
            JPanel prestamosPanel = crearPanelPrestamos();
            tabbedPane.addTab("Préstamos", prestamosPanel);

            add(tabbedPane, BorderLayout.CENTER);
        }

        private JPanel crearPanelCatalogo() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Panel de búsqueda
            JPanel busquedaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buscarTextField = new JTextField(20);
            tipoComboBox = new JComboBox<>(new String[]{"Todos", "Libro", "Revista", "DVD"});
            buscarButton = new JButton("Buscar");
            buscarButton.addActionListener(e -> buscarElementos());

            busquedaPanel.add(new JLabel("Buscar por título:"));
            busquedaPanel.add(buscarTextField);
            busquedaPanel.add(new JLabel("Tipo:"));
            busquedaPanel.add(tipoComboBox);
            busquedaPanel.add(buscarButton);

            panel.add(busquedaPanel, BorderLayout.NORTH);

            // Tabla de elementos
            String[] columnas = {"Tipo", "Título", "Autor/Editorial", "Año", "Detalles", "Estado"};
            elementosTableModel = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            elementosTable = new JTable(elementosTableModel);
            JScrollPane scrollPane = new JScrollPane(elementosTable);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Panel de acciones
            JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            prestarButton = new JButton("Prestar");
            devolverButton = new JButton("Devolver");

            prestarButton.addActionListener(e -> prestarElemento());
            devolverButton.addActionListener(e -> devolverElemento());

            accionesPanel.add(prestarButton);
            accionesPanel.add(devolverButton);
            panel.add(accionesPanel, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel crearPanelAgregar() {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            // Selector de tipo
            JComboBox<String> tipoElementoComboBox = new JComboBox<>(new String[]{"Libro", "Revista", "DVD"});
            JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            tipoPanel.add(new JLabel("Tipo de elemento:"));
            tipoPanel.add(tipoElementoComboBox);

            panel.add(tipoPanel, BorderLayout.NORTH);

            // Campos comunes
            JTextField tituloField = new JTextField(20);
            JTextField autorField = new JTextField(20);
            JTextField anioField = new JTextField(4);

            // Campos específicos según tipo
            JPanel libroPanel = new JPanel(new GridBagLayout());
            JTextField paginasField = new JTextField(4);
            JTextField generoField = new JTextField(15);

            gbc.gridx = 0; gbc.gridy = 0;
            libroPanel.add(new JLabel("Número de páginas:"), gbc);
            gbc.gridx = 1;
            libroPanel.add(paginasField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            libroPanel.add(new JLabel("Género:"), gbc);
            gbc.gridx = 1;
            libroPanel.add(generoField, gbc);

            JPanel revistaPanel = new JPanel(new GridBagLayout());
            JTextField numeroField = new JTextField(4);
            JTextField temaField = new JTextField(15);

            gbc.gridx = 0; gbc.gridy = 0;
            revistaPanel.add(new JLabel("Número:"), gbc);
            gbc.gridx = 1;
            revistaPanel.add(numeroField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            revistaPanel.add(new JLabel("Tema:"), gbc);
            gbc.gridx = 1;
            revistaPanel.add(temaField, gbc);

            JPanel dvdPanel = new JPanel(new GridBagLayout());
            JTextField duracionField = new JTextField(4);
            JTextField categoriaField = new JTextField(15);

            gbc.gridx = 0; gbc.gridy = 0;
            dvdPanel.add(new JLabel("Duración (min):"), gbc);
            gbc.gridx = 1;
            dvdPanel.add(duracionField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            dvdPanel.add(new JLabel("Categoría:"), gbc);
            gbc.gridx = 1;
            dvdPanel.add(categoriaField, gbc);

            // Panel de campos comunes
            JPanel comunesPanel = new JPanel(new GridBagLayout());
            gbc.gridx = 0; gbc.gridy = 0;
            comunesPanel.add(new JLabel("Título:"), gbc);
            gbc.gridx = 1;
            comunesPanel.add(tituloField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            comunesPanel.add(new JLabel("Autor/Editorial:"), gbc);
            gbc.gridx = 1;
            comunesPanel.add(autorField, gbc);
            gbc.gridx = 0; gbc.gridy = 2;
            comunesPanel.add(new JLabel("Año:"), gbc);
            gbc.gridx = 1;
            comunesPanel.add(anioField, gbc);

            // CardLayout para paneles específicos
            JPanel specificPanel = new JPanel(new CardLayout());
            specificPanel.add(libroPanel, "Libro");
            specificPanel.add(revistaPanel, "Revista");
            specificPanel.add(dvdPanel, "DVD");

            // Evento para cambiar panel específico
            tipoElementoComboBox.addActionListener(e -> {
                CardLayout cl = (CardLayout) specificPanel.getLayout();
                cl.show(specificPanel, (String) tipoElementoComboBox.getSelectedItem());
            });

            // Panel principal del formulario
            JPanel mainFormPanel = new JPanel(new BorderLayout());
            mainFormPanel.add(comunesPanel, BorderLayout.NORTH);
            mainFormPanel.add(specificPanel, BorderLayout.CENTER);

            panel.add(mainFormPanel, BorderLayout.CENTER);

            // Botón de agregar
            JButton agregarButton = new JButton("Agregar Elemento");
            agregarButton.addActionListener(e -> {
                try {
                    String tipo = (String) tipoElementoComboBox.getSelectedItem();
                    String titulo = tituloField.getText();
                    String autor = autorField.getText();
                    int anio = Integer.parseInt(anioField.getText());

                    if (titulo.isEmpty() || autor.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Por favor, complete los campos obligatorios",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    switch (tipo) {
                        case "Libro":
                            int paginas = Integer.parseInt(paginasField.getText());
                            String genero = generoField.getText();
                            Libro libro = new Libro(titulo, autor, anio, paginas, genero);
                            libro.registrarLibro(biblioteca);
                            break;
                        case "Revista":
                            int numero = Integer.parseInt(numeroField.getText());
                            String tema = temaField.getText();
                            Revista revista = new Revista(titulo, autor, anio, numero, tema);
                            revista.registrarRevista(biblioteca);
                            break;
                        case "DVD":
                            int duracion = Integer.parseInt(duracionField.getText());
                            String categoria = categoriaField.getText();
                            DVD dvd = new DVD(titulo, autor, anio, duracion, categoria);
                            dvd.registrarDVD(biblioteca);
                            break;
                    }

                    JOptionPane.showMessageDialog(this, "Elemento agregado correctamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Limpiar campos
                    tituloField.setText("");
                    autorField.setText("");
                    anioField.setText("");
                    paginasField.setText("");
                    generoField.setText("");
                    numeroField.setText("");
                    temaField.setText("");
                    duracionField.setText("");
                    categoriaField.setText("");

                    // Actualizar tabla
                    actualizarTablaElementos();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(agregarButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel crearPanelPrestamos() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // En este panel podríamos agregar funcionalidad para gestionar préstamos
            // Por ahora, dejamos un panel simple con un mensaje
            JLabel label = new JLabel("Funcionalidad de préstamos en desarrollo");
            label.setHorizontalAlignment(JLabel.CENTER);
            panel.add(label, BorderLayout.CENTER);

            return panel;
        }

        private void actualizarTablaElementos() {
            // Limpiar tabla
            elementosTableModel.setRowCount(0);

            // Obtener elementos de la biblioteca
            // Aquí necesitaríamos un método para obtener todos los elementos
            // Por ahora, simulamos con una lista vacía
            List<ElementoBiblioteca> elementos = new ArrayList<>();

            // Aquí iría el código para obtener todos los elementos de la biblioteca
            // Como no tenemos acceso directo a la lista de elementos, tendríamos que
            // modificar la clase Biblioteca para que nos devuelva la lista completa

            // Agregar elementos a la tabla
            for (ElementoBiblioteca elem : elementos) {
                String detalles = "";
                if (elem instanceof Libro) {
                    Libro libro = (Libro) elem;
                    detalles = libro.getNumeroPaginas() + " págs., " + libro.getGenero();
                } else if (elem instanceof Revista) {
                    Revista revista = (Revista) elem;
                    detalles = "Número " + revista.getNumero() + ", " + revista.getTema();
                } else if (elem instanceof DVD) {
                    DVD dvd = (DVD) elem;
                    detalles = dvd.getDuracion() + " min., " + dvd.getCategoria();
                }

                elementosTableModel.addRow(new Object[]{
                        elem.getTipo(),
                        elem.getTitulo(),
                        elem.getAutor(),
                        elem.getAnoPublicacion(),
                        detalles,
                        elem.estaPrestado() ? "Prestado" : "Disponible"
                });
            }
        }

        private void buscarElementos() {
            String texto = buscarTextField.getText().trim();
            String tipo = (String) tipoComboBox.getSelectedItem();

            if (texto.isEmpty() && tipo.equals("Todos")) {
                actualizarTablaElementos();
                return;
            }

            // Limpiar tabla
            elementosTableModel.setRowCount(0);

            // Buscar elementos por título
            List<ElementoBiblioteca> elementos = biblioteca.buscarPorTitulo(texto);

            // Filtrar por tipo si es necesario
            if (!tipo.equals("Todos")) {
                elementos = elementos.stream()
                        .filter(e -> e.getTipo().equals(tipo))
                        .collect(java.util.stream.Collectors.toList());
            }

            // Agregar elementos a la tabla (mismo código que en actualizarTablaElementos)
            for (ElementoBiblioteca elem : elementos) {
                String detalles = "";
                if (elem instanceof Libro) {
                    Libro libro = (Libro) elem;
                    detalles = libro.getNumeroPaginas() + " págs., " + libro.getGenero();
                } else if (elem instanceof Revista) {
                    Revista revista = (Revista) elem;
                    detalles = "Número " + revista.getNumero() + ", " + revista.getTema();
                } else if (elem instanceof DVD) {
                    DVD dvd = (DVD) elem;
                    detalles = dvd.getDuracion() + " min., " + dvd.getCategoria();
                }

                elementosTableModel.addRow(new Object[]{
                        elem.getTipo(),
                        elem.getTitulo(),
                        elem.getAutor(),
                        elem.getAnoPublicacion(),
                        detalles,
                        elem.estaPrestado() ? "Prestado" : "Disponible"
                });
            }
        }

        private void prestarElemento() {
            int selectedRow = elementosTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un elemento",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String titulo = (String) elementosTable.getValueAt(selectedRow, 1);
            String estado = (String) elementosTable.getValueAt(selectedRow, 5);

            if (estado.equals("Prestado")) {
                JOptionPane.showMessageDialog(this, "Este elemento ya está prestado",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar el elemento en la biblioteca
            List<ElementoBiblioteca> elementos = biblioteca.buscarPorTitulo(titulo);
            if (!elementos.isEmpty()) {
                ElementoBiblioteca elemento = elementos.get(0);
                elemento.prestar();
                actualizarTablaElementos();
                JOptionPane.showMessageDialog(this, "Elemento prestado correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void devolverElemento() {
            int selectedRow = elementosTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un elemento",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String titulo = (String) elementosTable.getValueAt(selectedRow, 1);
            String estado = (String) elementosTable.getValueAt(selectedRow, 5);

            if (estado.equals("Disponible")) {
                JOptionPane.showMessageDialog(this, "Este elemento no está prestado",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Buscar el elemento en la biblioteca
            List<ElementoBiblioteca> elementos = biblioteca.buscarPorTitulo(titulo);
            if (!elementos.isEmpty()) {
                ElementoBiblioteca elemento = elementos.get(0);
                elemento.devolver();
                actualizarTablaElementos();
                JOptionPane.showMessageDialog(this, "Elemento devuelto correctamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                BibliotecaUI ui = new BibliotecaUI();
                ui.setVisible(true);
            });
        }
}
