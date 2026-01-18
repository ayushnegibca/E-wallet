package org.gfg.UserService.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreatedResponse extends Response{

    String name;
    String mobileNo;
    String email;
}
