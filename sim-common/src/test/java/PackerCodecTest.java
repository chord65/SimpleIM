import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.chord.sim.common.protocol.chat.codec.ChatPacketCodec;
import org.chord.sim.common.protocol.chat.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.chat.response.RegisterResponsePacket;
import org.chord.sim.common.protocol.chat.response.status.Status;
import org.chord.sim.common.serializer.Serializer;
import org.chord.sim.common.serializer.impl.JSONSerializer;
import org.chord.sim.common.util.SIMUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author chord
 * date 2022/1/11 16:22
 * function:
 */
public class PackerCodecTest {

    @Test
    public void packetCodecTest() throws InterruptedException {
        RegisterRequestPacket registerRequestPacket = new RegisterRequestPacket();
        //registerRequestPacket.setUserName("mike");
        //System.out.println(registerRequestPacket.getUserName());

        Thread.sleep(1000);
        RegisterResponsePacket registerResponsePacket = new RegisterResponsePacket();
        registerResponsePacket.setStatus(Status.OK);
        registerResponsePacket.setMsg("注册成功");
        registerResponsePacket.setRequestSeqNumber(registerRequestPacket.getSeqNumber());
        registerResponsePacket.setUserId("123");
        System.out.println(registerResponsePacket.getMsg());
        System.out.println(registerResponsePacket.getRequestSeqNumber());
        System.out.println(registerResponsePacket.getSeqNumber());

        ChatPacketCodec chatPacketCodec = ChatPacketCodec.INSTANCE;
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();

        chatPacketCodec.encode(byteBuf, registerResponsePacket);
        RegisterResponsePacket decodedPacket = (RegisterResponsePacket) chatPacketCodec.decode(byteBuf);

        if(decodedPacket == null) System.out.println("here");

        System.out.println(decodedPacket.getMsg());
        System.out.println(decodedPacket.getRequestSeqNumber());
        System.out.println(decodedPacket.getSeqNumber());
        System.out.println(decodedPacket.getStatus());

        Serializer serializer = new JSONSerializer();
        Assert.assertArrayEquals(serializer.serialize(registerResponsePacket), serializer.serialize(decodedPacket));

    }

}
