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
 * ֻ�����Ϸ������ӣ�
 * */
public class particleMehod2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print(Math.log(24.0/4));
	}
	//������
		public static void runParticelMethod(){
			//1,�����ھӱ�
			   //����д��txt�ļ�
			 String file = "E:\\workspace\\Myjavas\\src\\particle\\example.txt";
			//2,��������
			  //�ڸ��Է�����д�����������ã����λ�ø��º��ٶȸ��£�
			 int populationNum =20;//��Ⱥ��Ŀ�����Ӹ���
			 int maxIter = 300;//������
			 //����ȫ����Ӧ��ֵ����
			 List<Double> global = new ArrayList<>();
			 
			//3��������Ⱥ��ʼ��
			Map<Integer,List<Integer>> netNeighbor = initNetNeighbor(file);
			int m = computeM(netNeighbor);//�ܵı���
			int totleNode = netNeighbor.keySet().size();
			List<List<Integer>> xt = initParticlePosition(totleNode,populationNum,netNeighbor);
			List<List<Double>> vt = initParticleSpeed(totleNode,populationNum);
			//�޳��Ƿ�����
			//4-5-6,������Ӧ��ֵ����(��ʼ���������--���Լ�����;Ȼ���ڸ���������ҳ�ȫ�ֵ�1,2,3������)
			List<List<Integer>> p = xt;
			Map<String,List<Integer>> ps = computePbdAndPcd(p,m);//psֻ��pg��
			
			double a = 0.0;
			global.add(computeQ(findStructInParticle(ps.get("pg")),ps.get("pg"),m));
			int step =1;
			while(step<maxIter&&xt.size()>=2){
				//7������λ�ú��ٶ�
				List<List<Double>> vt_1 = speedUpdate(totleNode,vt,xt,p,ps);
				List<List<Integer>> xt_1 = positionsUpdate(totleNode,a,vt_1,xt,netNeighbor,ps);

				p = findParticleP(p,xt_1,m,netNeighbor);
				a = conpute_a(p,m);
				//���º��ٴμ���ȫ�����ţ�����¼
			    ps = computePbdAndPcd(p,m);
			    global.add(computeQ(findStructInParticle(ps.get("pg")),ps.get("pg"),m));
			    //�ٶ�λ��ͬ�����()
			    vt = vt_1;
			    xt = xt_1;
			    step++;
			}
			//������������е�ȫ����Ӧ��ֵ
			global.forEach(one->{
				System.out.println(one);
			});
			
		}
		
		
		//��ʼ����Ⱥ�ٶ�
		public static List<List<Double>> initParticleSpeed(int totleNode,int populationNum){
			List<List<Double>> result = new ArrayList<>();
			result.add(Arrays.asList(0.0));//�׸����±�Ϊ0��Ĭ�ϣ����Ҳ��������ڡ�
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
		
		//��ʼ����Ⱥλ��
		public static List<List<Integer>> initParticlePosition(int totleNode,int populationNum,Map<Integer,List<Integer>> initNetNeighbor){
			List<List<Integer>> result = new ArrayList<>();
			result.add(Arrays.asList(0));//�׸����±�Ϊ0��Ĭ�ϣ����Ҳ��������ڡ�
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
		
		//�������е�<particle_i,Qֵ>�ҳ�ǰ��������
		public static Map<String,List<Integer>> computePbdAndPcd(List<List<Integer>> data,int m){
			Map<String,List<Integer>> result = findPbAndPcInMapScore(data,m);
			return result;
		}
		
		//����aֵ�����ڶ���͵�����ֵ�ڱ��������ٱ���
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
				//��������
				public int compare(Entry<Integer,Double> o1,Entry<Integer,Double> o2){
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			//�ҳ��ڶ���͵�����ռ��
			int start = 1;
			double first = -1, second = -1, third = -1;
			double count = 0;
			boolean isSec=false,isThird = false;
			for (Map.Entry<Integer, Double> mapp:list){
				if (start==1){
					first = mapp.getValue();
				}
				if (start>1){ //�ҵڶ���ֵ
					if (mapp.getValue()!=first&&!isSec){
						second = mapp.getValue();
						isSec=true;
					}
				}
				if (start>2&&isSec){//�ҳ�������ֵ
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
		
		//��map���ҳ�ǰ�����ֵ
		public static Map<String,List<Integer>> findPbAndPcInMapScore(List<List<Integer>> data,int m){
			Map<Integer,Double> particleQs = new TreeMap<>();
			int len = data.size();
			for (int i=1;i<len;i++){
				double value = computeQ(findStructInParticle(data.get(i)),data.get(i),m);
				particleQs.put(i,value);
			} 
			List<Map.Entry<Integer, Double>> list = new ArrayList<>(particleQs.entrySet());
			Collections.sort(list,new Comparator<Map.Entry<Integer, Double>>(){
				//��������
				public int compare(Entry<Integer,Double> o1,Entry<Integer,Double> o2){
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			Map<String,List<Integer>> result = new HashMap<>();
			//�ҳ��ڶ���͵�����ռ��
			int start = 1;
			double first = -1, second = -1, third = -1;
			boolean isSec=false,isThird = false;
			for (Map.Entry<Integer, Double> mapp:list){
				if (start==1){
					first = mapp.getValue();
					result.put("pg", data.get(mapp.getKey()));
				}
				if (start>1){ //�ҵڶ���ֵ
					if (mapp.getValue()!=first&&!isSec){
						second = mapp.getValue();
						result.put("pb", data.get(mapp.getKey()));
						isSec=true;
					}
				}
				if (start>2&&isSec){//�ҳ�������ֵ
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
		 *���Ӹ������ż�¼���жϵ���ǰ�͵��������ӵ���Ӧ��ֵ
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
		
		//���ӵ�λ�ø���
		public static List<List<Integer>> positionsUpdate(int totleNode,double a,List<List<Double>> vt_1,List<List<Integer>> xt,Map<Integer,List<Integer>> netNeighbor,Map<String,List<Integer>> ps){
			List<List<Integer>> xt_1 = new ArrayList<>();
			xt_1.add(Arrays.asList(0));
			List<Integer> pb = ps.get("pb");
			List<Integer> pc = ps.get("pc");

			int len = xt.size();
			for (int i=1;i<len;i++){//��Ⱥ����ѭ��
				List<Integer> particle = new ArrayList<>();
				particle.add(0);
				for (int d=1;d<=totleNode;d++){//�ڵ�ѭ��
				  double posX_1 = (1-2*a)*xt.get(i).get(d)+a*(pb.get(d)+pc.get(d)-2*xt.get(i).get(d))+vt_1.get(i).get(d)%netNeighbor.get(d).size();
				  int posX_1Ceil = new Double(Math.ceil(posX_1)).intValue();
//				  //ע���п���ȡ������ڽڵ�������������һ���߽紦�������ڵ�������������ھӱ���ȡֵ����֤����ͨ��
//				    //�����ñ߽�ȡֵ��������ѡ��󣬲�����ѡ��С���ᵼ��ͼ����仯��
//				  if (posX_1Ceil>totleNode||posX_1Ceil<1){//���Ľڵ��ǽڵ�����
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
		
		//���ӵ��ٶȸ���
			public static List<List<Double>> speedUpdate(int totleNode,List<List<Double>> vt,List<List<Integer>> xt,List<List<Integer>> p,Map<String,List<Integer>> ps){
				List<List<Double>> vt_1 = new ArrayList<>();
				vt_1.add(Arrays.asList(0.0));
				double w = 0.8;//����Ȩ��
				double c1 = 0.7,c2=0.49;//ѧϰ��
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
		
	    //��Ϊ�Ƿ����Ӳ��ܼ��㣬�޳��Ƿ����ӣ���Ӧ�Ƿ����ӵ��ٶ�Ҳ�޳���
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
		 //�ж�ĳ�������Ƿ�Ϸ�
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
			
			
		 
		//���������ҳ������ṹ,����ͬ��ͼ(�����ж��)
		  //���ӵĴ洢�ǵ�һά��0�����������ڣ�
		public static List<List<Integer>> findStructInParticle(List<Integer> particle){
			List<List<Integer>> result = new ArrayList<>();
			int particleNumber = particle.size();
			Set<Integer> deleteParticle = new HashSet<>();
			for (int i = 1;i < particleNumber;i++){
				if (!deleteParticle.contains(i)){
					Set<Integer> one = new HashSet<>();
					one.add(i);one.add(particle.get(i));
					//��һ��ѡȡ
					for (int j = 1; j < particleNumber; j++){
						Set<Integer> two = new HashSet<>();
						two.add(j);two.add(particle.get(j));
						//ȡ����
						Set<Integer> intersect = intersectSet(one,two);
						if (intersect !=null && intersect.size()>0){
							one.add(j);one.add(particle.get(j));
							deleteParticle.add(j);
						}
					}
					//�ڶ���ѡȡ
					for (int k = 1; k < particleNumber; k++){
						Set<Integer> two = new HashSet<>();
						two.add(k);two.add(particle.get(k));
						//ȡ����
						Set<Integer> intersect = intersectSet(one,two);
						if (intersect !=null && intersect.size()>0){
							one.add(k);one.add(particle.get(k));
							deleteParticle.add(k);
						}
					}
					//�����µĽṹ
					if (one !=null && one.size()>0){
						//�����ṹ�нڵ�������
						List<Integer> list = new ArrayList<Integer>(one);
						Collections.sort(list);
						result.add(list);
					}
				}
			}
			return result;
		}
		
		public static Map<Integer,List<Integer>> initNetNeighbor(String file){
			//��txt�ı��ж�ȡ,��Ϊ��д�ã�\tǰ�ǽڵ��ţ���1��ʼ��\t�����ھ������
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
		
		//���������ܱ���m���ڵ��֮�͵�һ��
		public static int computeM(Map<Integer,List<Integer>> neibors){
			int result = 0;
			Set<Integer> nodes = neibors.keySet();
			for (int node : nodes){
				result += neibors.get(node).size();
			}
			return result/2;
			
		}
		//ע�⣺�߸���Ĺ�ϵ���Ǳ���=�ڵ���-1����Ϊ�߽ڵ����ߵ���ͬ������Ϊ��һ���ߣ�
		public static double computeQ(List<List<Integer>> structs,List<Integer> particle,int m){
			//����ͨ��ͼ�У����нڵ�Ķ�֮��=����*2���Ӷ���Q�ļ��㹫ʽ���еȼ�ת����
			double q = 0.0;
			int size = structs.size();//��������
			for (int i=0;i<size;i++){
				int edges = computeEdgesInOneStruct(structs.get(i),particle);
				q += (double) 1.0*edges*(m-edges)/(m*m);
			}		
			return q;
		}
		
		//ע�����Ӵ��±�1��ʼ���±�0������ֵ��
		   //�ṹ�Ǵ��±�0��ʼ��
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
	         * ����Set��Ԫ�ز����ظ�������unionSet�����Ԫ���γɲ���
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
	         * ����ForEachѭ����HashSet�е�contains�����ж�����Set��Ԫ���Ƿ��ཻ
	         * �ཻ�����SameSet��
	         */
	        for(Integer s : s1){
	            if(s2.contains(s))
	                sameSet.add(s);
	        }
	        return sameSet;
	    }
}
