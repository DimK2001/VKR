import com.vm.jcomplex.Complex;
public class FFT
{
	public static Complex[] fft(Complex[] x)
	{
		int n = x.length;
		//Базовый случай
		if (n == 1) return new Complex[] { x[0] };

		//Проверка n - степень 2, для алгоритма Кули — Тьюки
		if (n % 2 != 0)
		{
			throw new IllegalArgumentException("n не делится на 2");
		}

		//Для четных
		Complex[] even = new Complex[n/2];
		//Для нечетных
		Complex[] odd = new Complex[n/2];
		for (int k = 0; k < n/2; ++k)
		{
			even[k] = x[2*k];
			odd[k] = x[2*k + 1];
		}
		Complex[] evenFFT = fft(even);
		Complex[] oddFFT = fft(odd);

		//Объединение
		Complex[] freqs = new Complex[n];
		for (int k = 0; k < n/2; ++k)
		{
			double kth = -2 * k * Math.PI / n;
			Complex complexExp = new Complex(Math.cos(kth), Math.sin(kth)).multiply(oddFFT[k]);
			freqs[k]        = evenFFT[k].add (complexExp);
			freqs[k + n/2]  = evenFFT[k].add (complexExp).negate();
		}
		return freqs;
	}
}
