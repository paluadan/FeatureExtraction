package english_feature_extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class IG {
	private String _documentPathString = "E:\\java_Eclipse\\FeatureExtraction\\�ĵ�����2";            //�ļ�·��
	private String[] _documentClassNameString;          //�洢�����ĵ����ļ���
	private File _documenFile;                          //��Ŵ��������µ��ļ�
	private DocumentWord[] _documentTextHashMap;        //��ÿƪ����һ��ƪ���ɴ˱�����һ��Ԫ��,�Ա��ڼ�������ʵ��ĵ�Ƶ��
	private HashMap<String, Double> _featureIG;         //��¼���дʵĴ�Ƶ���Ա�����ȷ������Ƶ������ǰ�Ĵ����������Ϊ����
	private HashSet<String> _allWord;                   //�����洢�������µ����д���������ظ����Ա�����������
	private FeatureValue[] _featureValue;               //�����洢��ƪ�ĵ��������ʵ��ĵ�Ƶ��
	private String[] _featureStrings;                   //�����洢��ȡ������������
	private final int _numOfFeature = 300;              //��������Ŀ
	private HashMap<String, Integer> _numOfClassDocument;                  //��¼ÿ������µ������м�ƪ
	private int _numOfdocument;                         //��¼�������µ�����
	
	
	public IG() throws IOException{
		_numOfdocument = 0;
		_numOfClassDocument = new HashMap<String, Integer>();
		
		_documenFile = new File(_documentPathString);
		if (!_documenFile.isDirectory())           //�������ļ������Ƿ�Ϊ�ļ���,��Ϊ�����ǰѴ���������±��浽����ļ����µ�,��Ҫ�����ж�
        {
            throw new IllegalArgumentException("����ѡ���ĵ�����ʧ�ܣ� [" +_documentPathString + "]");
        }
		this._documentClassNameString = _documenFile.list();//��������������µ��ļ��������ɹ�,�����д�ŵ��������µ��ļ����洢��_documentNameString��
		
		for(int i = 0; i < _documentClassNameString.length; i++){
			File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			_numOfdocument = _numOfdocument + tempFile.list().length;
			_numOfClassDocument.put(_documentClassNameString[i], tempFile.list().length);
		}//ͳ�����Ͽ���ĵ���Ŀ
		
		
		_documentTextHashMap = new DocumentWord[_numOfdocument];
        _featureStrings = new String[_numOfFeature];
        _featureValue = new FeatureValue[_numOfdocument];
        _featureIG = new HashMap<String,Double>();
        _allWord = new HashSet<String>();
        
        int index = 0;
		for(int i = 0; i < _documentClassNameString.length; i++){
			File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			String[] textString = tempFile.list();
			for(int j = 0; j < textString.length; j++){
				InputStreamReader isReader =
	        			new InputStreamReader(new FileInputStream(_documenFile.getPath() + File.separator +_documentClassNameString[i] + File.separator + textString[j]),"GBK");
	            BufferedReader reader = new BufferedReader(isReader);
	            String aline;
	            StringBuilder sb = new StringBuilder();
	            while ((aline = reader.readLine()) != null)
	            {
	                sb.append(aline + " ");
	            }
	            isReader.close();
	            reader.close(); 
	            String[] tempStrings1 =sb.toString().split(" ");
	            StopWordsHandler.setstopWordsList();
	            Vector<String> v1 = new Vector<String>();
	            
	            for(int ii = 0; ii < tempStrings1.length; ++ii)
	            {
	                if(StopWordsHandler.IsStopWord(tempStrings1[ii]) == false)
	                {//����ͣ�ô�
	                    v1.add(tempStrings1[ii]);
	                }
	            }
	            String[] tempStrings = new String[v1.size()];//��vector������ת�����ַ��������Ա��������
	            v1.toArray(tempStrings);
	            
	            _documentTextHashMap[index] = new DocumentWord();
	            
	            //����ǰ��ƪ���µĴ���洢��Hashset��,�ﵽȥ�ظ���Ŀ��,ͬʱΪ�������ͳ�ƴ�����ĵ�Ƶ��
	            HashMap<String, Double> temp = new HashMap<String,Double>();
	            
	            for(int jj = 0; jj < tempStrings.length; jj++){
	            	if(temp.containsKey(tempStrings[jj])){
	            		double value = temp.get(tempStrings[jj]) + 1;
	            		temp.put(tempStrings[jj], value);
	            	}
	            	else {
	            		temp.put(tempStrings[jj], 1.0);
					}
	            	_allWord.add(tempStrings[jj]);

	            }//ͳ�ƺ�����������ƪ���µĴ�Ƶ�ٴ洢����Ӧ��_documentTextHashMap��ȥ
	            _documentTextHashMap[index].setallWordHashMap(temp);
	            _documentTextHashMap[index].setclassNameString(_documentClassNameString[i]);
	            index++;
	            
			}
			
		}//��forѭ��������_allWord�д洢�����еĲ��ظ��Ĵʣ�_documentTextHashMap�д洢�˸�ƪ���µĴ�Ƶ�������������еĳ������Hashmap��keyֵ���ж���ƪ�����Ƿ�����ض�����
		
		String[] tempwordStrings = new String[_allWord.size()];
        _allWord.toArray(tempwordStrings);
		
        //���濪ʼ����ÿ���ʵ���Ϣ����
        for(int i = 0; i < tempwordStrings.length; i++){
        	double Pc_i;     //ci���ĵ������Ͽ��г��ֵĸ���
        	double Pt = 0.0;       //���Ͽ��а�������t���ĵ��ĸ���
        	double nPt;      //Ԥ�Ͽ��в���������t���ĵ��ĸ���
        	double Pc_i_t;   //�ĵ���������tʱ����ci����������
        	double Pc_i_nt;  //�ĵ�����������tʱ����ci����������
        	double tempNumOfDocumentContainT = 0.0;
        	double sum1 = 0.0;
        	double sum2 = 0.0;
        	double sum3 = 0.0;
        	
        	//��Ϣ������㹫ʽ�е��������ʽ
        	
        	for(int j = 0; j < _documentClassNameString.length; j++){
        		Pc_i = (double)_numOfClassDocument.get(_documentClassNameString[j])/(double)_numOfdocument;
        		sum1 = sum1 + (0-Pc_i*Math.log(Pc_i));
        	}
        	
        	for(int j = 0; j < _documentTextHashMap.length; j++){
        		if(_documentTextHashMap[j].getallWordHashMap().containsKey(tempwordStrings[i])){
        			tempNumOfDocumentContainT = tempNumOfDocumentContainT + 1.0;
        		}//��¼�°�������t�ĵ��ĵ�����
        	}
        	Pt = tempNumOfDocumentContainT/_numOfdocument;
        	nPt = 1-Pt;
        	
        	for(int j = 0; j < _documentClassNameString.length; j ++){
        		double temp1 = 0.0;//���Լ�¼������ָ���࣬�ְ�������t���ĵ���
        		double temp2 = 0.0;//���Լ�¼������ָ���࣬������������t���ĵ���
        		for(int k = 0; k < _documentTextHashMap.length; k++){
        			if(_documentTextHashMap[k].getclassNameString().equals(_documentClassNameString[j])){
        				if(_documentTextHashMap[k].getallWordHashMap().containsKey(tempwordStrings[i])){
        					temp1 = temp1 + 1.0;//������ָ���࣬�ְ�������t���ĵ���
        				}
        				else {
							temp2 = temp2 + 1.0;//����ָ���࣬������������t���ĵ���
						}
        			}
        		}
        		
        		Pc_i_t = temp1/tempNumOfDocumentContainT;
        		Pc_i_nt = temp2/(_numOfdocument-tempNumOfDocumentContainT);
        		sum2 = sum2 + Pc_i_t * Math.log(Pc_i_t);
        		sum3 = sum3 + Pc_i_nt * Math.log(Pc_i_nt);
        		
        	}
        	sum2 = sum2 * Pt;
        	sum3 = sum3 * nPt;
        	double result = sum1 + sum2 + sum3;
        	_featureIG.put(tempwordStrings[i], result);

        }//forѭ������_featureIG�д洢�����дʵ���Ϣ����ֵ�������������ѡ��
        List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(_featureIG.entrySet()); 
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
	    Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>(){
	    	public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2){ 
	        if ((o2.getValue() - o1.getValue())>0)  
	        	return 1;  
	        else if((o2.getValue() - o1.getValue())==0)  
	        	return 0;  
	        else   
	        	return -1;  
	        }  }  );  
	    
	    System.out.println(list_Data);
	    
	    //��ȡ��ǰn����������Ϊ��������
	    for(int i = 0; i < _numOfFeature; i++){
	    	_featureStrings[i] = list_Data.get(i).getKey();
	    }
	    
	    //��tf��������ռ�
	    for(int i = 0; i < _documentTextHashMap.length; i++){
	    	
	    	_featureValue[i] = new FeatureValue();
	    	_featureValue[i].setdocumentClassString(_documentTextHashMap[i].getclassNameString());//�洢����
	    	
	    	for(int j = 0; j < _featureStrings.length; j++){
	    		String featureString = _featureStrings[j];
	    		if(_documentTextHashMap[i].getallWordHashMap().containsKey(_featureStrings[j]))
	    			_featureValue[i].setfeatureValueHashMap(featureString, _documentTextHashMap[i].getallWordHashMap().get(_featureStrings[j]));
	    		else {
	    			_featureValue[i].setfeatureValueHashMap(featureString,0.0);
				}
	    	}
	    }
		
	}
	
	public FeatureValue[] getFeatureValue(){
		return _featureValue;
	}
}

