package com.qu.word2pdf;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ClassPathImageProvider;
import fr.opensagres.xdocreport.document.images.FileImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import fr.opensagres.xdocreport.itext.extension.font.ITextFontRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: qu
 * @date: 2021.06.19.17:44
 */
public class Utils {

    private static final String basePath = "src/main/resources/template/";
    private static final String fontPath = "src/main/resources/font/";

    public static void main(String[] args) throws IOException, XDocReportException {
        try {
            // 1) Load Docx file by filling Freemarker template engine and cache
            // it to the registry
            InputStream in =
                    new FileInputStream(basePath + "DocxProjectWithFreemarkerAndImageList.docx");
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);

            // 2) Create fields metadata to manage lazy loop ([#list Freemarker) for table row.
            FieldsMetadata metadata = report.createFieldsMetadata();
            // Old API
            /*
             * metadata.addFieldAsList("developers.name"); metadata.addFieldAsList("developers.lastName");
             * metadata.addFieldAsList("developers.mail"); metadata.addFieldAsList("developers.photo");
             */
            // NEW API
            metadata.load("developers", DeveloperWithImage.class, true);

            // image
            metadata.addFieldAsImage("logo");
            // the following code is managed with @FieldMetadata( images = { @ImageMetadata( name = "photo" ) } )
            // in the DeveloperWithImage.
            // metadata.addFieldAsImage("photo", "developers.photo");

            // 3) Create context Java model
            IContext context = report.createContext();
            Project project = new Project("XDocReport");
            project.setURL("http://code.google.com/p/xdocreport/");
            context.put("project", project);
            IImageProvider logo = new FileImageProvider(new File(basePath+"logo.png"));
            context.put("logo", logo);

            // Register developers list
            List<DeveloperWithImage> developers = new ArrayList<DeveloperWithImage>();
            developers.add(new DeveloperWithImage(
                    "ZERR",
                    "Angelo",
                    "angelo.zerr@gmail.com",
                    new FileImageProvider(new File(basePath+"AngeloZERR.jpg"))));
            developers.add(new DeveloperWithImage(
                    "Leclercq",
                    "Pascal",
                    "pascal.leclercq@gmail.com",
                    new FileImageProvider(new File(basePath+"PascalLeclercq.jpg"))));
            context.put("developers", developers);

            // 4) Generate report by merging Java model with the Docx
            OutputStream out=new FileOutputStream(new File(basePath+"test.pdf"));

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

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XDocReportException e) {
            e.printStackTrace();
        }
    }
}
