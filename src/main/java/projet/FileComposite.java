package projet;

import java.io.File;

public abstract class FileComposite extends File {
    public FileComposite(File file) {
        super(file.getPath());
    }
    public abstract void display(String prefix);
}
