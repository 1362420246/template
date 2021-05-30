package com.qbk.pojo.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserImportResult {

    /**
     * 批次码
     */
    private String batchCode;

    /**
     * 统计总数
     */
    private Integer total ;
    /**
     * 成功数
     */
    private Integer success ;

    /**
     * 失败数
     */
    private Integer fail;
}
