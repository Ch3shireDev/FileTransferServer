import communication.FileSenderService;
import communication.HttpServerService;
import mockups.MockFileinfoService;
import mockups.MockSocketService;
import mockups.MockTicketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileSenderServiceTest {

    MockSocketService socketService;
    MockTicketService ticketService;
    private MockFileinfoService fileinfoService;

    @BeforeEach
    public void SetUp() {
        socketService = new MockSocketService();
        ticketService = new MockTicketService();
        fileinfoService = new MockFileinfoService("a.png", new byte[]{1, 2, 3});
    }

    @Test
    public void SendFileToServerTest() throws Exception {
        FileSenderService fileSenderService = new FileSenderService(socketService, fileinfoService);
        HttpServerService serverService = new HttpServerService(socketService, ticketService);
        socketService.setServer(serverService);
        fileSenderService.sendFile("a.png");
        Assertions.assertEquals(1, ticketService.tickets.size());
    }


}