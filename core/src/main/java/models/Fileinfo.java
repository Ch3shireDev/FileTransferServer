package models;

public class Fileinfo {
    private String filename;
    private Integer filesize;

    public Fileinfo() {
    }

    public Fileinfo(String filename, Integer filesize) {
        this.filename = filename;
        this.filesize = filesize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
    }
}
