package org.arain.spider.pez;

public class Teacher {
	private String name;
	private String desc;
	private String image;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	@Override
	public String toString() {
		return "Teacher [name=" + name + ", desc=" + desc + ", image=" + image + "]";
	}
	
	
	
}
