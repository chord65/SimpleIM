package org.chord.sim.common.protocol;

import io.netty.buffer.ByteBuf;
import org.chord.sim.common.protocol.constant.Command;
import org.chord.sim.common.protocol.constant.MsgType;
import org.chord.sim.common.protocol.notify.P2pChatNotifyPacket;
import org.chord.sim.common.protocol.request.*;
import org.chord.sim.common.protocol.response.*;
import org.chord.sim.common.serializer.Serializer;
import org.chord.sim.common.serializer.impl.JSONSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chord
 * date 2022/1/10 16:14
 * function:
 */
public class PacketCodec {

    public static final int MAGIC_NUMBER = 0x01010101;

    public static final PacketCodec INSTANCE = new PacketCodec();

    private final Map<Short, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializerMap;

    private PacketCodec() {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(getPacketTypeCode(Command.REGISTER, MsgType.REQUEST), RegisterRequestPacket.class);
        packetTypeMap.put(getPacketTypeCode(Command.REGISTER, MsgType.RESPONSE), RegisterResponsePacket.class);

        packetTypeMap.put(getPacketTypeCode(Command.LOGIN, MsgType.REQUEST), LogInRequestPacket.class);
        packetTypeMap.put(getPacketTypeCode(Command.LOGIN, MsgType.RESPONSE), LogInResponsePacket.class);

        packetTypeMap.put(getPacketTypeCode(Command.AUTHEN, MsgType.REQUEST), AuthenRequestPacket.class);
        packetTypeMap.put(getPacketTypeCode(Command.AUTHEN, MsgType.RESPONSE), AuthenResponsePacket.class);

        packetTypeMap.put(getPacketTypeCode(Command.P2P_CHAT, MsgType.REQUEST), P2pChatRequestPacket.class);
        packetTypeMap.put(getPacketTypeCode(Command.P2P_CHAT, MsgType.RESPONSE), P2pChatResponsePacket.class);
        packetTypeMap.put(getPacketTypeCode(Command.P2P_CHAT, MsgType.NOTIFY), P2pChatNotifyPacket.class);

        packetTypeMap.put(getPacketTypeCode(Command.PULL_MESSAGES, MsgType.REQUEST), PullMessagesRequestPacket.class);
        packetTypeMap.put(getPacketTypeCode(Command.PULL_MESSAGES, MsgType.RESPONSE), PullMessagesResponsePacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializeAlgorithm(), serializer);
    }

    public void encode(ByteBuf byteBuf, Packet packet) {
        // 1.序列化packet对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 2.编码
        // magic number
        byteBuf.writeInt(MAGIC_NUMBER);
        // 版本号
        byteBuf.writeByte(packet.getVersion());
        // 序列化算法
        byteBuf.writeByte(Serializer.DEFAULT.getSerializeAlgorithm());
        // 业务命令，例如心跳、单聊、群聊等
        byteBuf.writeByte(packet.getCommand());
        // 消息类型，例如request、response、notification
        byteBuf.writeByte(packet.getMsgType());
        // 序列号，使用时间戳
        byteBuf.writeLong(packet.getSeqNumber());
        // 数据长度
        byteBuf.writeInt(bytes.length);
        // 数据包
        byteBuf.writeBytes(bytes);
    }

    public Packet decode(ByteBuf byteBuf) {
        // 跳过 magic number
        byteBuf.skipBytes(4);

        // 跳过版本号
        byteBuf.skipBytes(1);

        // 序列化算法
        byte serializeAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 消息类型
        byte msgType = byteBuf.readByte();

        // 序列号
        long seqNumber = byteBuf.readLong();

        // 数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command, msgType);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }

        return null;
    }

    private Class<? extends Packet> getRequestType(byte commandType, byte msgType) {
        short packetTypeCode = getPacketTypeCode(commandType, msgType);
        return packetTypeMap.get(packetTypeCode);
    }

    private Serializer getSerializer(byte serializeAlgorithm) {
        return serializerMap.get(serializeAlgorithm);
    }

    public static short getPacketTypeCode(byte commandType, byte msgType) {
        return (short) ((((short) commandType) << 8) + msgType);
    }

    public static int getMagicNumber(ByteBuf byteBuf) {
        return byteBuf.getInt(0);
    }

    public static byte getVersion(ByteBuf byteBuf) {
        return byteBuf.getByte(4);
    }

    public static byte getSerializeAlgorithm(ByteBuf byteBuf) {
        return byteBuf.getByte(5);
    }

    public static byte getCommand(ByteBuf byteBuf) {
        return byteBuf.getByte(6);
    }

    public static byte getMsgType(ByteBuf byteBuf) {
        return byteBuf.getByte(7);
    }

    public static long getSeqNumber(ByteBuf byteBuf) {
        return byteBuf.getLong(8);
    }

    public static int getDataLength(ByteBuf byteBuf) {
        return byteBuf.getInt(16);
    }

}
