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
		//获取Excel对象
		System.out.println("read start .....");
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(path));
        System.out.println("read in .....");
        //获取一个Sheet对象
        XSSFSheet sheet = wb.getSheetAt(0);
        int totleRow = sheet.getLastRowNum();
        Map<String,List<CommEvaluate>> mapResults = new HashMap<>();
        String key="default";
        System.out.println("read end .");
        System.out.println(totleRow);
        //循环读取所有行
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
		
		//如果含有key，则在已有的list里添加，
		if (mapResults.keySet().contains(key)){
			List<CommEvaluate> alreayList = mapResults.get(key);
			alreayList.add(commEva);
			mapResults.put(key, alreayList);
		}else{
	   //如果没有，则新建一个list，把现有的值加进去
			List<CommEvaluate> list = new ArrayList<>();
			list.add(commEva);
			mapResults.put(key, list);
		}
	 }
	
  //按照key写入文件，且key是文件名，同key下的value写入key的文件里，
  	public static void writeTextByKey(Map<String,List<CommEvaluate>> mapResults){
  		
  	   String dirFile = "default";
  	   Set<String> keys = mapResults.keySet();
  	   for (String key:keys){ //key=cityId_cityName_commId_commName
  		   //拉出目录以cityId_cityName为目录，commId_commName为文件名字
  		   String[] vs = key.split("_");
  		   dirFile = "d:/comm-evaluate-files3/"+vs[0]+"_"+vs[1];
  		   createDir(dirFile);
  		   File file = new File(dirFile+"/"+vs[2]+"_"+vs[3]+".xlsx");  
  		   writeTextByValue(file,mapResults.get(key));
  	   }
  	   
  	}
  	
  	public static void writeTextByValue(File file,List<CommEvaluate> list){
		try {  
            // 打开文件  
			XSSFWorkbook wb = new XSSFWorkbook();
            // 生成名为“sheet1”的工作表，参数0表示这是第一页  
			XSSFSheet sheet = wb.createSheet();
            int row = 1;//要写的总行数
            
            //首先第一行是每列的列名，list中的值从第二行开始写
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
            
            for (CommEvaluate one:list){ //每一行
            	row1 = sheet.createRow(row);//从第二行开始写正式数据，
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
            // 写入数据并关闭文件  
            //输出文件
            FileOutputStream output = new FileOutputStream(file);
            wb.write(output);
            output.flush();
            wb.close();
        } catch (Exception e) {  
            System.out.println(e);  
        }  
	}
	
	/**创建目录***/
	public static boolean createDir(String destDirName) {  
        File dir = new File(destDirName);  
        if (dir.exists()) {  
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");  
            return false;  
        }  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        //创建目录  
        if (dir.mkdirs()) {  
            System.out.println("创建目录" + destDirName + "成功！");  
            return true;  
        } else {  
            System.out.println("创建目录" + destDirName + "失败！");  
            return false;  
        }  
    }  
	
}
