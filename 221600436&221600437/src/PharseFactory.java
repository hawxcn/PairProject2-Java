class PhraseFactory {//用于生成短语
    private int formerIterator;
    private int iterator = 0;//end
    private int startPoint = 0;
    private int size;
    private PhraseBorder[] pool;

    public PhraseFactory(int sizeNumber) {//循环列表实现构造短语
        size = sizeNumber;
        formerIterator = size - 1;
        pool = new PhraseBorder[size];
        for (int i = 0; i < size; i++) {
            pool[i] = new PhraseBorder();
        }
    }

    public boolean isFull() {
        return (iterator + 1) % pool.length == startPoint;
    }

    public boolean isEmpty() {
        return iterator == startPoint;
    }

    public PhraseBorder storeBorder(int start, int end) {//添加新的单词位置
        if (!isFull()) {
            if (isFormerEnd(start)) {
                pool[iterator].start = start;
                pool[iterator].end = end;
                addIterator();
            } else {
                clear();
                pool[iterator].start = start;
                pool[iterator].end = end;
                addIterator();
            }
        } else {
            if (isFormerEnd(start)) {
                pool[iterator].start = start;
                pool[iterator].end = end;
                addIterator();
                int outputStart = pool[startPoint].start;
                addStartPoint();//后移开头指针等于输出
                return new PhraseBorder(outputStart, end);
            } else {
                clear();
                pool[iterator].start = start;
                pool[iterator].end = end;
                addIterator();
            }
        }
        return null;
    }

    public void addStartPoint() {//tou部指针++
        startPoint = ++startPoint % size;
    }

    public void addIterator() {//尾部指针++
        iterator = ++iterator % size;
        formerIterator = ++formerIterator % size;
    }

    public boolean isFormerEnd(int start) {
        return isEmpty() || pool[formerIterator].end == start;
    }

    public void clear() {//清除构造池
        iterator = 0;//end
        startPoint = 0;
        formerIterator = size - 1;
    }
}

class PhraseBorder {
    public int start = -1;
    public int end = -1;

    PhraseBorder() {
    }

    PhraseBorder(int s, int e) {
        start = s;
        end = e;
    }
}