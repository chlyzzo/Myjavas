package javaReadWriteOthers;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class commEvaluateReadAndWrite2_xlsx {

	public static void main(String[] args) throws   FileNotFoundException, IOException {
		readCommEvaluateDataBase("C:/Users/wenhaohu/Desktop/pingce-all-mess-0511.xlsx");
	}
    
	public static void readCommEvaluateDataBase(String path) throws FileNotFoundException, IOException{
		//��ȡExcel����
		System.out.println("read start .....");
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(path));
        System.out.println("read in .....");
        //��ȡһ��Sheet����
        XSSFSheet sheet = wb.getSheetAt(0);
        int totleRow = sheet.getLastRowNum();
        Map<String,List<CommEvaluate>> mapResults = new HashMap<>();
        String key="default";
        System.out.println("read end .");
        System.out.println(totleRow);
        //ѭ����ȡ������
        for(int row=0;row<totleRow;row++){
        	
        	XSSFRow row1 = sheet.getRow(row);
        	
        	key = row1.getCell(3).getRawValue()+"_"+row1.getCell(4)
        			+"_"+row1.getCell(5).getRawValue()+"_"+row1.getCell(6);
        	CommEvaluate commEva = new CommEvaluate(
        			row1.getCell(0).getRawValue(),
        			row1.getCell(1).toString(),
        			row1.getCell(2).toString(),
        			row1.getCell(3).getRawValue(),
        			row1.getCell(4).toString(),
        			row1.getCell(5).getRawValue(),
        			row1.getCell(6).toString(),
        			row1.getCell(7).getRawValue(),
        			row1.getCell(8).toString(),
        			row1.getCell(9).toString(),
        			row1.getCell(10).getRawValue(),
        			row1.getCell(11).toString()
        			);
        	appendIntoAlreadMap(mapResults,key,commEva);
        }
		wb.close();
		writeTextByKey(mapResults);
	}
	
    public static void appendIntoAlreadMap(Map<String,List<CommEvaluate>> mapResults,String key,CommEvaluate commEva){
		
		//�������key���������е�list����ӣ�
		if (mapResults.keySet().contains(key)){
			List<CommEvaluate> alreayList = mapResults.get(key);
			alreayList.add(commEva);
			mapResults.put(key, alreayList);
		}else{
	   //���û�У����½�һ��list�������е�ֵ�ӽ�ȥ
			List<CommEvaluate> list = new ArrayList<>();
			list.add(commEva);
			mapResults.put(key, list);
		}
	 }
	
  //����keyд���ļ�����key���ļ�����ͬkey�µ�valueд��key���ļ��
  	public static void writeTextByKey(Map<String,List<CommEvaluate>> mapResults){
  		
  	   String dirFile = "default";
  	   Set<String> keys = mapResults.keySet();
  	   for (String key:keys){ //key=cityId_cityName_commId_commName
  		   //����Ŀ¼��cityId_cityNameΪĿ¼��commId_commNameΪ�ļ�����
  		   String[] vs = key.split("_");
  		   dirFile = "d:/comm-evaluate-files3/"+vs[0]+"_"+vs[1];
  		   createDir(dirFile);
  		   File file = new File(dirFile+"/"+vs[2]+"_"+vs[3]+".xlsx");  
  		   writeTextByValue(file,mapResults.get(key));
  	   }
  	   
  	}
  	
  	public static void writeTextByValue(File file,List<CommEvaluate> list){
		try {  
            // ���ļ�  
			XSSFWorkbook wb = new XSSFWorkbook();
            // ������Ϊ��sheet1���Ĺ���������0��ʾ���ǵ�һҳ  
			XSSFSheet sheet = wb.createSheet();
            int row = 1;//Ҫд��������
            
            //���ȵ�һ����ÿ�е�������list�е�ֵ�ӵڶ��п�ʼд
            XSSFRow row1 = sheet.createRow(0);
            //row1.createCell(0).setCellValue("question_id");
            //row1.createCell(1).setCellValue("ques_create_dt");
            row1.createCell(0).setCellValue("title");
            //row1.createCell(3).setCellValue("city_id");
            //row1.createCell(4).setCellValue("city_name");
            //row1.createCell(5).setCellValue("comm_id");
        	//row1.createCell(6).setCellValue("comm_name");
        	//row1.createCell(7).setCellValue("answer_id");
        	//row1.createCell(8).setCellValue("answer_create_dt");
        	row1.createCell(1).setCellValue("answer_content");
        	//row1.createCell(10).setCellValue("model_id");
        	row1.createCell(2).setCellValue("category");
            
            for (CommEvaluate one:list){ //ÿһ��
            	row1 = sheet.createRow(row);//�ӵڶ��п�ʼд��ʽ���ݣ�
            	//row1.createCell(0).setCellValue(one.question_id);
               // row1.createCell(1).setCellValue(one.ques_create_dt);
                row1.createCell(0).setCellValue(one.title);
               // row1.createCell(3).setCellValue(one.city_id);
               // row1.createCell(4).setCellValue(one.city_name);
               // row1.createCell(5).setCellValue(one.comm_id);
            	//row1.createCell(6).setCellValue(one.comm_name);
            	//row1.createCell(7).setCellValue(one.answer_id);
            	//row1.createCell(8).setCellValue(one.answer_create_dt);
            	row1.createCell(1).setCellValue(one.answer_content);
            	//row1.createCell(10).setCellValue(one.model_id);
            	row1.createCell(2).setCellValue(one.category);
            	row = row + 1;
            }
            // д�����ݲ��ر��ļ�  
            //����ļ�
            FileOutputStream output = new FileOutputStream(file);
            wb.write(output);
            output.flush();
            wb.close();
        } catch (Exception e) {  
            System.out.println(e);  
        }  
	}
	
	/**����Ŀ¼***/
	public static boolean createDir(String destDirName) {  
        File dir = new File(destDirName);  
        if (dir.exists()) {  
            System.out.println("����Ŀ¼" + destDirName + "ʧ�ܣ�Ŀ��Ŀ¼�Ѿ�����");  
            return false;  
        }  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        //����Ŀ¼  
        if (dir.mkdirs()) {  
            System.out.println("����Ŀ¼" + destDirName + "�ɹ���");  
            return true;  
        } else {  
            System.out.println("����Ŀ¼" + destDirName + "ʧ�ܣ�");  
            return false;  
        }  
    }  
	
}
