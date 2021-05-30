package com.qbk.pojo.data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserData {

    @ExcelProperty(index = 0 , value = "用户名称")
    @NotBlank(message = "用户名称不能为空")
    private String userName;

    @ExcelProperty(index = 1 , value = "用户描述")
    private String userDesc;

    /**
     * 失败原因
     */
    @ExcelProperty(value = "失败原因")
    private String failureCause;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return userName.equals(userData.userName);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + userName.hashCode();
        return result;
    }
}
