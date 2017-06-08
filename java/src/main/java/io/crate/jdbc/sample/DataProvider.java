package io.crate.jdbc.sample;

import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.postgresql.jdbc.PgResultSetMetaData;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.*;
import java.util.*;

class DataProvider {

	private static final String FACE_TABLE = "mind.faces";
	private static final String APPEARANCE_TABLE = "mind.appearances";

	//private static final String IMAGE_TABLE = "mind_images";

	public static final String FACE_BLOB = "mind_faces";
	public static final String APPEARANCE_BLOB = "mind_appearances";

	private final Gson gson = new Gson();

	private static Properties properties;
	private final String host;
	private final int httpPort;

	private CloseableHttpClient httpClient = HttpClients.createSystem();
	private Connection connection;

	DataProvider() throws SQLException {
		int psqlPort = Integer.parseInt(getProperty("crate.psql.port"));
		httpPort = Integer.parseInt(getProperty("crate.http.port"));
		host = getProperty("crate.host");
		try {
			connection = DriverManager.getConnection(
					String.format(Locale.ENGLISH, "jdbc:crate://%s:%d/", host, psqlPort)
					);
		} catch (SQLException e) {
			throw new SQLException("Cannot connect to the database", e);
		}
	}

	static String getProperty(String name) {
		return properties().getProperty(name);
	}

	private static Properties properties() {
		if (properties == null) {
			try {
				properties = new Properties();
				properties.load(DataProvider.class.getResourceAsStream("/config.properties"));
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return properties;
	}



	// Get all the appearances
	List<Map<String, Object>> getAppearances() throws SQLException {

		PreparedStatement statement = connection.prepareStatement(String.format(
				"SELECT a.* " +
						"FROM %s AS a", APPEARANCE_TABLE));// +
		//"ORDER BY f.timestamp DESC", FACE_TABLE));
		ResultSet rs = statement.executeQuery();
		return resultSetToListOfMaps(rs);
	}


	// Get all the faces
	List<Map<String, Object>> getFaces() throws SQLException {

		PreparedStatement statement = connection.prepareStatement(String.format(
				"SELECT f.* " +
						"FROM %s AS f " +
						"ORDER BY f.timestamp DESC", FACE_TABLE));
		ResultSet rs = statement.executeQuery();
		return resultSetToListOfMaps(rs);
	}

	private List<Map<String, Object>> resultSetToListOfMaps(ResultSet rs) throws SQLException {
		List<Map<String, Object>> faces = new ArrayList<>();
		while (rs.next()) {
			faces.add(resultSetToMap.apply(rs));
		}
		return faces;
	}

	private final CheckedFunction<ResultSet, Map<String, Object>> resultSetToMap = rs -> {
		PgResultSetMetaData metaData = (PgResultSetMetaData) rs.getMetaData();
		int resultSetSize = metaData.getColumnCount();

		Map<String, Object> map = new HashMap<>(resultSetSize);
		for (int i = 1; i <= resultSetSize; i++) {
			map.put(metaData.getColumnName(i), rs.getObject(i));
		}
		return map;
	};

	// Get a face by its id
	Map<String, Object> getFace(String id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(String.format(
				"SELECT f.* " +
						"FROM %s AS f " +
						"WHERE f.id = ?", FACE_TABLE));
		statement.setString(1, id);
		ResultSet results = statement.executeQuery();
		if (results.next()) {
			return resultSetToMap.apply(results);
		} else {
			return Collections.emptyMap();
		}
	}



	// Get a face by its id
	Map<String, Object> getAppearance(String id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(String.format(
				"SELECT a.* " +
						"FROM %s AS a " +
						"WHERE a.id = ?", APPEARANCE_TABLE));
		statement.setString(1, id);
		ResultSet results = statement.executeQuery();
		if (results.next()) {
			return resultSetToMap.apply(results);
		} else {
			return Collections.emptyMap();
		}
	}


	// insert a new face
	List<Map<String, Object>> insertFace(Face face) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(String.format(
				/*"INSERT INTO %s " +
                "(id, featureData, timestamp, isMedoid, appearanceUUID, faceUUID, identityUUID, label, importance, imageDigest, imageData) " +
                "VALUES (?, ?, ?, ?,?, ?,?, ?,?,?,?)", FACE_TABLE));*/


				"INSERT INTO %s " +
				"(id, featureData, timestamp, isMedoid, appearanceUUID, faceUUID, identityUUID, label, importance, imageDigest) " +
				"VALUES (?, ?, ?, ?,?, ?,?, ?,?,?)", FACE_TABLE));


		// prepare all data
		statement.setString(1, face.getId());

		// objects can be streamed as json strings,
		// https://crate.io/docs/reference/en/latest/protocols/postgres.html#jdbc
		/*PGobject userObject = new PGobject();
        userObject.setType("json");
        userObject.setValue(gson.toJson(post.get("user")));
        statement.setObject(2, userObject);*/

		statement.setString(2, face.getFeatureData());
		statement.setLong(3, face.getTimestamp());
		statement.setBoolean(4, face.isMedoid());
		statement.setString(5, face.getAppearanceUUID());
		statement.setString(6, face.getFaceUUID());
		statement.setString(7, face.getIdentityUUID());
		statement.setInt(8, face.getLabel());
		statement.setInt(9, face.getImportance());
		statement.setString(10, face.getDigest());
		//statement.setBytes(11, face.getData());


		/*Map<String, String> prova;
		try {
			insertBlob(face.getDigest(), face.getData(), FACE_BLOB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		/*statement.setString(3, (String) post.get("text"));
        statement.setString(4, (String) post.get("image_ref"));
        statement.setLong(5, System.currentTimeMillis());
        statement.setLong(6, 0);*/

		if (statement.executeUpdate() == 0) {
			return Collections.emptyList();
		}

		connection.createStatement().execute(String.format("REFRESH TABLE %s", FACE_TABLE));
		return Collections.singletonList(getFace(face.getId()));
	}




	// insert a new appearance
	List<Map<String, Object>> insertAppearance(Appearance appearance) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(String.format(
				/*"INSERT INTO %s " +
                "(id, featureData, timestamp, isMedoid, appearanceUUID, identityUUID, label, imageDigest, imageData) " +
                "VALUES (?, ?, ?, ?, ?,?, ?,?,?)", APPEARANCE_TABLE));*/

				"INSERT INTO %s " +
				"(id, featureData, timestamp, isMedoid, appearanceUUID, identityUUID, label, imageDigest) " +
				"VALUES (?, ?, ?, ?, ?,?, ?,?)", APPEARANCE_TABLE));

		// 
		statement.setString(1, appearance.getId());

		// objects can be streamed as json strings,
		// https://crate.io/docs/reference/en/latest/protocols/postgres.html#jdbc
		/*PGobject userObject = new PGobject();
        userObject.setType("json");
        userObject.setValue(gson.toJson(post.get("user")));
        statement.setObject(2, userObject);*/

		statement.setString(2, appearance.getFeatureData());
		statement.setLong(3, appearance.getTimestamp());
		statement.setBoolean(4, appearance.isMedoid());
		statement.setString(5, appearance.getAppearanceUUID());
		statement.setString(6, appearance.getIdentityUUID());
		statement.setInt(7, appearance.getLabel());
		statement.setString(8, appearance.getDigest());
		//statement.setBytes(9, appearance.getData());


		/*try {
			insertBlob(appearance.getDigest(), appearance.getData(), APPEARANCE_BLOB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


		if (statement.executeUpdate() == 0) {
			return Collections.emptyList();
		}

		connection.createStatement().execute(String.format("REFRESH TABLE %s", APPEARANCE_TABLE));
		return Collections.singletonList(getAppearance(appearance.getId()));
	}


	/*Map<String, Object> updatePost(String id, String val) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(
                "UPDATE %s " +
                "SET text = ? " +
                "WHERE id = ?", FACE_TABLE));
        statement.setString(1, val);
        statement.setString(2, id);
        if (statement.executeUpdate() == 0) {
            return Collections.emptyMap();
        }
        connection.createStatement().execute(String.format("REFRESH TABLE %s", FACE_TABLE));
        return getFace(id);
    }*/

	/*Map<String, Object> incrementLike(String id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(
                "UPDATE %s " +
                "SET like_count = like_count + 1 " +
                "WHERE id = ?", FACE_TABLE));
        statement.setString(1, id);
        if (statement.executeUpdate() == 0) {
            return Collections.emptyMap();
        }
        connection.createStatement().execute(String.format("REFRESH TABLE %s", FACE_TABLE));
        return getPost(id);
    }*/

	boolean deletePost(String id) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(String.format(
				"DELETE FROM %s " +
						"WHERE id = ?", FACE_TABLE));
		statement.setString(1, id);
		return statement.executeUpdate() == 1;
	}

	List<Map<String, Object>> getBlobs(String targetblob) throws SQLException {
		ResultSet rs = connection.createStatement().executeQuery(String.format(
				"SELECT digest, last_modified " +
						"FROM %s " +
						"ORDER BY 2 DESC", String.format("blob.%s", targetblob)));
		return resultSetToListOfMaps(rs);
	}

	CloseableHttpResponse getBlob(String digest, String targetblob) throws IOException {
		HttpGet get = new HttpGet(blobUri(digest, targetblob));
		return httpClient.execute(get);
	}

	Map<String, String> insertBlob(String digest, byte[] body, String targetblob) throws IOException {
		Map<String, String> result= new HashMap<String, String>();
		if(!blobExists(digest, targetblob)){
			// TODO: inserire notifica
			String uri = blobUri(digest, targetblob);
			HttpPut put = new HttpPut(uri);
			if (body != null) {
				put.setEntity(new ByteArrayEntity(body));
			}
			CloseableHttpResponse response = httpClient.execute(put);
			result = Collections.unmodifiableMap(new HashMap<String, String>() {{
				put("digest", digest);
				put("url", "/image/" + digest);
				put("status", String.valueOf(response.getStatusLine().getStatusCode()));
			}});
		}else{
			/*result = Collections.unmodifiableMap(new HashMap<String, String>() {{
				put("digest", digest);
				put("url", "/image/" + digest);
				put("status", "Duplicate digest in " + targetblob); 
				}});*/
		}
		
		return result;
	}

	CloseableHttpResponse deleteBlob(String digest, String targetblob) throws IOException {
		HttpDelete delete = new HttpDelete(blobUri(digest, targetblob));
		return httpClient.execute(delete);
	}

	boolean blobExists(String digest, String targetblob) throws IOException {
		HttpHead head = new HttpHead(blobUri(digest, targetblob));
		CloseableHttpResponse response = httpClient.execute(head);
		return response.getStatusLine().getStatusCode() == 200;
	}

	private String blobUri(String digest, String targetblob) {
		return String.format(Locale.ENGLISH,
				"http://%s:%s/_blobs/%s", host, httpPort, blobResourceUri(targetblob, digest));
	}

	private String blobResourceUri(String index, String digest) {
		return String.format(Locale.ENGLISH, "%s/%s", index, digest);
	}

	/*List<Map<String, Object>> searchPosts(String query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format(
                "SELECT p.*, p._score as _score, c.name as country, c.geometry as area " +
                "FROM %s AS p, %s AS c " +
                "WHERE within(p.user['location'], c.geometry)" +
                "AND match(text, ?) " +
                "ORDER BY _score DESC", FACE_TABLE, COUNTRIES_TABLE));
        statement.setString(1, query);
        ResultSet results = statement.executeQuery();
        return resultSetToListOfMaps(results);
    }*/

	@FunctionalInterface
	interface CheckedFunction<T, R> {
		R apply(T t) throws SQLException;
	}
}
