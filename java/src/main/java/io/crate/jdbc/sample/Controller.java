package io.crate.jdbc.sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.eclipse.jetty.util.log.Log;

import spark.Response;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static spark.Spark.*;

class Controller {

	private final Gson gson = new GsonBuilder().serializeNulls().create();

	private static final int INTERNAL_ERROR = 500;
	private static final int BAD_REQUEST = 400;
	private static final int NOT_FOUND = 404;

	private static final int NO_CONTENT = 204;
	private static final int CREATED = 201;
	private static final int OK = 200;

	Logger logger = Logger.getLogger("MyLog");  
    FileHandler fh;  

	Controller(final DataProvider model) {		
		
		Spark.exception(Exception.class, (exception, request, response) -> {
			exception.getMessage();
		    exception.printStackTrace();
		});

		before(((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Request-Method", "*");
			response.header("Access-Control-Allow-Methods", "*");
			response.header("Access-Control-Allow-Headers", "*");
		}));

		options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			response.status(OK);
			return response;
		});


		//////////////////////////////////////////////////////
		/////////////////// FACES ///////////////////////////
		////////////////////////////////////////////////////
		get("/faces", (request, response) ->
		model.getFaces(), gson::toJson);

		post("/faces", (request, response) -> {
			String body = request.body();
			if (body.isEmpty()) {
				return argumentRequired(response, "Request body is required");
			}

			Face face = null;
			try{
				face = new Face(body);
			}catch(FaceException e){								
				return argumentRequired(response, e.getMessage());
			}
			
			response.status(CREATED);
			return model.insertFace(face);			

		}, gson::toJson);

		get("/face/:id", (request, response) -> {
			String id = request.params(":id");
			Map<String, Object> face = model.getFace(id);
			if (face.isEmpty()) {
				return notFound(response, (String.format("Post with id=\"%s\" not found", id)));
			}
			response.status(OK);
			return face;
		}, gson::toJson);



		/*put("/face/:id", (request, response) -> {
			String body = request.body();
			if (body.isEmpty()) {
				return argumentRequired(response, "Request body is required");
			}

			//noinspection unchecked
			Map<String, Object> post = gson.fromJson(body, Map.class);
			if (!post.containsKey("text")) {
				return argumentRequired(response, "Argument \"text\" is required");
			}

			String id = request.params(":id");
			Map<String, Object> updatePost = model.updatePost(id, (String) post.get("text"));
			if (updatePost.isEmpty()) {
				return notFound(response, (String.format("Post with id=\"%s\" not found", id)));
			}
			response.status(OK);
			return updatePost;
		}, gson::toJson);*/

		delete("/face/:id", (request, response) -> {
			String id = request.params(":id");
			if (model.deletePost(id)) {
				response.status(NO_CONTENT);
				return response;
			} else {
				return gson.toJson(
						notFound(response, String.format("Face with id=\"%s\" not found", id))
						);
			}
		});
		////////////////////////////////////////////////////////


		//////////////////////////////////////////////////////
		/////////////////// FACES ///////////////////////////
		////////////////////////////////////////////////////
		get("/appearances", (request, response) ->
		model.getAppearances(), gson::toJson);

		post("/appearances", (request, response) -> {
			String body = request.body();
			if (body.isEmpty()) {
				return argumentRequired(response, "Request body is required");
			}

			Appearance appearance = null;
			try{
				appearance = new Appearance(body);
			}catch(AppearanceException e){								
				return argumentRequired(response, e.getMessage());
			}

			response.status(CREATED);
			return model.insertAppearance(appearance);			

		}, gson::toJson);

		get("/appearance/:id", (request, response) -> {
			String id = request.params(":id");
			Map<String, Object> appearance = model.getAppearance(id);
			if (appearance.isEmpty()) {
				return notFound(response, (String.format("Appearance with id=\"%s\" not found", id)));
			}
			response.status(OK);
			return appearance;
		}, gson::toJson);



		/*put("/face/:id", (request, response) -> {
String body = request.body();
if (body.isEmpty()) {
return argumentRequired(response, "Request body is required");
}

//noinspection unchecked
Map<String, Object> post = gson.fromJson(body, Map.class);
if (!post.containsKey("text")) {
return argumentRequired(response, "Argument \"text\" is required");
}

String id = request.params(":id");
Map<String, Object> updatePost = model.updatePost(id, (String) post.get("text"));
if (updatePost.isEmpty()) {
return notFound(response, (String.format("Post with id=\"%s\" not found", id)));
}
response.status(OK);
return updatePost;
}, gson::toJson);*/

		delete("/appearance/:id", (request, response) -> {
			String id = request.params(":id");
			if (model.deletePost(id)) {
				response.status(NO_CONTENT);
				return response;
			} else {
				return gson.toJson(
						notFound(response, String.format("Appearance with id=\"%s\" not found", id))
						);
			}
		});
		////////////////////////////////////////////////////////


		/*put("/post/:id/like", (request, response) -> {
            String id = request.params(":id");
            Map<String, Object> post = model.incrementLike(id);
            if (post.isEmpty()) {
                return notFound(response, (String.format("Post with id=\"%s\" not found", id)));
            }
            response.status(OK);
            return post;
        }, gson::toJson);*/

		get("/face_images", (request, response) -> model.getBlobs(DataProvider.FACE_BLOB), gson::toJson);
		
		get("/appearance_images", (request, response) -> model.getBlobs(DataProvider.APPEARANCE_BLOB), gson::toJson);
		
		//post("/images", (request, response) -> model.getBlobs(), gson::toJson);

		/*post("/image", (request, response) -> {
			String body = request.body();
			if (body.isEmpty()) {
				return argumentRequired(response, "Request body is required");
			}

			//noinspection unchecked
			Map<String, Object> blobMap = gson.fromJson(body, Map.class);
			if (!blobMap.containsKey("blob")) {
				return argumentRequired(response, "Argument \"blob\" is required");
			}

			// compute the 
			byte[] decoded = Base64.getDecoder().decode((String) blobMap.get("blob"));
			String digest = DigestUtils.shaHex(decoded);
			Map<String, String> responseMap = model.insertBlob(digest, decoded);

			response.status(Integer.parseInt(responseMap.get("status")));
			return responseMap;
		}, gson::toJson);*/

		get("/face_image/:digest", (request, response) -> {
			String digest = request.params(":digest");
			if (model.blobExists(digest, DataProvider.FACE_BLOB)) {
				HttpResponse httpResponse = model.getBlob(digest,DataProvider.FACE_BLOB);

				response.status(httpResponse.getStatusLine().getStatusCode());
				response.header("Content-Type", "image/gif");
				response.header("Content-Length", httpResponse.getFirstHeader("Content-Length").getValue());

				InputStream in = httpResponse.getEntity().getContent();
				OutputStream out = response.raw().getOutputStream();
				IOUtils.copy(in, out);

				return response;
			} else {
				return gson.toJson(
						notFound(response, String.format("Image with digest=\"%s\" not found", digest))
						);
			}
		});
		
		
		get("/appearances_image/:digest", (request, response) -> {
			String digest = request.params(":digest");
			if (model.blobExists(digest, DataProvider.APPEARANCE_BLOB)) {
				HttpResponse httpResponse = model.getBlob(digest,DataProvider.APPEARANCE_BLOB);

				response.status(httpResponse.getStatusLine().getStatusCode());
				response.header("Content-Type", "image/gif");
				response.header("Content-Length", httpResponse.getFirstHeader("Content-Length").getValue());

				InputStream in = httpResponse.getEntity().getContent();
				OutputStream out = response.raw().getOutputStream();
				IOUtils.copy(in, out);

				return response;
			} else {
				return gson.toJson(
						notFound(response, String.format("Image with digest=\"%s\" not found", digest))
						);
			}
		});

		/*delete("/image/:digest", (request, response) -> {
			String digest = request.params(":digest");
			if (model.blobExists(digest)) {
				HttpResponse httpResponse = model.deleteBlob(digest);
				response.status(httpResponse.getStatusLine().getStatusCode());
				return response;
			} else {
				return gson.toJson(
						notFound(response, String.format("Image with digest=\"%s\" not found", digest))
						);
			}
		});*/

		/* post("/search", (request, response) -> {
            String body = request.body();
            if (body.isEmpty()) {
                return argumentRequired(response, "Request body is required");
            }

            //noinspection unchecked
            Map<String, Object> bodyMap = gson.fromJson(body, Map.class);
            if (!bodyMap.containsKey("query_string")) {
                return argumentRequired(response, "Argument \"query_string\" is required");
            }
            return model.searchPosts((String) bodyMap.get("query_string"));
        }, gson::toJson);*/

		exception(SQLException.class, (e, request, response) -> {
			response.status(INTERNAL_ERROR);
			response.body(e.getLocalizedMessage());
		});
	}

	private Map<String, Object> notFound(Response response, String msg) {
		return errorResponse(response, msg, NOT_FOUND);
	}

	private Map<String, Object> argumentRequired(Response response, String msg) {
		return errorResponse(response, msg, BAD_REQUEST);
	}

	private Map<String, Object> errorResponse(Response response, String msg, int code) {
		response.status(code);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("status", code);
		responseMap.put("error", msg);
		return responseMap;
	}
	
	
}
