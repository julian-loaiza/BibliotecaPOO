import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // Puedes sobreescribir estos valores con DB_URL, DB_USER y DB_PASSWORD.
    private static final String URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "12345");

    public static Connection getConnection() throws SQLException {
        try {
            // Registrar el driver (opcional desde Java 6)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL", e);
        }
    }

    public static void initializeDatabase() throws SQLException {
        // Crea tablas base y tablas específicas por tipo para persistir Libro, Revista y DVD.
        String sqlElementos = """
                CREATE TABLE IF NOT EXISTS elementos (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    titulo VARCHAR(255) NOT NULL,
                    autor VARCHAR(255) NOT NULL,
                    ano_publicacion INT NOT NULL,
                    tipo VARCHAR(30) NOT NULL,
                    prestado BOOLEAN NOT NULL DEFAULT FALSE
                )
                """;

        String sqlLibros;
        String sqlRevista;
        String sqlDvds;

        String sqlUsuarios = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(120) NOT NULL
                )
                """;

        String sqlPrestamos;
        String sqlReservas;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sqlElementos);

            // Reutiliza el tipo exacto de elementos.id para evitar errores de FK por signed/unsigned.
            String elementoIdType = getColumnType(connection, "elementos", "id", "BIGINT");

            sqlLibros = String.format("""
                    CREATE TABLE IF NOT EXISTS libros (
                        elemento_id %s PRIMARY KEY,
                        numero_paginas INT NOT NULL,
                        genero VARCHAR(100) NOT NULL,
                        CONSTRAINT fk_libro_elemento FOREIGN KEY (elemento_id) REFERENCES elementos(id) ON DELETE CASCADE
                    )
                    """, elementoIdType);

            sqlRevista = String.format("""
                    CREATE TABLE IF NOT EXISTS revista (
                        elemento_id %s PRIMARY KEY,
                        numero INT NOT NULL,
                        tema VARCHAR(120) NOT NULL,
                        CONSTRAINT fk_revista_elemento FOREIGN KEY (elemento_id) REFERENCES elementos(id) ON DELETE CASCADE
                    )
                    """, elementoIdType);

            sqlDvds = String.format("""
                    CREATE TABLE IF NOT EXISTS dvds (
                        elemento_id %s PRIMARY KEY,
                        duracion INT NOT NULL,
                        categoria VARCHAR(120) NOT NULL,
                        CONSTRAINT fk_dvd_elemento FOREIGN KEY (elemento_id) REFERENCES elementos(id) ON DELETE CASCADE
                    )
                    """, elementoIdType);

            sqlPrestamos = String.format("""
                    CREATE TABLE IF NOT EXISTS prestamos (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        elemento_id %s NOT NULL,
                        fecha_prestamo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
                        CONSTRAINT fk_prestamo_elemento FOREIGN KEY (elemento_id) REFERENCES elementos(id) ON DELETE CASCADE
                    )
                    """, elementoIdType);

            sqlReservas = String.format("""
                    CREATE TABLE IF NOT EXISTS reservas (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        elemento_id %s NOT NULL,
                        usuario VARCHAR(100) NOT NULL,
                        fecha_reserva TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
                        CONSTRAINT fk_reserva_elemento FOREIGN KEY (elemento_id) REFERENCES elementos(id) ON DELETE CASCADE
                    )
                    """, elementoIdType);

            statement.execute(sqlLibros);
            statement.execute(sqlRevista);
            statement.execute(sqlDvds);
            statement.execute(sqlUsuarios);
            statement.execute(sqlPrestamos);
            statement.execute(sqlReservas);

            // Usuario semilla para esquemas que exigen usuario_id en préstamos/reservas.
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.ensureUsuarioId(connection, 1L, "usuario_demo");
            usuarioDAO.ensureUsuarioId(connection, 2L, "usuario_demo_02");

            // Migra esquemas viejos: si la columna no existe, se agrega sin borrar datos existentes.
            ensureColumnExists(connection, "elementos", "prestado", "BOOLEAN NOT NULL DEFAULT FALSE");
        }
    }

    private static void ensureColumnExists(Connection connection, String tableName, String columnName, String definition) throws SQLException {
        String checkSql = "SHOW COLUMNS FROM " + tableName + " LIKE '" + columnName + "'";
        try (Statement check = connection.createStatement();
             ResultSet rs = check.executeQuery(checkSql)) {
            if (!rs.next()) {
                try (Statement alter = connection.createStatement()) {
                    alter.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
                }
            }
        }
    }

    private static String getColumnType(Connection connection, String tableName, String columnName, String fallback) throws SQLException {
        String checkSql = "SHOW COLUMNS FROM " + tableName + " LIKE '" + columnName + "'";
        try (Statement check = connection.createStatement();
             ResultSet rs = check.executeQuery(checkSql)) {
            if (rs.next()) {
                String type = rs.getString("Type");
                if (type != null && !type.isBlank()) {
                    return type.toUpperCase();
                }
            }
        }
        return fallback;
    }


    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("¡Conexión exitosa a la base de datos!");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}