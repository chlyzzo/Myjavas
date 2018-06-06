package javaReadWriteOthers;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStream;
import java.util.Date;

import jxl.Sheet;  
import jxl.Workbook;  
import jxl.read.biff.BiffException;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;  
import jxl.write.Number;  

/****
 * java读取和写excel，
 * 
 * ****/
public class readWriteByExcel {

	public static void main(String[] args) {

//		File file = new File("C:/Users/wenhaohu/Desktop/问答-小区评测详表.xls");  
//		readExcel(file);
		
		writeExcel("");
	}
	
	/****
	 * 读取excel表，
	 * 
	 * ****/
	public static void readExcel(File file){
		try {  
            // 创建输入流，读取Excel  
			System.out.println(file.getAbsolutePath());
            InputStream is = new FileInputStream(file.getAbsolutePath());  
            // jxl提供的Workbook类  
            Workbook wb = Workbook.getWorkbook(is);  
            // Excel的页签数量  
            int sheet_size = wb.getNumberOfSheets();  
            for (int index = 0; index < sheet_size; index++) {  
                // 每个页签创建一个Sheet对象  
                Sheet sheet = wb.getSheet(index);  
                // sheet.getRows()返回该页的总行数  
                int rows = sheet.getRows();
                int columns = sheet.getColumns();
                for (int i = 0; i < rows; i++) {  
                    // sheet.getColumns()返回该页的总列数  
                    for (int j = 0; j < columns; j++) {  
                        String cellinfo = sheet.getCell(j, i).getContents();  
                        System.out.println(cellinfo);  
                    }  
                }  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (BiffException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  

	
	
	/****
	 * 写excel表，
	 * 
	 * ****/
	public static void writeExcel(String path){
		try {  
            // 打开文件  
            WritableWorkbook book = Workbook.createWorkbook(new File(  
                    "C:/Users/wenhaohu/Desktop/write-test.xls"));  
            // 生成名为“sheet1”的工作表，参数0表示这是第一页  
            WritableSheet sheet = book.createSheet("sheet1", 0);  
            // 在Label对象的构造子中指名单元格位置是第一列第一行(0,0),单元格内容为string  
            Label label = new Label(0, 0, "string");  
            // 将定义好的单元格添加到工作表中  
            sheet.addCell(label);  
            // 生成一个保存数字的单元格,单元格位置是第二列，第一行，单元格的内容为1234.5  
            Number number = new Number(1, 0, 1234.5);  
            sheet.addCell(number);  
            // 生成一个保存日期的单元格，单元格位置是第三列，第一行，单元格的内容为当前日期  
            DateTime dtime = new DateTime(2, 0, new Date());  
            sheet.addCell(dtime);  
            // 写入数据并关闭文件  
            book.write();  
            book.close();  
        } catch (Exception e) {  
            System.out.println(e);  
        }  
	}
}
