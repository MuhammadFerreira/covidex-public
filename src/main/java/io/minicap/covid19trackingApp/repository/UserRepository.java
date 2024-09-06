package io.minicap.covid19trackingApp.repository;

import io.minicap.covid19trackingApp.appUsers.User;
import io.minicap.covid19trackingApp.appUsers.userRole;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//General search
@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> 
{
    public User findByEmail(String email);

    public User findByConfirmationToken(String confirmationToken);

    public List<User> findAllByUserRole(userRole patient);

}