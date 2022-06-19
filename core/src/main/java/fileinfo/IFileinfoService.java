package fileinfo;

import java.io.IOException;

public interface IFileinfoService {

    public Fileinfo getFileinfo(String fileToSend) throws IOException;

    public byte[] getFilebytes(String filename) throws IOException;
}
