package com.qbk.contoller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qbk.common.RestResponse;
import com.qbk.common.ResultUtil;
import com.qbk.constant.ExcelFileConstant;
import com.qbk.domain.TempUser;
import com.qbk.exception.BusinessException;
import com.qbk.listener.ExcelUserListener;
import com.qbk.pojo.data.UserData;
import com.qbk.pojo.group.AddGroup;
import com.qbk.pojo.group.GetGroup;
import com.qbk.pojo.group.ListGroup;
import com.qbk.pojo.request.UserImportRequest;
import com.qbk.pojo.request.UserRequest;
import com.qbk.pojo.result.UserImportResult;
import com.qbk.service.TempUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private TempUserService userService;

    @GetMapping("/list")
    public RestResponse<?> list(@Validated({ListGroup.class})UserRequest userRequest){
        IPage<TempUser> list = userService.pageList(userRequest);
        return ResultUtil.ok(list);
    }

    @GetMapping("/info")
    public RestResponse<?> getInfo(@Validated({GetGroup.class})UserRequest userRequest){
        Integer userId = userRequest.getUserId();
        TempUser user = userService.getInfo(userId);
        if(Objects.isNull(user)){
            throw BusinessException.getFailException("用户不存在");
        }
        return ResultUtil.ok(user);
    }

    @PostMapping("/add")
    public RestResponse<?> addUser(@Validated({AddGroup.class}) @RequestBody UserRequest request) {
        boolean result = userService.addUser(request);
        return result ? ResultUtil.ok("添加成功") : ResultUtil.error("添加失败");
    }

    /**
     * 批量导入
     */
    @PostMapping("/batch/import")
    public RestResponse<?> batchImport(@Valid UserImportRequest request) throws IOException {
        MultipartFile file = request.getFile();
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            return ResultUtil.error("请求参数不合法，上传文件无法获取文件名，请检查文件是否正确");
        }
        String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!ExcelFileConstant.FILE_TYPE.contains(substring)) {
            return ResultUtil.error( "请求参数不合法，上传文件格式不对，请选择xlsx或者xls格式文件上传");
        }
        //Excel
        request.setFile(null);
        ExcelUserListener excelSiteListener = new ExcelUserListener(userService,request);
        // 上传excel文件验证, ignoreEmptyRow(false)，是否跳过空行，默认跳过，设置false后，不跳过空行
        EasyExcel.read(
                file.getInputStream(),
                UserData.class,
                excelSiteListener)
                .ignoreEmptyRow(false).sheet(0).headRowNumber(2).doRead();
        if(StringUtils.isNotBlank(excelSiteListener.errorMsg)){
            return ResultUtil.error(excelSiteListener.errorMsg);
        }
        //封装返回结果
        UserImportResult result =
                UserImportResult
                        .builder()
                        .batchCode(excelSiteListener.getBatchCode())
                        .total(excelSiteListener.getTotal())
                        .success(excelSiteListener.getSuccessData().size())
                        .fail(excelSiteListener.getFailData().size())
                        .build();
        return ResultUtil.ok(result);
    }

//    /**
//     *  批量导出
//     */
//    @GetMapping(value = "/batch/export")
//    public void batchExport(@RequestParam String batchCode, HttpServletResponse response) throws Exception{
//        //查询数据
//        List<Fail> list = userService.lambdaQuery().eq(Fail::getBatchCode, batchCode).list();
//        List<UserData> dataList = BeanCopierUtil.copyBeanList(list, UserData.class);
//        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
//        response.setContentType("application/vnd.ms-excel");
//        response.setCharacterEncoding("utf-8");
//        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
//        String fileName = URLEncoder.encode(ExcelFileConstant.SITE_FILE_NAME, "UTF-8");
//        response.setHeader("Content-disposition", "attachment;filename=" + fileName );
//        EasyExcel.write(response.getOutputStream(), UserData.class)
//                .sheet(ExcelFileConstant.SITE_SHEET_NAME)
//                .doWrite(dataList);
//    }

}
