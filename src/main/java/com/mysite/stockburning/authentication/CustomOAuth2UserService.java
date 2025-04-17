
package com.mysite.stockburning.authentication;

import com.mysite.stockburning.dto.UserInfoDTO;
import com.mysite.stockburning.dto.response.KakaoResponse;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.repository.UserRepository;
import com.mysite.stockburning.util.ProviderType;
import com.mysite.stockburning.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


//인증서버로부터 받은 AccessToken 을 사용하여 회원정보를 요청하고 이를 OAuth2User 객체로 변환
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    private final Random random = new Random();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("[CustomOAuth2UserService] - loadUser 메소드 실행");
        //OAuth2 서버(카카오)에서 액세스 토큰을 이용해 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //Oauth2 제공자 정보 가져오기(ex google, kakao, naver)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        KakaoResponse kakaoResponse = null;

        if(registrationId.equals("kakao")){
            kakaoResponse = new KakaoResponse(oAuth2User.getAttributes());
        }else{
            return null;
        }

        String nickName;
        do{
            nickName = kakaoResponse.getNickname() + random.nextInt(10000);
        }while(userRepository.existsByNickName(nickName));

        Users kakaoUser = userRepository.findByProviderTypeAndProviderId(ProviderType.valueOf(kakaoResponse.getProvider()), kakaoResponse.getId()).orElse(null);
        if(kakaoUser == null){ //신규회원
            log.info("[CustomOAuth2UserService] - 신규회원 생성");
            kakaoUser = createUserFromKakao(nickName, kakaoResponse.getProfileImageUrl(), kakaoResponse.getId());
        }else{ //이미 존재하는 회원일 경우(프로필 이미지 업데이트)
            log.info("[CustomOAuth2UserService] - 기존회원 업데이트");
            kakaoUser = updateUserFromKakao(kakaoUser, kakaoResponse.getProfileImageUrl());
        }

        return new CustomOAuth2User(
                kakaoUser.getId(),
                kakaoUser.getNickName(),
                kakaoUser.getProviderId()
        ); // 이 때 반환되는 CustomOAuth2User 객체는 SecurityContext 에 자동 저장됨
    }
    private Users createUserFromKakao(String kakao_nickname, String kakao_image, Long kakao_id){
        Users user = Users.builder()
                .nickName(kakao_nickname)
                .profilePicture(kakao_image)
                .providerType(ProviderType.KAKAO)
                .providerId(kakao_id)
                .role(UserRole.USER)
                .build();
        return userRepository.save(user);
    }

    private Users updateUserFromKakao(Users originalUser, String kakao_image){
        Users user = originalUser.toBuilder()
                .profilePicture(kakao_image)
                .build();
        return userRepository.save(user);
    }
}