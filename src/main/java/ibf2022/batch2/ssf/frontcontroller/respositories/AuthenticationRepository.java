package ibf2022.batch2.ssf.frontcontroller.respositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationRepository {

	// TODO Task 5
	// Use this class to implement CRUD operations on Redis
	@Autowired
	RedisTemplate<String, String> template;

    public void addUser(String username) {

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
