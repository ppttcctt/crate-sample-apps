package io.crate.jdbc.sample;

import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Appearance extends ImageData{

	/// keys of the json file	
	private static final String JSON_APPEARANCE_IMAGEDATA = "imageData";
	private static final String JSON_APPEARANCE_FEATUREDATA = "featureData";
	private static final String JSON_APPEARANCE_TIMESTAMP = "timestamp";
	private static final String JSON_APPEARANCE_ISMEDOID = "isMedoid";
	private static final String JSON_APPEARANCE_APPAREANCEUUID = "appearanceUUID";
	private static final String JSON_APPEARANCE_IDENTITYUUID = "identityUUID";
	private static final String JSON_APPEARANCE_LABEL = "label";


	private String id;
	private String 	featureData;
	private long timestamp;
	private boolean isMedoid;
	private String appearanceUUID;
	private String identityUUID;
	private int label;


	public Appearance(long id, String featureData, long timestamp, boolean isMedoid, String appearanceUUID,
			String identityUUID, int label, String data) throws FaceException {
		super(appearanceUUID, data);

		// Generate 
		this.id = UUID.randomUUID().toString();
		this.featureData = featureData;
		this.timestamp = timestamp;
		this.isMedoid = isMedoid;
		this.appearanceUUID = appearanceUUID;
		this.identityUUID = identityUUID;
		this.label = label;
	}

	public Appearance(String jsonS) throws AppearanceException{

		Gson gson = new GsonBuilder().serializeNulls().create();

		Map<String, Object> faceMap = gson.fromJson(jsonS, Map.class);

		boolean throwExecption = false;
		AppearanceException fe= new AppearanceException();

		if (!faceMap.containsKey(JSON_APPEARANCE_FEATUREDATA)) {
			throwExecption = true;
			fe.setFeatureData(true);
		}
		if (!faceMap.containsKey(JSON_APPEARANCE_TIMESTAMP)) {
			throwExecption = true;
			fe.setTimestamp(true);
		}
		if (!faceMap.containsKey(JSON_APPEARANCE_ISMEDOID)) {
			throwExecption = true;
			fe.setMedoid(true);
		}
		if (!faceMap.containsKey(JSON_APPEARANCE_APPAREANCEUUID)) {
			throwExecption = true;
			fe.setAppearanceUUID(true);
		}
		if (!faceMap.containsKey(JSON_APPEARANCE_IDENTITYUUID)) {
			throwExecption = true;
			fe.setIdentityUUID(true);
		}
		if (!faceMap.containsKey(JSON_APPEARANCE_LABEL)) {
			throwExecption = true;
			fe.setLabel(true);
		}

		if (!faceMap.containsKey(JSON_APPEARANCE_IMAGEDATA)) {
			throwExecption = true;
			fe.setImageData(true);
		}

		if(throwExecption)
			throw fe;

		String featureData = (String) faceMap.get(JSON_APPEARANCE_FEATUREDATA);
		long timestamp = Long.parseLong( (String) faceMap.get(JSON_APPEARANCE_TIMESTAMP), 10);
		boolean isMedoid = (((double) faceMap.get(JSON_APPEARANCE_ISMEDOID))==1)?true:false;
		String appearanceUUID  = (String) faceMap.get(JSON_APPEARANCE_APPAREANCEUUID);
		String identityUUID = (String) faceMap.get(JSON_APPEARANCE_IDENTITYUUID);
		int label = ((Double) faceMap.get(JSON_APPEARANCE_LABEL)).intValue();
		String data = (String) faceMap.get(JSON_APPEARANCE_IMAGEDATA);

		this.id = UUID.randomUUID().toString();
		this.featureData = featureData;
		this.timestamp = timestamp;
		this.isMedoid = isMedoid;
		this.appearanceUUID = appearanceUUID;
		this.identityUUID = identityUUID;
		this.label = label;

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

	// TODO: parseJson
	/*public void parseJson(){

	}*/
}
