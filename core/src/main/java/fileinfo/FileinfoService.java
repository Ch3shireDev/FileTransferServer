package fileinfo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

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

    @Override
    public void writeFile(String filename, byte[] filebytes) throws IOException {
        Path filepath = getUnoccupiedPath(filename);
        System.out.printf("Zapis danych do pliku: %d B\n", filebytes.length);
        Files.write(filepath, filebytes);
        System.out.printf("Plik zapisano pod ścieżką %s\n", filepath.getFileName());
    }

    private Path getUnoccupiedPath(String filename) {
        Path filepath = Paths.get(filename);

        int i = 1;
        while (Files.exists(filepath)) {
            String[] parts = filename.split("\\.");
            String withoutExtension = String.join(".", Arrays.stream(parts).limit(parts.length - 1).toArray(String[]::new));
            String extension = parts[parts.length - 1];
            filepath = Paths.get(String.format("%s-%d.%s", withoutExtension, i++, extension));
        }
        return filepath;
    }

}
