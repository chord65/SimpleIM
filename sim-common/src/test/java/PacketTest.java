import org.chord.sim.common.protocol.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.response.RegisterResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.junit.Test;

/**
 * @author chord
 * date 2022/1/11 16:11
 * function:
 */
public class PacketTest {

    @Test
    public void RegisterPacketTest() throws InterruptedException {
        RegisterRequestPacket registerRequestPacket = new RegisterRequestPacket();
        //registerRequestPacket.setUserName("mike");
        //System.out.println(registerRequestPacket.getUserName());

        Thread.sleep(1000);
        RegisterResponsePacket registerResponsePacket = new RegisterResponsePacket();
        registerResponsePacket.setStatus(Status.OK);
        registerResponsePacket.setMsg("注册成功");
        registerResponsePacket.setRequestSeqNumber(registerRequestPacket.getSeqNumber());
        System.out.println(registerResponsePacket.getMsg());
        System.out.println(registerResponsePacket.getRequestSeqNumber());
        System.out.println(registerResponsePacket.getSeqNumber());
    }

}
