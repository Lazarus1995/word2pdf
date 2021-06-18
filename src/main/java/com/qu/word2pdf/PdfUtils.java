package com.qu.word2pdf;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import fr.opensagres.xdocreport.itext.extension.font.ITextFontRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.awt.*;
import java.io.*;

public class PdfUtils {

    private static final String basePath = "zczhgl-web-server/zczhgl-web-common/src/main/resources/template/";
    private static final String fontPath = "zczhgl-web-server/zczhgl-web-common/src/main/resources/font/";

    public static void word2pdf() throws IOException, XDocReportException {
        InputStream in = new FileInputStream(
                basePath + "test1.docx");
        IXDocReport report = XDocReportRegistry
                .getRegistry()
                .loadReport(in, TemplateEngineKind.Freemarker);

        FieldsMetadata fieldsMetadata = report.createFieldsMetadata();
       fieldsMetadata.load("car",car.class);
//        fieldsMetadata.load("Project",Project.class,true);

//        Employee employee = new Employee("川A12345","qu","3","blue");
//        List<Project> projectList = new ArrayList<>();
 //       projectList.add(new Project("qu","17358540214"));
//        projectList.add((new Project("zy","1566347")));
        car car = new car();
        car.setEvidenceNumber("111111");
        car.setPlate("2222");
        IContext context = report.createContext();
        //Project project = new Project("ABC");
//        context.put("title", "车辆");
        context.put("car",car);
//        context.put("Project",projectList);

        OutputStream out=new FileOutputStream(new File(basePath+"test1.pdf"));

        Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF).subOptions(PdfOptions.create()
                .fontEncoding("UTF-8").fontProvider(new IFontProvider() {
                    @Override
                    public Font getFont(String familyName, String encoding, float size, int style, Color color) {
                        try {
                            BaseFont bfChinese = BaseFont.createFont(fontPath+"SimHei.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
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
//        report.process(context,out);
    }

    public static void main(String[] args) throws IOException, XDocReportException {
        word2pdf();
    }
}