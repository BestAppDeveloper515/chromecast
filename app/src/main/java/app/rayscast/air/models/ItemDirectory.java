package app.rayscast.air.models;

import java.io.File;

public class ItemDirectory {
    private String directory;
    private File directoryFile;

    public ItemDirectory() {
    }

    public ItemDirectory(File directoryFile, String directory) {
        this.directoryFile = directoryFile;
        this.directory = directory;
    }

    public ItemDirectory(File directory) {
        this.directoryFile = directory;

    }

    public File getDirectoryFile() {
        return directoryFile;
    }

    public void setDirectoryFile(File directoryFile) {
        this.directoryFile = directoryFile;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }


}
