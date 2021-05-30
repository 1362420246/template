package com.qbk.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.StringJoiner;

/**
 * 数据格式验证类
 * @author dufa
 * @date 2020/12/10 15:18
 */
public class EasyExcelVailHelper {
    private EasyExcelVailHelper(){}

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> String validateEntity(T obj) {
        StringJoiner result = new StringJoiner("、");
        Set<ConstraintViolation<T>> set = VALIDATOR.validate(obj, Default.class);
        if (CollectionUtils.isEmpty(set)) {
            return result.toString();
        }
        try {
            for (ConstraintViolation<T> cv : set) {
                Field declaredField = obj.getClass().getDeclaredField(cv.getPropertyPath().toString());
                ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                //拼接错误信息，包含当前出错数据的标题名字+错误信息
//                result.append(annotation.value()[0]).append(cv.getMessage());
                result.add(cv.getMessage());
            }
        } catch (Exception e) {
            result.add(e.getMessage());
        }
        return result.toString();
    }
}
