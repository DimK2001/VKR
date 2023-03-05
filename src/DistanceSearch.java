import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DistanceSearch implements ISearch
{
	private long countDistance(long x, long y)
	{
		return (long) Math.sqrt(Math.abs(x - y));
	}
	@Override
	public String search(ArrayList<String> data)
	{
		int found = 0;
		long distance = 10000000;
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
				throw new RuntimeException(e);
			}
			try
			{
				if (Files.lines(pathFr).count() > data.size())
				{
					for (int j = 0; j < Files.lines(pathFr).count() - data.size(); ++j)
					{
						int dS = 0;
						for (int f = 0; f < data.size(); ++f)
						{
							String[] wordsF = data.get(f).split("\\s+");
							String[] wordsR = readFr.get(f + j).split("\\s+");
							for (int k = 0; k < 5; ++k)
							{
								dS += countDistance(Long.parseLong(wordsF[k]), Long.parseLong(wordsR[k]));
							}
							if (dS > distance)
							{
								break;
							}
						}
						if (dS < distance) {
							found = i;
							distance = dS;
						}
					}
				}
				else
				{
					for (int j = 0; j < data.size() - Files.lines(pathFr).count(); ++j)
					{
						int dS = 0;
						for (int f = 0; f < readFr.size(); ++f)
						{
							String[] wordsF = data.get(f + j).split("\\s+");
							String[] wordsR = readFr.get(f).split("\\s+");
							for (int k = 0; k < 5; ++k)
							{
								dS += countDistance(Long.parseLong(wordsF[k]), Long.parseLong(wordsR[k]));
							}
						}
						if (dS < distance) {
							found = i;
							distance = dS;
						}
					}
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return db[found];
	}
}
