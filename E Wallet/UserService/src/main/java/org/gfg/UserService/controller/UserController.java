package org.gfg.UserService.controller;

import jakarta.validation.Valid;
import org.gfg.UserService.model.User;
import org.gfg.UserService.request.OTPValidationRequest;
import org.gfg.UserService.request.UserCreationRequest;
import org.gfg.UserService.response.OTPResponse;
import org.gfg.UserService.response.UserCreatedResponse;
import org.gfg.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-service/api")
public class UserController {


    @Autowired
    UserService userService;

    @PostMapping("/user/create")
    public ResponseEntity<UserCreatedResponse> createUser(@RequestBody @Validated UserCreationRequest userCreationRequest){
        UserCreatedResponse userCreatedResponse = new UserCreatedResponse();
        if (userCreationRequest==null){
            userCreatedResponse.setStatus("FAILED");
            userCreatedResponse.setMessage("Request Failed !! required parameters missing");
            return new ResponseEntity<>(userCreatedResponse, HttpStatus.BAD_REQUEST);
        }

       User user = userService.createUser(userCreationRequest);
        if (user==null){
            userCreatedResponse.setStatus("FAILED");
            userCreatedResponse.setMessage("Request Failed !! some error");
            return new ResponseEntity<>(userCreatedResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        userCreatedResponse.setStatus("SUCCESS");
        userCreatedResponse.setMessage("OTP Successfully sent to user");
        userCreatedResponse.setName(user.getName());
        userCreatedResponse.setEmail(user.getEmail());
        userCreatedResponse.setMobileNo(user.getMobileNo());

        return new ResponseEntity<>(userCreatedResponse,HttpStatus.CREATED);

    }

    @PostMapping("/otp/validate")
    public ResponseEntity<OTPResponse> validateOtp(@RequestBody OTPValidationRequest otpValidationRequest){
        String email = otpValidationRequest.getEmail();
        String otp = otpValidationRequest.getOtp();
        OTPResponse response = new OTPResponse();
        response.setEmail(email);
        if (userService.validateOTP(email,otp)){
            response.setMessage("OTP successfully validated");
            response.setStatus("SUCCESS");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }else {
            response.setMessage("Incorrect OTP");
            response.setStatus("FAILED");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password){

        return userService.validateAndReturnToken(username,password);

    }

    @GetMapping("/validate/user/{username}")
    public String validateUsername(@PathVariable("username") String username){
        User user = userService.validateUser(username);
        if (user==null){
            return "NOT OK";
        }else {
            return "OK";
        }
    }
}
