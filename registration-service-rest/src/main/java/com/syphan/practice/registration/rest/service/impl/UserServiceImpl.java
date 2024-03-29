package com.syphan.practice.registration.rest.service.impl;

import com.syphan.practice.commonservice.exception.BIZException;
import com.syphan.practice.commonservice.model.enumclass.ErrType;
import com.syphan.practice.commonservice.service.impl.BaseServiceImpl;
import com.syphan.practice.commonservice.util.GenerateAvatarUtils;
import com.syphan.practice.dto.registration.AdminCreateUserDto;
import com.syphan.practice.dto.registration.UserCreateDto;
import com.syphan.practice.registration.api.model.Role;
import com.syphan.practice.registration.api.model.User;
import com.syphan.practice.registration.api.repository.RoleRepository;
import com.syphan.practice.registration.api.repository.UserRepository;
import com.syphan.practice.registration.api.service.UserService;
import com.syphan.practice.registration.rest.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import com.syphan.practice.houseservice.service.BoardingHouseService;

@Service
@org.apache.dubbo.config.annotation.Service
public class UserServiceImpl extends BaseServiceImpl<User, Integer> implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

//    @Reference
//    private BoardingHouseService houseService;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public String sendUserSignUpMailCaptcha(String email) throws BIZException {
        return null;
    }

    @Transactional
    @Override
    public User signUp(UserCreateDto data) throws BIZException {
        User user = generalCreateUser(data.getUsername(), data.getEmail(), data.getPhoneNum(), data.getPassword(),
                data.getAvatar(), data.getFullName());

        Set<Role> roles = new HashSet<>();
        roles.add(getDefaultUserRole());
        user.setRoles(roles);
        return repository.save(user);
    }

    @Transactional
    @Override
    public User adminCreateUser(AdminCreateUserDto data) throws BIZException {
        User user = generalCreateUser(data.getUsername(), data.getEmail(), data.getPhoneNum(), data.getPassword(),
                data.getAvatar(), data.getFullName());
        Set<Role> roles = new HashSet<>();
        if (!data.getRoleIds().isEmpty()) {
            List<Role> roleList = roleRepository.findAllById(data.getRoleIds());
            if (roleList.size() != new HashSet<>(data.getRoleIds()).size())
                throw BIZException.buildBIZException(ErrType.NOT_FOUND,
                        "role.do.not.existed", String.format("%s%s%s", "Role with id in list Id[ ", data.getRoleIds(), "] do not existed"));
            roles.addAll(roleList);
        } else {
            roles.add(getDefaultUserRole());
        }
        user.setRoles(roles);
        return repository.save(user);
    }

    private User generalCreateUser(String userName, String email, String phoneNum, String password, String avatar, String fullName) {
        if (repository.findByUsername(userName) != null) {
            throw BIZException.buildBIZException(ErrType.CONFLICT,
                    "Signup.Username.Occupied", "The username to be registered is already occupied.");
        }

        if (email != null && repository.findByEmail(email) != null) {
            throw BIZException.buildBIZException(ErrType.CONFLICT,
                    "Signup.Email.Occupied", "The email address to be registered is already occupied.");
        }

        if (repository.findByPhoneNum(phoneNum) != null) {
            throw BIZException.buildBIZException(ErrType.CONFLICT,
                    "Signup.PhoneNumber.Occupied", "The phoneNumber to be registered is already occupied.");
        }

        User user = new User();
        user.setUsername(userName);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setFullName(fullName);
        user.setAvatar(avatar != null ? avatar : GenerateAvatarUtils.generate(email));
        user.setEmail(email);
        user.setPhoneNum(phoneNum);
        return user;
    }

    @Override
    public User findByUsername(String username) throws BIZException {
        return repository.findByUsername(username);
    }

    private Role getDefaultUserRole() {
        Role role = roleRepository.findByCode(Utils.DEFAULT_USER_ROLE);
        if (role != null) {
            return role;
        } else {
            role = new Role();
            role.setName("User");
            role.setCode(Utils.DEFAULT_USER_ROLE);
            return roleRepository.save(role);
        }
    }
}
