package com.qbk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qbk.domain.TempUser;
import com.qbk.pojo.data.UserData;
import com.qbk.pojo.request.UserImportRequest;
import com.qbk.pojo.request.UserRequest;

import javax.validation.Valid;
import java.util.List;

public interface TempUserService extends IService<TempUser> {

    IPage<TempUser> pageList(UserRequest userRequest);

    TempUser getInfo(Integer userId);

    boolean addUser(@Valid UserRequest request);

    String batchAdd(UserImportRequest request, List<UserData> successData, List<UserData> failData);
}
