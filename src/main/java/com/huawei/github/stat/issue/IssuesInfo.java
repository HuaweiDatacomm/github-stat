package com.huawei.github.stat.issue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.yangcentral.yangkit.utils.url.URLUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IssuesInfo {
    private List<IssueInfo> issues = new ArrayList<>();

    public IssuesInfo() {
    }

    public List<IssueInfo> getIssues() {
        return issues;
    }

    public void addIssue(IssueInfo issue) {
        this.issues.add(issue);
    }

    public void export(String out) throws IOException {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        CreationHelper creationHelper = workbook.getCreationHelper();
        SXSSFSheet sheet = workbook.createSheet("issues");
        sheet.setDisplayGridlines(true);
        sheet.setAutobreaks(true);
        sheet.setColumnWidth(0,1000);
        sheet.setColumnWidth(1,10000);
        sheet.setColumnWidth(2,30000);
        sheet.setColumnWidth(3,2000);
        sheet.setColumnWidth(4,5000);
        sheet.setColumnWidth(5,10000);

        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        CellStyle desStyle = workbook.createCellStyle();
        desStyle.cloneStyleFrom(style);
        desStyle.setAlignment(HorizontalAlignment.JUSTIFY);
        CellStyle hyperLinkStyle = workbook.createCellStyle();
        hyperLinkStyle.cloneStyleFrom(style);
        Font hyperLinkFont = workbook.createFont();
        hyperLinkFont.setUnderline(Font.U_SINGLE);
        hyperLinkFont.setColor(IndexedColors.BLUE.getIndex());
        hyperLinkStyle.setFont(hyperLinkFont);

        sheet.setDefaultColumnStyle(0,hyperLinkStyle);
        sheet.setDefaultColumnStyle(1,desStyle);
        sheet.setDefaultColumnStyle(2,desStyle);
        sheet.setDefaultColumnStyle(3,style);
        sheet.setDefaultColumnStyle(4,hyperLinkStyle);
        sheet.setDefaultColumnStyle(5,style);

        //header
        SXSSFRow firstRow = sheet.createRow(0);
        SXSSFCell number = firstRow.createCell(0);
        number.setCellValue("Issue Number");
        number.setCellStyle(style);

        SXSSFCell title = firstRow.createCell(1);
        title.setCellValue("Title");
        title.setCellStyle(style);

        SXSSFCell description = firstRow.createCell(2);
        description.setCellValue("Description");
        description.setCellStyle(style);

        SXSSFCell state = firstRow.createCell(3);
        state.setCellValue("State");
        state.setCellStyle(style);

        SXSSFCell createdBy = firstRow.createCell(4);
        createdBy.setCellValue("CreatedBy");
        createdBy.setCellStyle(style);

        SXSSFCell company = firstRow.createCell(5);
        company.setCellValue("Company");
        company.setCellStyle(style);

        //body
        int rowIndex = 1;
        for(IssueInfo issueInfo:issues){
            SXSSFRow row = sheet.createRow(rowIndex++);
            //number
            SXSSFCell numberCell = row.createCell(0);
            numberCell.setCellValue(issueInfo.getSeqNo());
            Hyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(issueInfo.getIssueUrl().toString());
            numberCell.setHyperlink(hyperlink);
            //title
            SXSSFCell titleCell = row.createCell(1);
            numberCell.setCellValue(issueInfo.getTitle());
            //description
            SXSSFCell descCell = row.createCell(2);
            descCell.setCellValue(issueInfo.getDescription());
            //state
            SXSSFCell stateCell = row.createCell(3);
            stateCell.setCellValue(issueInfo.getState());
            //created by
            SXSSFCell createdCell = row.createCell(4);
            createdCell.setCellValue(issueInfo.getUserInfo().getName());
            Hyperlink homePageLink = creationHelper.createHyperlink(HyperlinkType.URL);
            homePageLink.setAddress(issueInfo.getUserInfo().getHomepage().toString());
            createdCell.setHyperlink(homePageLink);
            //company
            if(issueInfo.getUserInfo().getCompany() != null){
                SXSSFCell companyCell = row.createCell(5);
                companyCell.setCellValue(issueInfo.getUserInfo().getCompany());
            }

        }

        workbook.write(new FileOutputStream(new File(out)));
        workbook.close();
    }

    public static IssuesInfo parse(String baseURL) throws IOException {
        IssuesInfo issuesInfo = new IssuesInfo();
        int size = 0;
        int perPage = 100;
        int page = 1;
        do {
            StringBuilder sb = new StringBuilder(baseURL);
            sb.append("&per_page="+ perPage);
            sb.append("&page="+ page);
            URL fullURL = new URL(sb.toString());
            String str = URLUtil.URLGet(fullURL);
            JsonElement jsonElement = JsonParser.parseString(str);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            size = jsonArray.size();
            for(int i =0; i< size;i++){
                JsonElement issueElement = jsonArray.get(i);
                IssueInfo issueInfo = IssueInfo.parse(issueElement);
                issuesInfo.addIssue(issueInfo);
            }
            page++;
        }while (size == perPage);
        return issuesInfo;
    }

    public static void main(String[] args) throws IOException {
        IssuesInfo issuesInfo = IssuesInfo.parse("https://api.github.com/repos/openconfig/public/issues?state=all");
        issuesInfo.export("issues.xlsx");
    }
}
