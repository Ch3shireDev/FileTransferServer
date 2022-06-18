package tickets;

import models.Fileinfo;
import models.Ticket;

public interface ITicketService {
    public Ticket createTicketResponse(Fileinfo fileinfo);
    public byte[] getDataFromTicket(String url);
    public void sendDataToTicket(String url, byte[] bytes);
    Ticket getTicket(String url);
}
