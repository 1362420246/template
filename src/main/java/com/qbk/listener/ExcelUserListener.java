package com.qbk.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.qbk.constant.ExcelFileConstant;
import com.qbk.domain.TempUser;
import com.qbk.pojo.data.UserData;
import com.qbk.pojo.request.UserImportRequest;
import com.qbk.service.TempUserService;
import com.qbk.utils.EasyExcelVailHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class ExcelUserListener extends AnalysisEventListener<UserData> {

    private TempUserService userService;

    private UserImportRequest request;

    /**
     * 错误消息
     */
    public String errorMsg ;

    /**
     * 成功数据
     */
    private final List<UserData> successData = new ArrayList<>();

    /**
     * 失败数据
     */
    private final List<UserData> failData = new ArrayList<>();

    /**
     * 统计总数
     */
    private Integer total = 0;

    /**
     * 批次码
     */
    private String batchCode;

    public ExcelUserListener(TempUserService userService, UserImportRequest request) {
        this.userService = userService;
        this.request = request;
    }

    /**
     * Excel文件请求头验证
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        Collection<String> excelHead = ExcelFileConstant.EXCEL_HEAD;
        Collection<String> values = headMap.values();
        if (context.readRowHolder().getRowIndex() == 0) {
            return;
        }
        boolean equalList = ListUtils.isEqualList(values, excelHead);
        if (!equalList) {
            errorMsg = "excel文件头内容格式错误，请下载最新模板";
            log.debug("[导入失败]excel文件头内容格式错误，请下载最新模板");
        }
    }

    /**
     * 每简析一行数据执行此方法，可以进行行数据校验
     * @param data       数据封装实体
     * @param context   简析Context
     */
    @Override
    public void invoke(UserData data, AnalysisContext context) {
        if (ObjectUtils.isEmpty(data) || !StringUtils.isEmpty(errorMsg)) {
            return;
        }
        //计算总数
        total ++ ;
        //校验字段
        String msg = EasyExcelVailHelper.validateEntity(data);
        if (!StringUtils.isEmpty(msg)) {
            log.debug("[导入失败]行号为"+(context.readRowHolder().getRowIndex()+1)+"的数据:" + msg);
            data.setFailureCause(msg);
            //添加失败
            failData.add(data);
            return;
        }

        //去重
        if(successData.contains(data)){
            log.debug("[导入失败]行号为"+(context.readRowHolder().getRowIndex()+1)+"的数据: 名称重复");
            data.setFailureCause("名称重复");
            //添加失败
            failData.add(data);
            return;
        }
        //校验名称  TODO 高并发下,可能存在幂等问题
        int count = userService.lambdaQuery()
                .eq(TempUser::getUserName,data.getUserName())
                .count();
        if(count > 0){
            log.debug("[导入失败]行号为"+(context.readRowHolder().getRowIndex()+1)+"的数据: 名已存在");
            data.setFailureCause("名已存在");
            //添加失败
            failData.add(data);
            return;
        }
        //添加成
        successData.add(data);
    }

    /**
     * 全量数据简析完成后执行，可以进行数据操作，比如持久化到数据库
     * @param context  简析Context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!StringUtils.isEmpty(errorMsg)) {
            return;
        }
        if (total.equals(0)) {
            errorMsg = "您导入的文件中没有数据";
            log.debug("[导入失败]您导入的文件中没有数据");
            return;
        }
        //批量保存数据
        try {
            this.batchCode = userService.batchAdd(request ,successData , failData);
        }catch (Exception e){
            errorMsg = "导入错误";
            log.error("[导入错误]",e);
        }
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("解析失败:{}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            String error = String.format("第%d行，第%d列解析异常,类型转换错误", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex());
            log.error(error);

            if(exception.getCause().toString().startsWith("java.lang.IllegalArgumentException: can not find date format for")){
                errorMsg = "时间格式类型转换错误" ;
            }else {
                errorMsg = error;
            }
        }else {
            errorMsg = "数据错误" ;
        }
    }
}
