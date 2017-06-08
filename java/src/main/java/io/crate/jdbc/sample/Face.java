package io.crate.jdbc.sample;

import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Face extends ImageData{

	/// keys of the json file	
	private static final String JSON_FACE_IMAGEDATA = "imageData";
	private static final String JSON_FACE_FEATUREDATA = "featureData";
	private static final String JSON_FACE_TIMESTAMP = "timestamp";
	private static final String JSON_FACE_ISMEDOID = "isMedoid";
	private static final String JSON_FACE_APPAREANCEUUID = "appearanceUUID";
	private static final String JSON_FACE_FACEUUID = "faceUUID";
	private static final String JSON_FACE_IDENTITYUUID = "identityUUID";
	private static final String JSON_FACE_LABEL = "label";
	private static final String JSON_FACE_IMPORTANCE = "importance";

	private String id;
	private String 	featureData;
	private long timestamp;
	private boolean isMedoid;
	private String appearanceUUID;
	private String faceUUID;
	private String identityUUID;
	private int label;
	private int importance;
	
	
	public Face(String featureData, long timestamp, boolean isMedoid, String appearanceUUID, String faceUUID,
			String identityUUID, int label, int importance, String data) throws FaceException{
		super(faceUUID, data);
		
		// Generate 
		this.id = UUID.randomUUID().toString();
		this.featureData = featureData;
		this.timestamp = timestamp;
		this.isMedoid = isMedoid;
		this.appearanceUUID = appearanceUUID;
		this.faceUUID = faceUUID;
		this.identityUUID = identityUUID;
		this.label = label;
		this.importance = importance;
	}
	
	public Face(String jsonS) throws FaceException{
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		
		Map<String, Object> faceMap = gson.fromJson(jsonS, Map.class);
		
		/*boolean featureDataB = false;
		boolean timestampB = false;
		boolean isMedoidB = false;
		boolean appearanceUUIDB = false;
		boolean faceUUIDB = false;
		boolean identityUUIDB = false;
		boolean labelB = false;
		boolean importanceB = false;*/
		
		boolean throwExecption = false;
		FaceException fe= new FaceException();
		
		if (!faceMap.containsKey(JSON_FACE_FEATUREDATA)) {
			throwExecption = true;
			fe.setFeatureData(true);
		}
		if (!faceMap.containsKey(JSON_FACE_TIMESTAMP)) {
			throwExecption = true;
			fe.setTimestamp(true);
		}
		if (!faceMap.containsKey(JSON_FACE_ISMEDOID)) {
			throwExecption = true;
			fe.setMedoid(true);
		}
		if (!faceMap.containsKey(JSON_FACE_APPAREANCEUUID)) {
			throwExecption = true;
			fe.setAppearanceUUID(true);
		}
		if (!faceMap.containsKey(JSON_FACE_FACEUUID)) {
			throwExecption = true;
			fe.setFaceUUID(true);
		}
		if (!faceMap.containsKey(JSON_FACE_IDENTITYUUID)) {
			throwExecption = true;
			fe.setIdentityUUID(true);
		}
		if (!faceMap.containsKey(JSON_FACE_LABEL)) {
			throwExecption = true;
			fe.setLabel(true);
		}
		if (!faceMap.containsKey(JSON_FACE_IMPORTANCE)) {
			throwExecption = true;
			fe.setImportance(true);
		}
		if (!faceMap.containsKey(JSON_FACE_IMAGEDATA)) {
			throwExecption = true;
			fe.setImageData(true);
		}
		
		if(throwExecption)
			throw fe;
		
		String featureData = (String) faceMap.get(JSON_FACE_FEATUREDATA);
		long timestamp = Long.parseLong( (String) faceMap.get(JSON_FACE_TIMESTAMP), 10);
		boolean isMedoid = (((double) faceMap.get(JSON_FACE_ISMEDOID))==1)?true:false;
		String appearanceUUID  = (String) faceMap.get(JSON_FACE_APPAREANCEUUID);
		String faceUUID = (String) faceMap.get(JSON_FACE_FACEUUID);
		String identityUUID = (String) faceMap.get(JSON_FACE_IDENTITYUUID);
		int label = ((Double) faceMap.get(JSON_FACE_LABEL)).intValue();
		int importance = ((Double) faceMap.get(JSON_FACE_IMPORTANCE)).intValue();
		String data = (String) faceMap.get(JSON_FACE_IMAGEDATA);
		
		this.id = UUID.randomUUID().toString();
		this.featureData = featureData;
		this.timestamp = timestamp;
		this.isMedoid = isMedoid;
		this.appearanceUUID = appearanceUUID;
		this.faceUUID = faceUUID;
		this.identityUUID = identityUUID;
		this.label = label;
		this.importance = importance;
		
		this.setDataString(data);
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFeatureData() {
		return featureData;
	}
	public void setFeatureData(String featureData) {
		this.featureData = featureData;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isMedoid() {
		return isMedoid;
	}
	public void setMedoid(boolean isMedoid) {
		this.isMedoid = isMedoid;
	}
	public String getAppearanceUUID() {
		return appearanceUUID;
	}
	public void setAppearanceUUID(String appearanceUUID) {
		this.appearanceUUID = appearanceUUID;
	}
	public String getFaceUUID() {
		return faceUUID;
	}
	public void setFaceUUID(String faceUUID) {
		this.faceUUID = faceUUID;
	}
	public String getIdentityUUID() {
		return identityUUID;
	}
	public void setIdentityUUID(String identityUUID) {
		this.identityUUID = identityUUID;
	}
	public int getLabel() {
		return label;
	}
	public void setLabel(int label) {
		this.label = label;
	}
	public int getImportance() {
		return importance;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}
	
	
	
	


}
