package com.qbk.service.impl;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qbk.exception.BusinessException;
import com.qbk.pojo.data.UserData;
import com.qbk.pojo.request.UserImportRequest;
import com.qbk.pojo.request.UserRequest;
import com.qbk.service.TempUserService;
import com.qbk.utils.BeanCopierUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qbk.mapper.TempUserMapper;
import com.qbk.domain.TempUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
public class TempUserServiceImpl extends ServiceImpl<TempUserMapper, TempUser> implements TempUserService {

    @Autowired
    private IdentifierGenerator identifierGenerator;

    @Value("${user.password:123}")
    private String password;

    @Override
    public IPage<TempUser> pageList(UserRequest userRequest) {
        String searchName = userRequest.getSearchName();
        IPage<TempUser> page = new Page<>(userRequest.getCurrent(),userRequest.getSize());
        return this.baseMapper.pageList(page ,searchName );
    }

    @Override
    public TempUser getInfo(Integer userId) {
        return this.lambdaQuery()
                .select(TempUser.class ,field -> !"user_password".equals(field.getColumn()))
                .eq(TempUser::getUserId,userId)
                .one();
    }

    @Override
    public boolean addUser(@Valid UserRequest request) {
        int count = this.lambdaQuery()
                .eq(TempUser::getUserName,request.getUserName())
                .count();
        if(count > 0){
            throw BusinessException.getFailException("用户名称已存在");
        }
        //设置 密码、编码
        TempUser user = TempUser.builder().build();
        BeanCopierUtil.copyBean(request,user);
        user.setUserPassword(password);
        user.setUserCode(identifierGenerator.nextId(DefaultIdentifierGenerator.class).longValue());
        //插入
        return this.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String batchAdd(UserImportRequest request, List<UserData> successData, List<UserData> failData) {
        //批次码
        String batchCode = identifierGenerator.nextId(DefaultIdentifierGenerator.class).toString();
        //处理成功数据
        if(!CollectionUtils.isEmpty(successData)){
            List<TempUser> users = new ArrayList<>(successData.size());

            for (UserData userData: successData) {
                TempUser user = new TempUser();
                BeanCopierUtil.copyBean(userData,user);
                //user.setBatchCode(batchCode);
                user.setUserPassword(password);
                user.setUserCode(identifierGenerator.nextId(DefaultIdentifierGenerator.class).longValue());
                users.add(user);
            }
            this.saveBatch(users);
        }
        //保存失败数据
        if(!CollectionUtils.isEmpty(failData)){

        }
        return batchCode;
    }
}
