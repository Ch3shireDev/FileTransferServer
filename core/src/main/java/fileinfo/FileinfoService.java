package fileinfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileinfoService implements IFileinfoService {


    public Fileinfo getFileinfo(String fileToSend) throws IOException {
        Path path = Paths.get(fileToSend);
        String filename = path.getFileName().toFile().getName();
        long filesize = Files.size(path);
        return new Fileinfo(filename, filesize);
    }

    @Override
    public byte[] getFilebytes(String filename) throws IOException {
        Path path = Paths.get(filename);
        return Files.readAllBytes(path);
    }

}
