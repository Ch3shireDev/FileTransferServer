package fileinfo;

import java.io.IOException;

public interface IFileinfoService {
    Fileinfo getFileinfo(String fileToSend) throws IOException;

    byte[] getFilebytes(String filename) throws IOException;

    void writeFile(String filename, byte[] filebytes) throws IOException;
}
