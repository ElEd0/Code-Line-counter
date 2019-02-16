/**
 * Created by Ed0 in 25 ene. 2019
 */
package es.ed0.linecounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LineCounter {

	public static void main(String[] args) {
		new LineCounter();
	}
	
	private ArrayList<Profile> profiles;
	
	
	private LineCounter() {
		
		System.out.println(".____    .__              _________                      __                ");
		System.out.println("|    |   |__| ____   ____ \\_   ___ \\  ____  __ __  _____/  |_  ___________ ");
		System.out.println("|    |   |  |/    \\_/ __ \\/    \\  \\/ /  _ \\|  |  \\/    \\   __\\/ __ \\_  __ \\");
		System.out.println("|    |___|  |   |  \\  ___/\\     \\___(  <_> )  |  /   |  \\  | \\  ___/|  | \\/");
		System.out.println("|_______ \\__|___|  /\\___  >\\______  /\\____/|____/|___|  /__|  \\___  >__|   ");
		System.out.println("        \\/       \\/     \\/        \\/                  \\/          \\/       ");

		final Scanner t = new Scanner(System.in);
		
		boolean stop = false;
		while (!stop) {
			profiles = Profile.getProfiles();
			
			System.out.println("Select a profile");
			System.out.println("0 - new profile");
			for (int i = 1; i <= profiles.size(); i++)
				System.out.println(i + " - " + profiles.get(i - 1).getName());
			
			try {
				int pIndex = Integer.valueOf(t.nextLine());
				if (pIndex == 0)
					createProfile();
				else
					profiles.get(pIndex - 1).printResults();
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				System.err.println("Invalid profile option!");
				e.printStackTrace();
			}
		}
		
		t.close();
		
	}
	
	private void createProfile() {
		final Scanner t = new Scanner(System.in);
		System.out.print(" - Profile name: ");
		String name = t.nextLine();
		System.out.print(" - Proyect paths (separated by commas): ");
		ArrayList<Project> ps = new ArrayList<Project>();
		for (String p : Profile.getArrayFromCsv(t.nextLine(), ","))
			try {
				ps.add(new Project(p));
			} catch (IOException e) {
				System.err.println("Project " + p + " could not be found.");
			}
		System.out.print(" - Code file extensions (separated by commas): ");
		ArrayList<String> exts = Profile.getArrayFromCsv(t.nextLine(), ",");
		
		Profile prof = new Profile(name, ps, exts);
		prof.serialize();
		
		t.close();
	}
	
	
}
