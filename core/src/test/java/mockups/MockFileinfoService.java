package mockups;

import fileinfo.Filedata;
import fileinfo.Fileinfo;
import fileinfo.IFileinfoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MockFileinfoService implements IFileinfoService {

    private final byte[] filebytes;
    public Map<String, byte[]> files = new HashMap<>();

    public MockFileinfoService(byte[] filebytes) {
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

    @Override
    public void writeFile(Filedata filedata){
        files.put(filedata.getFilename(), filedata.getFilebytes());
    }

}
