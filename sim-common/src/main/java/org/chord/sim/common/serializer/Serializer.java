package org.chord.sim.common.serializer;


import org.chord.sim.common.serializer.impl.JSONSerializer;

/**
 * @author chord
 * date 2022/1/10 16:32
 * function:
 */
public interface Serializer {

    Serializer DEFAULT = new JSONSerializer();

    /**
     * 获得使用的序列化算法
     */
    byte getSerializeAlgorithm();

    /**
     * Java对象转为二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转为Java对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

}
