package io.crate.jdbc.sample;

class FaceException extends Exception
{

	private boolean featureData;
	private boolean timestamp;
	private boolean isMedoid;
	private boolean appearanceUUID;
	private boolean faceUUID;
	private boolean identityUUID;
	private boolean label;
	private boolean importance;
	private boolean imageData;




	// Parameterless Constructor
	public FaceException() {
		featureData = false;
		timestamp = false;
		isMedoid = false;
		appearanceUUID = false;
		faceUUID = false;
		identityUUID = false;
		label = false;
		importance = false;
		imageData = false;
	}

	// Constructor that accepts a message
	public FaceException(String message)
	{
		super(message);
		
		featureData = false;
		timestamp = false;
		isMedoid = false;
		appearanceUUID = false;
		faceUUID = false;
		identityUUID = false;
		label = false;
		importance = false;
		imageData = false;
	}
	
	public boolean isImageData() {
		return imageData;
	}

	public void setImageData(boolean imageData) {
		this.imageData = imageData;
	}

	public boolean isFeatureData() {
		return featureData;
	}

	public void setFeatureData(boolean featureData) {
		this.featureData = featureData;
	}

	public boolean isTimestamp() {
		return timestamp;
	}

	public void setTimestamp(boolean timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isMedoid() {
		return isMedoid;
	}

	public void setMedoid(boolean isMedoid) {
		this.isMedoid = isMedoid;
	}

	public boolean isAppearanceUUID() {
		return appearanceUUID;
	}

	public void setAppearanceUUID(boolean appearanceUUID) {
		this.appearanceUUID = appearanceUUID;
	}

	public boolean isFaceUUID() {
		return faceUUID;
	}

	public void setFaceUUID(boolean faceUUID) {
		this.faceUUID = faceUUID;
	}

	public boolean isIdentityUUID() {
		return identityUUID;
	}

	public void setIdentityUUID(boolean identityUUID) {
		this.identityUUID = identityUUID;
	}

	public boolean isLabel() {
		return label;
	}

	public void setLabel(boolean label) {
		this.label = label;
	}

	public boolean isImportance() {
		return importance;
	}

	public void setImportance(boolean importance) {
		this.importance = importance;
	}
	
	@Override 
	public String getMessage(){
		String errorS = super.getMessage() + "\n Face Error! \n";
		
		if(featureData)
			errorS += "Argument \"featureData\" \n"; 
			if(timestamp)
				errorS += "Argument \"timestamp\" \n";
			if(isMedoid)
				errorS += "Argument \"isMedoid\" \n";
			if(appearanceUUID)
				errorS += "Argument \"appearanceUUID\" \n";
			if(faceUUID)
				errorS += "Argument \"faceUUID\" \n";
			if(identityUUID)
				errorS += "Argument \"identityUUID\" \n";
			if(label)
				errorS += "Argument \"label\" \n";
			if(importance)
				errorS += "Argument \"importance\" \n";
			if(imageData)
				errorS += "Argument \"imageData\" \n";
		
		return errorS;
	}
}
