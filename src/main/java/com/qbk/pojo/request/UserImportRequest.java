package com.qbk.pojo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class UserImportRequest {
    /**
     * 导入文件
     */
    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

}
