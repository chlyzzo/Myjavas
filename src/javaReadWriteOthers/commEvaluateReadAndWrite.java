package javaReadWriteOthers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/***
 * 
 * 小区评测数据的拉取，每个小区一个excel文件，
 * 
 * ****/



public class commEvaluateReadAndWrite {

	public static void main(String[] args) {
		readCommEvaluateDataBase("C:/Users/wenhaohu/Desktop/问答-小区评测详细表2.xls");
	}

	public static void readCommEvaluateDataBase(String path){
		
		File file = new File(path);  
		try {  
            // 创建输入流，读取Excel  
			System.out.println(file.getAbsolutePath());
            InputStream is = new FileInputStream(file.getAbsolutePath());  
            // jxl提供的Workbook类  
            Workbook wb = Workbook.getWorkbook(is);  
            // Excel的页签数量  
            int sheet_size = wb.getNumberOfSheets();  
            Map<String,List<CommEvaluate>> mapResults = new HashMap<>();
            String key="default";
            for (int index = 0; index < sheet_size; index++) {  
                // 每个页签创建一个Sheet对象  
                Sheet sheet = wb.getSheet(index);  
                // sheet.getRows()返回该页的总行数  
                int rows = sheet.getRows();
                for (int i = 0; i < rows; i++) {  
                    //cityId_cityName_commId_commName作为key,3456
                	key = sheet.getCell(3, i).getContents()+"_"+sheet.getCell(4, i).getContents()
                			+"_"+sheet.getCell(5, i).getContents()+"_"+sheet.getCell(6, i).getContents();
                	
                	CommEvaluate commEva = new CommEvaluate(
                			sheet.getCell(0, i).getContents(),
                			sheet.getCell(1, i).getContents(),
                			sheet.getCell(2, i).getContents(),
                			sheet.getCell(3, i).getContents(),
                			sheet.getCell(4, i).getContents(),
                			sheet.getCell(5, i).getContents(),
                			sheet.getCell(6, i).getContents(),
                			sheet.getCell(7, i).getContents(),
                			sheet.getCell(8, i).getContents(),
                			sheet.getCell(9, i).getContents(),
                			sheet.getCell(10, i).getContents(),
                			sheet.getCell(11, i).getContents()
                			);
                	appendIntoAlreadMap(mapResults,key,commEva);
                }//read for end
                
                //get result for output
                System.out.println(mapResults.size());
                writeTextByKey(mapResults);
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (BiffException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	}//read function end
	
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
		   dirFile = "d:/comm-evaluate-files2/"+vs[0]+"_"+vs[1];
		   createDir(dirFile);
		   File file = new File(dirFile+"/comm-evaluate-"+vs[2]+"_"+vs[3]+"-output.xls");  
		   writeTextByValue(file,mapResults.get(key));
	   }
	   
	}
	
	public static void writeTextByValue(File file,List<CommEvaluate> list){
		try {  
            // 打开文件  
            WritableWorkbook book = Workbook.createWorkbook(file);  
            // 生成名为“sheet1”的工作表，参数0表示这是第一页  
            WritableSheet sheet = book.createSheet("sheet1", 0);  
            int row = 0;
            //首先第一行是每列的列名，list中的值从第二行开始写
            Label label_1 = new Label(0,row, "question_id");
        	Label label_2 = new Label(1,row, "ques_create_dt");
        	Label label_3 = new Label(2,row, "title");
        	Label label_4 = new Label(3,row, "city_id");
        	Label label_5 = new Label(4,row, "city_name");
        	Label label_6 = new Label(5,row, "comm_id");
        	Label label_7 = new Label(6,row, "comm_name");
        	Label label_8 = new Label(7,row, "answer_id");
        	Label label_9 = new Label(8,row, "answer_create_dt");
        	Label label_10 = new Label(9,row, "answer_content");
        	Label label_11 = new Label(10,row, "model_id");
        	Label label_12 = new Label(11,row, "category");
        	sheet.addCell(label_1);  sheet.addCell(label_2); sheet.addCell(label_3); 
        	sheet.addCell(label_4); sheet.addCell(label_5); sheet.addCell(label_6); 
        	sheet.addCell(label_7); sheet.addCell(label_8); sheet.addCell(label_9); 
        	sheet.addCell(label_10); sheet.addCell(label_11); sheet.addCell(label_12); 
        	row = row + 1;
            
            for (CommEvaluate one:list){ //每一行
            	label_1 = new Label(0,row, one.question_id);
            	label_2 = new Label(1,row, one.ques_create_dt);
            	label_3 = new Label(2,row, one.title);
            	label_4 = new Label(3,row, one.city_id);
            	label_5 = new Label(4,row, one.city_name);
            	label_6 = new Label(5,row, one.comm_id);
            	label_7 = new Label(6,row, one.comm_name);
            	label_8 = new Label(7,row, one.answer_id);
            	label_9 = new Label(8,row, one.answer_create_dt);
            	label_10 = new Label(9,row, one.answer_content);
            	label_11 = new Label(10,row, one.model_id);
            	label_12 = new Label(11,row, one.category);
            	sheet.addCell(label_1);  sheet.addCell(label_2); sheet.addCell(label_3); 
            	sheet.addCell(label_4); sheet.addCell(label_5); sheet.addCell(label_6); 
            	sheet.addCell(label_7); sheet.addCell(label_8); sheet.addCell(label_9); 
            	sheet.addCell(label_10); sheet.addCell(label_11); sheet.addCell(label_12); 
            	row = row + 1;
            }
            // 写入数据并关闭文件  
            book.write();  
            book.close();  
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
