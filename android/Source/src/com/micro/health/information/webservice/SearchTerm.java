package com.micro.health.information.webservice;

public class SearchTerm {
	private int id;
	private String name;
	private String summary;
	private String status;
	private String url;
	
	public SearchTerm() {
	}

	public SearchTerm(int id, String name, String summary, String status, String url) {
		super();
		this.id = id;
		this.name = name;
		this.summary = summary;
		this.status = status;
		this.url = url;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "SearchTerm [id=" + id + ", name=" + name + ", summary="
				+ summary + ", status=" + status + ", url=" + url + "]";
	}
}
