import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Arrays;

public class Main
{
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException
	{
		System.out.println("Hello World!");
		AutoParam autoParam = new AutoParam();
		System.out.println(Arrays.toString(autoParam.CountParam()));
		System.out.println("End");
	}
}