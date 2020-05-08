package exarb.fmgamelogic.service;

import exarb.fmgamelogic.client.LoginUserClient;
import exarb.fmgamelogic.client.dto.LoginUser;
import exarb.fmgamelogic.model.User;
import exarb.fmgamelogic.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private LoginUserClient loginUserClient;
    private UserRepository userRepository;

    public void retrieveUserById(String userId) {
        LoginUser loginUser = loginUserClient.retrieveUserById(userId);
        log.info("loginUser {} received from User service", loginUser.getUserName());

        User savedUser = userRepository.save(new User(loginUser.getId(), loginUser.getUserName()));
        log.info("User id and userName saved for id: {}", savedUser.getUserId());
    }

    public void saveUser(String userId, String userName){
        User user = new User();
        user.setUserId("aaa");
        user.setUserName("hej");
        System.out.println("user: " + user);
        // userRepository.save(new User(userId, userName));
        userRepository.save(user);
    }

}
