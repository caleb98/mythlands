package net.mythlands.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.data.util.Pair;
import org.springframework.util.StringUtils;

public class NameGenerator {

	private String nameFormat;
	private HashMap<Character, ArrayList<String>> characterClasses = new HashMap<>();
	private Random rand;
	
	public NameGenerator(String nameFormat) {
		this.nameFormat = nameFormat;
		rand = new Random();
	}
	
	public NameGenerator(String nameFormat, long seed) {
		this.nameFormat = nameFormat;
		rand = new Random(seed);
	}
	
	public void setSeed(long seed) {
		rand.setSeed(seed);
	}
	
	public void addClass(char charClass, String... values) {
		// Disallow using control characters as the class
		if("[]:,()".contains("" + charClass)) {
			throw new IllegalArgumentException("charClass may not be a control character: " + charClass);
		}
		
		if(!characterClasses.containsKey(charClass)) {
			characterClasses.put(charClass, new ArrayList<String>());
		}
		for(String value : values) {
			characterClasses.get(charClass).add(value);
		}
	}
	
	public String generateName() {
		return Arrays.stream(generateName(nameFormat).split(" "))
				.map(StringUtils::capitalize)
				.collect(Collectors.joining(" "));
	}
	
	public String generateName(String nameFormat) {
		char[] format = nameFormat.toCharArray();
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < format.length; i++) {
			char marker = format[i];
			
			// Check for character class marker
			if(characterClasses.containsKey(marker)) {
				ArrayList<String> choices = characterClasses.get(marker);
				sb.append(choices.get(rand.nextInt(choices.size())));
			}
			// Check for OR. Format [weight:pattern,weight:pattern,...]
			else if(marker == '[') {
				int end = -1;
				int depth = 1;
				ArrayList<Pair<String, Integer>> inners = new ArrayList<>();
				String currentWeight = "";
				String currentPattern = "";
				boolean parsingWeight = true;
				int totalWeight = 0;
				
				// Search for ending ]
				for(int k = i + 1; k < format.length; k++) {
					char innerMarker = format[k];
					
					// Adjust depth if encountering a new brace
					if(innerMarker == '[')  {
						depth++;
						currentPattern += innerMarker;
					}
					else if(innerMarker == ']') {
						depth--;
						if(depth == 0) {
							end = k;
							int weight = Integer.parseInt(currentWeight);
							inners.add(Pair.of(currentPattern, weight));
							totalWeight += weight;
							break;
						}
						else {
							currentPattern += innerMarker;
						}
					}
					
					// Check for a switch from weight to pattern
					else if(innerMarker == ':' && depth == 1 && parsingWeight) {
						parsingWeight = false;
					}
					
					// Add inner pattern to list if encountering a , at depth 1
					else if(innerMarker == ',' && depth == 1) {
						int weight = Integer.parseInt(currentWeight);
						inners.add(Pair.of(currentPattern, weight));
						totalWeight += weight;
						
						currentWeight = "";
						currentPattern = "";
						parsingWeight = true;
					}
					
					// Otherwise, just append the current character to the inner pattern
					else if(parsingWeight) {
						currentWeight += innerMarker;
					}
					else {
						currentPattern += innerMarker;
					}
				}
				
				// Loop through to find the element that was selected
				int selected = rand.nextInt(totalWeight);
				int high = 0;
				for(int k = 0; k < inners.size(); k++) {
					high += inners.get(k).getSecond();
					if(selected < high) {
						sb.append(generateName(inners.get(k).getFirst()));
						break;
					}
				}
				
				i = end;
			}
			
			// Check for OPTIONAL. Format (odds:pattern)
			else if(marker == '(') {
				int end = -1;
				int depth = 1;
				
				String oddsString = "";
				String pattern = "";
				boolean parsingOdds = true;
				float odds = 0;
				
				// Search for ending )
				for(int k = i + 1; k < format.length; k++) {
					char innerMarker = format[k];
					
					// Adjust depth if encountering a new parentheses
					if(innerMarker == '(')  {
						depth++;
						pattern += innerMarker;
					}
					else if(innerMarker == ')') {
						depth--;
						// If depth is zero, we're at the matching closing parentheses
						if(depth == 0) {
							end = k;
							odds = Float.parseFloat(oddsString);
							break;
						}
						else {
							pattern += innerMarker;
						}
					}
					// Check for a switch from weight to pattern
					else if(innerMarker == ':' && depth == 1 && parsingOdds) {
						parsingOdds = false;
					}					
					// Otherwise, just append the current character to the inner pattern
					else if(parsingOdds) {
						oddsString += innerMarker;
					}
					else {
						pattern += innerMarker;
					}
				}
				
				// Add the pattern with the given odds
				if(rand.nextFloat() < odds) {
					sb.append(generateName(pattern));
				}
				
				i = end;
			}
			
			// Otherwise, just add the character
			else {
				sb.append(marker);
			}
			
		}
		
		return sb.toString();
	}
	
}
