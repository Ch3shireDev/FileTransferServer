package models;

public class Ticket {
    private String filename;
    private long filesize;
    private String url;

    public Ticket() {
    }

    public Ticket(String url, String filename, long filesize) {
        this.filename = filename;
        this.filesize = filesize;
        this.url = url;
    }

    public Ticket(String url, Fileinfo fileinfo) {
        this.filename = fileinfo.getFilename();
        this.filesize = fileinfo.getFilesize();
        this.url = url;
    }

    public Ticket(String url) {
        this.url = url;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFilesize() {
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
