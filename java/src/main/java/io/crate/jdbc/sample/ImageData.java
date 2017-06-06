package io.crate.jdbc.sample;

import java.util.Base64;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

public class ImageData {
	private byte[] data;
	private String id;
	//private String digest;
	
	protected ImageData(){}
	
	public ImageData(String data, String id) {
		this.id = UUID.randomUUID().toString();			
		this.setData(data);
		//String digest = DigestUtils.shaHex(this.data + this.id);		
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDataString() {
		return Base64.getEncoder().encodeToString(data);
	}
	public void setData(String data) {
		this.data = Base64.getDecoder().decode(data);
	}
	

}
