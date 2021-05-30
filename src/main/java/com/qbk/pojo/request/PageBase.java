package com.qbk.pojo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageBase {

    /**
     * 开始的页数
     */
    private Long current = 1L;

    /**
     * 每页数量
     */
    private Long size = 10L;
}
