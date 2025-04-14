package com.mysite.stockburning.service;

import com.mysite.stockburning.authentication.PasswordEncryptor;
import com.mysite.stockburning.dto.request.*;
import com.mysite.stockburning.dto.response.LoginResponse;
import com.mysite.stockburning.entity.Posts;
import com.mysite.stockburning.entity.Users;
import com.mysite.stockburning.exception.BadRequestException;
import com.mysite.stockburning.repository.UserRepository;
import com.mysite.stockburning.util.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;
    private final S3Service s3Service;
    @Transactional //트랜잭션 내에서 작업 중 예외발생하면 자동으로 롤백된다.
    public Long createUser(SignupRequest request){
        validateRequest(request); //유효성 검사(아이디, 이메일, 닉네임 중복)
        String encryptedPassword = passwordEncryptor.encrypt(request.userPw()); //비밀번호 암호화
        Users user = Users.of(
                request.nickName(),
                request.userId(),
                encryptedPassword,
                request.email()
        );
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request){
        Users user = userRepository.findByUserId(request.userId())
                .orElseThrow(() -> new BadRequestException("존재하지 않는 아이디입니다."));
        boolean isPasswordValid = passwordEncryptor.matches(request.userPw(), user.getUserPw());
        if(!isPasswordValid){
            throw new BadRequestException("잘못된 비밀번호입니다.");
        }
        return LoginResponse.getInfo(user);
    }
    @Transactional(readOnly = true)
    public LoginResponse loginAsOauth2(ProviderType providerType, Long providerId){
        Users user = userRepository.findByProviderTypeAndProviderId(providerType, providerId).orElse(null);
        if(user != null){
            return LoginResponse.getInfo(user);
        }else{
            throw new BadRequestException("잘못된 회원정보입니다.");
        }
    }
    @Transactional(readOnly = true)
    public boolean isEmailAndIdValid(String userid, String email){
        Optional<Users> user = userRepository.findByUserIdAndEmail(userid, email);
        return user.isPresent();
    }
    @Transactional(readOnly = true)
    public String getEmail(String userid){
        Users user = userRepository.findByUserId(userid)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 유저정보입니다."));
        return user.getEmail();
    }
    @Transactional(readOnly = true)
    public String getUserId(String email){
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()->new BadRequestException("존재하지 않는 유저정보입니다."));
        return user.getUserId();
    }
    @Transactional(readOnly = true)
    public String getUserId(Long id){
        Users user = userRepository.findById(id)
                .orElseThrow(()->new BadRequestException("존재하지 않는 유저정보입니다."));
        return user.getUserId();
    }
    @Transactional(readOnly = true)
    public String getProfilePicture(Long id){
        Users user = userRepository.findById(id)
                .orElseThrow(()->new BadRequestException("존재하지 않는 유저정보입니다."));
        return user.getProfilePicture();
    }
    @Transactional
    public Users modifyNickNameOrEmail(String userId, ModifyRequest request){
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new BadRequestException("개인정보수정 요청이 거부되었습니다."));
        Users updatedUser = user.toBuilder()
                .nickName(request.nickName())
                .email(request.email())
                .build();
        return this.userRepository.save(updatedUser);
    }
    @Transactional
    public Users modifyProfileImage(Long id, String imagePath){
        Users user = userRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("개인정보수정 요청이 거부되었습니다."));
        Users updatedUser = user.toBuilder()
                .profilePicture(imagePath)
                .build();
        return this.userRepository.save(updatedUser);
    }
    @Transactional
    public void modifyPassword(PasswordRequest request){
        Users user = userRepository.findByUserId(request.userid())
                .orElseThrow(()-> new BadRequestException("비밀번호 수정 요청이 거부되었습니다."));
        Users updatedUser = user.toBuilder()
                .userPw(passwordEncryptor.encrypt(request.password()))
                .build();
        this.userRepository.save(updatedUser);
    }
    @Transactional
    public void modifyPassword(String userid, String tempPw){
        Users user = userRepository.findByUserId(userid)
                .orElseThrow(()-> new BadRequestException("비밀번호 수정 요청이 거부되었습니다."));
        Users updatedUser = user.toBuilder()
                .userPw(passwordEncryptor.encrypt(tempPw))
                .build();
        this.userRepository.save(updatedUser);
    }
    @Transactional
    public void deleteUser(Long id){
        Users user = userRepository.findById(id).orElse(null);
        assert user!=null;
        if(!user.getProfilePicture().equals("default_img"))
            s3Service.deleteS3Image(user.getProfilePicture());

        for(Posts post : user.getPosts()){
            s3Service.deleteS3Image(post.getImagePath());
        }

        this.userRepository.delete(user);
    }
    public void validateRequest(SignupRequest request){ //유효성 검사 메소드
        validateUserIdDuplication(request.userId());
        validateEmailDuplication(request.email());
        validateNickNameDuplication(request.nickName());
    }
    public void validateEmailDuplication(String email){  //이메일 중복 검사 메소드
        if(userRepository.existsByEmail(email)){
            throw new BadRequestException("이미 가입된 이메일입니다.");
        }
    }
    public void validateUserIdDuplication(String userId){ //아이디 중복 검사 메소드
        if(userRepository.existsByUserId(userId)){
            throw new BadRequestException("이미 가입된 아이디입니다.");
        }
    }
    public void validateNickNameDuplication(String nickName){
        if(userRepository.existsByNickName(nickName)) {
            throw new BadRequestException("이미 존재하는 닉네임입니다.");
        }
    }
    public boolean isNickNameDuplication(String nickName){
        return userRepository.existsByNickName(nickName);
    }
    public boolean isUserIdDuplication(String userid){
        return userRepository.existsByUserId(userid);
    }
    public boolean isEmailDuplication(String email){
        return userRepository.existsByEmail(email);
    }
    public boolean isPasswordValid(String userId, String rawPassword) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(()->new BadRequestException("(UserService)존재하지 않는 사용자 정보입니다."));

        boolean isValidPassword = passwordEncryptor.matches(rawPassword, user.getUserPw());
        if(isValidPassword)
            return true;
        else
            return false;
    }
}

