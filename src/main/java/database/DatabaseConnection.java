package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Klasa odpowiada za połączenie z bazą danych SQLite
public class DatabaseConnection
{
    // Zmienna do przechowywania połączenia z bazą
    private Connection connection;

    // Getter (Zwraca aktualne połączenie z bazą)
    public Connection getConnection() {return connection;}

    // Łączy się z bazą danych na podstawie podanej ścieżki do pliku
    public void connect(String databasePath) throws SQLException
    {
        try
        {
            // Tworzymy adres (URL) do naszej bazy danych
            String url = "jdbc:sqlite:" + databasePath;

            // Nawiązujemy połączenie
            connection = DriverManager.getConnection(url);

            // Komunikat, jeśli się udało
            System.out.println("Connected to the database.");
        }
        catch (SQLException e)
        {
            // Komunikat, jeśli coś poszło nie tak
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    // Zamyka połączenie z bazą danych
    public void disconnect() throws SQLException
    {
        try
        {
            // Sprawdzamy, czy połączenie istnieje i jest otwarte
            if(this.connection != null && !this.connection.isClosed())
            {
                // Zamykanie połączenia
                this.connection.close();

                // Komunikat po rozłączeniu
                System.out.println("Database connection is closed.");
            }
        }
        catch (SQLException e)
        {
            // Komunikat, jeśli wystąpił problem przy rozłączaniu
            System.out.println("An error occurred while disconnecting from the DB " + e.getMessage());
        }
    }
}
