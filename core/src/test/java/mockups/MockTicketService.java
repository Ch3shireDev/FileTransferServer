package mockups;

import fileinfo.Fileinfo;
import tickets.Ticket;
import tickets.ITicketService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MockTicketService implements ITicketService {

    public List<Ticket> tickets = new ArrayList<>();
    Integer counter = 0;
    private byte[] bytes;

    public MockTicketService() {
    }

    public MockTicketService(Collection<Ticket> tickets) {
        this.tickets.addAll(tickets);
    }

    @Override
    public Ticket createTicketResponse(Fileinfo fileinfo) {
        Integer number = counter++;
        String url = String.format("/tickets/%d", number);
        Ticket ticket = new Ticket(url, fileinfo.getFilename(), fileinfo.getFilesize());
        tickets.add(ticket);
        return ticket;
    }

    @Override
    public byte[] getDataFromTicket(String url) {
        return bytes;
    }

    @Override
    public void sendDataToTicket(String url, byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public Ticket getTicket(String url) {
        return this.tickets.stream().filter(f -> f.getUrl().equals(url)).findFirst().get();
    }


}
