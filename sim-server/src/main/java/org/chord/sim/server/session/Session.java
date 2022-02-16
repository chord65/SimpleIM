package org.chord.sim.server.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chord
 * date 2022/2/2 18:55
 * function:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Session {

    private String userId;
    private String userName;

}
