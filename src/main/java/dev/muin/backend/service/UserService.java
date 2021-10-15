package dev.muin.backend.service;

import dev.muin.backend.config.jwt.JwtTokenProvider;
import dev.muin.backend.domain.User.Role;
import dev.muin.backend.domain.User.User;
import dev.muin.backend.domain.User.UserRepository;
import dev.muin.backend.web.request.JoinRequest;
import dev.muin.backend.web.request.LoginRequest;
import dev.muin.backend.web.response.LoginResponse;
import dev.muin.backend.web.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public short join(JoinRequest joinRequest) throws IllegalArgumentException{
        // TODO 이미 존재하면 update 테케 추가
        User user = User.builder()
                .uuid(joinRequest.getUuid())
                .email(joinRequest.getEmail())
                .name(joinRequest.getName())
                .role(Role.USER)
                .build();

        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        User member = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 e-mail 입니다."));
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String jwt = jwtTokenProvider.createToken(member.getEmail(), member.getUuid(), member.getRole());

        return new LoginResponse(jwt, member.getUuid(), member.getRole());
    }

    @Transactional(readOnly = true)
    public UserResponse myInfo(String uuid){
        User member = userRepository.findByUuid(uuid).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        return UserResponse.of(member);
    }
}
