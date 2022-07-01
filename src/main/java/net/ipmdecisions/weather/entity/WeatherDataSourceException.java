package net.ipmdecisions.weather.entity;

public class WeatherDataSourceException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int httpErrorCode;
	private String dataSourceURL;

	public WeatherDataSourceException(String msg)
	{
		super(msg);
	}
	
	public WeatherDataSourceException(String dataSourceURL, String msg, int httpErrorCode)
	{
		super(msg);
		this.dataSourceURL = dataSourceURL;
		this.httpErrorCode = httpErrorCode;
	}

	public int getHttpErrorCode() {
		return this.httpErrorCode;
	}

	public void setHttpErrorCode(int httpErrorCode) {
		this.httpErrorCode = httpErrorCode;
	}

	public String getDataSourceURL() {
		return dataSourceURL;
	}

	public void setDataSourceURL(String dataSourceURL) {
		this.dataSourceURL = dataSourceURL;
	}
}
