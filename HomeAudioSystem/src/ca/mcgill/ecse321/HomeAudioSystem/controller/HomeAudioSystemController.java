package ca.mcgill.ecse321.HomeAudioSystem.controller;

import ca.mcgill.ecse321.HomeAudioSystem.persistence.PersistenceXStream;

import java.sql.Date;

import ca.mcgill.ecse321.HomeAudioSystem.model.Album;
import ca.mcgill.ecse321.HomeAudioSystem.model.Artist;
import ca.mcgill.ecse321.HomeAudioSystem.model.HAS;

public class HomeAudioSystemController {
	
	public HomeAudioSystemController ()
	{
	}
	
	public void add_Album(String title, String genre, Date date) throws InvalidInputException
	{
		String error = "";
		if (title == null || title.trim().length() == 0)
			error = error + "Album title cannot be empty! ";
		if (genre == null || genre.trim().length() == 0)
			error = error + "Album genre cannot be empty! ";
		if (date == null)
			error = error + "Album release date cannot be empty! ";
		error = error.trim();
		if (error.length() > 0)
			throw new InvalidInputException(error);
		
		Album ab = new Album(title, genre, date);
		HAS has = HAS.getInstance();
		has.addAlbum(ab);
		PersistenceXStream.saveToXMLwithXStream(has);
	}
	
	public void delete_Album(String title, String genre, Date date) throws InvalidInputException
	{
		
	}
}
