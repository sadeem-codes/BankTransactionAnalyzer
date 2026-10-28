
# Bank Transaction Analyzer

A Java console application that uses SQLite to store, retrieve, and analyze sample bank transactions.

## Features
- Creates a SQLite transactions table if it does not exist.
- Inserts four sample transactions when the table is empty.
- Displays transaction details in chronological order.
- Calculates total deposits, total withdrawals, and net balance change.
- Flags transactions exceeding SAR 3,000. These alerts do not prove fraud.
- Stores monetary amounts as integer cents to avoid floating-point rounding issues.

## Technologies
- Java (developed using Java 22)
- SQLite
- JDBC (SQLite JDBC driver)
- Eclipse IDE

## Project Files
- `Main.java` — Java source code and application entry point.
- `README.md` — Project documentation.

## How to Run
1. Install a compatible Java Development Kit (JDK).
2. Download the SQLite JDBC driver and add the JAR to your project's classpath.
3. Compile and run `Main.java` in your Java IDE, with the JDBC driver available at runtime.
4. The application creates `bank_transactions.db` in the working directory if it does not exist and populates an empty transactions table with sample data.

## Example Output
```text
BANK TRANSACTION ANALYZER
TX001 | DEPOSIT    |    5000.00 SAR | 2026-10-01
TX002 | WITHDRAWAL |     750.00 SAR | 2026-10-02
TX003 | WITHDRAWAL |    3500.00 SAR | 2026-10-03
  [ALERT] Amount exceeds 3,000 SAR.
  This does not prove fraud.
TX004 | DEPOSIT    |    1200.00 SAR | 2026-10-04
------------------------------------
Transactions:       4
Total deposits:     6200.00 SAR
Total withdrawals:  4250.00 SAR
Net balance change: 1950.00 SAR
```

## Notes
This is an educational console project using fictional sample transactions. The threshold alert is a simple rule and is not a fraud detection system.
