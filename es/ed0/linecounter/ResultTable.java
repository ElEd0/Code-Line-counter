/**
 * Created by Ed0 in 16 feb. 2019
 */
package es.ed0.linecounter;

import java.util.ArrayList;

public class ResultTable<T> {

	public interface ResultPopulator<T> {
		public ArrayList<String> getViewForRow(int i, T entry);
	}
	
	private String[] cols;
	private ArrayList<T> entries;
	private ResultPopulator<T> populator;
	
	public ResultTable(ArrayList<T> entries, String... cols) {
		this.entries = entries;
		this.cols = cols;
	}
	
	public void setResultPopulator(ResultPopulator<T> populator) {
		this.populator = populator;
	}
	
	public void setData(ArrayList<T> entries) {
		this.entries = entries;
	}
	
	
	private ArrayList<ArrayList<String>> rows;
	
	private ArrayList<Integer> colWidths;
	private int totalWidth = 0;
	
	private final int MARGIN = 2;
	
	public void print() {
		if (populator == null)
			return;
		
		// get rows from populator
		rows = new ArrayList<>(); 
		for (int i = 0; i < entries.size(); i++) {
			rows.add(populator.getViewForRow(i, entries.get(i)));
		}

		// calculate max column widths
		colWidths = new ArrayList<Integer>();
		for (String s : cols) 
			colWidths.add(s.length());
		
		for (int i = 0; i < colWidths.size(); i++) {
			for (int r = 0; r < rows.size(); r++) {
				int valueLength = rows.get(r).get(i).length();
				if (valueLength > colWidths.get(i)) {
					colWidths.remove(i);
					colWidths.add(i, valueLength);
				}
			}
		}
		
		// calculate total width using margin and border
		for (Integer w : colWidths)
			totalWidth += (w + 1 + (2 * MARGIN)); // 2 margin + 1 border
		totalWidth += 1;// remaining border
		
		printLine();
		printData(cols);
		printLine();
		for (int i = 0; i < rows.size(); i++) {
			printData(rows.get(i).toArray(new String[0]));
			printLine();
		}
		
	}
	
//	----------------------------------
//	|  ghgh  |  hujy  |  jhdhdhdhdh  |
	
	private void printLine() {
		for (int i = 0; i < totalWidth; i++)
			System.out.print("-");
		System.out.println();
	}
	
	
	private void printData(String[] data) {
		for (int i = 0; i < data.length; i++) {
			int marginSpace = colWidths.get(i) - data[i].length();
			System.out.print("|");
			for (int m = 0; m < MARGIN; m++) System.out.print(" ");
			System.out.print(data[i]);
			for (int m = 0; m < marginSpace + MARGIN; m++) System.out.print(" ");
			// extra margin if value is even
			//if (data[i].length() % 2 == 0) System.out.print(" ");
		}
		System.out.println("|");
	}
	
	
}
