import org.chord.sim.common.protocol.Packet;
import org.chord.sim.common.protocol.PacketCodec;
import org.chord.sim.common.protocol.request.RegisterRequestPacket;
import org.junit.Test;

/**
 * @author chord
 * date 2022/1/11 15:20
 * function:
 */
public class LombokTest {

    @Test
    public void testPacket() {

        RegisterRequestPacket registerRequestPacket = new RegisterRequestPacket();
        System.out.println(registerRequestPacket.getSeqNumber());

    }

}
