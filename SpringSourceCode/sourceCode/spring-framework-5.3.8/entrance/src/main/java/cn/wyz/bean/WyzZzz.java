package cn.wyz.bean;

/**
 * @author wnx
 */
public class WyzZzz {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WyzZzz() {
	}

	public WyzZzz(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "WyzZzz{" +
				"name='" + name + '\'' +
				'}';
	}
}
