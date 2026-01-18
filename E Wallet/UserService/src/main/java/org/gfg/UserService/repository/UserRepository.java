package org.gfg.UserService.repository;

import org.gfg.UserService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

     User findByEmail(String email);
     User findByMobileNo(String mobile);
}
