import org.chord.sim.client.SIMClientApplication;
import org.chord.sim.client.client.SIMClient;
import org.chord.sim.client.util.SessionUtil;
import org.chord.sim.client.util.SpringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chord
 * date 2022/1/17 23:20
 * function:
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SIMClientApplication.class)
public class SpringUtilTest {

    @Test
    public void test() {
        SIMClient simClient = SpringUtils.getBean(SIMClient.class);

        simClient.getUserInfo().setUserName("chord");

        System.out.println(simClient.getUserInfo());

    }
}
