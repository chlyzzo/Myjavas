package particle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

/*
 * 只保留合法的粒子，
 * */
public class particleMehod2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print(Math.log(24.0/4));
	}
	//迭代跑
		public static void runParticelMethod(){
			//1,建立邻居表
			   //线下写入txt文件
			 String file = "E:\\workspace\\Myjavas\\src\\particle\\example.txt";
			//2,参数设置
			  //在各自方法中写明（不再设置，详见位置更新和速度更新）
			 int populationNum =20;//种群数目，粒子个数
			 int maxIter = 300;//迭代数
			 //设置全局适应度值保存
			 List<Double> global = new ArrayList<>();
			 
			//3，粒子种群初始化
			Map<Integer,List<Integer>> netNeighbor = initNetNeighbor(file);
			int m = computeM(netNeighbor);//总的边数
			int totleNode = netNeighbor.keySet().size();
			List<List<Integer>> xt = initParticlePosition(totleNode,populationNum,netNeighbor);
			List<List<Double>> vt = initParticleSpeed(totleNode,populationNum);
			//剔除非法粒子
			//4-5-6,个体适应度值计算(初始化个体最佳--即自己本身;然后，在个体最佳中找出全局第1,2,3的粒子)
			List<List<Integer>> p = xt;
			Map<String,List<Integer>> ps = computePbdAndPcd(p,m);//ps只有pg？
			
			double a = 0.0;
			global.add(computeQ(findStructInParticle(ps.get("pg")),ps.get("pg"),m));
			int step =1;
			while(step<maxIter&&xt.size()>=2){
				//7，更新位置和速度
				List<List<Double>> vt_1 = speedUpdate(totleNode,vt,xt,p,ps);
				List<List<Integer>> xt_1 = positionsUpdate(totleNode,a,vt_1,xt,netNeighbor,ps);

				p = findParticleP(p,xt_1,m,netNeighbor);
				a = conpute_a(p,m);
				//更新后，再次计算全局最优，并记录
			    ps = computePbdAndPcd(p,m);
			    global.add(computeQ(findStructInParticle(ps.get("pg")),ps.get("pg"),m));
			    //速度位置同理迭代()
			    vt = vt_1;
			    xt = xt_1;
			    step++;
			}
			//输出迭代过程中的全局适应度值
			global.forEach(one->{
				System.out.println(one);
			});
			
		}
		
		
		//初始化种群速度
		public static List<List<Double>> initParticleSpeed(int totleNode,int populationNum){
			List<List<Double>> result = new ArrayList<>();
			result.add(Arrays.asList(0.0));//首个即下标为0的默认，并且不计算在内。
			Random re = new Random();
			for (int one=1;one<=populationNum;one++){
				List<Double> oneInit = new ArrayList<>();
				oneInit.add(0.0);
				for (int valuei=1;valuei<=totleNode;valuei++){
					oneInit.add(re.nextDouble());
				}
				result.add(oneInit);
			}
			return result;			
		}
		
		//初始化种群位置
		public static List<List<Integer>> initParticlePosition(int totleNode,int populationNum,Map<Integer,List<Integer>> initNetNeighbor){
			List<List<Integer>> result = new ArrayList<>();
			result.add(Arrays.asList(0));//首个即下标为0的默认，并且不计算在内。
			Random re = new Random();
			for (int one=1;one<=populationNum;one++){
				List<Integer> oneInit = new ArrayList<>();
				oneInit.add(0);
				for (int valuei=1;valuei<=totleNode;valuei++){
					List<Integer> mayValues = initNetNeighbor.get(valuei);
					oneInit.add(mayValues.get(re.nextInt(mayValues.size())));
				}
				result.add(oneInit);
			}
			return result;				
		}
		
		//根据已有的<particle_i,Q值>找出前三大粒子
		public static Map<String,List<Integer>> computePbdAndPcd(List<List<Integer>> data,int m){
			Map<String,List<Integer>> result = findPbAndPcInMapScore(data,m);
			return result;
		}
		
		//计算a值，即第二大和第三大值在本次中所再比重
		public static double conpute_a(List<List<Integer>> data,int m){
			double a = 0.0;
			Map<Integer,Double> particleQs = new TreeMap<>();
			int len = data.size();
			for (int i=1;i<len;i++){
				double value = computeQ(findStructInParticle(data.get(i)),data.get(i),m);
				particleQs.put(i,value);
			} 
			List<Map.Entry<Integer, Double>> list = new ArrayList<>(particleQs.entrySet());
			Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>(){
				//升序排序
				public int compare(Entry<Integer,Double> o1,Entry<Integer,Double> o2){
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			//找出第二大和第三大占比
			int start = 1;
			double first = -1, second = -1, third = -1;
			double count = 0;
			boolean isSec=false,isThird = false;
			for (Map.Entry<Integer, Double> mapp:list){
				if (start==1){
					first = mapp.getValue();
				}
				if (start>1){ //找第二大值
					if (mapp.getValue()!=first&&!isSec){
						second = mapp.getValue();
						isSec=true;
					}
				}
				if (start>2&&isSec){//找出第三大值
					if (mapp.getValue()!=second&&!isThird){
						third = mapp.getValue();
						isThird=true;
					}
				}
				if (mapp.getValue()==second||mapp.getValue()==third){
					count = count +1;
				}
				start = start + 1;
			}
			a = count/list.size();
			return a;
		}
		
		//在map中找出前三大的值
		public static Map<String,List<Integer>> findPbAndPcInMapScore(List<List<Integer>> data,int m){
			Map<Integer,Double> particleQs = new TreeMap<>();
			int len = data.size();
			for (int i=1;i<len;i++){
				double value = computeQ(findStructInParticle(data.get(i)),data.get(i),m);
				particleQs.put(i,value);
			} 
			List<Map.Entry<Integer, Double>> list = new ArrayList<>(particleQs.entrySet());
			Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>(){
				//升序排序
				public int compare(Entry<Integer,Double> o1,Entry<Integer,Double> o2){
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			Map<String,List<Integer>> result = new HashMap<>();
			//找出第二大和第三大占比
			int start = 1;
			double first = -1, second = -1, third = -1;
			boolean isSec=false,isThird = false;
			for (Map.Entry<Integer, Double> mapp:list){
				if (start==1){
					first = mapp.getValue();
					result.put("pg", data.get(mapp.getKey()));
				}
				if (start>1){ //找第二大值
					if (mapp.getValue()!=first&&!isSec){
						second = mapp.getValue();
						result.put("pb", data.get(mapp.getKey()));
						isSec=true;
					}
				}
				if (start>2&&isSec){//找出第三大值
					if (mapp.getValue()!=second&&!isThird){
						third = mapp.getValue();
						result.put("pc", data.get(mapp.getKey()));
						isThird=true;
					}
				}
				if (isThird){break;}
				start = start + 1;
			}
			
			return result;
		}
		
		/*
		 *粒子个体最优记录，判断迭代前和迭代后粒子的适应度值
		 * */
		public static List<List<Integer>> findParticleP(List<List<Integer>> p,List<List<Integer>> dataT_1,int m,Map<Integer,List<Integer>> netNeighbor){
			List<List<Integer>> result = new ArrayList<>();
			result.add(Arrays.asList(0));
			int len = p.size();
			for (int i=1;i<len;i++){
				double qt = computeQ(findStructInParticle(p.get(i)),p.get(i),m);
				double qt_1 = 0.0;
				if (judeParticleIsWell(dataT_1.get(i),netNeighbor)){
					qt_1 = computeQ(findStructInParticle(dataT_1.get(i)),dataT_1.get(i),m);
				}
				if (qt>qt_1){
					result.add(p.get(i));
				}else{
					result.add(dataT_1.get(i));
				}
			}
			return result;
		}
		
		//粒子的位置更新
		public static List<List<Integer>> positionsUpdate(int totleNode,double a,List<List<Double>> vt_1,List<List<Integer>> xt,Map<Integer,List<Integer>> netNeighbor,Map<String,List<Integer>> ps){
			List<List<Integer>> xt_1 = new ArrayList<>();
			xt_1.add(Arrays.asList(0));
			List<Integer> pb = ps.get("pb");
			List<Integer> pc = ps.get("pc");

			int len = xt.size();
			for (int i=1;i<len;i++){//种群粒子循环
				List<Integer> particle = new ArrayList<>();
				particle.add(0);
				for (int d=1;d<=totleNode;d++){//节点循环
				  double posX_1 = (1-2*a)*xt.get(i).get(d)+a*(pb.get(d)+pc.get(d)-2*xt.get(i).get(d))+vt_1.get(i).get(d)%netNeighbor.get(d).size();
				  int posX_1Ceil = new Double(Math.ceil(posX_1)).intValue();
//				  //注：有可能取整会大于节点总数，这里做一个边界处理，超过节点总数则让其从邻居表中取值，保证可连通并
//				    //不可用边界取值，即超出选最大，不超出选最小（会导致图构造变化）
//				  if (posX_1Ceil>totleNode||posX_1Ceil<1){//最大的节点是节点总数
//					  List<Integer> mayValues = netNeighbor.get(d);
//					  Random re = new Random();
//					  posX_1Ceil = mayValues.get(re.nextInt(mayValues.size()));
//				  }
				  particle.add(posX_1Ceil);
				}
				xt_1.add(particle);
			}
			return xt_1;
		}
		
		public static List<List<Integer>> updateXtAndXt_1GetBetter(int m,List<List<Integer>> xt_1,List<List<Integer>> xt,Map<Integer,List<Integer>> netNeighbor){
			List<List<Integer>> result = new ArrayList<>();
			result.add(Arrays.asList(0));
			int len = xt.size();
			for (int i=1;i<len;i++){
				boolean isxtwell = judeParticleIsWell(xt.get(i),netNeighbor);
				boolean isxt_1well = judeParticleIsWell(xt_1.get(i),netNeighbor);
				if (isxtwell==true&&isxt_1well==false){
					result.add(xt.get(i));
				}
				if (isxtwell==false&&isxt_1well==true){
					result.add(xt_1.get(i));
				}
				if (isxtwell==true&&isxt_1well==true){				
				    double qt = computeQ(findStructInParticle(xt.get(i)),xt.get(i),m);
				    double qt_1 = computeQ(findStructInParticle(xt_1.get(i)),xt.get(i),m);
				    if (qt>qt_1){
					     result.add(xt.get(i));
				    }else{
					     result.add(xt_1.get(i));
				    }
				}
			}
			return result;
		}
		
		public static List<List<Double>> updateVtAndVt_1GetBetter(int m,List<List<Double>> vt_1,List<List<Double>> vt,List<List<Integer>> xt_1,List<List<Integer>> xt,Map<Integer,List<Integer>> netNeighbor){
			List<List<Double>> result = new ArrayList<>();
			result.add(Arrays.asList(0.0));
			int len = xt.size();
			for (int i=1;i<len;i++){
				boolean isxtwell = judeParticleIsWell(xt.get(i),netNeighbor);
				boolean isxt_1well = judeParticleIsWell(xt_1.get(i),netNeighbor);
				if (isxtwell==true||isxt_1well==true){
					result.add(vt_1.get(i));
				}
			}
			return result;
		}
		
		//粒子的速度更新
			public static List<List<Double>> speedUpdate(int totleNode,List<List<Double>> vt,List<List<Integer>> xt,List<List<Integer>> p,Map<String,List<Integer>> ps){
				List<List<Double>> vt_1 = new ArrayList<>();
				vt_1.add(Arrays.asList(0.0));
				double w = 0.8;//惯性权重
				double c1 = 0.7,c2=0.49;//学习率
				Random re = new Random();
				List<Integer> pg = ps.get("pg");
				int len = xt.size();
				for (int i=1;i<len;i++){
					List<Double> speed = new ArrayList<>();
					speed.add(0.0);
					for (int d=1;d<=totleNode;d++){
					  double vtV_1 = w*vt.get(i).get(d)+c1*re.nextDouble()*(p.get(i).get(d)-xt.get(i).get(d))+c2*re.nextDouble()*(pg.get(d)-xt.get(i).get(d));
					  speed.add(vtV_1);
					}
					vt_1.add(speed);
				}
				return vt_1;
			}
		
	    //因为非法粒子不能计算，剔除非法粒子，对应非法粒子的速度也剔除掉
		 public static List<List<Integer>> judeParticleXtIsWell(List<List<Integer>> xt,Map<Integer,List<Integer>> netNeighbor){
			 List<List<Integer>> result = new ArrayList<>();
			 result.add(Arrays.asList(0));
			 int len = xt.size();
			 for (int i=1;i<len;i++){
				 List<Integer> particle = xt.get(i);
				 if (judeParticleIsWell(particle,netNeighbor)){
					 result.add(particle);
				 }
			 }
			 return result;
		 }
		 //判断某个粒子是否合法
		 public static boolean judeParticleIsWell(List<Integer> particle,Map<Integer,List<Integer>> netNeighbor){
			int nodeNum = particle.size();
			for (int d=1;d<nodeNum;d++){
			     List<Integer> mayToNodes = netNeighbor.get(d);
				 if (!mayToNodes.contains(particle.get(d))){
	                      return false;
				  }
			}
	             return true;
		 }
		 
		 
		 
		 public static List<List<Double>> judeParticleVtIsWell(List<List<Double>> vt,List<List<Integer>> xt,Map<Integer,List<Integer>> netNeighbor){
			 
			 List<List<Double>> result = new ArrayList<>();
			 result.add(Arrays.asList(0.0));
			 int len = xt.size();
			 for (int i=1;i<len;i++){
				 List<Integer> particle = xt.get(i);
				 if (judeParticleIsWell(particle,netNeighbor)){
						 result.add(vt.get(i));
					 }
			 }
			 return result;
		 }
			
			
		 
		//根据粒子找出社区结构,即连同子图(可能有多个)
		  //粒子的存储是第一维度0，不计算在内；
		public static List<List<Integer>> findStructInParticle(List<Integer> particle){
			List<List<Integer>> result = new ArrayList<>();
			int particleNumber = particle.size();
			Set<Integer> deleteParticle = new HashSet<>();
			for (int i = 1;i < particleNumber;i++){
				if (!deleteParticle.contains(i)){
					Set<Integer> one = new HashSet<>();
					one.add(i);one.add(particle.get(i));
					//第一趟选取
					for (int j = 1; j < particleNumber; j++){
						Set<Integer> two = new HashSet<>();
						two.add(j);two.add(particle.get(j));
						//取交集
						Set<Integer> intersect = intersectSet(one,two);
						if (intersect !=null && intersect.size()>0){
							one.add(j);one.add(particle.get(j));
							deleteParticle.add(j);
						}
					}
					//第二趟选取
					for (int k = 1; k < particleNumber; k++){
						Set<Integer> two = new HashSet<>();
						two.add(k);two.add(particle.get(k));
						//取交集
						Set<Integer> intersect = intersectSet(one,two);
						if (intersect !=null && intersect.size()>0){
							one.add(k);one.add(particle.get(k));
							deleteParticle.add(k);
						}
					}
					//加入新的结构
					if (one !=null && one.size()>0){
						//社区结构中节点编号排序
						List<Integer> list = new ArrayList<Integer>(one);
						Collections.sort(list);
						result.add(list);
					}
				}
			}
			return result;
		}
		
		public static Map<Integer,List<Integer>> initNetNeighbor(String file){
			//从txt文本中读取,人为先写好，\t前是节点编号，从1开始，\t后是邻居有序表
			//1 \t 2,3,4,5
			//2 \t 6,7,8,9
			//....
			Map<Integer,List<Integer>> result = new HashMap<>();
			InputStreamReader inputReader = null;
			BufferedReader bufferReader = null;
			try {
				InputStream input = new FileInputStream(file);
				inputReader = new InputStreamReader(input);
				bufferReader = new BufferedReader(inputReader);
				
				String line = null;
				while((line = bufferReader.readLine())!=null){
					String[] values = line.split("\t");
					Integer nodeId = Integer.parseInt(values[0]); 
					List<Integer> neibors = transferStringArrayToListInteger(values[1].split(","));
					result.put(nodeId, neibors);
				}
				bufferReader.close();
				inputReader.close();
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
		
		//求出网络的总边数m，节点度之和的一半
		public static int computeM(Map<Integer,List<Integer>> neibors){
			int result = 0;
			Set<Integer> nodes = neibors.keySet();
			for (int node : nodes){
				result += neibors.get(node).size();
			}
			return result/2;
			
		}
		//注意：边跟点的关系不是边数=节点数-1；因为边节点两边点相同，则认为是一条边；
		public static double computeQ(List<List<Integer>> structs,List<Integer> particle,int m){
			//在连通子图中，所有节点的度之和=边数*2；从而把Q的计算公式进行等价转换，
			double q = 0.0;
			int size = structs.size();//社区个数
			for (int i=0;i<size;i++){
				int edges = computeEdgesInOneStruct(structs.get(i),particle);
				q += (double) 1.0*edges*(m-edges)/(m*m);
			}		
			return q;
		}
		
		//注：粒子从下标1开始，下标0是无用值；
		   //结构是从下标0开始的
		public static int computeEdgesInOneStruct(List<Integer> struct,List<Integer> particle){
			Set<String> edgeSet = new HashSet<>();
			Set<String> allEdges = new HashSet<>();
			struct.forEach(node->{
				int fromNode = node;
				int toNode = particle.get(fromNode);
				String oneEdge = fromNode+">"+toNode;
				if (!allEdges.contains(oneEdge)){
					edgeSet.add(oneEdge);
					String twoEdge = toNode+">"+fromNode;
					allEdges.add(oneEdge);allEdges.add(twoEdge);
				}
			});
			return edgeSet.size();
		}
		
		public static List<Integer> transferStringArrayToListInteger(String[] data){
			List<Integer> result = new ArrayList<>();
			int len = data.length;
			for (int i = 0;i<len;i++){
				result.add(Integer.parseInt(data[i]));
			}
			return result;
		}
		
		public static Set<Integer> unionSet(Set<Integer> s1 , Set<Integer> s2){
	        Set <Integer> unionSet = new HashSet<Integer>();
	        /*
	         * 利用Set的元素不可重复特性往unionSet中添加元素形成并集
	         */
	       for(Integer s : s1){
	           unionSet.add(s);
	       }
	       for(Integer s : s2){
	            unionSet.add(s);
	       }
	       return unionSet ;
	   }
		
		public static Set<Integer> intersectSet(Set<Integer> s1 , Set<Integer> s2){
			Set <Integer> sameSet = new HashSet<Integer>();
	        /*
	         * 利用ForEach循环和HashSet中的contains方法判断两个Set中元素是否相交
	         * 相交则存入SameSet中
	         */
	        for(Integer s : s1){
	            if(s2.contains(s))
	                sameSet.add(s);
	        }
	        return sameSet;
	    }
}
