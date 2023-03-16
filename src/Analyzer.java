import com.vm.jcomplex.Complex;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Analyzer
{
	public Boolean running = false;
	public int SearchType;
	public String Result = "Не найдено.";
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
			ArrayList<String> data = new ArrayList<>();
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
				switch (SearchType)
				{
					case 0 :
						search = new FastSearch();
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
							data = determinatedData[0];
						}
						out.close();
						break;
					case 1 :
						search = new DistanceSearch();
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
							data = determinatedData[1];
						}
						out.close();
						break;
				}
				Result = search.search(data);
			}
			catch (IOException e)
			{
				System.err.println("I/O problems: " + e);
				System.exit(-1);
			}
		}
	};

	public void CreateBase() throws UnsupportedAudioFileException, IOException
	{
		ArrayList<String> hashes = new ArrayList<>();
		ArrayList<String> freqs = new ArrayList<>();
		//BASE/////////////////////////////////////////////////////////////////
		final AudioFormat format = getFormat();
		File musicBase = new File(".\\Music");
		String[] music = musicBase.list();
		for (String m : music)
		{
			Path path = Paths.get(".\\Music\\" + m);
			File file = new File(String.valueOf(path));
			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioInputStream convert = AudioSystem.getAudioInputStream(format, in);
			byte[] data = new byte[convert.available()];

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try
			{
				int count = convert.read(data, 0, data.length);
				if (count > 0)
				{
					out.write(data, 0, count);
				}
				Complex[][] results = Transform(out);

				Determinator determinator = new Determinator();
				ArrayList<String>[] determinatedData = determinator.Determinate(results);
				hashes = determinatedData[0];
				freqs = determinatedData[1];
				out.close();
				while (hashes.get(hashes.size() - 1).equals("00000000000"))
				{
					hashes.remove(hashes.size() - 1);
				}
				while (hashes.get(0).equals("00000000000"))
				{
					hashes.remove(0);
				}
				path = Paths.get(".\\HashDB\\" + m).normalize();
				String st = path.toString();
				int index = st.indexOf(".");
				String name = st.substring(0, index);
				System.out.println(name);
				path = Paths.get(name + ".txt");
				Files.write(path, hashes, StandardCharsets.UTF_8);

				while (freqs.get(freqs.size() - 1).equals("0 0 0 0 0"))
				{
					freqs.remove(freqs.size() - 1);
				}
				while (freqs.get(0).equals("0 0 0 0 0"))
				{
					freqs.remove(0);
				}
				path = Paths.get(".\\DB\\" + m).normalize();
				index = path.toString().indexOf(".");
				name = path.toString().substring(0, index);
				System.out.println(name);
				path = Paths.get(name + ".txt");
				Files.write(path, freqs, StandardCharsets.UTF_8);
			}
			catch (IOException e)
			{
				System.err.println("I/O problems: " + e);
				System.exit(-1);
			}
		}
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
}
