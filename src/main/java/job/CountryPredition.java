package job;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import Entity.CountryFrequencyEntity;
import Entity.CountryWordFrequencyEntity;
import Entity.PreditionResultEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class CountryPredition {
    private Map<String, Map<String, CountryWordFrequencyEntity>> countryWordMap;//<国家<单词，CountryWordFrequencyEntity>>
    private Map<String, CountryFrequencyEntity> countryMap;//<国家，当前国家包含先验概率的相关数据>
    private double totalDifferentWord;//总的单词种类数量
    private Map<String, Double> countryTotalWordMap;//每个国家所有单词的数量
    private static final String[] Country = new String[]{"CHINA", "AUSTR","UK"};//测试文档的国家类别

    public CountryPredition() {
    }
    //根据hadoop的输出对每个国家的单词进行条件概率的计算。
    public void readCountryWord(String path) throws IOException,NullPointerException {
        this.countryWordMap = new HashMap();
        this.countryTotalWordMap = new HashMap();

        File file = new File(path);
        double total1=0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            File[] fileLists = files;
            int filesLength = files.length;

            for(int i = 0; i < filesLength; i++) {
                File inFile = fileLists[i];
                total1+=this.readCountryWordFile(inFile);
            }
        } else {
            total1+=this.readCountryWordFile(file);
        }

        this.totalDifferentWord =total1;//训练样本中不重复特征词总数

        Iterator var16 = this.countryWordMap.keySet().iterator();

        while(var16.hasNext()) {
            String key1 = (String)var16.next();
            Map<String, CountryWordFrequencyEntity> oneCountryMap = (Map)this.countryWordMap.get(key1);
            Iterator var10 = oneCountryMap.keySet().iterator();

            while(var10.hasNext()) {
                String key2 = (String)var10.next();
                CountryWordFrequencyEntity wf= oneCountryMap.get(key2);
                //考虑测试集部分单词在训练集没有的情况，在分子分母加上平滑因子，
                double probability = (double)(wf.getCount() + 1) / (((double)this.countryTotalWordMap.get(key1))+ total1);
    //按照学规算法，当多个足够小的小数相乘时会出现下溢而得不到准确的结果，通过对数运算将求积运算转换成求和运算
                wf.setProbability(Math.log(probability));//存放条件概率在contryWordMap中
            }
        }

    }
    //读取计算条件概率在hadoop代码中的输出结果
    private double readCountryWordFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        Set<String> wordCountSet=new HashSet<String>();
        String[] strs;
        CountryWordFrequencyEntity wf;
        Map map;
        for(String str = null; (str = br.readLine()) != null; ) {
            strs = str.split("\t");
            wordCountSet.add(strs[1]);
            if (this.countryTotalWordMap.get(strs[0]) == null) {//计算每个国家所有单词的数量-存放在countryTotalWordMap当中-
                this.countryTotalWordMap.put(strs[0], Double.valueOf(strs[2]).doubleValue());
            } else {
                this.countryTotalWordMap.put(strs[0], ((Double)this.countryTotalWordMap.get(strs[0])).doubleValue() + Double.valueOf(strs[2]).doubleValue());
            }

            wf = new CountryWordFrequencyEntity(strs[0], strs[1], Integer.valueOf(strs[2]).intValue());//将 国家 、单词 、单词数量存放在这个Map中

            map = this.countryWordMap.get(strs[0]);
            if (map == null) {
                map = new HashMap();
                this.countryWordMap.put(strs[0], map);
            }
            ((Map)map).put(strs[1], wf);
        }
        return wordCountSet.size();
    }
    //对每个国家的先验概率进行计算，
    public void readCountry(String path) throws IOException {
        this.countryMap = new HashMap();
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String str = null;
        int total = 0;

        CountryFrequencyEntity cf;
        while((str = br.readLine()) != null) {
            String[] strs = str.split(",");
            int count = Integer.valueOf(strs[1]).intValue();
            total += count;
            cf = new CountryFrequencyEntity(strs[0], count);
            this.countryMap.put(strs[0], cf);
        }

        Iterator var12 = this.countryMap.keySet().iterator();

        while(var12.hasNext()) {
            String key = (String)var12.next();
            cf = (CountryFrequencyEntity)this.countryMap.get(key);
            cf.setProbability(Math.log(((double)cf.getCount()) / ((double)total)));//
            this.countryMap.put(key, cf);//存放先验概率
        }

    }
    //对阶段性的中间结果进行打印输出
    public void print() {
        Iterator var2 = this.countryTotalWordMap.keySet().iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            System.out.print("国家：" + key + "  " + "总单词数：" + this.countryTotalWordMap.get(key));
            System.out.println(" 单词类别数：" + ((Map)this.countryWordMap.get(key)).size());
        }

        System.out.println("字典大小为：" + this.totalDifferentWord);
    }
    //读取测试文档，根据根据之前的中间结果对测试文档进行预测
    public List<PreditionResultEntity> predition(String path) throws IOException {
        List<PreditionResultEntity> result = new ArrayList();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            File[] fileArray = files;
            int fileLength = files.length;

            for(int var6 = 0; var6 < fileLength; var6++) {
                File inFile = fileArray[var6];
                String fileName = inFile.getName();
                FileInputStream fis = new FileInputStream(inFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String str = null;
                double probability1 = ((CountryFrequencyEntity)this.countryMap.get(Country[0])).getProbability();
                double probability2 = ((CountryFrequencyEntity)this.countryMap.get(Country[1])).getProbability();
                double probability3 = ((CountryFrequencyEntity)this.countryMap.get(Country[2])).getProbability();
                //根据中间结果求最终的概率-求对数-求积转化成求和
                while((str = br.readLine()) != null) {
                    if (((Map)this.countryWordMap.get(Country[0])).get(str) != null) {
                        probability1 += this.countryWordMap.get(Country[0]).get(str).getProbability();//单词
                    } else {
                        probability1 += Math.log(1.0D / (((double)this.countryTotalWordMap.get(Country[0])) + this.totalDifferentWord));
                    }

                    if (((Map)this.countryWordMap.get(Country[1])).get(str) != null) {
                        probability2 += ((CountryWordFrequencyEntity)((Map)this.countryWordMap.get(Country[1])).get(str)).getProbability();
                    } else {
                        probability2 += Math.log(1.0D / (((double)this.countryTotalWordMap.get(Country[1])) + this.totalDifferentWord));
                    }

                    if (((Map)this.countryWordMap.get(Country[2])).get(str) != null) {
                        probability3 += ((CountryWordFrequencyEntity)((Map)this.countryWordMap.get(Country[2])).get(str)).getProbability();
                    } else {
                        probability3 += Math.log(1.0D / (((double)this.countryTotalWordMap.get(Country[2])) + this.totalDifferentWord));
                    }
                }

                String country = null;
                //比较对每个类别的计算概率-哪个就是哪个类别
                if (probability1 > probability2) {
                    if(probability1>probability3)
                        country = Country[0];
                    else
                        country= Country[2];
                } else {
                    if(probability2>probability3)
                        country = Country[1];
                    else
                        country= Country[2];
                }

                result.add(new PreditionResultEntity(fileName, probability1, probability2, probability3,country));
            }
        }

        return result;
    }
    //主函数-控制流程，并且根据文档的预测结果计算精度和召回率。
    public static void main(String[] args) throws IOException {
        CountryPredition cp = new CountryPredition();
        cp.readCountry("E:\\hadoop/CountryPrior/part-r-00000");
        cp.readCountryWord("E:\\hadoop/CountryWordConditional/part-r-00000");
        cp.print();
        List<PreditionResultEntity> result = cp.predition("E:\\hadoop/test");
        Iterator var4 = result.iterator();
        float CTP=0,CTN=0,CFP=0,CFN=0;
        float ATP=0,ATN=0,AFP=0,AFN=0;
        float UTP=0,UTN=0,UFP=0,UFN=0;
        while(var4.hasNext()) {
            //打印每个文档的真实国家和预测国家
            PreditionResultEntity pr = (PreditionResultEntity)var4.next();
            System.out.print(pr.getFileName() + " " );//+ " 概率" + Country[0] + "：" + pr.getProbability1() + " ");
            //System.out.print(" 概率" + Country[1] + "：" + pr.getProbability2() + " ");
            //System.out.print(" 概率" + Country[2] + "：" + pr.getProbability3() + " ");
            System.out.println(" 国家：" + pr.getCountry());


            if(pr.getFileName().startsWith("AUSTR")&& pr.getCountry().equals("AUSTR")){
                ATP++;//true positive
            }
            if(pr.getFileName().startsWith("AUSTR")&& !pr.getCountry().equals("AUSTR")){
                AFN++;//false negative
            }
            if(!pr.getFileName().startsWith("AUSTR")&& pr.getCountry().equals("AUSTR")){
                AFP++;//false positive
            }


            if(pr.getFileName().startsWith("CHINA")&& pr.getCountry().equals("CHINA")){
                CTP++;//true positive
            }
            if(pr.getFileName().startsWith("CHINA")&& ! pr.getCountry().equals("CHINA")){
                CFN++;//false negative
            }
            if(! pr.getFileName().startsWith("CHINA")&& pr.getCountry().equals("CHINA")){
                CFP++;//false positive
            }

            if(pr.getFileName().startsWith("UK")&& pr.getCountry().equals("UK")){
                UTP++;//true positive
            }
            if(pr.getFileName().startsWith("UK")&& ! pr.getCountry().equals("UK")){
                UFN++;//false negative
            }
            if(! pr.getFileName().startsWith("UK")&& pr.getCountry().equals("UK")){
                UFP++;//false positive
            }



        }
        //对每个国家类别的文档计算精确性和召回率心腹F1 score
        float precisionA=ATP/(ATP+AFP);//精确性
        float recallA=ATP/(ATP+AFN);//召回率
        float F1ScoreA=2*precisionA*recallA/(precisionA+recallA);//F1 score
        System.out.println("ATP:"+ATP+",AFN:"+AFN+",AFP:"+AFP);
        System.out.println("---AUSTR-- precisionA:"+precisionA+",recallA:"+recallA+",F1ScoreA:"+F1ScoreA);

        float precisionC=CTP/(CTP+CFP);
        float recallC=CTP/(CTP+CFN);
        float F1ScoreC=2*precisionC*recallC/(precisionC+recallC);
        System.out.println("CTP:"+CTP+",CFN:"+CFN+",CFP:"+CFP);
        System.out.println("---CHINA--- precisionC:"+precisionC+",recallC:"+recallC+",F1ScoreC:"+F1ScoreC);

        float precisionU=UTP/(UTP+UFP);
        float recallU=UTP/(UTP+UFN);
        float F1ScoreU=2*precisionU*recallU/(precisionU+recallU);
        System.out.println("UTP:"+UTP+",UFN:"+UFN+",UFP:"+UFP);
        System.out.println("---UK--- precisionC:"+precisionU+",recallC:"+recallU+",F1ScoreC:"+F1ScoreU);
        //用两种方式计算总的precision
        //Macroaveraged precision
        float MacAvePrecision=(precisionA+precisionC+precisionU)/3;
        System.out.println("Macroaveraged precision:"+MacAvePrecision);
        //Microaveraged precision
        float MicAvePrecision=(ATP+CTP+UTP)/((ATP+CTP+UTP)+(AFP+CFP+UFP));
        System.out.println("Microaveraged precision:"+MicAvePrecision);



    }
}
