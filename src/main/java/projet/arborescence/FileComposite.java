package projet.arborescence;

import java.io.File;
import java.util.List;

public abstract class FileComposite extends File {
    public FileComposite(File file) {
        super(file.getPath());
    }
    public abstract List<FileComposite> getChildren();

    public String toString() {
        return getName();
    }
}
