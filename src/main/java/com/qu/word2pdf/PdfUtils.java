package com.qu.word2pdf;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.FileImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import fr.opensagres.xdocreport.itext.extension.font.ITextFontRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

import java.awt.*;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfUtils {

    private static final String basePath = "src/main/resources/template/";
    private static final String fontPath = "src/main/resources/font/";

    public static void word2pdf() throws IOException, XDocReportException {
        InputStream in = new FileInputStream(basePath + "test1.docx");
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);


        FieldsMetadata fieldsMetadata = report.createFieldsMetadata();
        fieldsMetadata.load("car",Car.class);
        fieldsMetadata.load("image",CarImage.class,true);

        List<CarImage> carImageList = new ArrayList<>();
        carImageList.add(new CarImage("名称1",new FileImageProvider(new File(basePath+"AngeloZERR.jpg"))));
        carImageList.add(new CarImage("名称2",new FileImageProvider(new File(basePath+"PascalLeclercq.jpg"))));

        IContext context = report.createContext();
        context.put("car",new Car());
        context.put("image",carImageList);


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
    }

    public static void main(String[] args) throws IOException, XDocReportException {
        word2pdf();
    }
}