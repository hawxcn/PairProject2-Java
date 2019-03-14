//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class Try {
//    private static void process_line_withPhrase(String str, Result resultClass, int number, int weight) {
//        Pattern p = Pattern.compile("(^|[^a-z0-9])([a-z]{4,}[a-z0-9]*)");
//        Matcher m = p.matcher(str);
//        PhraseFactory phraseFactory = new PhraseFactory(number);
//        PhraseBorder phraseBorder;//result
//        while (m.find()) {
//            phraseFactory.storeBorder(m.start(), m.end());
//            if ((phraseBorder = phraseFactory.getPhrase()) != null)
//                resultClass.addWord(str.substring(phraseBorder.start, phraseBorder.end), weight);
//
//        }
//    }
//}
//class PhraseFactory {//用于生成短语
//    private static int iterator = 0;
//    private static int size;
//    private PhraseBorder[] pool;
//
//    public PhraseFactory(int sizeNumber) {
//        size = sizeNumber;
//        pool = new PhraseBorder[size];
//        for (int i = 0; i < size; i++) {
//            pool[i] = new PhraseBorder();
//        }
//    }
//
//    public void storeBorder(int start, int end) {//添加新的单词位置
//        pool[iterator].start = start;
//        addIterator();
//        pool[iterator].end = end;
//    }
//
//    public static void addIterator() {//指针++
//        iterator = ++iterator % size;
//    }
//
//    public PhraseBorder getPhrase() {
//        //Phrase通常位于 指针的位置
//        return pool[iterator].start < pool[iterator].end ? new PhraseBorder(pool[iterator].start, pool[iterator].end) : null;// 若边界结构可生成短语则返回否则返回NULL
//    }
//}
//
//class PhraseBorder {
//    public int start = 9999;//start > end 短语不成立
//    public int end = 0;
//
//    PhraseBorder() {
//    }
//
//    PhraseBorder(int s, int e) {
//        start = s;
//        end = e;
//    }
//}