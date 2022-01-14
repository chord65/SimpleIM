package org.chord.sim.common.serializer.impl;

import com.alibaba.fastjson.JSON;
import org.chord.sim.common.serializer.SerializeAlgorithm;
import org.chord.sim.common.serializer.Serializer;

/**
 * @author chord
 * date 2022/1/10 16:42
 * function:
 */
public class JSONSerializer implements Serializer {

    @Override
    public byte getSerializeAlgorithm() {
        return SerializeAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
