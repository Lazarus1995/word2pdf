package com.qu.word2pdf;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Description:
 * @author: qu
 * @date: 2021.06.19.17:22
 */
public class WordUtil {
    /**
     * 获取 Word 模板的两个操作对象 IXDocReport 和 IContext
     * @param url 模板相对于类路径的地址
     * @return 模板数据对象
     */
    public static ExportData createExportData(String url) {
        try {
            IXDocReport report = createReport(url);
            IContext context = report.createContext();
            return new ExportData(report, context);
        } catch (XDocReportException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    /**
     * 加载模板的方法，主要是指定模板的路径和选择渲染数据的模板
     * @param url 模板相对于类路径的地址
     * @return word 文档操作类
     */
    private static IXDocReport createReport(String url) {
        try (
                InputStream in = new FileInputStream(url);
        ) {
            IXDocReport ix = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);
            return ix;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
