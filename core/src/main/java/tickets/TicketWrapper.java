package tickets;

public class TicketWrapper {
    private final Ticket ticket;
    public byte[] bytes;

    public TicketWrapper(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
