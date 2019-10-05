package com.syphan.practice.registrationservice.service.impl;

import com.syphan.practice.commonservice.dao.JpaQueryRepository;
import com.syphan.practice.commonservice.service.impl.BaseServiceImpl;
import com.syphan.practice.registrationservice.model.Permission;
import com.syphan.practice.registrationservice.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission, Integer> implements PermissionService {

    @Autowired
    protected PermissionServiceImpl(JpaQueryRepository<Permission, Integer> repository) {
        super(repository);
    }
}
