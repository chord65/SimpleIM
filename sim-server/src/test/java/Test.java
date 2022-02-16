import org.chord.sim.common.util.SIMUtils;
import org.chord.sim.server.SIMServerApplication;
import org.chord.sim.server.entity.Message;
import org.chord.sim.server.service.MessageService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SIMServerApplication.class)
public class Test {

    @Autowired
    private MessageService messageService;

    @org.junit.Test
    public void testAddMessageService() {
        Message msg = new Message();
        msg.setFromId("100001");
        msg.setToId("100001");
        msg.setMsgId(SIMUtils.generateUUID());
        msg.setSeqNumber("2132131312");
        msg.setMsgContent("hello");

        messageService.addMessage(msg);
    }

    @org.junit.Test
    public void testSelectMessageService() {
        List<Message> list = messageService.findMessageByToId("100001");
        for(Message msg : list) {
            System.out.println(msg);
        }
    }

    @org.junit.Test
    public void testUpdateStatus() {
        messageService.updateStatus("113cdb2d472841739d933544c739a88a", 1);
    }

}
