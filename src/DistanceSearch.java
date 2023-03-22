import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DistanceSearch implements ISearch
{
	private long Distance = 10000000;
	private long countDistance(long x, long y)
	{
		return (long)(5 * (1 + Math.cbrt(Math.abs(x - y) + 1)));
	}
	@Override
	public String search(ArrayList<String> data) throws IOException
	{
		File file = new File(".\\DB");
		String[] db = file.list();
		int found = 0;
		for (int i = 0; i < Objects.requireNonNull(db).length; ++i)
		{
			Path pathFr = Paths.get(".\\DB\\" + db[i]);
			List<String> readFr = null;
			try
			{
				readFr = Files.readAllLines(pathFr);
			}
			catch (IOException e)
			{
				throw new Error(e);
			}
			if (Files.lines(pathFr).count() > data.size())
			{
				for (int j = 0; j < Files.lines(pathFr).count() - data.size(); ++j)
				{
					int dist = find(readFr, data, j);
					if (dist < Distance)
					{
						found = i;
						Distance = dist;
					}
				}
			}
			else
			{
				for (int j = 0; j < data.size() - Files.lines(pathFr).count(); ++j)
				{
					int dist = find(data, readFr, j);
					if (dist < Distance)
					{
						found = i;
						Distance = dist;
					}
				}
			}
		}
		return db[found];
	}

	public int find(List<String> dataBig, List<String> dataSmall, int j)
	{
		int res = 0;
		for (int f = 0; f < dataSmall.size(); ++f)
		{
			String[] wordsS = dataSmall.get(f).split("\\s+");
			String[] wordsB = dataBig.get(f + j).split("\\s+");
			for (int k = 0; k < 5; ++k)
			{
				res += countDistance(Long.parseLong(wordsS[k]), Long.parseLong(wordsB[k]));
			}
			if (res > Distance)
			{
				break;
			}
		}
		return res;
	}
}
