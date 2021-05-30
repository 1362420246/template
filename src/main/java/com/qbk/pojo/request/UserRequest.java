package com.qbk.pojo.request;

import com.qbk.pojo.group.AddGroup;
import com.qbk.pojo.group.GetGroup;
import com.qbk.pojo.group.ListGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRequest extends PageBase{

    @NotNull( message = "用户id不能为空" , groups = {GetGroup.class})
    private Integer userId;

    private Long userCode;

    @NotBlank( message = "用户名不能为空" , groups = {AddGroup.class})
    private String userName;

    private String userDesc;

    @NotBlank( message = "搜索不能为空" , groups = { ListGroup.class})
    private String searchName;
}
