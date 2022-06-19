package helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Fileinfo;
import models.Ticket;

import java.io.IOException;

public class JsonConverterHelpers {
    static ObjectMapper mapper = new ObjectMapper();

    public static String getJson(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    public static String getTicketAsJson(Ticket ticket) throws JsonProcessingException {
        return mapper.writeValueAsString(ticket);
    }

    public static Ticket getJsonAsTicket(String ticket) throws JsonProcessingException {

        return mapper.readValue(ticket, Ticket.class);
    }

    public static Ticket getJsonAsTicket(byte[] bytes) throws JsonProcessingException {
        String ticketJson = new String(bytes);
        return getJsonAsTicket(ticketJson);
    }

    public static Fileinfo getFileinfoFromJson(byte[] buffer) throws IOException {
        return mapper.readValue(buffer, Fileinfo.class);
    }

    public <T> T getObject(String json, Class<T> valueType) throws JsonProcessingException {
        return mapper.readValue(json, valueType);
    }
}
