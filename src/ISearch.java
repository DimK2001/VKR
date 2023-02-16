import java.io.File;
import java.util.ArrayList;

public interface ISearch
{
	File file = new File(".\\DB");
	String search(ArrayList<String> data);
	long countDistance(long x, long y);
}
