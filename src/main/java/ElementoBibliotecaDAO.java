import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElementoBibliotecaDAO {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void guardarElemento(ElementoBiblioteca elemento) throws SQLException {
        // Se usa 'ano_publicacion' para coincidir con el esquema ya creado en MySQL.
        String sqlElemento = "INSERT INTO elementos (titulo, autor, ano_publicacion, tipo, prestado) VALUES (?, ?, ?, ?, ?)";
        Long previousId = elemento.getIdEnBaseDatos();

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement pstElemento = connection.prepareStatement(sqlElemento, Statement.RETURN_GENERATED_KEYS)) {
                pstElemento.setString(1, elemento.getTitulo());
                pstElemento.setString(2, elemento.getAutor());
                pstElemento.setInt(3, elemento.getAnoPublicacion());
                pstElemento.setString(4, elemento.getTipo());
                pstElemento.setBoolean(5, elemento.estaPrestado());
                pstElemento.executeUpdate();

                long elementoId = obtenerIdGenerado(pstElemento);
                guardarDetallePorTipo(connection, elementoId, elemento);
                connection.commit();
                // Asigna el ID solo cuando la transacción completa fue confirmada.
                elemento.setIdEnBaseDatos(elementoId);
            } catch (SQLException e) {
                connection.rollback();
                // Evita conservar un ID en memoria que pudo no existir por rollback.
                elemento.setIdEnBaseDatos(previousId);
                throw e;
            }
        }
    }

    public boolean existsElementoId(Long elementoId) throws SQLException {
        if (elementoId == null) {
            return false;
        }

        String sql = "SELECT 1 FROM elementos WHERE id = ? LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setLong(1, elementoId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private long obtenerIdGenerado(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            throw new SQLException("No se pudo obtener el ID generado para el elemento.");
        }
    }

    private void guardarDetallePorTipo(Connection connection, long elementoId, ElementoBiblioteca elemento) throws SQLException {
        // Inserta en la tabla hija correspondiente para mantener el modelo normalizado.
        if (elemento instanceof Libro libro) {
            String sql = "INSERT INTO libros (elemento_id, numero_paginas, genero) VALUES (?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, elementoId);
                pst.setInt(2, libro.getNumeroPaginas());
                pst.setString(3, libro.getGenero());
                pst.executeUpdate();
            }
            return;
        }

        if (elemento instanceof Revista revista) {
            String tablaRevista = resolveRevistaTable(connection);
            insertRevistaFlexible(connection, tablaRevista, elementoId, revista);
            return;
        }

        if (elemento instanceof DVD dvd) {
            String sql = "INSERT INTO dvds (elemento_id, duracion, categoria) VALUES (?, ?, ?)";
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setLong(1, elementoId);
                pst.setInt(2, dvd.getDuracion());
                pst.setString(3, dvd.getCategoria());
                pst.executeUpdate();
            }
        }
    }

    public void registrarPrestamo(ElementoBiblioteca elemento, Long usuarioId) throws SQLException {
        Long elementoId = elemento.getIdEnBaseDatos();
        if (elementoId == null) {
            throw new SQLException("El elemento aún no tiene id persistido para registrar préstamo.");
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                actualizarEstadoPrestado(connection, elementoId, true);

                boolean tieneUsuarioId = hasColumn(connection, "prestamos", "usuario_id");
                boolean tieneFechaEsperada = hasColumn(connection, "prestamos", "fecha_devolucion_esperada");
                String estadoPrestamo = resolveEstadoValue(connection, "prestamos", "estado", "ACTIVO");
                long usuarioIdFinal = usuarioId != null ? usuarioId : 1L;
                if (tieneUsuarioId) {
                    usuarioIdFinal = usuarioDAO.ensureUsuarioId(connection, usuarioIdFinal, "usuario_prestamo_" + usuarioIdFinal);
                }

                StringBuilder columnas = new StringBuilder("elemento_id");
                StringBuilder values = new StringBuilder("?");
                if (tieneUsuarioId) {
                    columnas.append(", usuario_id");
                    values.append(", ?");
                }
                if (hasColumn(connection, "prestamos", "estado")) {
                    columnas.append(", estado");
                    values.append(", ?");
                }
                if (tieneFechaEsperada) {
                    columnas.append(", fecha_devolucion_esperada");
                    values.append(", ?");
                }

                String sqlInsert = "INSERT INTO prestamos (" + columnas + ") VALUES (" + values + ")";

                try (PreparedStatement pstPrestamo = connection.prepareStatement(sqlInsert)) {
                    int index = 1;
                    pstPrestamo.setLong(index++, elementoId);
                    if (tieneUsuarioId) {
                        pstPrestamo.setLong(index++, usuarioIdFinal);
                    }
                    if (hasColumn(connection, "prestamos", "estado")) {
                        pstPrestamo.setString(index++, estadoPrestamo);
                    }
                    if (tieneFechaEsperada) {
                        // Fecha estimada simple: 7 dias desde el prestamo.
                        pstPrestamo.setDate(index, Date.valueOf(LocalDate.now().plusDays(7)));
                    }
                    pstPrestamo.executeUpdate();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void registrarReserva(ElementoBiblioteca elemento, Long usuarioId, String usuario) throws SQLException {
        Long elementoId = elemento.getIdEnBaseDatos();
        if (elementoId == null) {
            throw new SQLException("El elemento aún no tiene id persistido para registrar reserva.");
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean tieneUsuarioTexto = hasColumn(connection, "reservas", "usuario");
            boolean tieneUsuarioId = hasColumn(connection, "reservas", "usuario_id");
            boolean tieneEstado = hasColumn(connection, "reservas", "estado");
            boolean tieneFechaReservaHasta = hasColumn(connection, "reservas", "fecha_reserva_hasta");
            String estadoReserva = resolveEstadoValue(connection, "reservas", "estado", "PENDIENTE");
            long usuarioIdFinal = usuarioId != null ? usuarioId : 1L;
            if (tieneUsuarioId) {
                usuarioIdFinal = usuarioDAO.ensureUsuarioId(connection, usuarioIdFinal, usuario);
            }

            StringBuilder columnas = new StringBuilder("elemento_id");
            StringBuilder values = new StringBuilder("?");
            if (tieneUsuarioTexto) {
                columnas.append(", usuario");
                values.append(", ?");
            }
            if (tieneUsuarioId) {
                columnas.append(", usuario_id");
                values.append(", ?");
            }
            if (tieneEstado) {
                columnas.append(", estado");
                values.append(", ?");
            }
            if (tieneFechaReservaHasta) {
                columnas.append(", fecha_reserva_hasta");
                values.append(", ?");
            }

            String sqlInsert = "INSERT INTO reservas (" + columnas + ") VALUES (" + values + ")";

            try (PreparedStatement pstReserva = connection.prepareStatement(sqlInsert)) {
                int index = 1;
                pstReserva.setLong(index++, elementoId);
                if (tieneUsuarioTexto) {
                    pstReserva.setString(index++, usuario);
                }
                if (tieneUsuarioId) {
                    pstReserva.setLong(index++, usuarioIdFinal);
                }
                if (tieneEstado) {
                    pstReserva.setString(index++, estadoReserva);
                }
                if (tieneFechaReservaHasta) {
                    // Ventana de reserva por defecto: 3 dias.
                    pstReserva.setDate(index, Date.valueOf(LocalDate.now().plusDays(3)));
                }
                pstReserva.executeUpdate();
            }
        }
    }

    public void actualizarEstadoPrestado(ElementoBiblioteca elemento, boolean prestado) throws SQLException {
        Long elementoId = elemento.getIdEnBaseDatos();
        if (elementoId == null) {
            throw new SQLException("El elemento aún no tiene id persistido para actualizar estado.");
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            actualizarEstadoPrestado(connection, elementoId, prestado);
        }
    }

    private void actualizarEstadoPrestado(Connection connection, Long elementoId, boolean prestado) throws SQLException {
        String sqlUpdate = "UPDATE elementos SET prestado = ? WHERE id = ?";
        try (PreparedStatement pstEstado = connection.prepareStatement(sqlUpdate)) {
            pstEstado.setBoolean(1, prestado);
            pstEstado.setLong(2, elementoId);
            pstEstado.executeUpdate();
        }
    }

    private boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        String checkSql = "SHOW COLUMNS FROM " + tableName + " LIKE ?";
        try (PreparedStatement pst = connection.prepareStatement(checkSql)) {
            pst.setString(1, columnName);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String resolveRevistaTable(Connection connection) throws SQLException {
        if (tableExists(connection, "revista")) {
            return "revista";
        }
        if (tableExists(connection, "revistas")) {
            return "revistas";
        }
        throw new SQLException("No existe la tabla de revistas (revista/revistas) en la base de datos.");
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        String sql = "SHOW TABLES LIKE ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, tableName);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String resolveEstadoValue(Connection connection, String tableName, String columnName, String fallback) throws SQLException {
        String checkSql = "SHOW COLUMNS FROM " + tableName + " LIKE ?";
        try (PreparedStatement pst = connection.prepareStatement(checkSql)) {
            pst.setString(1, columnName);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return fallback;
                }

                String type = rs.getString("Type");
                if (type == null) {
                    return fallback;
                }

                String normalized = type.toLowerCase();
                if (normalized.startsWith("enum(")) {
                    Matcher matcher = Pattern.compile("'([^']+)'").matcher(type);
                    if (matcher.find()) {
                        // Primer valor permitido por el enum para evitar truncation.
                        return matcher.group(1);
                    }
                }

                if (normalized.contains("tinyint") || normalized.contains("int") || normalized.contains("decimal")) {
                    return "1";
                }

                return fallback;
            }
        }
    }

    private void insertRevistaFlexible(Connection connection, String tablaRevista, long elementoId, Revista revista) throws SQLException {
        List<ColumnMeta> columns = describeTable(connection, tablaRevista);
        List<ColumnMeta> selected = new ArrayList<>();

        for (ColumnMeta column : columns) {
            String field = column.field().toLowerCase();
            if (field.equals("elemento_id") || field.equals("id")) {
                selected.add(column);
                continue;
            }
            if (field.contains("numero") || field.contains("tema") || field.contains("categoria") || field.contains("editorial")) {
                selected.add(column);
                continue;
            }
            if (isRequiredWithoutDefault(column)) {
                selected.add(column);
            }
        }

        if (selected.isEmpty()) {
            throw new SQLException("No se encontraron columnas válidas para insertar en " + tablaRevista + ".");
        }

        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < selected.size(); i++) {
            if (i > 0) {
                fields.append(", ");
                values.append(", ");
            }
            fields.append(selected.get(i).field());
            values.append("?");
        }

        String sql = "INSERT INTO " + tablaRevista + " (" + fields + ") VALUES (" + values + ")";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            int index = 1;
            for (ColumnMeta column : selected) {
                pst.setObject(index++, buildRevistaValue(column, elementoId, revista, connection, tablaRevista));
            }
            pst.executeUpdate();
        }
    }

    private Object buildRevistaValue(ColumnMeta column, long elementoId, Revista revista, Connection connection, String tableName) throws SQLException {
        String field = column.field().toLowerCase();
        String type = column.type() == null ? "" : column.type().toLowerCase();

        if (field.equals("elemento_id") || field.equals("id")) {
            return elementoId;
        }
        if (field.contains("numero")) {
            return revista.getNumero();
        }
        if (field.contains("tema") || field.contains("categoria") || field.contains("genero")) {
            return revista.getTema();
        }
        if (field.contains("editorial") || field.contains("autor")) {
            return revista.getAutor();
        }
        if (field.contains("estado")) {
            return resolveEstadoValue(connection, tableName, column.field(), "ACTIVO");
        }
        if (type.startsWith("enum(")) {
            return resolveEstadoValue(connection, tableName, column.field(), "ACTIVO");
        }
        if (type.contains("tinyint") || type.contains("int") || type.contains("decimal") || type.contains("double") || type.contains("float")) {
            return 1;
        }
        if (type.contains("date") && !type.contains("time")) {
            return Date.valueOf(LocalDate.now());
        }
        if (type.contains("datetime") || type.contains("timestamp")) {
            return Timestamp.valueOf(LocalDateTime.now());
        }
        if (type.contains("bool") || type.contains("bit")) {
            return 1;
        }
        return "generico";
    }

    private boolean isRequiredWithoutDefault(ColumnMeta column) {
        boolean notNull = "NO".equalsIgnoreCase(column.isNullable());
        boolean noDefault = column.defaultValue() == null;
        boolean autoIncrement = column.extra() != null && column.extra().toLowerCase().contains("auto_increment");
        return notNull && noDefault && !autoIncrement;
    }

    private List<ColumnMeta> describeTable(Connection connection, String tableName) throws SQLException {
        List<ColumnMeta> columns = new ArrayList<>();
        String sql = "SHOW COLUMNS FROM " + tableName;
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                columns.add(new ColumnMeta(
                        rs.getString("Field"),
                        rs.getString("Type"),
                        rs.getString("Null"),
                        rs.getString("Default"),
                        rs.getString("Extra")
                ));
            }
        }
        return columns;
    }

    private record ColumnMeta(String field, String type, String isNullable, String defaultValue, String extra) {
    }

}

