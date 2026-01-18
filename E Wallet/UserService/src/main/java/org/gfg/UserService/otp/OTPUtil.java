package org.gfg.UserService.otp;

import com.google.gson.Gson;
import org.gfg.UserService.model.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OTPUtil {

    private final static int OTP_LENGTH=6;


    @Autowired
    @Qualifier("otpRedisTemplate")
    RedisTemplate<String,String> otpRedisTemplate;

    @Autowired
    @Qualifier("userRedisTemplate")
    RedisTemplate<String,String> userRedisTemplate;

    public String createOTP(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=1;i<=OTP_LENGTH;i++){
           int digit = (int) (Math.random()*10);
           stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }


    public boolean saveOtp(String email, String otp){
        boolean isSaved = true;
        try {
            otpRedisTemplate.opsForValue().set(email + "_OTP", otp, 3, TimeUnit.MINUTES);
        }
        catch (Exception e){
            isSaved = false;
        }
        return isSaved;
    }

    public boolean saveUser(String email, String userjson){
        boolean isSaved = true;
        try {
            userRedisTemplate.opsForValue().set(email + "_USER", userjson, 4, TimeUnit.MINUTES);
        }
        catch (Exception e){
            isSaved = false;
        }
        return isSaved;
    }

    public boolean validateOTP(String email, String otp){
       String redisOTP = otpRedisTemplate.opsForValue().get(email+"_OTP");
       if (otp.equals(redisOTP)){
           System.out.println("OTP Match");
           return true;
       }else {
           System.out.println("OTP didn't match");
           return false;
       }
    }


    public User fetchUser(String email){
        String userjson = userRedisTemplate.opsForValue().get(email+"_USER");
        System.out.println(userjson);
        Gson gson = new Gson();
       User user = gson.fromJson(userjson, User.class);
        System.out.println(user);
        return user;
    }

}
