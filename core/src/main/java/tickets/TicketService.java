package tickets;

import fileinfo.Fileinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TicketService implements ITicketService {

    Map<String, TicketWrapper> tickets = new HashMap<>();

    Integer count = 0;

    public Ticket createTicketResponse(Fileinfo fileinfo) {
        int ticketCount = getNewTicketNumber();
        String url = String.format("/tickets/%d", ticketCount);
        String filename = fileinfo.getFilename();
        long filesize = fileinfo.getFilesize();
        var ticket = new Ticket(url, filename, filesize);
        tickets.put(url, new TicketWrapper(ticket));
        return ticket;
    }

    @Override
    public byte[] getDataFromTicket(String url) {
        TicketWrapper ticketWrapper = tickets.get(url);
        var bytes = ticketWrapper.bytes;
        if (bytes == null) return new byte[0];
        //ticketWrapper.bytes = new byte[0];
        return bytes;
    }

    @Override
    public void sendDataToTicket(String url, byte[] bytes) {
        TicketWrapper ticketWrapper = tickets.get(url);
        ticketWrapper.bytes = bytes;
    }

    @Override
    public Ticket getTicket(String url) throws NoSuchElementException {
        if (!this.tickets.containsKey(url)) return null;
        return this.tickets.get(url).getTicket();
    }

    private Integer getNewTicketNumber() {
        count++;
        return count;
    }

}

