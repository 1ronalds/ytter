package api.ytter.backend.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileObject {
    byte[] file;
    String filename;
    String mimeType;
}
