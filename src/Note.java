import java.util.ArrayList;
import java.util.List;

public class Note {
    private String heading;
    private String note;
    private List<String> tags;

    public Note(String heading, String content, List<String> tags) {
        this.heading = heading;
        this.note = content;
        this.tags = new ArrayList<>(tags); // Copy on construction
    }

    public String getHeading() {
        return heading;
    }

    public String getNote() {
        return note;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags); // Return a copy to prevent external modification
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags); // Copy to avoid external references
    }

    @Override
    public String toString() {
        return "Heading: " + heading + "\nContent: " + note + "\nTags: " + String.join(", ", tags);
    }
}