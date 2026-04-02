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

public class UsuarioDAO {

    public long ensureUsuarioId(Long preferredId, String nombre) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return ensureUsuarioId(connection, preferredId, nombre);
        }
    }

    public long ensureUsuarioId(Connection connection, Long preferredId, String nombre) throws SQLException {
        if (preferredId != null && existsUsuario(connection, preferredId)) {
            return preferredId;
        }

        if (preferredId != null) {
            try {
                insertUsuario(connection, preferredId, nombre);
                return preferredId;
            } catch (SQLException ignored) {
                // Continúa con las estrategias de respaldo para esquemas más estrictos.
            }
        }

        Long existente = findAnyUsuarioId(connection);
        if (existente != null) {
            return existente;
        }

        return insertUsuario(connection, null, nombre);
    }

    public List<String> listarUsuarios() throws SQLException {
        List<String> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre FROM usuarios ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                usuarios.add(rs.getLong("id") + " - " + rs.getString("nombre"));
            }
        }

        return usuarios;
    }

    private boolean existsUsuario(Connection connection, long usuarioId) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE id = ? LIMIT 1";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setLong(1, usuarioId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Long findAnyUsuarioId(Connection connection) throws SQLException {
        String sql = "SELECT id FROM usuarios ORDER BY id LIMIT 1";
        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        }
    }

    private long insertUsuario(Connection connection, Long preferredId, String nombre) throws SQLException {
        List<ColumnMeta> columns = describeTable(connection, "usuarios");
        List<ColumnMeta> selected = new ArrayList<>();

        for (ColumnMeta column : columns) {
            String field = column.field();
            if ("id".equalsIgnoreCase(field)) {
                if (preferredId != null) {
                    selected.add(column);
                }
                continue;
            }

            if ("nombre".equalsIgnoreCase(field) || isRequiredWithoutDefault(column)) {
                selected.add(column);
            }
        }

        if (selected.isEmpty()) {
            throw new SQLException("No se encontraron columnas válidas para insertar en usuarios.");
        }

        StringBuilder fieldList = new StringBuilder();
        StringBuilder valueList = new StringBuilder();
        for (int i = 0; i < selected.size(); i++) {
            if (i > 0) {
                fieldList.append(", ");
                valueList.append(", ");
            }
            fieldList.append(selected.get(i).field());
            valueList.append("?");
        }

        String sql = "INSERT INTO usuarios (" + fieldList + ") VALUES (" + valueList + ")";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            for (ColumnMeta column : selected) {
                Object value = buildValue(column, preferredId, nombre);
                pst.setObject(index++, value);
            }
            pst.executeUpdate();

            if (preferredId != null) {
                return preferredId;
            }

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }

        Long existing = findAnyUsuarioId(connection);
        if (existing != null) {
            return existing;
        }

        throw new SQLException("No se pudo crear un usuario válido en la tabla usuarios.");
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

    private boolean isRequiredWithoutDefault(ColumnMeta column) {
        boolean notNull = "NO".equalsIgnoreCase(column.isNullable());
        boolean noDefault = column.defaultValue() == null;
        boolean autoIncrement = column.extra() != null && column.extra().toLowerCase().contains("auto_increment");
        return notNull && noDefault && !autoIncrement;
    }

    private Object buildValue(ColumnMeta column, Long preferredId, String nombre) {
        String field = column.field().toLowerCase();
        String type = column.type() == null ? "" : column.type().toLowerCase();

        if ("id".equals(field)) {
            return preferredId;
        }
        if ("nombre".equals(field)) {
            return nombre;
        }
        if (field.contains("email") || field.contains("correo")) {
            long suffix = preferredId != null ? preferredId : System.currentTimeMillis() % 100000;
            return "usuario" + suffix + "@demo.local";
        }
        if (field.contains("apellido")) {
            return "generico";
        }

        if (type.startsWith("enum(")) {
            Matcher matcher = Pattern.compile("'([^']+)'").matcher(column.type());
            if (matcher.find()) {
                return matcher.group(1);
            }
            return "ACTIVO";
        }
        if (type.contains("int") || type.contains("decimal") || type.contains("double") || type.contains("float")) {
            return 1;
        }
        if (type.contains("date") && !type.contains("time")) {
            return Date.valueOf(LocalDate.now());
        }
        if (type.contains("datetime") || type.contains("timestamp")) {
            return Timestamp.valueOf(LocalDateTime.now());
        }
        if (type.contains("bit") || type.contains("bool")) {
            return 1;
        }
        return "generico";
    }

    private record ColumnMeta(String field, String type, String isNullable, String defaultValue, String extra) {
    }
}

