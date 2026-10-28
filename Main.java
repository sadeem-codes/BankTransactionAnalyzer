import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    private static final String DB_URL =
            "jdbc:sqlite:bank_transactions.db";

    private static final BigDecimal ALERT_LIMIT =
            new BigDecimal("3000.00");

    public static void main(String[] args) {

        try (Connection connection =
                DriverManager.getConnection(DB_URL)) {

            createTable(connection);
            insertSampleDataIfEmpty(connection);
            printReport(connection);

        } catch (SQLException e) {
            System.err.println(
                    "Database error: " + e.getMessage());
            System.err.println(
                    "Check the SQLite JDBC library.");
        }
    }

    private static void createTable(
            Connection connection) throws SQLException {

        String sql =
                "CREATE TABLE IF NOT EXISTS transactions ("
                + "id TEXT PRIMARY KEY, "
                + "type TEXT NOT NULL "
                + "CHECK(type IN ('DEPOSIT', 'WITHDRAWAL')), "
                + "amount_cents INTEGER NOT NULL "
                + "CHECK(amount_cents > 0), "
                + "transaction_date TEXT NOT NULL)";

        try (Statement statement =
                connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static void insertSampleDataIfEmpty(
            Connection connection) throws SQLException {

        try (Statement statement =
                connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "SELECT COUNT(*) FROM transactions")) {

            if (result.next() && result.getInt(1) > 0) {
                return;
            }
        }

        String sql =
                "INSERT INTO transactions "
                + "(id, type, amount_cents, transaction_date) "
                + "VALUES (?, ?, ?, ?)";

        String[][] samples = {
                {"TX001", "DEPOSIT", "500000", "2026-10-01"},
                {"TX002", "WITHDRAWAL", "75000", "2026-10-02"},
                {"TX003", "WITHDRAWAL", "350000", "2026-10-03"},
                {"TX004", "DEPOSIT", "120000", "2026-10-04"}
        };

        try (PreparedStatement statement =
                connection.prepareStatement(sql)) {

            for (String[] sample : samples) {
                statement.setString(1, sample[0]);
                statement.setString(2, sample[1]);
                statement.setLong(
                        3, Long.parseLong(sample[2]));
                statement.setString(4, sample[3]);
                statement.executeUpdate();
            }
        }
    }

    private static void printReport(
            Connection connection) throws SQLException {

        long deposits = 0;
        long withdrawals = 0;
        int count = 0;

        System.out.println("====================================");
        System.out.println("       BANK TRANSACTION ANALYZER");
        System.out.println("====================================");

        String sql =
                "SELECT id, type, amount_cents, transaction_date "
                + "FROM transactions "
                + "ORDER BY transaction_date, id";

        try (Statement statement =
                connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                String id = result.getString("id");
                String type = result.getString("type");
                long cents = result.getLong("amount_cents");
                count++;

                if ("DEPOSIT".equals(type)) {
                    deposits = Math.addExact(deposits, cents);
                } else {
                    withdrawals = Math.addExact(
                            withdrawals, cents);
                }

                System.out.printf(
                        "%s | %-10s | %10s SAR | %s%n",
                        id,
                        type,
                        money(cents),
                        result.getString("transaction_date"));

                if (BigDecimal.valueOf(cents, 2)
                        .compareTo(ALERT_LIMIT) > 0) {

                    System.out.println(
                            "  [ALERT] Amount exceeds 3,000 SAR.");
                    System.out.println(
                            "  This does not prove fraud.");
                }
            }
        }

        System.out.println("------------------------------------");
        System.out.println("Transactions:       " + count);
        System.out.println(
                "Total deposits:     "
                + money(deposits) + " SAR");
        System.out.println(
                "Total withdrawals:  "
                + money(withdrawals) + " SAR");
        System.out.println(
                "Net balance change: "
                + money(Math.subtractExact(
                        deposits, withdrawals)) + " SAR");
    }

    private static String money(long cents) {
        return BigDecimal.valueOf(
                cents, 2).toPlainString();
    }
}