package org.chord.sim.rpc.protocol.codec;

import io.netty.buffer.ByteBuf;
import org.chord.sim.common.protocol.chat.request.P2pChatRequestPacket;
import org.chord.sim.rpc.protocol.Packet;
import org.chord.sim.rpc.protocol.RequestPacket;
import org.chord.sim.rpc.protocol.ResponsePacket;
import org.chord.sim.rpc.protocol.constant.PacketType;
import org.chord.sim.rpc.protocol.serializer.Serializer;
import org.chord.sim.rpc.protocol.serializer.impl.JSONSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chord
 * date 2022/2/19 20:40
 * function:
 */
public class RpcPacketCodec implements PacketCodec {

    public static final int MAGIC_NUMBER = 0x00001111;

    public static final RpcPacketCodec INSTANCE = new RpcPacketCodec();

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializerMap;

    private RpcPacketCodec() {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(PacketType.REQUEST, RequestPacket.class);
        packetTypeMap.put(PacketType.RESPONSE, ResponsePacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializeAlgorithm(), serializer);
    }

    @Override
    public void encode(ByteBuf byteBuf, Packet packet) {
        // 1.序列化packet对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);
        // 2.编码
        // magic number
        byteBuf.writeInt(MAGIC_NUMBER);
        // 序列化算法
        byteBuf.writeByte(Serializer.DEFAULT.getSerializeAlgorithm());
        // 包类型，例如request、response
        byteBuf.writeByte(packet.getPacketType());
        // 数据长度
        byteBuf.writeInt(bytes.length);
        // 数据包
        byteBuf.writeBytes(bytes);
    }

    @Override
    public Packet decode(ByteBuf byteBuf) {
        // 跳过 magic number
        byteBuf.skipBytes(4);

        // 序列化算法
        byte serializeAlgorithm = byteBuf.readByte();

        // 消息类型
        byte packetTypeCode = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();

        // 数据包
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Serializer serializer = getSerializer(serializeAlgorithm);

        Class<? extends Packet> packetType = getRequestType(packetTypeCode);

        if (packetType != null && serializer != null) {
            return serializer.deserialize(packetType, bytes);
        }

        return null;
    }

    private Class<? extends Packet> getRequestType(byte packetType) {
        return packetTypeMap.get(packetType);
    }

    private Serializer getSerializer(byte serializeAlgorithm) {
        return serializerMap.get(serializeAlgorithm);
    }

    public static int getMagicNumber(ByteBuf byteBuf) {
        return byteBuf.getInt(0);
    }

    public static byte getSerializeAlgorithm(ByteBuf byteBuf) {
        return byteBuf.getByte(5);
    }

    public static int getDataLength(ByteBuf byteBuf) {
        return byteBuf.getInt(16);
    }

}
