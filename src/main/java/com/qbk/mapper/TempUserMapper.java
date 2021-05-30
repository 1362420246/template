package com.qbk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qbk.domain.TempUser;
import org.apache.ibatis.annotations.Param;

/**
* Created by Mybatis Generator 2021/05/30
*/
public interface TempUserMapper extends BaseMapper<TempUser> {
    IPage<TempUser> pageList(IPage<TempUser> page,@Param("searchName") String searchName);
}