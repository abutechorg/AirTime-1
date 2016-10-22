package utilities;

/**
 * Created by Hp on 10/18/2016.
 */
public class DataFactory {
    public boolean isValidAmount(String amount){
        try{
            double check = Double.parseDouble(amount);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
