
public class InstanceOf
{

 /**
  * @param args
  */
 public static void main(String[] args)
 {
  String a = "aaa";

  System.out.println("1: "+("aaa".equals(a)));
  
  a=null;
  
  System.out.println("2: "+("aaa".equals(a)));
 
  System.out.println("3: "+(null instanceof Object));
 }

}
