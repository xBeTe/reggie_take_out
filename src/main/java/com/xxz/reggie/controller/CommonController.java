package com.xxz.reggie.controller;

import com.xxz.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 文件上传和下载
 *
 * @author xzxie
 * @create 2022/11/22 15:47
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 处理上传文件的请求，并将文件转存到指定的位置
     *
     * @param file 前端请求上传的文件
     * @return 返回包含文件名的处理结果
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestPart("file") MultipartFile file) {
        // file 是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会被删除
        log.info(file.toString());


        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用 UUID 重新生成文件名，防止文件名重复，造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        // 创建一个目录对象
        File dir = new File(basePath);
        // 判断当前目录是否存在
        if (!dir.exists()) {
            // 目录不存在，创建目录
            dir.mkdir();

        }

        try {
            // 将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 处理文件下载的请求
     *
     * @param name     前端请求的文件名
     * @param response 用于将文件响应给前端页面
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fis = null;
        ServletOutputStream outputStream = null;

        try {
            // 通过输入流，读取文件内容
            fis = new FileInputStream(new File(basePath + name));

            // 通过输出流将文件写回浏览器，在浏览器展示图片
            outputStream = response.getOutputStream();


            response.setContentType("/image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
