package org.chord.sim.server.server.attribute;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * @author chord
 * date 2022/2/2 18:50
 * function:
 */
public interface ChannelAttributes {

    AttributeKey<String> USER_ID = AttributeKey.newInstance("userId");

}
