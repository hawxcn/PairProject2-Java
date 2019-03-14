
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Process {
    //Result resultClass;
//    public Process(Result result_class){
//        resultClass=result_class;
//    }
    static Pattern phrasePattern = Pattern.compile("([a-zA-Z]{4,}[a-zA-Z0-9]*(?:[^a-zA-Z0-9]+|$))");
    static Pattern wordPattern = Pattern.compile("(?:^|[^a-zA-Z0-9])([a-zA-Z]{4,}[a-zA-Z0-9]*)");//
    static Pattern contentPattern = Pattern.compile("[\\x21-\\x2f\\x3a-\\x7e]");
    static Pattern titlePattern = Pattern.compile("Title: ");
    static Pattern abstractPattern = Pattern.compile("Abstract: ");
    static Pattern OtherCodePattern = Pattern.compile("[^\\x00-\\x7f]");
    private static void commonProcess(Result resultClass, int mode,int i,String s,int l){
    }
    private static String removeOtherCode(String s){
        Matcher m1 = OtherCodePattern.matcher(s);
        return m1.replaceAll("");
    }
    private static String replaceOtherCode(String s){
        Matcher m1 = OtherCodePattern.matcher(s);
        return m1.replaceAll(" ");
    }
    public static void read_file(String file_name, String outputFileName, Result resultClass, int CodeMode, int outputSize, int mode, int weight) {//1 GBK
        MyBufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            //设置文件编码，解决文件乱码问题
            //将字节流转换为字符流，实际上使用了一种设计模式——适配器模式
            InputStreamReader isr;
            if (CodeMode == 1) {//选择编码
                isr = new InputStreamReader(new FileInputStream(file_name), "GBK");
            } else isr = new InputStreamReader(new FileInputStream(file_name), StandardCharsets.UTF_8);
            //UTF-8
            bufferedReader = new MyBufferedReader(isr);
            bufferedWriter = new BufferedWriter(new FileWriter(outputFileName));
            //每次读一行开始
            String s;
            int allLine = 0;
            int i = 0;//统计有效行数
            int lastLine = 0;//统计有效行的实际行数
            if (mode == 1) {//1单词模式
                if (weight == 1) {//带权重
                    while ((s = bufferedReader.readLine()) != null) {
                        allLine++;
                        if (hasContent(s)) {//判断是否有除数字外的可显示字符和内容
                            i++;
                            resultClass.line_count_plus();
                            CutHeadResult r = cutHeadWithWeight(s);//输出含有判断权重结果的结构,清除无关信息的字符串
                            s=replaceOtherCode(r.resultStr);
                            resultClass.char_count_plus(removeOtherCode(r.resultStr).length());//统计字符数
                            process_line_withRegularExpression(s, resultClass, r.weight);
                            lastLine = allLine;
                        }

                    }
                } else {//不带权重
                    while ((s = bufferedReader.readLine()) != null) {
                        allLine++;
                        if (hasContent(s)) {//判断是否有可显示字符和除数字外的内容
                            i++;
                            resultClass.line_count_plus();
                            s = cutHead(s);
                            resultClass.char_count_plus(removeOtherCode(s).length());//统计字符数
                            s=replaceOtherCode(s);
                            process_line_withRegularExpression(s, resultClass,1);
                            lastLine = allLine;
                        }
                    }
                }
            } else {//词组模式
                if (weight == 1) {//带权重
                    while ((s = bufferedReader.readLine()) != null) {
                        allLine++;
                        if (hasContent(s)) {//判断是否有可显示字符和除数字外的内容
                            i++;
                            resultClass.line_count_plus();
                            CutHeadResult r = cutHeadWithWeight(s);//输出含有判断权重结果的结构,清除无关信息的字符串
                            resultClass.char_count_plus(removeOtherCode(r.resultStr).length());//统计字符数
                            s=replaceOtherCode(r.resultStr);
                            process_line_withPhrase(s, resultClass, mode, r.weight);
                            lastLine = allLine;
                        }
                    }
                } else {//不带权重
                    while ((s = bufferedReader.readLine()) != null) {
                        allLine++;
                        if (hasContent(s)) {//判断是否有可显示字符和除数字外的内容
                            i++;
                            resultClass.line_count_plus();
                            s = cutHead(s);
                            resultClass.char_count_plus(removeOtherCode(s).length());//统计字符数
                            s=replaceOtherCode(s);
                            process_line_withPhrase(s, resultClass, mode,1);
                            lastLine = allLine;
                        }
                    }
                }
            }


            System.out.println(i);
            if (lastLine == allLine)
                resultClass.char_count_plus(i - 1);
            else
                resultClass.char_count_plus(i);
            //统计换行符 实际行数减一

            //每次读一行结束

            //System.out.println(countChar);
            //结果写入文件
            bufferedWriter.write(String.format("characters: %s\n", resultClass.getChar_count()));
            System.out.format("characters: %s\n", resultClass.getChar_count());
            //bufferedWriter.newLine();//按行读取，写入一个分行符，否则所有内容都在一行显示

            bufferedWriter.write(String.format("words: %s\n", resultClass.getWord_count()));
            System.out.format("words: %s\n", resultClass.getWord_count());
            //bufferedWriter.newLine();

            bufferedWriter.write(String.format("lines: %s\n", resultClass.getLine_count()));
            System.out.format("lines: %s\n", resultClass.getLine_count());
            //bufferedWriter.newLine();

            //输出单词排序
            List<Map.Entry<String, Integer>> list = resultClass.sort();
            for (int ii = 0; ii < outputSize && ii < list.size(); ii++) {//输出前10个数据
                System.out.format("<%s>: %s\n", list.get(ii).getKey(), list.get(ii).getValue());
                bufferedWriter.write(String.format("<%s>: %s\n", list.get(ii).getKey(), list.get(ii).getValue()));
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String cutHead(String str) {
        Matcher m1 = titlePattern.matcher(str);
        if (m1.lookingAt()) {//
            return str.substring(m1.end());//找到title头切头
        }
        Matcher m2 = abstractPattern.matcher(str);
        if (m2.lookingAt()) {
            return str.substring(m2.end());//找到title头切头
        }
        return str;//找不到 原样返回
    }

    public static CutHeadResult cutHeadWithWeight(String str) {
        Matcher m1 = titlePattern.matcher(str);
        if (m1.lookingAt()) {//
            return new CutHeadResult(10, str.substring(m1.end()));//找到title头切头
        }
        Matcher m2 = abstractPattern.matcher(str);
        if (m2.lookingAt()) {
            return new CutHeadResult(1, str.substring(m2.end()));//找到title头切头
        }
        return new CutHeadResult(1, str);//找不到 原样返回
    }

    public static String trim(String str) {//切除头尾不可显示字符 ASCII中的33个控制字符和空格（0-32、127）
        int var1 = str.length();
        int var2 = 0;

        for (; var2 < var1 && (str.charAt(var2) < 33 || str.charAt(var2) == 127); ++var2) {
        }

        while (var2 < var1 && (str.charAt(var1 - 1) < 33 || str.charAt(var1 - 1) == 127)) {
            --var1;
        }
        if (var1 == var2) return null;//字符串去空白字符为空后则返回null
        return var2 <= 0 && var1 >= str.length() ? str : str.substring(var2, var1);
    }

    public static int trimTail(String str, int start, int end) {//找到尾部除了分隔符的位置
        int var1 = str.length() < end ? str.length() : end;
        int var2 = start;

        while (var2 < var1 && (str.charAt(var1 - 1) < 48 || str.charAt(var1 - 1) > 122 || (str.charAt(var1 - 1) > 57 && str.charAt(var1 - 1) < 65) || (str.charAt(var1 - 1) > 90 && str.charAt(var1 - 1) < 97))) {
            --var1;
        }
        return var1;
    }

    public static boolean isSplit(String str) {
        Pattern p = Pattern.compile("[a-zA-Z0-9]+");
        Matcher m = p.matcher(str);
        return !m.find();
    }

    public static boolean hasContent(String str) {//判断是否有除数字外的非空白ascii字符
        Matcher m = contentPattern.matcher(str);
        return m.find();
    }


    private static void process_line_withRegularExpression(String str, Result resultClass, int weight) {
        Matcher m = wordPattern.matcher(str);
        while (m.find()) {
            resultClass.addWord(m.group(1), weight);
        }
    }
    private static void process_line_withPhrase(String str, Result resultClass, int number,int weight) {
        Matcher m = phrasePattern.matcher(str);
        PhraseFactory phraseFactory = new PhraseFactory(number);
        PhraseBorder phraseBorder;//result
        while (m.find()) {
            if (m.start() == 0 || !Character.isDigit(str.charAt(m.start() - 1))){
                resultClass.word_count_plus();
                if ((phraseBorder = phraseFactory.storeBorder(m.start(), m.end())) != null) {
                    int newEnd = trimTail(str, phraseBorder.start, phraseBorder.end);
                    //System.out.println(str.substring(phraseBorder.start, newEnd));
                    resultClass.addPhrase(str.substring(phraseBorder.start, newEnd), weight);
                }
            }
        }
    }
}


class CutHeadResult {
    int weight;
    String resultStr;

    CutHeadResult(int w, String resultString) {
        weight = w;
        resultStr = resultString;
    }
}
//废弃代码 存在BUG
//    public static void process_line(char char_str[], Result resultClass) {
//        //final int  isAlpha=4;//几个字母以上才开始算单词
//        int length = char_str.length;
//        int alphaCount = 0;
//        boolean emptyLine = true;
//        for (int i = 0; i < length; i++) {
//            if (emptyLine) {
//                if (!isBlank(char_str[i])) {
//                    emptyLine = false;
//                    resultClass.line_count_plus();//add line count
//                }
//            }
//            if (char_str[i] > 0x7F) continue;//is_ ascii?
//            resultClass.char_count_plus();
//            switch (alphaCount) {
//                case 0:
//                    if (Character.isAlphabetic(char_str[i])) {
//                        alphaCount++;
//                        continue;
//                    }else {
//                        alphaCount = 0;
//                    }
//                    break;
//                case 1:
//                case 2:
//                case 3:
//                case 4:
//                    if (Character.isAlphabetic(char_str[i])) {// 1-4
//                        alphaCount++;
//                        continue;
//                    }else{
//                        alphaCount=0;
//                        continue;
//                    }
//                default:// >4
//                    if (isSplit(char_str[i])) {
//                        String word = new String(char_str, i - alphaCount, alphaCount);
//                        //System.out.format("i:%d  account:%d \n",i,alphaCount);
//                        resultClass.addWord(word, 1);
//                        alphaCount=0;
//                        continue;
//                    }else{
//                        alphaCount++;
//                    }
//                    break;
//            }
//        }
//        if (alphaCount > 3) {
//            String word = new String(char_str, length - alphaCount, alphaCount);
//            resultClass.addWord(word, 1);
//        }
//    }
//
//    public static void process_line_with_phrase(char char_str[], Result resultClass,int phrase_length) {
//        //final int  isAlpha=4;//几个字母以上才开始算单词
//        int length = char_str.length;
//        int alphaCount = 0;
//        boolean emptyLine = true;
//        int currentWordNum=0;
//        int pharseStart = 0;
//        for (int i = 0; i < length; i++) {
//            if (emptyLine) {
//                if (!isBlank(char_str[i])) {
//                    emptyLine = false;
//                    resultClass.line_count_plus();//add line count
//                }
//            }
//            if (char_str[i] > 0x7F) continue;//is_ ascii?
//            resultClass.char_count_plus();
//            switch (alphaCount) {
//                case 0:
//                    if (Character.isAlphabetic(char_str[i])) {
//                        alphaCount++;
//                        if(currentWordNum==0) pharseStart=i;
//                        continue;
//                    }else {
//                        alphaCount = 0;
//                        currentWordNum=0;
//                    }
//                    break;
//                case 1:
//                case 2:
//                case 3:
//                case 4:
//                    if (Character.isAlphabetic(char_str[i])) {// 1-4
//                        alphaCount++;
//                        continue;
//                    }else{
//                        alphaCount=0;
//                        currentWordNum=0;
//                        continue;
//                    }
//                default:// >4
//                    if (isSplit(char_str[i])) {
//                        if(currentWordNum<phrase_length){
//                            currentWordNum++;
//                            alphaCount=0;
//                            continue;
//                        }else {
//                            String word = new String(char_str,pharseStart , i-pharseStart);
//                            //System.out.format("i:%d  account:%d \n",i,alphaCount);
//                            resultClass.addWord(word, 1);
//                            alphaCount=0;
//                            currentWordNum=0;
//                        }
//                    }else{
//                        alphaCount++;
//                    }
//                    break;
//            }
//        }
//        if(currentWordNum<phrase_length){
//            currentWordNum++;
//            alphaCount=0;
//        }else {
//            String word = new String(char_str,pharseStart , length-pharseStart);
//            //System.out.format("i:%d  account:%d \n",i,alphaCount);
//            resultClass.addWord(word, 1);
//            alphaCount=0;
//            currentWordNum=0;
//        }
//    }
//
//    private static boolean isSplit(char a) {
//        return !Character.isAlphabetic(a) && !Character.isDigit(a);
//    }
//
//    private static boolean isBlank(char a) {
//        switch (a) {
//            case ' ':
//            case '\n':
//            case '\t':
//            case 11:
//            case '\r':
//            case '\f':
//                return true;
//            default:
//                return false;
//        }
//    }


