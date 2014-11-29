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


public class DF {
	private String _documentPathString = "E:\\java_Eclipse\\FeatureExtraction\\�ĵ�����2";            //�ļ�·��
	private String[] _documentClassNameString;          //�洢�����ĵ����ļ���
	private File _documenFile;                     //��Ŵ��������µ��ļ�
	private DocumentWord[] _documentTextHashMap;   //��ÿƪ����һ��ƪ���ɴ˱�����һ��Ԫ��,�Ա��ڼ�������ʵ��ĵ�Ƶ��
	private HashMap<String, Double> _featureDF;    //��¼���дʵĴ�Ƶ���Ա�����ȷ������Ƶ������ǰ�Ĵ����������Ϊ����
	private HashSet<String> _allWord;              //�����洢�������µ����д���������ظ����Ա�����������
	private FeatureValue[] _featureValue;          //�����洢��ƪ�ĵ��������ʵĴ�Ƶ
	private String[] _featureStrings;              //�����洢��ȡ������������
	private final int _numOfFeature = 300;
	private int _numOfdocument;
	
	public DF() throws IOException{
		_numOfdocument = 0;
		_documenFile = new File(_documentPathString);
		if (!_documenFile.isDirectory())           //�������ļ������Ƿ�Ϊ�ļ���,��Ϊ�����ǰѴ���������±��浽����ļ����µ�,��Ҫ�����ж�
        {
            throw new IllegalArgumentException("����ѡ���ĵ�����ʧ�ܣ� [" +_documentPathString + "]");
        }
        this._documentClassNameString = _documenFile.list();//��������������µ��ļ��������ɹ�,�����д�ŵ��������µ��ļ����洢��_documentNameString��
        for(int i = 0; i < _documentClassNameString.length; i++){
			File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			_numOfdocument = _numOfdocument + tempFile.list().length;
		}//ͳ�����Ͽ���ĵ���Ŀ
        
        
        
        
        
        _documentTextHashMap = new DocumentWord[_numOfdocument];
        _featureStrings = new String[_numOfFeature];
        _featureValue = new FeatureValue[_numOfdocument];
        _featureDF = new HashMap<String,Double>();
        _allWord = new HashSet<String>();
        
        int index = 0;
        for(int i = 0; i < _documentClassNameString.length; i++){
        	File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			String[] textString = tempFile.list();
			for(int j = 0; j < textString.length; j++){
				InputStreamReader isReader =
        			new InputStreamReader(new FileInputStream(_documenFile.getPath() + File.separator + _documentClassNameString[i] + File.separator + textString[j]),"GBK");
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
            	//System.out.println(temp);
                	_allWord.add(tempStrings[jj]);
            	
            	
            	//_documentTextHashSet[i].setallWordHashSet(tempStrings[j]);
                }
                _documentTextHashMap[index].setallWordHashMap(temp);
                _documentTextHashMap[index].setclassNameString(_documentClassNameString[i]);
                index++;
			}
        }//forѭ������,_documentTextHashSet[i]��ÿ��Ԫ�ؾ���һ��DocumentWord���͵ı���,������Hashset�ౣ���˵�iƪ�ĵ������в��ظ��Ĵ�
         //_allWord�洢�����еĴ���
        
        
        String[] tempwordStrings = new String[_allWord.size()];
        _allWord.toArray(tempwordStrings);
        
        //==========================================================================================================
        System.out.println("########################################################");
        for(int i = 0 ; i < tempwordStrings.length; i++){
        	System.out.println(tempwordStrings[i]);
        }
        //==========================================================================================================
        
        
       
        
        //��ʼͳ�Ƹ���������ĵ�ƽ��
        for(int i = 0; i < tempwordStrings.length; i++){
        	double thiswordnum = 0;
        	for(int j = 0; j < _documentTextHashMap.length; j++){
        		if(_documentTextHashMap[j].getallWordHashMap().containsKey(tempwordStrings[i]))
        		{
        			thiswordnum = thiswordnum + 1;
        		}
        	}
        	
        	_featureDF.put(tempwordStrings[i], thiswordnum);
        	
        }//forѭ������_featureDF�б��������еĴʵ��ĵ�Ƶ��,��дsort������_featureDF��valueֵ����
        
		List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(_featureDF.entrySet()); 
		
	    System.out.println(list_Data);///////////////////////////////////////////////////////////////////////////////////////
	    
	    Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>()  
	        {    
	      public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)  
	      {  
	        if ((o2.getValue() - o1.getValue())>0)  
	          return 1;  
	        else if((o2.getValue() - o1.getValue())==0)  
	          return 0;  
	        else   
	          return -1;  
	      }  
	        }  
	        );  
        
	    System.out.println(list_Data);
	    //ȡ����Ƶ����ǰ20�Ĵ�����Ϊ������
	    for(int i = 0; i < _numOfFeature; i++){
	    	_featureStrings[i] = list_Data.get(i).getKey();
	    }
	    
	    
	    
	    System.out.println("000000000000000000000000000000000000000000");
	    for(int i = 0; i < _numOfFeature; i++){
	    	System.out.println(_featureStrings[i]);
	    }
	    System.out.println("000000000000000000000000000000000000000000");
	    
	    for(int i = 0; i < _documentTextHashMap.length; i++){
	    	
	    	_featureValue[i] = new FeatureValue();
	    	_featureValue[i].setdocumentClassString(_documentTextHashMap[i].getclassNameString());//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	    	
	    	for(int j = 0; j < _featureStrings.length; j++){
	    		String featureString = _featureStrings[j];
	    		double value = _featureDF.get(featureString);
	    		if(_documentTextHashMap[i].getallWordHashMap().containsKey(_featureStrings[j]))
	    			_featureValue[i].setfeatureValueHashMap(featureString, _documentTextHashMap[i].getallWordHashMap().get(_featureStrings[j]));
	    		else {
	    			_featureValue[i].setfeatureValueHashMap(featureString,0.0);
				}
	    	}
	    	
	    }
	    System.out.println("==============================================================================");
	    System.out.println(list_Data.size());
	    System.out.println(_featureDF.size());
	    System.out.println(tempwordStrings.length);
	    System.out.println(_featureDF);
        
	}
	
	public FeatureValue[] getFeatureValue(){
		return _featureValue;
	}
	
	
}
