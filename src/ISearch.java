import java.io.File;
import java.util.ArrayList;

public interface ISearch
{
	File file = new File(".\\DB");
	String[] db = file.list();
	String search(ArrayList<String> data);
}
