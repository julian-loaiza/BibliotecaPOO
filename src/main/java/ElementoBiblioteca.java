/// Clase abstracta base para elementos de la biblioteca
import java.sql.SQLException;

public abstract class ElementoBiblioteca {
    // Atributos protegidos para que sean accesibles por las clases hijas
    protected String titulo;
    protected String autor;
    protected int anioPublicacion;
    protected boolean prestado;
    protected Biblioteca biblioteca; // Referencia a la biblioteca
    protected Long idEnBaseDatos;
    private static final ElementoBibliotecaDAO ELEMENTO_DAO = new ElementoBibliotecaDAO();

    // Constructor
    public ElementoBiblioteca(String titulo, String autor, int anioPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.prestado = false;
    }

    // Método para agregar el elemento a una biblioteca
    public void agregarABiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        biblioteca.registrarElemento(this);
    }

    // Métodos abstractos que serán implementados por las clases hijas
    public abstract void mostrarDetalles();
    public abstract String getTipo();

    // Métodos comunes para todos los elementos
    public void prestar() {
        prestar(1L);
    }

    public void prestar(Long usuarioId) {
        if (!asegurarPersistencia()) {
            System.err.println("No se pudo registrar el préstamo en base de datos: el elemento no está persistido.");
            return;
        }

        if (!prestado) {
            prestado = true;
            System.out.println(titulo + " ha sido prestado.");
            try {
                // Registra el préstamo en historial y marca el estado en la tabla base.
                ELEMENTO_DAO.registrarPrestamo(this, usuarioId);
            } catch (SQLException e) {
                System.err.println("No se pudo registrar el préstamo en base de datos: " + e.getMessage());
            }
        } else {
            System.out.println(titulo + " ya está prestado.");
        }
    }

    public void devolver() {
        if (prestado) {
            prestado = false;
            System.out.println(titulo + " ha sido devuelto.");
            try {
                ELEMENTO_DAO.actualizarEstadoPrestado(this, false);
            } catch (SQLException e) {
                System.err.println("No se pudo actualizar devolución en base de datos: " + e.getMessage());
            }
        } else {
            System.out.println(titulo + " no estaba prestado.");
        }
    }

    public void reservar(String usuario) {
        reservar(1L, usuario);
    }

    public void reservar(Long usuarioId, String usuario) {
        if (!asegurarPersistencia()) {
            System.err.println("No se pudo registrar la reserva en base de datos: el elemento no está persistido.");
            return;
        }

        try {
            // Genera un registro de reserva para el elemento actual.
            ELEMENTO_DAO.registrarReserva(this, usuarioId, usuario);
            System.out.println(titulo + " fue reservado por " + usuario + ".");
        } catch (SQLException e) {
            System.err.println("No se pudo registrar la reserva en base de datos: " + e.getMessage());
        }
    }

    private boolean asegurarPersistencia() {
        if (idEnBaseDatos != null) {
            try {
                if (ELEMENTO_DAO.existsElementoId(idEnBaseDatos)) {
                    return true;
                }
                // Limpia IDs huérfanos en memoria para reintentar persistencia real.
                idEnBaseDatos = null;
            } catch (SQLException e) {
                System.err.println("No se pudo validar persistencia del elemento: " + e.getMessage());
                return false;
            }
        }

        try {
            // Reintenta la persistencia para operaciones que dependen del ID en BD (prestamo/reserva).
            ELEMENTO_DAO.guardarElemento(this);
            return idEnBaseDatos != null;
        } catch (SQLException e) {
            System.err.println("No se pudo persistir el elemento antes de la operación: " + e.getMessage());
            return false;
        }
    }

    // Getters para acceder a la información del elemento
    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnoPublicacion() {
        return anioPublicacion;
    }

    public boolean estaPrestado() {
        return prestado;
    }

    public Long getIdEnBaseDatos() {
        return idEnBaseDatos;
    }

    public void setIdEnBaseDatos(Long idEnBaseDatos) {
        this.idEnBaseDatos = idEnBaseDatos;
    }
}
