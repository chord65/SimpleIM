import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.chord.sim.common.protocol.Packet;
import org.chord.sim.common.protocol.PacketCodec;
import org.chord.sim.common.protocol.request.RegisterRequestPacket;
import org.chord.sim.common.protocol.response.RegisterResponsePacket;
import org.chord.sim.common.protocol.response.status.Status;
import org.chord.sim.common.serializer.Serializer;
import org.chord.sim.common.serializer.impl.JSONSerializer;
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

        PacketCodec packetCodec = PacketCodec.INSTANCE;
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();

        packetCodec.encode(byteBuf, registerResponsePacket);
        RegisterResponsePacket decodedPacket = (RegisterResponsePacket) packetCodec.decode(byteBuf);

        if(decodedPacket == null) System.out.println("here");

        System.out.println(decodedPacket.getMsg());
        System.out.println(decodedPacket.getRequestSeqNumber());
        System.out.println(decodedPacket.getSeqNumber());
        System.out.println(decodedPacket.getStatus());

        Serializer serializer = new JSONSerializer();
        Assert.assertArrayEquals(serializer.serialize(registerResponsePacket), serializer.serialize(decodedPacket));


    }

}
