package application.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubjectTest {
    private Subject subject;
    private int id = 1;
    private String name = "someName";
    private String thumbnailPath = "somePath";

    @Test
    public void testFieldsConstructor(){
        subject = new Subject(id, name, thumbnailPath);
        assertEquals(subject.getId(), id);
        assertEquals(subject.getName(), name);
        assertEquals(subject.getThumbnailPath(), thumbnailPath);
    }

}
