package org.gfg.UserService.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPValidationRequest {

    String email;
    String otp;
    String mobileNo;
}
