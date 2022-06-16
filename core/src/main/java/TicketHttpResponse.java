public class TicketHttpResponse {
    private Ticket ticket;
    private long time;

    public TicketHttpResponse(Ticket ticket, long time) {
        this.ticket = ticket;
        this.time = time;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
