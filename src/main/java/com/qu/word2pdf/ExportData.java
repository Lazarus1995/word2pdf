package com.qu.word2pdf;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import fr.opensagres.xdocreport.itext.extension.font.ITextFontRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * @Description:
 * @author: qu
 * @date: 2021.06.19.17:19
 */
public class ExportData {

    private IXDocReport report;
    private IContext context;

    /**
     * 构造方法
     * @param report
     * @param context
     */
    public ExportData(IXDocReport report, IContext context) {
        this.report = report;
        this.context = context;
    }

    /**
     * 设置普通数据，包括基础数据类型，数组，试题对象
     * 使用时，直接 ${key.k} 或者 [#list d as key]
     * @param key   健
     * @param value 值
     */
    public void setData(String key, Object value) {
        context.put(key, value);
    }

    /**
     * 设置表格数据，用来循环生成表格的 List 数据
     * 使用时，直接 ${key.k}
     * @param key   健
     * @param maps 集合
     */
    public void setTable(String key, List<SoMap> maps) {
        FieldsMetadata metadata = report.getFieldsMetadata();
        metadata = metadata == null ? new FieldsMetadata() : metadata;
        SoMap map = maps.get(0);
        for (String kk : map.keySet()) {
            metadata.addFieldAsList(key + "." + kk);
        }
        report.setFieldsMetadata(metadata);
        context.put(key, maps);
    }

    /**
     * 设置图片数据
     * 使用时 直接在书签出 key
     * @param key 健
     * @param url 图片地址
     */
    public void setImg(String key, String url) {
        FieldsMetadata metadata = report.getFieldsMetadata();
        metadata = metadata == null ? new FieldsMetadata() : metadata;
        metadata.addFieldAsImage(key);
        report.setFieldsMetadata(metadata);
        try (
                InputStream in = new FileInputStream(url);
        ) {
            IImageProvider img = new ByteArrayImageProvider(in);
            context.put(key, img);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * 获取文件流数据
     * @return 文件流数组
     */
    public void getByteArr() {
        try (
                OutputStream out=new FileOutputStream(new File("src/main/resources/template/test.pdf"));
        ) {
            Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF).subOptions(PdfOptions.create()
                    .fontEncoding("UTF-8").fontProvider(new IFontProvider() {
                        @Override
                        public Font getFont(String familyName, String encoding, float size, int style, Color color) {
                            try {
                                BaseFont bfChinese = BaseFont.createFont("src/main/resources/font/SimHei.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                                Font fontChinese = new Font(bfChinese, size, style, color);
                                if (familyName != null)
                                    fontChinese.setFamily(familyName);
                                return fontChinese;
                            } catch (Throwable e) {
                                e.printStackTrace();
                                // An error occurs, use the default font provider.
                                return ITextFontRegistry.getRegistry().getFont(familyName, encoding, size, style, color);
                            }
                        }
                    }));
            report.convert(context, options, out);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }
}
