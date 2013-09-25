package util;
/**
 *
 * This class is for finding coefficient of similarity of the two words
 * using Viterbi(Bellman) algorithm
 * @author Sergey
 */
public class ApproximateStringAnalyzer {
   
    private double init[][];
    private double result[][];
    private int n;
    private int m;
  
    private void calculateASCIIDifference(String str1, String str2) {
     try{
        n = str1.length();
        m = str2.length();
        init = new double[n][m];
        for (int i = n - 1,i2 = 0; i >= 0; i--, i2++) {
            for (int j = 0; j < m; j++) {
                 init[i2][j] = Math.abs((int) str1.charAt(i) - (int) str2.charAt(j));
            }
        }
     } catch(Exception e){
         System.out.println("calculateASCIIDifference exception: "+e.toString());
        }
    }
  
    public double[][] getASCIIDifference() {
        return init;
    }
  
    public void setASCIIDifference(double[][] a,int n, int m) {
        this.n = n;
        this.m = m;
        this.init = a;
    }
  
    // Calculate matrix by values from calculateASCIIDifference()
    public double calculateSimilarity(String str1, String str2) {
      
     calculateASCIIDifference(str1,str2);   
     try{
         result = new double[n][m];
         double res, val1, val2, val3;
   
         //initialize first element
         result[n - 1][0] = init[n - 1][0];
          
         //initialize first column
         for (int i = n - 2; i >= 0; i--) {
             result[i][0] = result[i + 1][0] + init[i][0];
         } 
         //initialize first row(from down)
         for (int j = 1; j < m; j++) {
             result[n - 1][j] = result[n - 1][j - 1] + init[n - 1][j];
         } 
         //initialize others
         for (int i = n - 2; i >= 0; i--) {
             for (int j = 1; j < m; j++) {
                 val1 = result[i][j - 1] + init[i][j];
                 val2 = result[i + 1][j] + init[i][j];
                 val3 = result[i + 1][j - 1] + (init[i][j] * 2);
   
                 //minimum of the 3 val's
                 res = val1 < val2 ? val1 : val2;
                 res = res < val3 ? res : val3;
   
                 result[i][j] = res;
             }
         }
         return getSimilarityValue();
     }
     catch(Exception e){
      System.out.println("calculateSimilarity exception: "+e.toString());
     }
     return -1;
    }
    //getting result of Similarity
    public double getSimilarityValue() {
     //need to devide if m != n
      if(m != n)
         return result[0][m - 1] / (m + n);
      else
      return result[0][m - 1];
    }
  
    //getting result array
    public double[][] getSimilarityArray() {
        return result;
    }
}
