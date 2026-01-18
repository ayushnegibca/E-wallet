package org.gfg.UserService.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gfg.model.DocumentType;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {

    @NotNull
    String name;
    @NotNull
    String email;
    @NotNull
    String password;
    @NotNull
    @Length(max = 13)
    String mobileNo;
    @NotNull
    String address;
    @NotNull
    String dob;
    @NotNull
    DocumentType documentType;
    @NotNull
    String documentNo;
}
