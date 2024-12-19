import java.util.*;

public class Manager {

    private Map<String, Note> notes;



    public boolean saveNote(String heading, String content, List<String> tags) {
        if (notes.containsKey(heading)) {
            return false;
        }
        Note newNote = new Note(heading, content, tags);
        notes.put(heading, newNote);
        return true;
    }

    public Collection<Note> getAllNotes() {
        return notes.values();
    }

    public Note getNoteByHeading(String heading) {
        Note note = notes.get(heading);
        if (note != null) {
            System.out.println(heading + "': " + note.getTags());
        } else {
            System.out.println(heading);
        }
        return note;
    }

    public void displayNotes() {
        if (notes.isEmpty()) {
            System.out.println("Finns inga anteckningar");
        } else {
            notes.values().forEach(note -> {
                System.out.println(note);
            });
        }
    }


    public boolean deleteNoteByHeading(String heading) {
        if (notes.containsKey(heading)) {
            notes.remove(heading);
            return true;
        }
        return false;
    }

    public Manager() {
        this.notes = new HashMap<>();
        System.out.println("Initsierar och är tom");
    }

    public boolean noteExists(String heading) {
        return notes.containsKey(heading);
    }
}
