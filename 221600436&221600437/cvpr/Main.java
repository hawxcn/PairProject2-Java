public class Main {
    public static void main(String[] args) throws Exception {
        long start,end;
        start = System.currentTimeMillis();

        MyRobot pyRobot=new MyRobot();
        System.out.format("Found %d URL\n",pyRobot.initialization("http://openaccess.thecvf.com/CVPR2018.py"));
        //System.out.format("Found %d URL\n",pyRobot.testRobot());
         //pyRobot.testWrite();
        pyRobot.workMethod();

        end = System.currentTimeMillis();
        System.out.println(" Run Time:" + (end - start) + "(ms)");
    }
}
