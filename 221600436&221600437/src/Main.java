
public class Main {

    public static void main(String[] args){
        System.out.println("hello fuck!");
        long start,end;
        start = System.currentTimeMillis();

        String inputFileName="input.txt",outputFileName="result.txt";//默认文件名
        int m=3,n=10,w=1,CodeMode=0;//默认 词组长度,输出数量,是否采用权重,编码模式
        Result tempResult=new Result();
        for (int i=0;i<args.length;i++) {
            switch (args[i]){
                case"-i":
                    System.out.format("input filename:%s\n",args[i+1]);
                    inputFileName=args[i+1];
                    break;
                case "-o":
                    System.out.format("output filename:%s\n",args[i+1]);
                    outputFileName=args[i+1];
                    break;
                case "-m":
                    System.out.format("%s词组词频统计\n",args[i+1]);
                    m=Integer.valueOf(args[i+1]);
                    if(m>100) m=100;
                    break;
                case "-n":
                    System.out.format("%s自定义词频统计输出\n",args[i+1]);
                    n=Integer.valueOf(args[i+1]);
                    if(n>100) n=100;
                    break;
                case "-w":
                    System.out.format("%s带权重词频统计输出\n",args[i+1]);
                    w=Integer.valueOf(args[i+1]);
                    if(w!=1) w=0;
                    break;
                case "-G":
                    System.out.format("Gbk");
                    CodeMode=1;
                    break;
                case "-h":
                    System.out.format("Help");
                    break;
            }
        }
        Process.read_file(inputFileName,outputFileName,tempResult, CodeMode,n,m,w);
        end = System.currentTimeMillis();
        System.out.println("start time:" + start+ "; end time:" + end+ "; Run Time:" + (end - start) + "(ms)");
        try {
            System.in.read();
        }catch (Exception e){

        }
    }
}
