package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * general interface
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "general interface")
@Slf4j
public class CommonController {

    @Autowired
    public AliOssUtil aliOssUtil;

    /**
     * upload file
     * @param file
     * @return
     */
    @ResponseBody
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("upload file: {}", file.getOriginalFilename());
        try {
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            assert originalFilename != null;
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + extension;

            String fillPath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(fillPath);
        } catch (IOException e) {
            log.error("upload file error: {}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
