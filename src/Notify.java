import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class Notify extends Application {

    private TextField headingField;
    private TextArea noteArea;
    private TextField newTagField;
    private ListView<String> noteListView;
    private ListView<String> tagsList;
    private Note selectedNote;

    Manager manager = new Manager();

    public static void Notify(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Notify");

        VBox leftPane = createLeftPane();
        VBox centerPane = createCenterPane();
        VBox rightPane = createRightPane();

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(leftPane);
        mainLayout.setCenter(centerPane);
        mainLayout.setRight(rightPane);

        Scene scene = new Scene(mainLayout, 1000, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftPane() {
        TextField searchField = new TextField();
        Label searchLabel = new Label("Sök");
        searchField.setStyle("-fx-prompt-text-fill: gray;");
        searchField.setPromptText("börja skriva för att söka");

        noteListView = new ListView<>();
        noteListView.setPrefWidth(150);
        noteListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displaySelectedNote(newValue);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterNotes(newValue.trim());
        });

        Button newNoteButton = new Button("Ny Anteckning");
        newNoteButton.setStyle("-fx-background-color: #006400; -fx-text-fill: white;");
        newNoteButton.setOnAction(e -> clearFieldsForNewNote());

        VBox leftPane = new VBox(10,searchLabel, searchField, noteListView, newNoteButton);
        leftPane.setPadding(new Insets(10));
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.setStyle("-fx-background-color: #4682B4");
        return leftPane;
    }

    private VBox createCenterPane() {
        Label headingLabel = new Label("Rubrik");
        headingField = new TextField();
        headingField.setPromptText("Skriv en rubrik");

        Label noteLabel = new Label("Innehåll");
        noteArea = new TextArea();
        noteArea.setPromptText("Skriv text");
        noteArea.setWrapText(true);


        Button deleteButton = new Button("Ta bort");
        deleteButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteNote());

        Button updateButton = new Button("Uppdatera");
        updateButton.setStyle("-fx-background-color: #ffcc00;");
        updateButton.setOnAction(e -> saveOrUpdateNote());

        HBox buttonBox = new HBox(10, deleteButton, updateButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox centerPane = new VBox(10,headingLabel, headingField,noteLabel, noteArea, buttonBox);
        centerPane.setPadding(new Insets(10));
        centerPane.setAlignment(Pos.TOP_CENTER);
        centerPane.setStyle("-fx-background-color: #4682B4;");
        centerPane.setPrefWidth(500);
        return centerPane;
    }

    private VBox createRightPane() {
        tagsList = new ListView<String>();
        Label tagsLabel = new Label("Taggar");
        tagsList.setPrefHeight(150);

        newTagField = new TextField();
        newTagField.setPromptText("separera taggar med komma");

        Button addTagButton = new Button("Ny tagg");
        addTagButton.setStyle("-fx-background-color: #006400; -fx-text-fill: white;");
        addTagButton.setOnAction(e -> addTagToNote());

        noteListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Note note = manager.getNoteByHeading(newValue);
                displaySelectedNoteTags(note);
            }
        });

        Button removeTagButton = new Button("Ta bort tagg");
        removeTagButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white;");
        removeTagButton.setOnAction(e -> removeSelectedTag());

        VBox rightPane = new VBox(10,tagsLabel, tagsList, newTagField, addTagButton, removeTagButton);
        rightPane.setPadding(new Insets(10));
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setStyle("-fx-background-color: #4682B4;");
        rightPane.setPrefWidth(250);
        return rightPane;
    }

    private void clearFieldsForNewNote() {
        headingField.clear();
        noteArea.clear();
        tagsList.getItems().clear();
        newTagField.clear();
        noteListView.getSelectionModel().clearSelection();
        selectedNote = null;
    }

    private void saveOrUpdateNote() {
        String heading = headingField.getText().trim();
        String content = noteArea.getText().trim();
        List<String> tags = tagsList.getItems();

        if (heading.isEmpty() || content.isEmpty()) {
            System.out.println("Rubrik och anteckningsfält får ej vara tomma");
            return;
        }

        if (manager.noteExists(heading)) {
            Note note = manager.getNoteByHeading(heading);
            note.setNote(content);
            note.setTags(tags);
            System.out.println("Anteckning uppdaterad");
        } else {
            boolean saved = manager.saveNote(heading, content, tags);
            if (saved) {
                noteListView.getItems().add(heading);
                System.out.println("Anteckning sparad.");
            } else {
                System.out.println("Misslyckades med att spara anteckning");
            }
        }
    }
    private void displaySelectedNote(String heading) {
        Note selectedNote = manager.getNoteByHeading(heading);
        if (selectedNote != null) {
            headingField.setText(selectedNote.getHeading());
            noteArea.setText(selectedNote.getNote());
            tagsList.getItems().setAll(selectedNote.getTags());
            this.selectedNote = selectedNote;
        } else {
            headingField.clear();
            noteArea.clear();
            tagsList.getItems().clear();
            this.selectedNote = null;
        }
    }
    private void deleteNote() {
        String selectedHeading = noteListView.getSelectionModel().getSelectedItem();
        if (selectedHeading != null) {
            manager.deleteNoteByHeading(selectedHeading);
            noteListView.getItems().remove(selectedHeading);
            clearFieldsForNewNote();
            System.out.println("Anteckning borttagen.");
        } else {
            System.out.println("Ingen anteckning finns att ta bort");
        }
    }
    private void addTagToNote() {
        String newTag = newTagField.getText().trim();

        if (!newTag.isEmpty()) {
            if (!tagsList.getItems().contains(newTag)) {
                tagsList.getItems().add(newTag);

                if (selectedNote != null) {
                    if (!selectedNote.getTags().contains(newTag)) {
                        selectedNote.getTags().add(newTag);
                    }
                }

                newTagField.clear();
            } else {
                System.out.println("Tagg finns redan");
            }
        } else {
            System.out.println("Måste skriva giltig tagg");
        }
    }
    private void displaySelectedNoteTags(Note note) {
        if (note != null) {
            tagsList.getItems().setAll(note.getTags());
            this.selectedNote = note;
        } else {
            tagsList.getItems().clear();
            this.selectedNote = null;
        }
    }
    private void removeSelectedTag() {
        String selectedTag = tagsList.getSelectionModel().getSelectedItem();

        if (selectedTag != null) {
            tagsList.getItems().remove(selectedTag);

            if (selectedNote != null) {
                selectedNote.getTags().remove(selectedTag);
            }

        } else {
            System.out.println("Ingen tagg vald att ta bort");
        }
    }
    private void filterNotes(String query) {
        noteListView.getItems().clear();

        if (query.isEmpty()) {

            manager.getAllNotes().forEach(note -> noteListView.getItems().add(note.getHeading()));
        } else {

            manager.getAllNotes().stream()
                    .filter(note -> note.getHeading().toLowerCase().contains(query.toLowerCase()) ||
                            note.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))
                    .forEach(note -> noteListView.getItems().add(note.getHeading()));
        }
    }
}

