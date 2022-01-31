package hesabyar.report_generator;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.TabStop;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.interfaces.PdfRunDirection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hesabyar.report_generator.persiandatepicker.util.PersianCalendar;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

import static java.lang.Math.abs;

/**
 * Created by Mahmood_M on 26/01/2018.
 */

public class PDFCreator {

    Context mContext;

    final int CELL_PADDING_TOP = 3;
    final int CELL_PADDING_BUTTOM = 8;
    final int CELL_PADDING_LEFT = 0;
    final int CELL_PADDING_RIGHT = 0;

    long Hesab_Sum_Bed, Hesab_Sum_Bes, Hesab_Sum_Mandeh;
    Double Hesab_Sum_Quantity, Hesab_Sum_Weight;
    String Hesab_Sum_Status;

    long Moein_Sum_Bed, Moein_Sum_Bes;

    public static final Rectangle[] MOEIN_COLUMNS = {
            new Rectangle(300, 30, 590, 832),
            new Rectangle(5, 30, 295, 832)
    };

    public static final Rectangle[] MOEIN_COLUMNS_FIRST = {
            new Rectangle(300, 30, 590, 768),
            new Rectangle(5, 30, 295, 768)
    };

    public static final Rectangle[] BARNAMEH_COLUMNS = {
            new Rectangle(5, 280, 155, 768),
            new Rectangle(160, 280, 590, 768),

            new Rectangle(5, 300, 155, 768),
            new Rectangle(160, 300, 590, 768)
    };

    Document hesabDoc, moeinDoc, barnamehDoc;

    List<HesabListItem> hesabList;
    List<MoeinListItem> moeinList;
    List<BarnamehListItem> barnamehList;

    RealmConfiguration realmConfig;
    Realm realm;

    public PDFCreator (Context ctx){
        mContext = ctx;
    }

    public String createHesabPDF(String AccName, String AccNo, String DateFrom, String DateTo, List<HesabListItem> list, String Name, String Address, String Phone){
        String StoragePath = Environment.getExternalStorageDirectory().getPath();
        String FilePath = StoragePath + "/Hesabyar_SuratHesab.pdf";
        try {
            File file = new File(FilePath);
            if(file.exists()) {
//                int i = 0;
//                while (file.exists()) {
//                    i++;
//                    FilePath = StoragePath + "/SuratHesab_" + Integer.toString(i) + ".pdf";
//                    file = new File(FilePath);
//                }
                file.delete();
            }
            file = new File(FilePath + "_Temp");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();

            hesabList = new ArrayList<>(list);

            hesabDoc = new Document(PageSize.A4, 5, 5, 10, 30);
            PdfWriter writer = PdfWriter.getInstance(hesabDoc, new FileOutputStream(file));
//            writer.setPageEvent(new FooterEvent());

            hesabDoc.open();

            BaseFont bf_titr = BaseFont.createFont("assets/fonts/titr.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font_titr = new Font(bf_titr, 18);

            BaseFont bf = BaseFont.createFont("assets/fonts/sahel_fd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(bf, 10);

            BaseColor cellColor = new BaseColor(240, 240, 240);

            PdfPTable table = new PdfPTable(11);
            table.setTotalWidth(PageSize.A4.getWidth() - 10);
            table.setLockedWidth(true);

            table.setTotalWidth(new float[]{25, 70, 70, 70, 40, 35, 30, 100, 55, 35, 55});

            PdfPCell headerCell;

            headerCell = new PdfPCell(new Paragraph(Name, font_titr));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setColspan(11);
            headerCell.setPaddingBottom(CELL_PADDING_TOP * 4);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(headerCell);

            font_titr.setSize(12);
            headerCell = new PdfPCell(new Paragraph("صورتحساب جناب : " + AccName + "                    شماره : " + AccNo, font_titr));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setColspan(11);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(headerCell);

            // Date - Begin
            PersianCalendar PC = new PersianCalendar();

            headerCell = new PdfPCell(new Paragraph( "تاریخ چاپ گزارش :  " + "\u200F" + PC.getPersianShortDate(), font));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerCell.setColspan(4);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
            headerCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph( "از تاریخ :   " + "\u200F" + DateFrom + "   تا تاریخ :   " + "\u200F" + DateTo, font));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setColspan(7);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
            headerCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(headerCell);
            // Date - End

            Font font_white = new Font(bf, 10, Font.NORMAL, BaseColor.WHITE);
            // Header - Begin
            headerCell = new PdfPCell(new Paragraph("وض", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("مانده", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("بستانکار", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("بدهکار", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("فی", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("وزن", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("تعداد", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("شرح", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("تاریخ", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("رسید", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("فاکتور", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            table.addCell(headerCell);
            // Header - End

            Hesab_Sum_Quantity = 0.0;
            Hesab_Sum_Weight = 0.0;
            Hesab_Sum_Bed = 0;
            Hesab_Sum_Bes = 0;
            Hesab_Sum_Mandeh = 0;

            DecimalFormat df = new DecimalFormat("###,###.##");

            for(int i = 0; i < hesabList.size(); i++){
                HesabListItem item = hesabList.get(i);
                Hesab_Sum_Quantity += item.getQuantity();
                Hesab_Sum_Weight += item.getWeight();
                Hesab_Sum_Bed += item.getBed();
                Hesab_Sum_Bes += item.getBes();
                if(i == hesabList.size() - 1){
                    Hesab_Sum_Mandeh = item.getMandeh();
                }

                int alignment = 0;

                for(int j = 1; j <= 11; j++){
                    String text;
                    switch (j){
                        case 11 : {
                            text = item.getInd();
                            alignment = 0;
                        }
                            break;
                        case 10 : {
                            text = Integer.toString(item.getResidNo());
                            alignment = 1;
                        }
                            break;
                        case 9 : {
                            text = item.getTarikh();
                            alignment = 0;
                        }
                            break;
                        case 8 : {
                            text = item.getSharh();
                            alignment = 1;
                        }
                            break;
                        case 7 : {
                            text = df.format(item.getQuantity());
                            alignment = 1;
                        }
                            break;
                        case 6 : {
                            text = df.format(item.getWeight());
                            alignment = 1;
                        }
                            break;
                        case 5 : {
                            text = String.format(new Locale("fa", "IR"),"%,d", item.getFi());
                            alignment = 1;
                        }
                            break;
                        case 4 : {
                            text = String.format(new Locale("fa", "IR"),"%,d", item.getBed());
                            alignment = 1;
                        }
                            break;
                        case 3 : {
                            text = String.format(new Locale("fa", "IR"),"%,d", item.getBes());
                            alignment = 1;
                        }
                            break;
                        case 2 : {
                            text = String.format(new Locale("fa", "IR"),"%,d", item.getMandeh());
                            alignment = 1;
                        }
                            break;
                        case 1 : {
                            switch(item.getStatus()){
                                case 2 : text = "بد";
                                    break;
                                case 3 : text = "بس";
                                    break;
                                default : text = "-";
                            }
                            if(i == list.size() - 1){
                                Hesab_Sum_Status = text;
                            }
                            alignment = 0;
                        } break;
                        default : text = "";
                    }

                    PdfPCell cell = new PdfPCell(new Phrase(text, font));
                    if(i % 2 == 1){
                        cell.setBackgroundColor(cellColor);
                    }else{
                        cell.setBackgroundColor(BaseColor.WHITE);
                    }
                    cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    switch(alignment){
                        case -1 : cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        break;
                        case 1  : cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        break;
                        default : cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                    cell.setLeading(0, 1.58f);

                    table.addCell(cell);
                }
            }

//            Hesab_Sum_Mandeh = abs(Hesab_Sum_Bed - Hesab_Sum_Bes);
//            if(Hesab_Sum_Bed > Hesab_Sum_Bes){
//                Hesab_Sum_Status = "بد";
//            }else if(Hesab_Sum_Bed < Hesab_Sum_Bes){
//                Hesab_Sum_Status = "بس";
//            }else{
//                Hesab_Sum_Status = "-";
//            }

            PdfPCell footerCell;
            footerCell = new PdfPCell(new Paragraph(Hesab_Sum_Status, font));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            table.addCell(footerCell);

            Font font_small = new Font(bf, 8);
            footerCell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Hesab_Sum_Mandeh), font));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            table.addCell(footerCell);

            footerCell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Hesab_Sum_Bes), font_small));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            table.addCell(footerCell);

            footerCell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"),"%,d", Hesab_Sum_Bed), font_small));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            table.addCell(footerCell);

            footerCell = new PdfPCell(new Paragraph("-", font));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            table.addCell(footerCell);

            footerCell = new PdfPCell(new Paragraph(df.format(Hesab_Sum_Weight), font_small));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            table.addCell(footerCell);

            footerCell = new PdfPCell(new Paragraph(df.format(Hesab_Sum_Quantity), font_small));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            footerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            table.addCell(footerCell);

            footerCell = new PdfPCell(new Paragraph(" مجموع : ", font));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            footerCell.setPaddingLeft(5);
            footerCell.setColspan(4);
            footerCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(footerCell);

//            font_titr.setSize(11);
            footerCell = new PdfPCell(new Paragraph("اشتباه از طرفین قابل برگشت است. این گزارش بدون مهر و امضا فاقد اعتبار است.", font));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            footerCell.setPaddingTop(CELL_PADDING_TOP * 4);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
            footerCell.setColspan(11);
            footerCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(footerCell);

            font_titr.setSize(12);
            footerCell = new PdfPCell(new Paragraph(Address, font_titr));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            footerCell.setColspan(11);
            footerCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(footerCell);

//            ColumnText mainCT = new ColumnText(writer.getDirectContent());
//            mainCT.addElement(table);
//
//            ColumnText markCT = new ColumnText(writer.getDirectContent());
//            PdfPTable markTable = new PdfPTable(1);
//            markTable.setTotalWidth(5);
//
//            PdfPCell markCell = new PdfPCell(new Phrase("نرم افزار حسابیار : 09121612664", font_small));
//            markCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
//            markCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            markCell.setPaddingTop(CELL_PADDING_BUTTOM);
//            markCell.setColspan(11);
//            markCell.setBorder(Rectangle.NO_BORDER);
//
//            markTable.addCell(markCell);
//
//            do {
//                hesabDoc.newPage();
//
//                markCT.addElement(markTable);
//                markCT.setSimpleColumn(HESAB_COLUMNS[0]);
//                markCT.go();
//
//                mainCT.setSimpleColumn(HESAB_COLUMNS[1]);
//            } while (ColumnText.hasMoreText(mainCT.go()));
//            hesabDoc.close();

            hesabDoc.add(table);
            hesabDoc.close();

            PdfReader reader = new PdfReader(FilePath + "_Temp");
            int n = reader.getNumberOfPages();
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(FilePath));
            PdfContentByte cb;
            Phrase footer = new Phrase("نرم افزار حسابیار", font);
            for (int i = 0; i < n; ) {
                cb = stamper.getOverContent(++i);
//            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.getPageSize().getWidth() - 20, 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("----------------------------------------------------------------------------" +
                                "----------------------------------------------------------------------------", font),
                        (hesabDoc.right() - hesabDoc.left()) / 2,
                        hesabDoc.bottom() - 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        footer,
                        hesabDoc.left() + 120,
                        hesabDoc.bottom() - 20, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase(String.format("صفحه %s از %s ", i, n), font),
                        hesabDoc.right() - 120,
                        hesabDoc.bottom() - 20, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
            }
            stamper.close();
            reader.close();

        }catch(IOException fileE){
            FilePath = "";
            fileE.printStackTrace();
        }catch(DocumentException docE){
            FilePath = "";
            docE.printStackTrace();
        }finally {
            if(hesabDoc != null){
                hesabDoc.close();
            }

            return FilePath;
        }
    }

    public String createMoeinPDF(String AccNo1, String AccNo2, List<MoeinListItem> list, String Name, String Address, String Phone){
        String StoragePath = Environment.getExternalStorageDirectory().getPath();
        String FilePath = StoragePath + "/Hesabyar_Moein.pdf";
        try {
            File file = new File(FilePath);
            if(file.exists()) {
//                int i = 0;
//                while (file.exists()) {
//                    i++;
//                    FilePath = StoragePath + "/SuratHesab_" + Integer.toString(i) + ".pdf";
//                    file = new File(FilePath);
//                }
                file.delete();
            }
            file = new File(FilePath + "_Temp");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();

            moeinList = new ArrayList<>(list);

            moeinDoc = new Document(PageSize.A4, 5, 5, 10, 30);
            PdfWriter writer = PdfWriter.getInstance(moeinDoc, new FileOutputStream(file));
            moeinDoc.open();

            BaseFont bf_titr = BaseFont.createFont("assets/fonts/titr.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font_titr = new Font(bf_titr, 18);

            Font font_titr_small = new Font(bf_titr, 11);

            BaseFont bf = BaseFont.createFont("assets/fonts/sahel_fd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(bf, 10);

            BaseColor cellColor = new BaseColor(240, 240, 240);

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setTotalWidth(PageSize.A4.getWidth() - 10);
            headerTable.setLockedWidth(true);
            headerTable.setTotalWidth(new float[]{290, 290});

            PdfPCell headerCell;

            headerCell = new PdfPCell(new Paragraph(Name, font_titr));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_TOP * 4);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
            headerCell.setColspan(2);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(headerCell);

            // Date - Begin
            PersianCalendar PC = new PersianCalendar();

            headerCell = new PdfPCell(new Paragraph( "تاریخ چاپ گزارش :  " + "\u200F" + PC.getPersianShortDate(), font));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(headerCell);
            // Date - End

            headerCell = new PdfPCell(new Paragraph( "مانده معین از شماره  " + AccNo1 + "  تا شماره  " + AccNo2, font_titr_small));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(headerCell);

            moeinDoc.add(headerTable);

            ColumnText mainCT = new ColumnText(writer.getDirectContent());

            PdfPTable mainTable = new PdfPTable(5);
            mainTable.setTotalWidth(290);
            mainTable.setLockedWidth(true);

            mainTable.setTotalWidth(new float[]{55, 25, 70, 100, 40});

            Font font_white = new Font(bf, 10, Font.NORMAL, BaseColor.WHITE);
            // Header - Begin
            headerCell = new PdfPCell(new Paragraph("تاریخ", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            headerCell.setLeading(0, 1.42f);
            headerCell.setFixedHeight(26);
            mainTable.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("وض", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            headerCell.setLeading(0, 1.42f);
            headerCell.setFixedHeight(26);
            mainTable.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("مبلغ", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            headerCell.setLeading(0, 1.42f);
            headerCell.setFixedHeight(26);
            mainTable.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("نام حساب", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            headerCell.setLeading(0, 1.42f);
            headerCell.setFixedHeight(26);
            mainTable.addCell(headerCell);

            headerCell = new PdfPCell(new Paragraph("ش ح", font_white));
            headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            headerCell.setBackgroundColor(BaseColor.GRAY);
            headerCell.setLeading(0, 1.42f);
            headerCell.setFixedHeight(26);
            mainTable.addCell(headerCell);
            // Header - End

            Moein_Sum_Bed = 0;
            Moein_Sum_Bes = 0;
            for(int i = 0; i < moeinList.size(); i++){
                MoeinListItem item = moeinList.get(i);
                switch (item.getStatus()){
                    case 2 : Moein_Sum_Bed += item.getPrice();
                        break;
                    case 3 : Moein_Sum_Bes += item.getPrice();
                        break;
                    default : {}
                }
                for(int j = 1; j <= 5; j++){
                    String text;
                    int alignment = 0;
                    boolean noWrap = false;
                    switch (j){
                        case 5 : {
                            text = Integer.toString(item.getAccNumber());
                            alignment = 0;
                        }
                            break;
                        case 4 : {
                            text = item.getAccName();
                            alignment = 1;
                            noWrap = true;
                        }
                            break;
                        case 3 : {
                            text = String.format(new Locale("fa", "IR"), "%,d", item.getPrice());
                            alignment = 1;
                        }
                            break;
                        case 2 : {
                            switch(item.getStatus()){
                                case 2 : text = "بد";
                                    break;
                                case 3 : text = "بس";
                                    break;
                                default : text = "-";
                            }
                            alignment = 0;
                        } break;
                        case 1 : {
                            text = item.getHDate();
                            alignment = 0;
                        }
                            break;
                        default : text = "";
                    }

                    PdfPCell cell = new PdfPCell(new Phrase(text, font));
                    if(i % 2 == 1){
                        cell.setBackgroundColor(cellColor);
                    }else{
                        cell.setBackgroundColor(BaseColor.WHITE);
                    }
                    cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    switch(alignment){
                        case -1 : cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            break;
                        case 1  : cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            break;
                        default : cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                    cell.setLeading(0, 1.42f);
                    cell.setFixedHeight(26);
//                    cell.setNoWrap(noWrap);

                    mainTable.addCell(cell);
                }
            }

            mainCT.addElement(mainTable);

            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setTotalWidth(290);

            PdfPCell footerCell;

            Font font_footer = new Font(bf, 14, Font.BOLD, BaseColor.BLACK);

            String value = String.format(new Locale("fa", "IR"), "%,d", Moein_Sum_Bed);
            footerCell = new PdfPCell(new Paragraph("مجموع بدهکاران : " + value, font_footer));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            footerCell.setPaddingTop(CELL_PADDING_TOP * 3);
            footerCell.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell);

            value = String.format(new Locale("fa", "IR"), "%,d", Moein_Sum_Bes);
            footerCell = new PdfPCell(new Paragraph("مجموع بستانکاران : " + value, font_footer));
            footerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
            footerCell.setBorder(Rectangle.NO_BORDER);
            footerTable.addCell(footerCell);

            mainCT.addElement(footerTable);

            int column = 0;
            do {
                if (column == 2) {
                    moeinDoc.newPage();
                    column = 0;
                }
                if(writer.getPageNumber() == 1) {
                    mainCT.setSimpleColumn(MOEIN_COLUMNS_FIRST[column++]);
                }else{
                    mainCT.setSimpleColumn(MOEIN_COLUMNS[column++]);
                }
            } while (ColumnText.hasMoreText(mainCT.go()));

            moeinDoc.close();

            PdfReader reader = new PdfReader(FilePath + "_Temp");
            int n = reader.getNumberOfPages();
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(FilePath));
            PdfContentByte cb;
            Phrase footer = new Phrase("نرم افزار حسابیار", font);
            for (int i = 0; i < n; ) {
                cb = stamper.getOverContent(++i);
//            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.getPageSize().getWidth() - 20, 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("----------------------------------------------------------------------------" +
                                          "----------------------------------------------------------------------------", font),
                        (moeinDoc.right() - moeinDoc.left()) / 2,
                        moeinDoc.bottom() - 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        footer,
                        moeinDoc.left() + 120,
                        moeinDoc.bottom() - 20, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase(String.format("صفحه %s از %s ", i, n), font),
                        moeinDoc.right() - 120,
                        moeinDoc.bottom() - 20, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
            }
            stamper.close();
            reader.close();

        }catch(IOException fileE){
            FilePath = "";
            fileE.printStackTrace();
        }catch(DocumentException docE){
            FilePath = "";
            docE.printStackTrace();
        }finally {
            if(moeinDoc != null){
                moeinDoc.close();
            }

            return FilePath;
        }
    }

    public String createBarnamehPDF(String AccName, String AccNo, String DateFrom, String DateTo, List<BarnamehListItem> list, String Name, String Address, String Phone){
        String StoragePath = Environment.getExternalStorageDirectory().getPath();
        String FilePath = StoragePath + "/Hesabyar_Barnameh.pdf";
        try{
            File file = new File(FilePath);
            if (file.exists()) {
//                int i = 0;
//                while (file.exists()) {
//                    i++;
//                    FilePath = StoragePath + "/SuratHesab_" + Integer.toString(i) + ".pdf";
//                    file = new File(FilePath);
//                }
                file.delete();
            }
            file = new File(FilePath + "_Temp");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            barnamehList = new ArrayList<>(list);

            barnamehDoc = new Document(PageSize.A4, 5, 5, 10, 30);
            PdfWriter writer = PdfWriter.getInstance(barnamehDoc, new FileOutputStream(file));
//            writer.setPageEvent(new FooterEvent());

            barnamehDoc.open();

            PdfContentByte canvas = writer.getDirectContent();

            BaseFont bf_titr = BaseFont.createFont("assets/fonts/titr.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font_titr = new Font(bf_titr, 18);

            Font font_titr_small = new Font(bf_titr, 12);

            BaseFont bf = BaseFont.createFont("assets/fonts/sahel_fd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(bf, 10);

            Font font_bold = new Font(bf, 12, Font.BOLD);

            BaseColor cellColor = new BaseColor(240, 240, 240);

            realmConfig = new RealmConfiguration.Builder()
                    .name("info.realm")
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(realmConfig);

            DecimalFormat df = new DecimalFormat("###,###.##");

            for (int i = 0; i < barnamehList.size(); i++) {
                if (i > 0) {
                    barnamehDoc.newPage();
                }

                BarnamehListItem item = barnamehList.get(i);

                int LadingNo = item.getLadingNo();
                String Tarikh = item.getTarikh();
                int ResidNo = item.getResidNo();
                String CarNo = item.getCarNo();
                double InCount = item.getInCount();
                double InWeight = item.getInWeight();
                double Baskul1 = item.getBaskul1();
                double Baskul2 = item.getBaskul2();
                long Sood = item.getSood();
                long Safi = item.getSafi();
                long Keraye = item.getKeraye();
                long Takhlie = item.getTakhlie();
                long Peybari = item.getPeybari();
                long Dasti = item.getDasti();
                long Motafarregheh = item.getMotafarregheh();
                String Note = item.getNote();

                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setTotalWidth(PageSize.A4.getWidth() - 10);
                headerTable.setLockedWidth(true);

                headerTable.setTotalWidth(new float[]{200, 385});

                PdfPCell headerCell;

                headerCell = new PdfPCell(new Paragraph(Name, font_titr));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setColspan(2);
                headerCell.setPaddingBottom(CELL_PADDING_TOP * 4);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
                headerCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(Address + "-" + Phone, font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setColspan(2);
                headerCell.setPaddingBottom(CELL_PADDING_TOP * 4);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
                headerCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(headerCell);

                // Date - Begin
                PersianCalendar PC = new PersianCalendar();
                String S[] = Tarikh.split("/");
                PC.setPersianDate(Integer.parseInt(S[0]), Integer.parseInt(S[1]), Integer.parseInt(S[2]));

                headerCell = new PdfPCell(new Paragraph("تاریخ : " + PC.getPersianWeekDayName() + " - " + "\u200F" + PC.getPersianShortDate(), font_titr_small));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                headerCell.setColspan(1);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
                headerCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(headerCell);
                // Date - End

                headerCell = new PdfPCell(new Paragraph("بارنامه جناب " + AccName + " ( " + AccNo + " )", font_titr_small));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                headerCell.setColspan(1);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
                headerCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(headerCell);

//                if (!(DateFrom.equals("-") || DateTo.equals("-"))) {
//                    headerCell = new PdfPCell(new Paragraph("( از تاریخ : " + "\u200F" + DateFrom + " تا تاریخ : " + "\u200F" + DateTo + "\u200F" + " )", font));
//                    headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
//                    headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                    headerCell.setColspan(2);
//                    headerCell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
//                    headerCell.setBorder(Rectangle.NO_BORDER);
//                    headerTable.addCell(headerCell);
//                }

                barnamehDoc.add(headerTable);

                // Header - Begin
                headerTable = new PdfPTable(9);
                headerTable.setTotalWidth(PageSize.A4.getWidth() - 10);
                headerTable.setLockedWidth(true);

                headerTable.setTotalWidth(new float[]{65, 65, 75, 75, 70, 70, 65, 70, 30});

                Font font_white = new Font(bf, 10, Font.NORMAL, BaseColor.WHITE);

                headerCell = new PdfPCell(new Paragraph("باسکول مقصد", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("باسکول مبدا", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("شماره رسید", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("وزن وارده", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("تعداد وارده", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("صافی", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("شماره ماشین", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("شماره بارنامه", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph("#", font_white));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(df.format(Baskul2), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(df.format(Baskul1), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(Integer.toString(ResidNo), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(df.format(InWeight), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(df.format(InCount), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Safi), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(CarNo, font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(Integer.toString(LadingNo), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell(new Paragraph(Integer.toString(i + 1), font));
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerTable.addCell(headerCell);

                headerCell = new PdfPCell();
                headerCell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPaddingBottom(CELL_PADDING_BUTTOM);
                headerCell.setColspan(9);
                headerCell.setBorder(Rectangle.NO_BORDER);
                headerCell.setFixedHeight(10);
                headerTable.addCell(headerCell);

                barnamehDoc.add(headerTable);
                // Header - End

                PdfPTable ajnasTable = new PdfPTable(9);
                ajnasTable.setTotalWidth(585);
                ajnasTable.setLockedWidth(true);

                ajnasTable.setTotalWidth(new float[]{80, 70, 5, 80, 80, 60, 60, 110, 40});

//                    cell = new PdfPCell(new Paragraph("اجناس :", font_titr_small));
//                    cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
//                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//                    cell.setPaddingBottom(CELL_PADDING_BUTTOM);
//                    cell.setColspan(6);
//                    cell.setBorder(Rectangle.NO_BORDER);
//                    ajnasTable.addCell(cell);

                PdfPCell cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Sood), font));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("حق العمل", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBackgroundColor(BaseColor.GRAY);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBorder(Rectangle.NO_BORDER);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("قیمت کل", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBackgroundColor(BaseColor.GRAY);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("فی فروش", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBackgroundColor(BaseColor.GRAY);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("وزن", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBackgroundColor(BaseColor.GRAY);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("تعداد", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBackgroundColor(BaseColor.GRAY);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("نوع جنس", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBackgroundColor(BaseColor.GRAY);
                ajnasTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("ردیف", font_white));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setBackgroundColor(BaseColor.GRAY);
                ajnasTable.addCell(cell);

                RealmResults<ForooshRecordObject> forooshList = realm.where(ForooshRecordObject.class).equalTo("LadingNo", LadingNo).findAll().sort("Fi", Sort.DESCENDING);
                int listSize = forooshList.size();
                long JSumFi = 0, JFi = 0;
                double JWeight = 0, JCount = 0;

                int loopCount;
                if (listSize < 6) {
                    loopCount = 6;
                } else {
                    loopCount = listSize + 1;
                }

                for (int j = 0; j < loopCount; j++) {
                    switch (j) {
                        case 0: {
                            cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Keraye), font));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("کرایه بار", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBackgroundColor(BaseColor.GRAY);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBorder(Rectangle.NO_BORDER);
                            ajnasTable.addCell(cell);
                        }
                        break;
                        case 1: {
                            cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Takhlie), font));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("تخلیه", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBackgroundColor(BaseColor.GRAY);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBorder(Rectangle.NO_BORDER);
                            ajnasTable.addCell(cell);
                        }
                        break;
                        case 2: {
                            cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Peybari), font));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("پی باری", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBackgroundColor(BaseColor.GRAY);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBorder(Rectangle.NO_BORDER);
                            ajnasTable.addCell(cell);
                        }
                        break;
                        case 3: {
                            cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Dasti), font));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("دستی", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBackgroundColor(BaseColor.GRAY);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBorder(Rectangle.NO_BORDER);
                            ajnasTable.addCell(cell);
                        }
                        break;
                        case 4: {
                            cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Motafarregheh), font));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("متفرقه", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBackgroundColor(BaseColor.GRAY);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBorder(Rectangle.NO_BORDER);
                            ajnasTable.addCell(cell);
                        }
                        break;
                        case 5: {
                            long Sum = Sood + Keraye + Takhlie + Peybari + Dasti + Motafarregheh;

                            cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", Sum), font));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("جمع مخارج", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBackgroundColor(BaseColor.DARK_GRAY);
                            ajnasTable.addCell(cell);

                            cell = new PdfPCell(new Paragraph("", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBorder(Rectangle.NO_BORDER);
                            ajnasTable.addCell(cell);
                        }
                        break;
                        default: {
                            cell = new PdfPCell(new Paragraph("", font_white));
                            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                            cell.setBorder(Rectangle.NO_BORDER);
                            cell.setColspan(3);
                            ajnasTable.addCell(cell);
                        }
                    }

                    if (j < listSize) {
                        ForooshRecordObject foroosh = forooshList.get(j);

                        cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", foroosh.getSumFi()), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        JSumFi = JSumFi + foroosh.getSumFi();

                        cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", foroosh.getFi()), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        JFi = JFi + foroosh.getFi();

                        cell = new PdfPCell(new Paragraph(df.format(foroosh.getWeight()), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        JWeight = JWeight + foroosh.getWeight();

                        cell = new PdfPCell(new Paragraph(df.format(foroosh.getQuantity()), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        JCount = JCount + foroosh.getQuantity();

                        cell = new PdfPCell(new Paragraph(foroosh.getItemName(), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        cell = new PdfPCell(new Paragraph(Integer.toString(j + 1), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                    } else if (j == listSize) {
                        cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", JSumFi), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        cell = new PdfPCell(new Paragraph(String.format(new Locale("fa", "IR"), "%,d", JFi), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        cell = new PdfPCell(new Paragraph(df.format(JWeight), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        cell = new PdfPCell(new Paragraph(df.format(JCount), font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        ajnasTable.addCell(cell);

                        cell = new PdfPCell(new Paragraph("جمع : ", font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        cell.setColspan(2);
                        ajnasTable.addCell(cell);
                    } else {
                        cell = new PdfPCell(new Paragraph("", font));
                        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setColspan(6);
                        ajnasTable.addCell(cell);
                    }
                }
                    // Ajnas - End

                barnamehDoc.add(ajnasTable);

                PdfPTable footerTable = new PdfPTable(1);
                footerTable.setTotalWidth(PageSize.A4.getWidth() - 10);
                footerTable.setLockedWidth(true);

                String SafiString = String.format(new Locale("fa", "IR"), "%,d", Safi);


                if (Safi >= 0) {
                    cell = new PdfPCell(new Paragraph("مبلغ صافی  " + SafiString + "  به اینجانب رسیده است که در محاسبه محسوب گردد", font_bold));
                } else {
                    cell = new PdfPCell(new Paragraph("مبلغ صافی  " + SafiString + "  به اینجانب رسیده است که در محاسبه محسوب گردد، الباقی بدهکار است", font_bold));
                }

                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingTop(CELL_PADDING_TOP * 4);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
                cell.setColspan(6);
                cell.setBorder(Rectangle.NO_BORDER);
                footerTable.addCell(cell);

                cell = new PdfPCell(new Paragraph("این بارنامه بدون مهر و امضا فاقد اعتبار می باشد، اشتباه از طرفین قابل برگشت است", font));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPaddingTop(CELL_PADDING_TOP);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
                cell.setColspan(6);
                cell.setBorder(Rectangle.NO_BORDER);
                footerTable.addCell(cell);

                if(!Note.equals("-")){
                    cell = new PdfPCell(new Paragraph(Note, font));
                    cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPaddingTop(CELL_PADDING_TOP);
                    cell.setPaddingBottom(CELL_PADDING_BUTTOM * 2);
                    cell.setColspan(6);
                    cell.setBorder(Rectangle.NO_BORDER);
                    footerTable.addCell(cell);
                }

                cell = new PdfPCell(new Paragraph("مهر و امضا", font));
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPaddingTop(CELL_PADDING_TOP * 5);
                cell.setPaddingBottom(CELL_PADDING_BUTTOM);
                cell.setColspan(6);
                cell.setBorder(Rectangle.NO_BORDER);
                footerTable.addCell(cell);

                barnamehDoc.add(footerTable);
            }


            barnamehDoc.close();

            PdfReader reader = new PdfReader(FilePath + "_Temp");
            int n = reader.getNumberOfPages();
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(FilePath));
            PdfContentByte cb;
            Phrase footer = new Phrase("نرم افزار حسابیار", font);
            for (int i = 0; i < n; ) {
                cb = stamper.getOverContent(++i);
//            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.getPageSize().getWidth() - 20, 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("----------------------------------------------------------------------------" +
                                "----------------------------------------------------------------------------", font),
                        (barnamehDoc.right() - barnamehDoc.left()) / 2,
                        barnamehDoc.bottom() - 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        footer,
                        barnamehDoc.left() + 120,
                        barnamehDoc.bottom() - 20, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase(String.format("صفحه %s از %s ", i, n), font),
                        barnamehDoc.right() - 120,
                        barnamehDoc.bottom() - 20, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
            }
            stamper.close();
            reader.close();

        }catch(IOException fileE){
            FilePath = "";
            fileE.printStackTrace();
        }catch(DocumentException docE) {
            FilePath = "";
            docE.printStackTrace();
        }catch(Exception E){
            E.printStackTrace();
        }finally {
            if(barnamehDoc != null){
                barnamehDoc.close();
            }

            return FilePath;
        }
    }

    private class FooterEvent extends PdfPageEventHelper{
        BaseFont bf;
        Font font;

        public FooterEvent(){
            try {
                bf = BaseFont.createFont("assets/fonts/sahel_fd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                font = new Font(bf, 10);
            }catch (IOException e){
                e.printStackTrace();
            }catch (DocumentException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase("نرم افزار حسابیار : 09121612664", font);
//            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, document.getPageSize().getWidth() - 20, 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("----------------------------------------------------------------------------" +
                                      "----------------------------------------------------------------------------", font),
                    (document.right() - document.left()) / 2,
                    document.bottom() - 10, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footer,
                    document.left() + 120,
                    document.bottom() - 20, 0, PdfWriter.RUN_DIRECTION_RTL, 0);
        }
    }

}
