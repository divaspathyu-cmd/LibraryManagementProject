package Library_ManagementProject;

import java.sql.*;
import java.util.*;


class Item_Divaspathy {
    int id;
    String title;
    String genre;
}


class Book_Divaspathy extends Item_Divaspathy {
    String author;
    String publisher;
    int year;

    public Book_Divaspathy(int id, String title, String author, String genre,
                           String publisher, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.year = year;
    }
}


class DBConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/Library_Divaspathy"; 
        String user = "root";
        String password = "linux";
        return DriverManager.getConnection(url, user, password);
    }
}


public class Divaspathy_LibraryAssessment {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n====== Library Menu ======");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book by ID");
            System.out.println("4. Count Books by Genre");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                sc.nextLine();
                continue;
            }

            switch (choice) {
                case 1 -> addBook(sc);
                case 2 -> viewBooks();
                case 3 -> searchBook(sc);
                case 4 -> countByGenre();
                case 5 -> {
                    System.out.println("Exiting... Goodbye!");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again!");
            }
        }
    }


    static void addBook(Scanner sc) {
        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter Title: ");
            String title = sc.nextLine();

            System.out.print("Enter Author: ");
            String author = sc.nextLine();

            System.out.print("Enter Genre: ");
            String genre = sc.nextLine();

            System.out.print("Enter Publisher: ");
            String publisher = sc.nextLine();

            int year;
            try {
                System.out.print("Enter Year: ");
                year = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid year! Please enter a number.");
                sc.nextLine();
                return;
            }


            String sql = "INSERT INTO books_Divaspathy (title, author, genre, publisher, year) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, title);
                pst.setString(2, author);
                pst.setString(3, genre);
                pst.setString(4, publisher);
                pst.setInt(5, year);

                pst.executeUpdate();
                System.out.println("✅ Book added successfully!");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: Duplicate entry detected.");
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }


    static void viewBooks() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM books_Divaspathy");
             ResultSet rs = pst.executeQuery()) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getInt("id")
                        + " | Title: " + rs.getString("title")
                        + " | Author: " + rs.getString("author")
                        + " | Genre: " + rs.getString("genre")
                        + " | Publisher: " + rs.getString("publisher")
                        + " | Year: " + rs.getInt("year"));
            }
            if (!found) {
                System.out.println("No books available.");
            }
        } catch (Exception e) {
            System.out.println("Error viewing books: " + e.getMessage());
        }
    }


    static void searchBook(Scanner sc) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.print("Enter Book ID: ");
            int id;
            try {
                id = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid ID! Please enter a number.");
                sc.nextLine();
                return;
            }

            String sql = "SELECT * FROM books_Divaspathy WHERE id=?";
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("ID: " + rs.getInt("id")
                                + " | Title: " + rs.getString("title")
                                + " | Author: " + rs.getString("author")
                                + " | Genre: " + rs.getString("genre")
                                + " | Publisher: " + rs.getString("publisher")
                                + " | Year: " + rs.getInt("year"));
                    } else {
                        System.out.println("Book not found!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error searching book: " + e.getMessage());
        }
    }


    static void countByGenre() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT genre, COUNT(*) as count FROM books_Divaspathy GROUP BY genre");
             ResultSet rs = pst.executeQuery()) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Genre: " + rs.getString("genre") + " | Count: " + rs.getInt("count"));
            }
            if (!found) {
                System.out.println("No genres found.");
            }
        } catch (Exception e) {
            System.out.println("Error counting books: " + e.getMessage());
        }
    }
}
