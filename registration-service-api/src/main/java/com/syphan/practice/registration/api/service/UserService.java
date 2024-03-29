package com.syphan.practice.registration.api.service;

import com.syphan.practice.commonservice.exception.BIZException;
import com.syphan.practice.commonservice.service.BaseService;
import com.syphan.practice.dto.registration.AdminCreateUserDto;
import com.syphan.practice.dto.registration.UserCreateDto;
import com.syphan.practice.registration.api.model.User;

public interface UserService extends BaseService<User, Integer> {

    String sendUserSignUpMailCaptcha(String email) throws BIZException;

    User signUp(UserCreateDto data) throws BIZException;

    User adminCreateUser(AdminCreateUserDto data) throws BIZException;

    User findByUsername(String username) throws BIZException;

}
