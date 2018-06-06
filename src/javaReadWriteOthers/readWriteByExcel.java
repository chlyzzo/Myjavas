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
 * java��ȡ��дexcel��
 * 
 * ****/
public class readWriteByExcel {

	public static void main(String[] args) {

//		File file = new File("C:/Users/wenhaohu/Desktop/�ʴ�-С���������.xls");  
//		readExcel(file);
		
		writeExcel("");
	}
	
	/****
	 * ��ȡexcel��
	 * 
	 * ****/
	public static void readExcel(File file){
		try {  
            // ��������������ȡExcel  
			System.out.println(file.getAbsolutePath());
            InputStream is = new FileInputStream(file.getAbsolutePath());  
            // jxl�ṩ��Workbook��  
            Workbook wb = Workbook.getWorkbook(is);  
            // Excel��ҳǩ����  
            int sheet_size = wb.getNumberOfSheets();  
            for (int index = 0; index < sheet_size; index++) {  
                // ÿ��ҳǩ����һ��Sheet����  
                Sheet sheet = wb.getSheet(index);  
                // sheet.getRows()���ظ�ҳ��������  
                int rows = sheet.getRows();
                int columns = sheet.getColumns();
                for (int i = 0; i < rows; i++) {  
                    // sheet.getColumns()���ظ�ҳ��������  
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
	 * дexcel��
	 * 
	 * ****/
	public static void writeExcel(String path){
		try {  
            // ���ļ�  
            WritableWorkbook book = Workbook.createWorkbook(new File(  
                    "C:/Users/wenhaohu/Desktop/write-test.xls"));  
            // ������Ϊ��sheet1���Ĺ���������0��ʾ���ǵ�һҳ  
            WritableSheet sheet = book.createSheet("sheet1", 0);  
            // ��Label����Ĺ�������ָ����Ԫ��λ���ǵ�һ�е�һ��(0,0),��Ԫ������Ϊstring  
            Label label = new Label(0, 0, "string");  
            // ������õĵ�Ԫ����ӵ���������  
            sheet.addCell(label);  
            // ����һ���������ֵĵ�Ԫ��,��Ԫ��λ���ǵڶ��У���һ�У���Ԫ�������Ϊ1234.5  
            Number number = new Number(1, 0, 1234.5);  
            sheet.addCell(number);  
            // ����һ���������ڵĵ�Ԫ�񣬵�Ԫ��λ���ǵ����У���һ�У���Ԫ�������Ϊ��ǰ����  
            DateTime dtime = new DateTime(2, 0, new Date());  
            sheet.addCell(dtime);  
            // д�����ݲ��ر��ļ�  
            book.write();  
            book.close();  
        } catch (Exception e) {  
            System.out.println(e);  
        }  
	}
}
