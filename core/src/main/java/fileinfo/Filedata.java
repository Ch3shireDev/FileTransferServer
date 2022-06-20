package fileinfo;

public class Filedata {
    private final String filename;
    private final byte[] filebytes;

    public Filedata(String filename, byte[] filebytes) {
        this.filename = filename;
        this.filebytes = filebytes;
    }

    public byte[] getFilebytes() {
        return filebytes;
    }

    public String getFilename() {
        return filename;
    }
}
