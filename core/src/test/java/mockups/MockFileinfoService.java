package mockups;

import fileinfo.IFileinfoService;
import fileinfo.Fileinfo;

import java.io.IOException;

public class MockFileinfoService implements IFileinfoService {

    private final String filename;
    private final byte[] filebytes;

    public MockFileinfoService(String filename, byte[] filebytes) {
        this.filename = filename;
        this.filebytes = filebytes;
    }

    @Override

    public Fileinfo getFileinfo(String filename) throws IOException {
        return new Fileinfo(filename, filebytes.length);
    }

    @Override
    public byte[] getFilebytes(String filename) {
        return filebytes;
    }
}
