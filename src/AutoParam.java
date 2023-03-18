import com.vm.jcomplex.Complex;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AutoParam
{
	//Change param
	private int[] range = new int[] {30, 32, 34, 36, 38};
	private String NAME = "nenavist\'";
	final AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
	public AutoParam() throws UnsupportedAudioFileException, IOException
	{
		for (int i = 0; range[0] < DATA.UPPER_LIMIT - 8; i += 2)
		{
			for (int j = i + 2; range[1] < DATA.UPPER_LIMIT - 6; j += 2)
			{
				for (int k = j + 2; range[2] < DATA.UPPER_LIMIT - 4; k += 2)
				{
					for (int m = k + 2; range[3] < DATA.UPPER_LIMIT - 2; m += 2)
					{
						for (int l = m + 2; range[4] < DATA.UPPER_LIMIT; l += 2)
						{
							range = new int[] {30 + i, 32 + j, 34 + k, 36 + m, 38 + l};
							//Open file EX/////////////////////////////////////////////////////////////////////
							Path path = Paths.get(".\\Music\\" + NAME);
							ArrayList<String>[] determinatedData = openFile(path);
							ArrayList<String> hashesEX = determinatedData[0];
							ArrayList<String> freqsEX = determinatedData[1];

							//Open file Test/////////////////////////////////////////////////////////////////////
							path = Paths.get(".\\TEST\\" + NAME);
							determinatedData = openFile(path);
							ArrayList<String> hashesTest = determinatedData[0];
							ArrayList<String> freqsTest = determinatedData[1];
						}
					}
				}
			}
		}
	}
	private ArrayList<String>[] openFile(Path path) throws UnsupportedAudioFileException, IOException
	{
		File file = new File(String.valueOf(path));
		AudioInputStream in = AudioSystem.getAudioInputStream(file);
		AudioInputStream convert = AudioSystem.getAudioInputStream(format, in);
		byte[] data = new byte[convert.available()];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int count = convert.read(data, 0, data.length);
		if (count > 0)
		{
			out.write(data, 0, count);
		}
		Complex[][] results = Transform(out);
		//Call determinate//////////////////////////////////////////////////////////////////
		ArrayList<String>[] determinatedData = optimize(Determinate(results));
		out.close();
		return determinatedData;
	}
	private ArrayList<String>[] optimize(ArrayList<String>[] data)
	{
		while (data[0].get(data[0].size() - 1).equals("00000000000"))
		{
			data[0].remove(data[0].size() - 1);
		}
		while (data[0].get(0).equals("00000000000"))
		{
			data[0].remove(0);
		}
		while (data[1].get(data[1].size() - 1).equals("0 0 0 0 0"))
		{
			data[1].remove(data[1].size() - 1);
		}
		while (data[1].get(0).equals("0 0 0 0 0"))
		{
			data[1].remove(0);
		}
		return data;
	}
	private Complex[][] Transform(ByteArrayOutputStream out)
	{
		byte[] audio = out.toByteArray();
		final int totalSize = audio.length;
		int amountPossible = totalSize / DATA.CHUNK_SIZE;
		Complex[][] results = new Complex[amountPossible][];

		//Для всех кусков:
		for(int i = 0; i < amountPossible; i++)
		{
			Complex[] complex = new Complex[DATA.CHUNK_SIZE];
			for(int j = 0; j < DATA.CHUNK_SIZE; j++)
			{
				complex[j] = new Complex(audio[(i * DATA.CHUNK_SIZE) + j], 0);
			}
			//Быстрое преобразование фурье
			results[i] = FFT.fft(complex);
		}
		return results;
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
	public ArrayList<String>[] Determinate(Complex[][] results) throws IOException
	{
		ArrayList<String> hashes = new ArrayList<>();
		ArrayList<String> freqs = new ArrayList<>();
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
