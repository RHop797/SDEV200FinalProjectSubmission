import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LibraryApp extends Application {
    private Library library = new Library();  
    private ObservableList<String> itemList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");

        // Create UI components
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");

        Button addButton = new Button("Add Book");
        Button removeButton = new Button("Remove Book");
        Button viewButton = new Button("View Library"); 
        ListView<String> itemListView = new ListView<>(itemList);

        // Event handlers
        addButton.setOnAction(e -> addBook(titleField, authorField, isbnField));
        removeButton.setOnAction(e -> removeBook(titleField));
        viewButton.setOnAction(e -> viewLibraryFile()); 

        // Layout setup
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
            titleField,
            authorField,
            isbnField,
            addButton,
            removeButton,
            viewButton, 
            itemListView
        );

        // Load books from file
        loadBooksFromFile();

        // Scene setup
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to add a book
    private void addBook(TextField titleField, TextField authorField, TextField isbnField) {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();

        if (!title.isEmpty() && !author.isEmpty() && !isbn.isEmpty()) {
            // Adding a Book
            Book book = new Book(title, author, isbn);
            library.addItem(book);
            updateItemList();

            // Save to text file
            saveBooksToFile();

            // Clear input fields
            titleField.clear();
            authorField.clear();
            isbnField.clear();
        } else {
            showAlert("Error", "Title, Author, and ISBN fields must be filled in.");
        }
    }

    // Method to remove a book
    private void removeBook(TextField titleField) {
        String title = titleField.getText();
        if (!title.isEmpty()) {
            LibraryItem toRemove = null;
            for (LibraryItem item : library.getItems()) {
                if (item.getTitle().equalsIgnoreCase(title)) {
                    toRemove = item;
                    break;
                }
            }

            if (toRemove != null) {
                library.removeItem(toRemove);
                updateItemList();

                // Save to text file
                saveBooksToFile();
            } else {
                showAlert("Error", "Book with the given title not found.");
            }

            titleField.clear();
        } else {
            showAlert("Error", "Please provide a title to remove.");
        }
    }

    // Method to update the ListView
    private void updateItemList() {
        itemList.clear();
        for (LibraryItem item : library.getItems()) {
            itemList.add(item.toString());
        }
    }

    // Method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to save books to a text file (CSV format)
    private void saveBooksToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("LibraryBooks.txt"))) {
            for (LibraryItem item : library.getItems()) {
                if (item instanceof Book) {
                    Book book = (Book) item;
                    writer.write(book.getTitle() + "," + book.getAuthor() + "," + book.getIsbn());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to save books to file.");
            e.printStackTrace();
        }
    }

    // Method to load books from a text file (CSV format)
    private void loadBooksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("LibraryBooks.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookData = line.split(",");
                if (bookData.length == 3) {
                    String title = bookData[0];
                    String author = bookData[1];
                    String isbn = bookData[2];
                    Book book = new Book(title, author, isbn);
                    library.addItem(book);
                }
            }
            updateItemList(); // Refresh the UI list view
        } catch (IOException e) {
            showAlert("Error", "Failed to load books from file.");
            e.printStackTrace();
        }
    }

    // Method to view the content of the LibraryBooks.txt file
    private void viewLibraryFile() {
        Stage fileStage = new Stage();
        fileStage.setTitle("LibraryBooks.txt");

        TextArea textArea = new TextArea();
        textArea.setEditable(false); // Make the text area read-only

        try (BufferedReader reader = new BufferedReader(new FileReader("LibraryBooks.txt"))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            textArea.setText(content.toString());
        } catch (IOException e) {
            showAlert("Error", "Failed to read the library file.");
            e.printStackTrace();
        }

        // Set up the layout for the new window
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().add(textArea);

        // Set up the scene and show the new window
        Scene scene = new Scene(layout, 400, 300);
        fileStage.setScene(scene);
        fileStage.show();
    }

}
