package javaReadWriteOthers;

/****
 * –°«¯∆¿≤‚¿‡
 * 
 * ***/
public class CommEvaluate {

	String question_id;
	String ques_create_dt;
	String title;
	String city_id;
	String city_name;
	String comm_id;
	String comm_name;
	String answer_id;
	String answer_create_dt;
	String answer_content;
	String model_id;
	String category;
	
	public CommEvaluate(String question_id,String ques_create_dt,
	String title,
	String city_id,
	String city_name,
	String comm_id,
	String comm_name,
	String answer_id,
	String answer_create_dt,
	String answer_content,
	String model_id,
	String category){
		
		this.answer_content=answer_content;
		this.answer_create_dt=answer_create_dt;
		this.answer_id=answer_id;
		this.category=category;
		this.city_id=city_id;
		this.city_name=city_name;
		this.comm_id=comm_id;
		this.comm_name=comm_name;
		this.model_id=model_id;
		this.ques_create_dt=ques_create_dt;
		this.question_id=question_id;
		this.title=title;
	}
	
}
