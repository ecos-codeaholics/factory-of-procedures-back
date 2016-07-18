package edu.uniandes.ecos.codeaholics.oldmodels;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;

import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.persistence.Episode;


/**
 * Created by snaphuman on 6/8/16.
 */
public class EpisodeModel {

	// Atributos
	private final static Logger log = LogManager.getLogger(EpisodeModel.class);

	// Metodos
	public boolean addEpisode(Episode data) {

		Date date = data.getFecha();
		String time = data.getHora();
		int intensity = data.getNivelDolor();
		int userId = data.getCedula();
		String medicament = data.getMedicamento();
		String activity = data.getActividad();

		Document episode = new Document();

		episode.append("fecha", date)
				.append("hora", time)
				.append("intensidad", intensity)
				.append("cedula", userId)
				.append("medicamento", medicament)
				.append("actividad", activity);

		try {

			DataBaseUtil.save(episode, "episode");
			return true;
		} catch (MongoWriteException e) {

			if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
				log.error("Duplicate episode");
				return false;
			}
			throw e;
		}
	}

	public List<Episode> getList (int id) {

		try {

			List<Episode> dataset = new ArrayList<>();

			Document cedula = new Document();
			cedula.append("cedula", id);

			ArrayList<Document> episodes = DataBaseUtil.find(cedula, "episode");

			for (Document episode: episodes) {
				Episode json = new Episode();

				json.setId(episode.get("_id").toString());
				json.setActividad(episode.get("actividad").toString());
				json.setNivelDolor((int) episode.get("intensidad"));
				json.setCedula((int) episode.get("cedula"));
				json.setFecha((Date) episode.get("fecha"));
				json.setHora(episode.get("hora").toString());
				json.setMedicamento(episode.get("medicamento").toString());

				dataset.add(json);
			}

			return dataset;
		} catch (MongoWriteException e) {

			if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
				System.out.println("Error");
				return new ArrayList<>();
			}
			throw e;

		}
	}

	public List<Episode> getListByDate (int id, Date start, Date end) {

		List<Episode> dataset = new ArrayList<>();

		System.out.println("Entrada");
		System.out.println(start);
		System.out.println(end);

		// Prepare Document
		Document filter = new Document("$and", asList(
				new Document("cedula", new Document("$eq", id)),
				new Document("fecha", new Document("$gte", start)),
				new Document("fecha", new Document("$lte", end))));

		ArrayList<Document> episodes = DataBaseUtil.find(filter, "episode");
		System.out.println(episodes);

		for (Document episode : episodes) {

			Episode json = new Episode();

			json.setId(episode.get("_id").toString());
			json.setActividad(episode.get("actividad").toString());
			json.setNivelDolor((int) episode.get("intensidad"));
			json.setCedula((int) episode.get("cedula"));
			json.setFecha((Date) episode.get("fecha"));
			json.setHora(episode.get("hora").toString());
			json.setMedicamento(episode.get("medicamento").toString());

			dataset.add(json);
		}
		System.out.println(dataset);

		return dataset;
	}


}
