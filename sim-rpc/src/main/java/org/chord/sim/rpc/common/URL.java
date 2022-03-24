package org.chord.sim.rpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author chord
 * date 2022/3/19 23:23
 * function:
 */

@Data
@AllArgsConstructor
public class URL {

    private String hostName;
    private Integer port;
}
