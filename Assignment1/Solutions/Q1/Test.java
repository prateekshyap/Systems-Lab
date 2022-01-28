import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

class Test
{
	public static void main(String[] args)throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = br.readLine();
		System.out.println("Hello, you entered: "+input);
	}
}