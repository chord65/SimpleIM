import org.chord.sim.common.util.RedisKeyUtil;
import org.chord.sim.router.RouterApplication;
import org.chord.sim.router.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author chord
 * date 2022/1/16 0:12
 * function:
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RouterApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Test
    public void incrTest() {

        String nextUerIdKey = RedisKeyUtil.getNextUserIdKey();
        System.out.println(nextUerIdKey);

        Long id = getLongFromRedis(nextUerIdKey);
        if (id != null) {
            System.out.println(id.toString());
        }

        id = redisTemplate.opsForValue().increment(nextUerIdKey);
        if (id != null) {
            System.out.println(id.toString());
        }
    }

    private Long getLongFromRedis(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        return (long) obj;
    }

}
