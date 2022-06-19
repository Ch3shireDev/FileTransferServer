package tickets;

import fileinfo.Fileinfo;

public interface ITicketService {
    public Ticket createTicketResponse(Fileinfo fileinfo);
    public byte[] getDataFromTicket(String url);
    public void sendDataToTicket(String url, byte[] bytes);
    Ticket getTicket(String url);
}
