import com.vm.jcomplex.Complex;

import java.io.IOException;
import java.util.ArrayList;

public class AutoParam
{
	//Change param
	private int[] range = new int[] {30, 32, 34, 36, 38};
	public AutoParam()
	{
		for (int i = 0; range[0] < DATA.UPPER_LIMIT - 8; i += 2)
		{
			for (int j = 0; range[1] < DATA.UPPER_LIMIT - 6; j += 2)
			{
				for (int k = 0; range[2] < DATA.UPPER_LIMIT - 4; k += 2)
				{
					for (int m = 0; range[3] < DATA.UPPER_LIMIT - 2; m += 2)
					{
						for (int l = 0; range[4] < DATA.UPPER_LIMIT; l += 2)
						{
							range = new int[] {30 + i, 32 + j, 34 + k, 36 + m, 38 + l};
							//TODO: open file
							//TODO: call determinate
						}
					}
				}
			}
		}
	}
	private int getIndex(int freq)
	{
		int i = 0;
		while (range[i] < freq)
		{
			i++;
		}
		return i;
	}
	//Determinate
	private ArrayList<String> hashes = new ArrayList<>();
	private ArrayList<String> freqs = new ArrayList<>();
	public ArrayList<String>[] Determinate(Complex[][] results) throws IOException
	{
		double[] highscores = new double[DATA.UPPER_LIMIT];
		int[] recordPoints = new int[DATA.UPPER_LIMIT];

		for (int i = 0; i < results.length; ++i)
		{
			for (int freq = DATA.LOWER_LIMIT; freq < DATA.UPPER_LIMIT - 1; ++freq)
			{
				//Получим силу сигнала
				double mag = Math.log(results[i][freq].abs() + 1);

				//Выясним, в каком мы диапазоне
				int index = getIndex(freq);

				//Сохраним самое высокое значение силы сигнала и соответствующую частоту
				if (mag > highscores[index])
				{
					highscores[index] = mag;
					recordPoints[index] = freq;
				}
			}
			//Составление хеша
			long h = hash(recordPoints[0], recordPoints[1], recordPoints[2], recordPoints[3], recordPoints[4]);
			freqs.add(recordPoints[0] + " " + recordPoints[1] + " " + recordPoints[2] + " " + recordPoints[3] + " " + recordPoints[4]);
			if (h == 0)
			{
				hashes.add(i, "00000000000");
			}
			else
			{
				hashes.add(i, String.valueOf(h));
			}
			highscores = new double[DATA.UPPER_LIMIT];
			recordPoints = new int[DATA.UPPER_LIMIT];
		}
		return new ArrayList[]{hashes, freqs};
	}
	private static final int FUZ_FACTOR = 2;
	private long hash(long p1, long p2, long p3, long p4, long p5)
	{
		return   ((p5 - (p5 % FUZ_FACTOR)) * 100000000
				+ (p4 - (p4 % FUZ_FACTOR)) * 1000000
				+ (p3 - (p3 % FUZ_FACTOR)) * 10000
				+ (p2 - (p2 % FUZ_FACTOR)) * 100
				+ (p1 - (p1 % FUZ_FACTOR)));
	}
	//TODO: Search and write distance or matches
}
