package com.lgcns.test.suno.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.lgcns.test.suno.model.ICollection;
import com.lgcns.test.suno.model.IEntity;

public class FileLineReader {
	private String filename;
	private BufferedReader reader;
	
	public FileLineReader(String filename) {
		this.filename = filename;
		try {
			reader = new BufferedReader(new FileReader(this.filename));
			
		} catch (FileNotFoundException e) {
			System.out.println("File could not fouind. " + this.filename);
			System.out.println(e.getMessage());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			
//		} finally {
//			
		}
	}
	
	@Override
	public String toString() {
		return super.toString();
	}


	public IEntity readOnce(IEntity entity) {
		String line;
		
		try {
			line = reader.readLine();
			if (line != null) {
				return entity.fromString(line);
			} else {
				reader.close();
				return null;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
	public ICollection readAll(IEntity entity, ICollection collection) {
		String line;
		IEntity item; 
		
		try {
			
			while ((line = reader.readLine()) != null) {
				item = entity.fromString(line);
				collection = collection.append(item);
			}
			
			reader.close();
			
			return collection;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return null;
	}
}

