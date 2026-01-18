package org.gfg.UserService.service;

import com.google.gson.Gson;
import org.gfg.CommonConstants;
import org.gfg.UserService.jwt.JWTUtil;
import org.gfg.UserService.model.User;
import org.gfg.UserService.otp.OTPUtil;
import org.gfg.UserService.repository.UserRepository;
import org.gfg.UserService.request.UserCreationRequest;
import org.gfg.model.UserStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OTPUtil otpUtil;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    JWTUtil jwtUtil;


    @Autowired
    UserRepository userRepository;

    public User createUser(UserCreationRequest userCreationRequest){
        User user = User.builder()
                .name(userCreationRequest.getName())
                .email(userCreationRequest.getEmail())
                .mobileNo(userCreationRequest.getMobileNo())
                .userStatus(UserStatus.ACTIVE)
                .dob(userCreationRequest.getDob())
                .address(userCreationRequest.getAddress())
                .documentType(userCreationRequest.getDocumentType())
                .documentNo(userCreationRequest.getDocumentNo())
                .password(passwordEncoder.encode(userCreationRequest.getPassword()))
                .build();

        Gson gson = new Gson();
        String userjson = gson.toJson(user);
        System.out.println(userjson);

        String otp = otpUtil.createOTP();
        boolean isOTPSaved = otpUtil.saveOtp(userCreationRequest.getEmail(),otp);
        boolean isUserSaved = otpUtil.saveUser(userCreationRequest.getEmail(),userjson);
        if (isOTPSaved && isUserSaved){
            JSONObject otpMessage = new JSONObject();
            otpMessage.put(CommonConstants.USER_MOBILE,user.getMobileNo());
            otpMessage.put(CommonConstants.USER_OTP,otp);
            otpMessage.put(CommonConstants.USER_EMAIL,user.getEmail());
            kafkaTemplate.send(CommonConstants.USER_OTP_TOPIC,otpMessage.toString());
            return user;
        }else {
            return null;
        }
    }


    public boolean validateOTP(String email, String otp){
        boolean validate = otpUtil.validateOTP(email,otp);
        if (validate){
            System.out.println("User is validated, going to persist the user in database");
            User dbUser = otpUtil.fetchUser(email);
            User savedUser = userRepository.save(dbUser);
            JSONObject userMessage = new JSONObject();
            userMessage.put(CommonConstants.USER_MOBILE, savedUser.getMobileNo());
            userMessage.put(CommonConstants.USER_EMAIL, savedUser.getEmail());
            userMessage.put(CommonConstants.USER_DOCUMENT_TYPE,savedUser.getDocumentType());
            userMessage.put(CommonConstants.USER_DOCUMENT_VALUE,savedUser.getDocumentNo());
            userMessage.put(CommonConstants.USER_ID,savedUser.getId());
            userMessage.put(CommonConstants.USER_NAME,savedUser.getName());
            kafkaTemplate.send(CommonConstants.USER_CREATED_TOPIC,userMessage.toString());
            return true;
        }else {
            System.out.println("OTP was invalid");
            return false;
        }
    }



    public String validateAndReturnToken(String username, String password){
       User user = userRepository.findByEmail(username);
       if (passwordEncoder.matches(password,user.getPassword())){
           return jwtUtil.createToken(user.getMobileNo());
       }else {
           return "Invalid Credentials";
       }
    }


    public User validateUser(String username){
        return userRepository.findByMobileNo(username);
    }
}
