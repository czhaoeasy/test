package z;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		
		
		File file = new File("F:/Kettle.pdf");
		File outfile = new File("F:/Kettle.pdf.Z");
		BufferedInputStream bufferedInput = null;
		BufferedOutputStream bufferedOut = null;
		try {
			bufferedInput = new BufferedInputStream(new FileInputStream(file));
			bufferedOut = new BufferedOutputStream(new FileOutputStream(outfile));
			Compress.spec_select_action(bufferedInput,bufferedOut,0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(bufferedInput != null){
					bufferedInput.close();
					bufferedInput = null;
				}
				if(bufferedOut != null){
					bufferedOut.close();
					bufferedOut = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
