package org.chord.sim.common.protocol.chat.codec;

import io.netty.buffer.ByteBuf;
import org.chord.sim.common.protocol.chat.Packet;
import org.chord.sim.common.protocol.chat.constant.Command;
import org.chord.sim.common.protocol.chat.constant.MsgType;
import org.chord.sim.common.protocol.chat.notify.P2pChatNotifyPacket;
import org.chord.sim.common.protocol.chat.request.*;
import org.chord.sim.common.protocol.chat.response.*;
import org.chord.sim.common.serializer.Serializer;
import org.chord.sim.common.serializer.impl.JSONSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chord
 * date 2022/1/10 16:14
 * function:
 */
public class ChatPacketCodec implements PacketCodec{

    public static final int MAGIC_NUMBER = 0x01010101;

    public static final ChatPacketCodec INSTANCE = new ChatPacketCodec();

    private final Map<Short, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializerMap;

    private ChatPacketCodec() {
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

        packetTypeMap.put(getPacketTypeCode(Command.MESSAGE_RECEIVED, MsgType.RESPONSE), MessageReceivedResponsePacket.class);

        packetTypeMap.put(getPacketTypeCode(Command.HEART_BEAT, MsgType.REQUEST), HeartBeatRequestPacket.class);
        packetTypeMap.put(getPacketTypeCode(Command.HEART_BEAT, MsgType.RESPONSE), HeartBeatResponsePacket.class);

        serializerMap = new HashMap<>();
        Serializer serializer = new JSONSerializer();
        serializerMap.put(serializer.getSerializeAlgorithm(), serializer);
    }

    @Override
    public void encode(ByteBuf byteBuf, Packet packet) {
        // 1.?????????packet??????
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 2.??????
        // magic number
        byteBuf.writeInt(MAGIC_NUMBER);
        // ?????????
        byteBuf.writeByte(packet.getVersion());
        // ???????????????
        byteBuf.writeByte(Serializer.DEFAULT.getSerializeAlgorithm());
        // ????????????????????????????????????????????????
        byteBuf.writeByte(packet.getCommand());
        // ?????????????????????request???response???notification
        byteBuf.writeByte(packet.getMsgType());
        // ???????????????????????????
        byteBuf.writeLong(packet.getSeqNumber());
        // ????????????
        byteBuf.writeInt(bytes.length);
        // ?????????
        byteBuf.writeBytes(bytes);
    }

    @Override
    public Packet decode(ByteBuf byteBuf) {
        // ?????? magic number
        byteBuf.skipBytes(4);

        // ???????????????
        byteBuf.skipBytes(1);

        // ???????????????
        byte serializeAlgorithm = byteBuf.readByte();

        // ??????
        byte command = byteBuf.readByte();

        // ????????????
        byte msgType = byteBuf.readByte();

        // ?????????
        long seqNumber = byteBuf.readLong();

        // ???????????????
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
