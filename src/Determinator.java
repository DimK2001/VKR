import com.vm.jcomplex.Complex;

import java.io.IOException;
import java.util.ArrayList;

class Determinator
{
	public final int[] RANGE = new int[] { 50, 100, 220, 350, DATA.CHUNK_SIZE +1 };
	private ArrayList<String> hashes = new ArrayList<>();
	private ArrayList<String> freqs = new ArrayList<>();

	// Функция для определения того, в каком диапазоне находится частота
	private int getIndex(int freq)
	{
		int i = 0;
		while (RANGE[i] < freq)
		{
			i++;
		}
		return i;
	}

	public ArrayList<String>[] Determinate(Complex[][] results) throws IOException
	{
		double[] highscores = new double[DATA.CHUNK_SIZE];
		int[] recordPoints = new int[DATA.CHUNK_SIZE];

		for (int i = 0; i < results.length; ++i)
		{
			for (int freq = DATA.LOWER_LIMIT; freq < DATA.CHUNK_SIZE - 1; ++freq)
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
			highscores = new double[DATA.CHUNK_SIZE];
			recordPoints = new int[DATA.CHUNK_SIZE];
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
}