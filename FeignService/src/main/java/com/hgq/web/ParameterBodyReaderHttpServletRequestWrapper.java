package com.hgq.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-07-22 10:35
 * @since 1.0
 **/
@Slf4j
public class ParameterBodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public ParameterBodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        String sessionStream = this.getBodyString(request);
        this.body = sessionStream.getBytes(Charset.forName("UTF-8"));
    }

    /**
     * 从流中获取信息
     *
     * @param request
     * @return
     * @throws IOException
     */
    public String getBodyString(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while (StringUtils.isNotBlank((line = reader.readLine()))) {
                sb.append(line);
            }
        } catch (Exception e) {
            log.error("读取body信息异常", e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("关闭 inputStream 流异常", e);
                }
            }

            if (null != reader) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.error("关闭 reader 流异常", e);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取http字符串body
     *
     * @param request
     * @return body字符串
     * @throws IOException
     */
    public String getBodyStr(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        String str, wholeStr = "";
        while ((str = br.readLine()) != null) {
            wholeStr += str;
        }
        return wholeStr;
    }


    /**
     * 获取http二进制body
     *
     * @param request
     * @return body字符串
     */
    public String getBodyData(HttpServletRequest request) {
        StringBuffer data = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while (null != (line = reader.readLine()))
                data.append(line);
        } catch (IOException e) {
        } finally {
        }
        return data.toString();
    }


    public InputStream cloneInputStream(ServletInputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        try {
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            byteArrayOutputStream.flush();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }
}
