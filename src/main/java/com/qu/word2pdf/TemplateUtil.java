package com.qu.word2pdf;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.config.ConfigureBuilder;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.google.common.collect.Lists;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 文书模板处理工具类
 *
 * @author zhangkaiyong
 * @date 2021/08/21 14:21
 */
@Slf4j
public class TemplateUtil {

    /**
     * 模板中填充数据
     * 应用场景：已知模板文件位置，目标文件位置
     *
     * @param tempPath   模板路径
     * @param targetPath 目标文件路径
     * @param data       数据
     */
    public static void fillData(String tempPath, String targetPath, Map<String, Object> data) {
        try (FileInputStream fis = new FileInputStream(tempPath);
             FileOutputStream fos = new FileOutputStream(targetPath)) {
            fillData(fis, fos, data);
        } catch (Exception e) {
            log.error("生成文书时出现异常：", e);
            e.printStackTrace();
        }
    }

    /**
     * 模板中填充数据
     * 应用场景：已知获取模板文件的方式(如本地文件/网络等)，保存目标文件的方式(保存到文件系统/直接获取文件的字节码/下载到浏览器端等)
     *
     * @param is   模板的输入流，调用者自己关闭流
     * @param os   目标文件的输出流，调用者自己关闭流
     * @param data 数据
     */
    public static void fillData(InputStream is, OutputStream os, Map<String, Object> data) {
        try {
            XWPFTemplate template = XWPFTemplate.compile(is, getConfig(data)).render(data);
            template.write(os);
            template.close();
        } catch (Exception e) {
            log.error("生成文书时出现异常：", e);
            e.printStackTrace();
        }
    }

    /**
     * 填充模板并转换为PDF
     * 应用场景：已知模板文件位置，目标文件位置
     *
     * @param tempPath   模板路径
     * @param targetPath 目标文件路径
     * @param data       数据
     * @param converter  转换器
     */
    public static void fillAndConvert2Pdf(String tempPath,
                                          String targetPath,
                                          Map<String, Object> data,
                                          DocumentConverter converter) {
        try (FileInputStream fis = new FileInputStream(tempPath);
             FileOutputStream fos = new FileOutputStream(targetPath)) {
            fillAndConvert2Pdf(fis, fos, data, converter);
        } catch (Exception e) {
            log.error("生成并转换文书时出现异常：", e);
            e.printStackTrace();
        }
    }

    /**
     * 填充模板、转换为PDF并下载
     * 应用场景：已知获取模板文件的方式(如本地文件/网络等)，生成、转换后文件下载到浏览器端
     *
     * @param is        模板的输入流，调用者自己关闭流
     * @param response  http响应对象
     * @param fileName  文件名称，带后缀(扩展名)
     * @param data      数据
     * @param converter 转换器
     */
    public static void fillAndConvert2Pdf(InputStream is,
                                          HttpServletResponse response,
                                          String fileName,
                                          Map<String, Object> data,
                                          DocumentConverter converter) {
        try (ServletOutputStream sos = response.getOutputStream()) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename="
                    + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            fillAndConvert2Pdf(is, sos, data, converter);
            sos.flush();
        } catch (Exception e) {
            log.error("生成/转换/下载文书时出现异常：", e);
            e.printStackTrace();
        }
    }

    /**
     * 填充模板并转换为PDF
     * 应用场景：已知获取模板文件的方式(如本地文件/网络等)，保存目标文件的方式(保存到文件系统/直接获取文件的字节码/下载到浏览器端等)
     *
     * @param is        模板的输入流，调用者自己关闭流
     * @param os        目标文件的输出流，调用者自己关闭流
     * @param data      数据
     * @param converter 转换器
     */
    public static void fillAndConvert2Pdf(InputStream is,
                                          OutputStream os,
                                          Map<String, Object> data,
                                          DocumentConverter converter) {
        try {
            // 先写模板
            XWPFTemplate template = XWPFTemplate.compile(is, getConfig(data)).render(data);
            // 如果耗内存严重的话，可以将转换的文件写到磁盘(bos)，再从磁盘中读取，以时间换空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            // 再转换
            converter.convert(new ByteArrayInputStream(bos.toByteArray()))
                    .as(DefaultDocumentFormatRegistry.DOC)
                    .to(os)
                    .as(DefaultDocumentFormatRegistry.PDF).execute();
        } catch (Exception e) {
            log.error("生成并转换文书时出现异常：", e);
            e.printStackTrace();
        }
    }


    /**
     * 填充模板并转换为PDF
     * 应用场景：已知获取模板文件的方式(如本地文件/网络等)，返回转换后文件的字节码
     *
     * @param is        模板的输入流，调用者自己关闭流
     * @param data      数据
     * @param converter 转换器
     * @return byte[] 文件的字节码
     */
    public static byte[] fillAndConvert2PdfBytes(InputStream is,
                                                 Map<String, Object> data,
                                                 DocumentConverter converter) {
        try {
            // 先写数据到模板
            XWPFTemplate template = XWPFTemplate.compile(is, getConfig(data)).render(data);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            template.write(bos);
            // 再转换
            ByteArrayOutputStream newBos = new ByteArrayOutputStream();
            converter.convert(new ByteArrayInputStream(bos.toByteArray()))
                    .as(DefaultDocumentFormatRegistry.DOC)
                    .to(newBos)
                    .as(DefaultDocumentFormatRegistry.PDF).execute();
            // 去空白页
            return removeBlankPage(newBos.toByteArray());
        } catch (Exception e) {
            log.error("生成并转换文件时出现异常：", e);
            throw new RuntimeException("生成并转换文件时出现异常");
        }
    }

    /**
     * 获取配置
     */
    private static Configure getConfig(Map<String, Object> data) {
        ConfigureBuilder builder = Configure.builder();
        LoopRowTableRenderPolicy policy = new LoopRowTableRenderPolicy();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof List || entry.getValue() instanceof Set) {
                builder.bind(entry.getKey(), policy);
            }
        }
        return builder.build();
    }

    /**
     * 获取PDF文件页数
     *
     * @param is 文件的输入流
     * @return Integer
     */
    public static Integer pdfPages(InputStream is) {
        try {
            PdfReader reader = new PdfReader(is);
            return reader.getNumberOfPages();
        } catch (IOException e) {
            throw new RuntimeException("读取pdf文档页数失败");
        }
    }

    /**
     * 获取PDF文件页数
     *
     * @param bytes 文件的字节码
     * @return Integer
     */
    public static Integer pdfPages(byte[] bytes) {
        try {
            PdfReader reader = new PdfReader(bytes);
            return reader.getNumberOfPages();
        } catch (IOException e) {
            throw new RuntimeException("获取 PDF 失败");
        }
    }

    /**
     * 移除PDF文件中的空白页
     *
     * @param source 原PDF文件
     * @return byte[] 移除空白页后的PDF文件
     * @author zhangkaiyong
     * @date 2021/11/03 19:13
     */
    public static byte[] removeBlankPage(byte[] source) {
        try {
            // step 1: create new reader
            PdfReader reader = new PdfReader(source);
            Document document = new Document(reader.getPageSizeWithRotation(1));
            // step 2: create a writer that listens to the document
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfCopy writer = new PdfCopy(document, baos);
            // step 3: open the document
            document.open();
            // step 4: write content which not blank to new file
            Rectangle rectangle = reader.getPageSize(1);
            float width = rectangle.getWidth();
            float height = rectangle.getHeight();
            for (int pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
                // read content
                Position postion = getContentPostion(reader, pageNum, width, height);
                if (postion.getContent().toString().trim().length() == 0) {
                    continue;
                }
                writer.addPage(writer.getImportedPage(reader, pageNum));
            }
            // step 5: close io
            document.close();
            writer.close();
            reader.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("移除 PDF 空白页失败");
        }
    }

    /**
     * 获取PDF文档信息及关键字位置
     *
     * @param bytes 文件的字节码
     * @param keys  搜索关键字
     * @return PdfInfo PDF文档信息
     */
    public static PdfInfo pdfInfo(byte[] bytes, String... keys) {
        try {
            PdfReader reader = new PdfReader(bytes);
            if (reader.getNumberOfPages() == 0) {
                return PdfInfo.builder().pages(0).build();
            }
            // 非空文件，获取文件页数、宽高
            Rectangle rectangle = reader.getPageSize(1);
            float width = rectangle.getWidth();
            float height = rectangle.getHeight();
            if (null == keys || keys.length == 0 || Arrays.stream(keys).allMatch(Objects::isNull)) {
                return PdfInfo.builder()
                        .pages(reader.getNumberOfPages())
                        .width(width)
                        .height(height).build();
            }

            // 获取文件关键字位置
            String[] keywords = Arrays.stream(keys).filter(Objects::nonNull).toArray(String[]::new);
            MultiValueMap<String, Keyword> keywordsMap = new LinkedMultiValueMap<>();
            for (int i = 0; i < reader.getNumberOfPages(); i++) {
                keywordsMap.addAll(findPositions(keywords, getContentPostion(reader, i + 1, width, height)));
            }
            return PdfInfo.builder()
                    .pages(reader.getNumberOfPages())
                    .width(width)
                    .height(height)
                    .keywords(keywords)
                    .keywordsMap(keywordsMap).build();
        } catch (IOException e) {
            throw new RuntimeException("读取pdf文档页数失败");
        }
    }

    /**
     * 获取指定页码文本的位置坐标集合
     */
    private static Position getContentPostion(PdfReader reader, int pageNum, float width, float height) throws IOException {
        // 创建监听
        CustRenderListener listener = new CustRenderListener(pageNum, width, height);
        // 解析文本，获取位置坐标
        PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
        processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNum),
                reader.getPageN(pageNum).getAsDict(PdfName.RESOURCES));
        // 返回结果
        return listener.getPosition();
    }

    /**
     * 查询关键字的位置
     */
    private static MultiValueMap<String, Keyword> findPositions(String[] keywords, Position position) {
        MultiValueMap<String, Keyword> keywordsMap = new LinkedMultiValueMap<>();
        String content = position.getContent().toString();
        List<float[]> points = position.getPoints();
        for (String keyword : keywords) {
            for (int pos = 0; pos < content.length(); ) {
                int positionIndex = content.indexOf(keyword, pos);
                if (positionIndex == -1) {
                    break;
                }
                float[] point = points.get(positionIndex);
                keywordsMap.add(keyword, Keyword.builder()
                        .keyword(keyword)
                        .pageNum(position.getPageNum())
                        .x(point[0])
                        .y(point[1])
                        .xRatio(Math.round(point[0] / position.getWidth() * 10000) / 10000f)
                        .yRatio(Math.round(point[1] / position.getHeight() * 10000) / 10000f)
                        .build());
                pos = positionIndex + 1;
            }
        }
        return keywordsMap;
    }

    /**
     * PDF文件信息实体类
     */
    @Data
    @Builder
    public static class PdfInfo {
        /**
         * 总页数
         */
        private Integer pages;
        /**
         * 文档宽度
         */
        private Float width;
        /**
         * 文档高度
         */
        private Float height;
        /**
         * 关键字列表
         */
        private String[] keywords;
        /**
         * 多值Map，key为关键字，value为关键字的位置(多个位置，可能页码不同，也可能位置不同)
         */
        private MultiValueMap<String, Keyword> keywordsMap;

        /**
         * 获取指定keys最后一个匹配到的关键字
         */
        public Keyword getLastKeyword(String... keys) {
            if (null == keywordsMap || keywordsMap.isEmpty()
                    || null == keys || keys.length == 0) {
                return null;
            }
            // 指定了关键字则以指定的为准，否则直接取遍历时第一个key
            List<Keyword> value = null;
            for (String k : keys) {
                if (null != (value = keywordsMap.get(k))) {
                    break;
                }
            }
            if (null == value || value.size() == 0) {
                return null;
            }
            return value.get(value.size() - 1);
        }
    }

    @Data
    @Builder
    public static class Keyword {
        /**
         * 关键字
         */
        private String keyword;
        /**
         * 关键字所在页码
         */
        private Integer pageNum;
        /**
         * 关键字X坐标，页面左上角为原点
         */
        private Float x;
        /**
         * 关键字Y坐标，页面左上角为原点
         */
        private Float y;
        /**
         * 关键字X坐标比例，页面左上角为原点
         */
        private Float xRatio;
        /**
         * 关键字Y坐标比例，页面左上角为原点
         */
        private Float yRatio;
    }

    /**
     * 每页文本中每个字符下标对应的坐标位置
     */
    @Data
    private static class Position {
        /**
         * 文档宽度
         */
        private float width;
        /**
         * 文档宽度
         */
        private float height;
        /**
         * 文档页码
         */
        private int pageNum;
        /**
         * 文本内容
         */
        private StringBuilder content = new StringBuilder();
        /**
         * 文本中点的位置
         */
        private List<float[]> points = Lists.newArrayList();
    }

    @Data
    private static class CustRenderListener implements RenderListener {
        /**
         * 当前页码
         */
        int pageNum;
        /**
         * 宽度
         */
        float width;
        /**
         * 高度
         */
        float height;
        /**
         * 文本中每个字下标对应的位置
         */
        private Position position = new Position();

        public CustRenderListener(int pageNum, float width, float height) {
            this.pageNum = pageNum;
            this.width = width;
            this.height = height;
        }

        @Override
        public void renderText(TextRenderInfo renderInfo) {
            List<TextRenderInfo> subs = renderInfo.getCharacterRenderInfos();
            for (TextRenderInfo subInfo : subs) {
                String word = subInfo.getText();
                if (word.length() > 1) {
                    word = word.substring(word.length() - 1);
                }
                Rectangle2D.Float rectangle = subInfo.getAscentLine().getBoundingRectange();
                // 位置坐标及追加文本内容。变换下坐标：默认是以左下为原点，变更为以左上为原点
                position.getPoints().add(new float[]{(float) rectangle.getX(), height - (float) rectangle.getY()});
                position.getContent().append(word);
            }
            position.setPageNum(pageNum);
            position.setWidth(width);
            position.setHeight(height);
        }

        @Override
        public void beginTextBlock() {
        }

        @Override
        public void endTextBlock() {
        }

        @Override
        public void renderImage(ImageRenderInfo renderInfo) {
        }
    }
}
