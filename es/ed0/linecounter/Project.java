/**
 * Created by Ed0 in 16 feb. 2019
 */
package es.ed0.linecounter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Project {
	
	
	private String path;
	private File root;
	private ArrayList<File> files;
	
	public Project(String path) throws IOException {
		this.path = path;
		this.root = new File(path);
		if (!root.exists() || !root.isDirectory())
			throw new IOException("Project path doesnt exist");
	}
	
	public void explore(ArrayList<String> extensions) {
		files = exploreAllFiles(root, extensions);
	}
	
	public boolean isExplored() {
		return files != null;
	}
	
	private ArrayList<File> exploreAllFiles(File f, ArrayList<String> extensions) {
		ArrayList<File> files = new ArrayList<File>();
		if(f.isFile()) {
			int indx = f.getName().lastIndexOf('.') + 1;
			if (indx != -1) {
				String ext = f.getName().substring(indx);
				for (String e : extensions)
					if (ext.equals(e))
						files.add(f);
			}
		} else
			for(File son : f.listFiles())
				files.addAll(exploreAllFiles(son, extensions));
		return files;
	}
	

	public String getPath() {
		return path;
	}
	
	public List<File> getLongerFiles(int max) {
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File f0, File f1) {
				return (int) (getTotalLinesForFile(f1) - getTotalLinesForFile(f0));
			}
		});
		return files.subList(0, (max >= files.size() ? files.size() : max));
	}
	

	public static long getTotalLinesForFile(File f) {
		long total = 0;
		try {
			final BufferedReader br = new BufferedReader(new FileReader(f));
			while(br.readLine() != null)
				total++;
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return total;
	}


	public long getTotalLines() {
		long total = 0;
		for(File f : files) total += getTotalLinesForFile(f);
		return total;
	}
	
	public String getSize() {
		double s = 0;
		for(File f : files) s += f.length();
		
		final StringBuilder sb = new StringBuilder();
		int curr = 0;
		boolean keep = true;
		while(keep)
			if(s > 1024) {
				s /= 1024;
				curr++;
			} else
				keep = false;
		
		s = ((double) ((int) (s * 100))) / 100;
		
		sb.append(s + "");
		switch(curr) {
		case 0: sb.append("bytes"); break;
		case 1: sb.append("KB"); break;
		case 2: sb.append("MB"); break;
		case 3: sb.append("GB"); break;
		case 4: sb.append("TB"); break;
		default: sb.append("PB"); break;
		}
			
		return sb.toString();
	}
}
