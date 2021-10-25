package config;

public class Result {

	private String msg;
	private boolean valid;
	
	public Result(String msg, boolean valid) {
		this.msg = msg;
		this.valid = valid;
	}
	
	public String getMessage() {
		return this.msg;
	}
	
	public boolean isValid() {
		return this.valid;
	}
}
