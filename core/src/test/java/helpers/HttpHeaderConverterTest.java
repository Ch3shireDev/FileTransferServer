package helpers;

import models.HttpRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpHeaderConverterTest {

    @Test
    void toHeaderLines() {

        String[] expectedLines = new String[]{
                "POST /tickets HTTP/1.1\r\n",
                "Content-Length: 38\r\n",
                "Content-Type: application/json\r\n",
                "\r\n"
        };

        var request = new HttpRequest("POST", "/tickets", "{\"filename\":\"a.png\",\"filesize\":213123}");

        var actualLines = HttpHeaderConverter.toRequestHeaderLines(request.getHeader()).toArray(String[]::new);
        Assertions.assertArrayEquals(expectedLines, actualLines);

    }
}