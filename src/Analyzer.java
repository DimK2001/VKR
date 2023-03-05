import com.vm.jcomplex.Complex;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Analyzer
{
	public Boolean running = false;
	public int SearchType;
	private ISearch search;
	private AudioFormat getFormat()
	{
		float sampleRate = 44100;
		int sampleSizeInBits = 8;
		int channels = 1;          //Монофонический звук
		boolean signed = true;     //Флаг указывает на то, используются ли числа со знаком или без
		boolean bigEndian = true;  //Флаг указывает на то, следует ли использовать обратный (big-endian) или прямой (little-endian) порядок байтов
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	Thread thread = new Thread()
	{
		public void run()
		{
			ArrayList<String> hashes = new ArrayList<>();
			ArrayList<String> freqs = new ArrayList<>();
			byte[] buffer = new byte[2048];
			final AudioFormat format = getFormat(); //Заполнить объект класса AudioFormat параметрами
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			final TargetDataLine line;
			try
			{
				line = (TargetDataLine) AudioSystem.getLine(info);
			}
			catch (LineUnavailableException e)
			{
				throw new RuntimeException(e);
			}
			try
			{
				line.open(format, buffer.length);
			}
			catch (LineUnavailableException e)
			{
				throw new RuntimeException(e);
			}
			line.start();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try
			{
				System.out.println("searching...");
				while (running)
				{
					int count = line.read(buffer, 0, buffer.length);
					if (count > 0)
					{
						out.write(buffer, 0, count);
					}
					Complex[][] results = Transform(out);

					Determinator determinator = new Determinator();
					ArrayList<String>[] determinatedData = determinator.Determinate(results);
					hashes = determinatedData[0];
					freqs = determinatedData[1];
				}
				out.close();

				//TODO: search
				switch (SearchType)
				{
					case 0 -> search = new FastSearch();
					case 1 -> search = new DistanceSearch();
				}
				//search.search()
			}
			catch (IOException e)
			{
				System.err.println("I/O problems: " + e);
				System.exit(-1);
			}
		}
	};

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
}
