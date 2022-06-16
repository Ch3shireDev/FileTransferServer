package models;

public class Ticket {
    Integer number;
    private String filename;
    private Integer filesize;
    private String url;

    public Ticket() {
    }

    public Ticket(Integer number, String filename, Integer filesize, String url) {
        this.number = number;
        this.filename = filename;
        this.filesize = filesize;
        this.url = url;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getFilename() {
        return this.filename;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
