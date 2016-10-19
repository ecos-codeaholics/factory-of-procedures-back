package edu.uniandes.ecos.codeaholics.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uniandes.ecos.codeaholics.config.DataBaseUtil;
import edu.uniandes.ecos.codeaholics.config.DatabaseSingleton;

public class DBUpdateTest {

	Logger log = LogManager.getRootLogger();

	private static Gson GSON = new GsonBuilder().serializeNulls().create();

	private class MockProcedure {

		Long fileNumber;
		MockActivity activity;

		/**
		 * 
		 */
		public MockProcedure() {
			fileNumber = (long) 123456789;
			activity = new MockActivity();
		}

		/**
		 * @return the activity
		 */
		public MockActivity getActivity() {
			return activity;
		}

		/**
		 * @return the fileNumber
		 */
		public Long getFileNumber() {
			return fileNumber;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MockProcedure [fileNumber=" + this.getFileNumber() + ", activity=" + this.getActivity() + "]";
		}

		public Document toDocument() {
			Document procedure = new Document();
			procedure.append("fileNumber", this.getFileNumber()).
			append("activity", this.getActivity().toDocument());
			return procedure;
		}

	}

	private class MockActivity {

		Integer step;
		String description;
		String status;
		String email;

		/**
		 * 
		 */
		public MockActivity() {
			step = 1;
			description = "Activity One";
			email = "jvaldez@anapoima";
			status = "ONGOING"; // "DONE"
		}

		/**
		 * @return the step
		 */
		public Integer getStep() {
			return step;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @return the status
		 */
		public String getStatus() {
			return status;
		}

		/**
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MockActivity [step=" + this.getStep() + ", description=" + this.getDescription() + ", status="
					+ this.getStatus() + ", email=" + this.getEmail() + "]";
		}

		public Document toDocument() {
			Document activity = new Document();
			activity.append("step", this.getStep()).append("description", this.getDescription())
					.append("status", this.getStatus()).append("email", this.getEmail());
			return activity;
		}

	}

	private static MongoDatabase db = DatabaseSingleton.getInstance().getDatabase();

	private static String TEST_COLLECTION = "junittest";

	public void insertMockActivity() {

		try {

			MockActivity activity = new MockActivity();

			log.info("Saving " + activity.toString());
			log.info("In Collection " + TEST_COLLECTION);
			MongoCollection<Document> collection = db.getCollection(TEST_COLLECTION);

			Document register = activity.toDocument();

			Document prevRegister = new Document().append("description", "Activity One");
			ArrayList<Document> documents = DataBaseUtil.find(prevRegister, TEST_COLLECTION);

			if (documents.isEmpty()) {
				collection.insertOne(register);
			} else {
				log.info("Register already exists");
				collection.findOneAndDelete(documents.get(0));
				collection.insertOne(register);
			}

		} catch (Exception mongoEx) {
			log.info(mongoEx.getMessage());
		}

	}

	public void insertMockProcedure() {

		try {

			MockProcedure procedure = new MockProcedure();

			log.info("Saving " + procedure.toString());
			log.info("In Collection " + TEST_COLLECTION);
			MongoCollection<Document> collection = db.getCollection(TEST_COLLECTION);

			Document register = procedure.toDocument();

			Document prevRegister = new Document().append("fileNumber", Long.parseLong("123456789"));
			ArrayList<Document> documents = DataBaseUtil.find(prevRegister, TEST_COLLECTION);

			if (documents.isEmpty()) {
				collection.insertOne(register);
			} else {
				log.info("Register already exists");
				collection.findOneAndDelete(documents.get(0));
				collection.insertOne(register);
			}

		} catch (Exception mongoEx) {
			log.info(mongoEx.getMessage());
		}

	}

	@Test
	public void simpleUpdateTest() {

		insertMockActivity();

		// we will test an uptade field with simple query
		Document filter = new Document();
		filter.append("email", "jvaldez@anapoima");
		filter.append("step", 1);

		Document valueToReplace = new Document();
		valueToReplace.append("status", "DONE");

		DataBaseUtil.update(filter, valueToReplace, TEST_COLLECTION);

		Document currentRegister = new Document().append("description", "Activity One");
		ArrayList<Document> documents = DataBaseUtil.find(currentRegister, TEST_COLLECTION);

		MockActivity activity = GSON.fromJson(documents.get(0).toJson(), MockActivity.class);

		log.info(activity.toString());
		Assert.assertEquals(true, "DONE".equals(activity.getStatus()));

	}

	@Test
	public void compositeUpdateTest() {

		insertMockProcedure();

		List<Document> procedureFilter = new ArrayList<>();
		procedureFilter.add(new Document("fileNumber", new Document("$eq", Long.parseLong("123456789"))));
		procedureFilter.add(new Document("activity.step", new Document("$eq", Integer.parseInt("1"))));
		
		Document replaceValue = new Document("activity.status", "DONE");
		
		Document filterOperator = new Document();
		filterOperator.append("$and", procedureFilter);
		
		log.info(filterOperator.toJson());
		
		DataBaseUtil.compositeUpdate(procedureFilter, replaceValue, TEST_COLLECTION);
		
		Document currentRegister = new Document().append("fileNumber", Long.parseLong("123456789"));
		ArrayList<Document> documents = DataBaseUtil.find(currentRegister, TEST_COLLECTION);

		if( ! documents.isEmpty() ) {
						
			Document activityDoc = (Document) documents.get(0).get("activity");
			
			MockActivity activity = GSON.fromJson(activityDoc.toJson(), MockActivity.class);

			log.info(activity.toString());
			
			Assert.assertEquals(true, "DONE".equals(activity.getStatus()) );
			
		}
		
	}

}
