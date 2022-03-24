import org.chord.sim.common.protocol.chat.request.RegisterRequestPacket;
import org.junit.Test;

import java.util.Random;

/**
 * @author chord
 * date 2022/1/11 15:20
 * function:
 */
public class LombokTest {

    @Test
    public void testPacket() {

        // RegisterRequestPacket registerRequestPacket = new RegisterRequestPacket();
        // System.out.println(registerRequestPacket.getSeqNumber());

        Integer i = null;
        i = getInt();
        System.out.println(i);
    }

    int getInt() {
        return 2;
    }

}
