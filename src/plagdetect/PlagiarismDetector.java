package plagdetect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PlagiarismDetector implements IPlagiarismDetector {
	private int n;
	private Map<String, Set<String>> ngramsInFile;
	private Map<String, Map<String, Integer>> ngramsInCommon;
	
	public PlagiarismDetector(int n) {
		this.n = n;
		this.ngramsInFile = new HashMap<>();
		this.ngramsInCommon = new HashMap<String, Map<String, Integer>>();
	}
	
	@Override
	public int getN() {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public Collection<String> getFilenames() {
		// TODO Auto-generated method stub
		return this.ngramsInFile.keySet();
	}

	@Override
	public Collection<String> getNgramsInFile(String filename) {
		// TODO Auto-generated method stub
		return ngramsInFile.get(filename);
	}

	@Override
	public int getNumNgramsInFile(String filename) {
		// TODO Auto-generated method stub
		return ngramsInFile.get(filename).size();
	}

	@Override
	public Map<String, Map<String, Integer>> getResults() {
		// TODO Auto-generated method stub
		int count = 0;
		Map<String, Map<String, Integer>> results = new HashMap<>();
		
		for (String f1 : ngramsInFile.keySet()) {
			results.put(f1, new HashMap<>());
			
			for (String f2 : ngramsInFile.keySet()) {
				count = 0;
				if (!f1.equals(f2)) {
					for (String gram : ngramsInFile.get(f1)) {
						if (ngramsInFile.get(f2).contains(gram)) {
							count++;
						}
					}
					
					if (count > 0) {
						results.get(f1).put(f2, count);
					}
				}
			}
			
		}
		return results;
	}

	@Override
	public void readFile(File file) throws IOException {
		// TODO Auto-generated method stub
		// most of your work can happen in this method
		Scanner scan = new Scanner(file);
		String sentence = "";
		Set<String> ngrams = new HashSet<>();
		String[] words;
		String ngram = "";
		
		while (scan.hasNextLine()) {
			sentence = scan.nextLine();
			words = sentence.split(" ");
			ngram = "";
			
			for (int i = 0; i < words.length; i++) {
				if (i + n <= words.length) {
					for (int j = i; j < i + n; j++) {
						ngram += " " + words[j];
					}
					ngrams.add(ngram.substring(1));
					ngram = "";
				} else {
					break;
				}
			}
			
		}
		
		this.ngramsInFile.put(file.getName(), ngrams);
		
	}

	@Override
	public int getNumNGramsInCommon(String file1, String file2) {
		// TODO Auto-generated method stub
		int x = 0;
		int y = 0;
		if (ngramsInCommon.get(file1).containsKey(file2)) {
			x = ngramsInCommon.get(file1).get(file2);
		}
		if (ngramsInCommon.get(file2).containsKey(file1)) {
			y = ngramsInCommon.get(file2).get(file1);
		}
		
		if (x > y) {
			return y;
		}
		return x;
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		// TODO Auto-generated method stub
		Set<String> susPairs = new HashSet<>();
		
		for (String f1 : ngramsInFile.keySet()) {
			for (String f2 : ngramsInFile.keySet()) {
				int x = getNumNGramsInCommon(f1, f2);
				
				if (x >= minNgrams) {
					if (f1.compareTo(f2) < 0) {
						susPairs.add(f1 + " " + f2 + " " + x);
					} else if (f1.compareTo(f2) > 0) {
						susPairs.add(f2 + " " + f1 + " " + x);
					}
				}
			}
		}
		
		return susPairs;
	}

	@Override
	public void readFilesInDirectory(File dir) throws IOException {
		// delegation!
		// just go through each file in the directory, and delegate
		// to the method for reading a file
		for (File f : dir.listFiles()) {
			readFile(f);
		}
		
		this.ngramsInCommon = getResults();
	}
}
