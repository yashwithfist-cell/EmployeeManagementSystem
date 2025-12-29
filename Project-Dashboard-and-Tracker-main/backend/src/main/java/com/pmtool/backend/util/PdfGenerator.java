package com.pmtool.backend.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.pmtool.backend.DTO.SalarySlipDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PdfGenerator {

    public static ByteArrayInputStream generateSlip(SalarySlipDto slip) {
    	Document document = new Document(PageSize.A5, 20, 20, 15, 15);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // ------------- FONTS ---------------
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font sectionHeaderFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);

            // ------------- HEADER ---------------
            Paragraph title = new Paragraph(slip.getCompanyName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subTitle = new Paragraph("Salary Slip - " + slip.getMonth(), subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);

            document.add(Chunk.NEWLINE);

            // ------------- EMPLOYEE INFORMATION ---------------
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.5f, 2.5f, 1.5f, 2.5f});
            infoTable.setSpacingBefore(10);

            addInfoRow(infoTable, "Employee Name", slip.getEmployeeName(), "Department", slip.getDepartment(), normalFont);
            addInfoRow(infoTable, "Designation", slip.getDepartment(), "Location", slip.getLocation(), normalFont);
            addInfoRow(infoTable, "Bank Name", slip.getBankName(), "Account Number", slip.getBankAccountNo(), normalFont);

            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            // ------------- SECTION HEADERS (Blue background) --------------- 
            PdfPCell earningHeader = new PdfPCell(new Phrase("EARNINGS", sectionHeaderFont));
            earningHeader.setBackgroundColor(new BaseColor(0, 102, 204));
            earningHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            earningHeader.setPadding(7);

            PdfPCell deductionHeader = new PdfPCell(new Phrase("DEDUCTIONS", sectionHeaderFont));
            deductionHeader.setBackgroundColor(new BaseColor(178, 34, 34));
            deductionHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            deductionHeader.setPadding(7);

            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidths(new float[]{1, 1});
            mainTable.setWidthPercentage(100);
            mainTable.addCell(earningHeader);
            mainTable.addCell(deductionHeader);

            // ------------- EARNINGS TABLE ---------------
            PdfPTable earningsTable = new PdfPTable(2);
            earningsTable.setWidthPercentage(100);
            addRow(earningsTable, "Basic + Dearness Allowance", slip.getBasic(), normalFont);
//            addRow(earningsTable, "Dearness Allowance", slip.getDa(), normalFont);
            addRow(earningsTable, "House Rent Allowance", slip.getHra(), normalFont);
//            addRow(earningsTable, "Conveyance Allowance", slip.getConveyance(), normalFont);
//            addRow(earningsTable, "Medical Allowance", slip.getMedical(), normalFont);
            addRow(earningsTable, "Special Allowance", slip.getSpecial(), normalFont);

            // ------------- DEDUCTIONS TABLE ---------------
            PdfPTable deductionTable = new PdfPTable(2);
            deductionTable.setWidthPercentage(100);
            addRow(deductionTable, "Professional Tax", slip.getProfessionalTax(), normalFont);
            addRow(deductionTable, "Tax Deducted at Source", slip.getTds(), normalFont);
            addRow(deductionTable, "Employee Provident Fund", slip.getProvidentFund(), normalFont);

            mainTable.addCell(new PdfPCell(earningsTable));
            mainTable.addCell(new PdfPCell(deductionTable));
            document.add(mainTable);

            document.add(Chunk.NEWLINE);

            // ------------- TOTAL SECTION ---------------
//            double gross = slip.getSalary() + slip.getDa() + slip.getHra() + slip.getConveyance() + slip.getMedical() + slip.getSpecial();
            double gross = slip.getBasic() + slip.getHra() + slip.getSpecial();
            double totalDeductions = slip.getProfessionalTax() + slip.getTds() + slip.getProvidentFund();
            double net = gross - totalDeductions;

            PdfPTable totals = new PdfPTable(2);
            totals.setWidthPercentage(60);
            totals.setHorizontalAlignment(Element.ALIGN_LEFT);
            totals.setSpacingBefore(15);
            totals.setWidths(new float[]{3, 1});

            addTotalRow(totals, "Gross Salary", gross, boldFont);
            addTotalRow(totals, "Deductions", totalDeductions, boldFont);
            addTotalRow(totals, "Net Salary", net, boldFont);
            addTotalRow(totals, "Total Working Days", slip.getTotalDays(), boldFont);

            document.add(totals);

            document.add(Chunk.NEWLINE);

            // ------------- FOOTER (Bordered acknowledgement) ---------------
            Paragraph footer = new Paragraph(
                    "This is a computer-generated salary slip; no signature is required.",
                    new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // --------------- Helper Functions ---------------
    private static void addInfoRow(PdfPTable table, String label1, String value1, String label2, String value2, Font font) {
        table.addCell(makeLabel(label1 + ":", font));
        table.addCell(makeValue(value1, font));
        table.addCell(makeLabel(label2 + ":", font));
        table.addCell(makeValue(value2, font));
    }

    private static PdfPCell makeLabel(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        return cell;
    }

    private static PdfPCell makeValue(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    private static void addRow(PdfPTable table, String head, double amount, Font font) {
        table.addCell(new Phrase(head, font));
        table.addCell(new Phrase(String.format("%.2f", amount), font));
    }

    private static void addTotalRow(PdfPTable table, String label, double amount, Font font) {
        PdfPCell left = new PdfPCell(new Phrase(label, font));
        left.setBorder(Rectangle.BOX);
        table.addCell(left);

        PdfPCell right = new PdfPCell(new Phrase(String.format("%.2f", amount), font));
        right.setBorder(Rectangle.BOX);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(right);
    }
}
