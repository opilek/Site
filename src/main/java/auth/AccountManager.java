package auth;

import database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Klasa odpowiedzialna za zarządzanie kontami użytkowników
public class AccountManager
{
    private final Connection connection; // Połączenie z bazą danych

    // Konstruktor przyjmuje połączenie i od razu tworzy tabelę accounts, jeśli jej nie ma
    public AccountManager(Connection connection)
    {
        this.connection = connection;
        initializeTable(); // Tworzy tabelę accounts
    }


    // Tworzy tabelę accounts, jeśli jeszcze nie istnieje
    private void initializeTable()
    {
        try (PreparedStatement stmt = this.connection.prepareStatement("""
                        CREATE TABLE IF NOT EXISTS accounts (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL
                        );
                """))
        {
            stmt.execute(); // Wykonanie zapytania SQL
        }
        catch (SQLException e)
        {
            System.err.println("accounts table creation error: " + e.getMessage());
        }
    }



    // Sprawdza, czy podana nazwa użytkownika i hasło są poprawne
    public boolean authenticate(String username, String password)
    {
        try (PreparedStatement stmt = this.connection.prepareStatement(
                "SELECT password FROM accounts WHERE username = ?;"))
        {
            stmt.setString(1, username); // Podstawienie nazwy użytkownika
            ResultSet rs = stmt.executeQuery(); // Wykonanie zapytania


            if (rs.next())
            {
                String hashed = rs.getString("password"); // Pobranie zahaszowanego hasła z bazy
                return BCrypt.checkpw(password, hashed); // Sprawdzenie, czy hasło pasuje
            }
        }
        catch (SQLException e)
        {
            System.err.println("Auth error: " + e.getMessage());
        }
        return false; // Jeśli nie znaleziono użytkownika lub coś poszło nie tak
    }



    // Pobiera dane konta użytkownika na podstawie ID lub nazwy użytkownika
    public Account getAccount(String usernameOrId)
    {
        try
        {
            PreparedStatement stmt;
            boolean isNumeric = usernameOrId.matches("\\d+"); // Sprawdzenie, czy przekazany argument to liczba (ID)

            if (isNumeric)
            {
                // Wyszukiwanie po ID
                stmt = this.connection.prepareStatement("SELECT id, username FROM accounts WHERE id = ?;");
                stmt.setInt(1, Integer.parseInt(usernameOrId));
            } else
            {
                // Wyszukiwanie po nazwie użytkownika
                stmt = this.connection.prepareStatement("SELECT id, username FROM accounts WHERE username = ?;");
                stmt.setString(1, usernameOrId);
            }

            ResultSet rs = stmt.executeQuery(); // Wykonanie zapytania

            if (rs.next())
            {
                // Jeśli znaleziono konto, tworzymy i zwracamy obiekt Account
                return new Account(rs.getInt("id"), rs.getString("username"));
            }
        }
        catch (SQLException e)
        {
            System.err.println("Account retrival error: " + e.getMessage());
        }
        return null; // Jeśli nie znaleziono użytkownika
    }

}