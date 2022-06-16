package helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Fileinfo;
import models.Ticket;

import java.io.IOException;

public class JsonConverterHelpers {
    static ObjectMapper mapper = new ObjectMapper();

    public static String getTicketAsJson(Ticket ticket) throws JsonProcessingException {
        return mapper.writeValueAsString(ticket);
    }

    public static Ticket getJsonAsTicket(String ticket) throws JsonProcessingException {

        return mapper.readValue(ticket, Ticket.class);
    }

    public static Fileinfo getFileinfoFromJson(byte[] buffer) throws IOException {
        return mapper.readValue(buffer, Fileinfo.class);
    }
}
