package ibf2022.batch2.ssf.frontcontroller.respositories;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepository {

	// TODO Task 5
	// Use this class to implement CRUD operations on Redis
	@Autowired
	private RedisTemplate<String, String> template;

	private static final String DISABLED_USER_LIST_VALUE = "disabled_at_"; 

    public void addUser(String username) {
		template.opsForValue().set(username, DISABLED_USER_LIST_VALUE + LocalDateTime.now().toString(), 30, TimeUnit.MINUTES);
    }

	public boolean canFindUser(String username) {
		// true if found in redis
		// false if not found in redis
		if(template.opsForValue().get(username) == null) {
			return false;
		}
		return true;
	}
}
