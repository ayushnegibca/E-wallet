package org.gfg.UserService.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.gfg.model.DocumentType;
import org.gfg.model.UserStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    @Column(unique = true)
    String email;


    String password;

    String address;

    @Column(unique = true, length = 10)
    String mobileNo;

    @Enumerated(EnumType.STRING)
    UserStatus userStatus;

    @Column(unique = true)
    String documentNo;

    @Enumerated(EnumType.STRING)
    DocumentType documentType;

    String dob;

    @CreationTimestamp
    Date createdOn;

    @UpdateTimestamp
    Date updatedOn;
}
