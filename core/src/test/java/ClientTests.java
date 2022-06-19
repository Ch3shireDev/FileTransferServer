import communication.FileReceiverService;
import communication.FileSenderService;
import communication.HttpServerService;
import fileinfo.Fileinfo;
import mockups.MockFileinfoService;
import mockups.MockSocketService;
import mockups.MockTicketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tickets.Ticket;

class ClientTests {

    MockSocketService socketService;
    MockTicketService ticketService;
    private MockFileinfoService fileinfoService;

    @BeforeEach
    public void SetUp() {
        socketService = new MockSocketService();
        ticketService = new MockTicketService(new byte[]{1,2,3});
        fileinfoService = new MockFileinfoService(new byte[]{1, 2, 3});
    }

    @Test
    public void sendFileTest() throws Exception {
        FileSenderService fileSenderService = new FileSenderService(socketService, fileinfoService);
        HttpServerService serverService = new HttpServerService(socketService, ticketService);
        socketService.setServer(serverService);
        fileSenderService.sendFile("a.png");
        Assertions.assertEquals(1, ticketService.tickets.size());
    }

    @Test
    public void fileReceiveTest() throws Exception {
        ticketService.tickets.add(new Ticket("/tickets/1", new Fileinfo("a.png", 3)));
        FileReceiverService fileReceiverService = new FileReceiverService(socketService, fileinfoService);
        HttpServerService serverService = new HttpServerService(socketService, ticketService);
        socketService.setServer(serverService);
        var result = fileReceiverService.receiveFile("/tickets/1");
        Assertions.assertTrue(result);
        Assertions.assertEquals(1, fileinfoService.files.keySet().size());
        String filename = fileinfoService.files.keySet().stream().findFirst().get();
        byte[] bytes = fileinfoService.files.get(filename);
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, bytes);
    }

}