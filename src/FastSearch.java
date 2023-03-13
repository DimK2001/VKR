import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FastSearch implements ISearch
{
	@Override
	public String search(ArrayList<String> data)
	{
		// Открытие базы данных с сигнатурами
		File file = new File(".\\DB");
		String[] db = file.list();
		int found = 0;
		int matches = 0;
		for (int i = 0; i < Objects.requireNonNull(db).length; ++i)
		{
			////////////////////////////////////////////////////////// Открытие сигнатуры по хешам
			Path pathHash = Paths.get(".\\HashDB\\" + db[i]);
			List<String> readHash = null;
			try
			{
				readHash = Files.readAllLines(pathHash);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			for (int match : find(data, readHash).values())
			{
				if (match > matches)
				{
					matches = match;
					found = i;
				}
			}
		}
		return db[found];
	}
	private HashMap<String, Integer> find(ArrayList<String> data, List<String> readHash)
	{
		////////////////////////////////////////////////////////// Быстрый поиск по смещению
		HashMap<String, Integer> offset = new HashMap<>();
		for (int j = 0; j < readHash.size(); ++j)
		{
			for (int k = 0; k < data.size(); ++k)
			{
				if (readHash.get(j).equals(data.get(k)))
				{
					if (!offset.containsKey(String.valueOf(j - k)))
					{
						offset.put(String.valueOf(j - k), 1);
					}
					else
					{
						offset.put(String.valueOf(j - k), offset.get(String.valueOf(j - k)) + 1);
					}
				}
			}
		}
		return offset;
	}
}
