package io.crate.jdbc.sample;

import java.util.Base64;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jetty.util.log.Log;

public class ImageData {
	private byte[] data;
	private String id;
	private String digest;
	
	

	protected ImageData(){}
	
	public ImageData(String data, String id) {
		this.id = UUID.randomUUID().toString();			
		
		this.setDataString(data);		
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
		return Base64.getEncoder().encodeToString(this.data);
	}
	public void setDataString(String data) {
		this.data = Base64.getDecoder().decode(data);
		
		this.digest = DigestUtils.shaHex(this.data);
	}
	
	public String getDigest() {
		return digest;
	}
	

}
