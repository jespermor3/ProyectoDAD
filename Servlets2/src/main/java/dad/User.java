package dad;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
	private String user;
	private String password;
	private String name;
	private String surname;
	private int age;
	private List<String> addres;
	
	
	public User() {
		super();
		addres=new ArrayList<>();
	}


	public User(String user, String password, String name, String surname, int age, List<String> addres) {
		super();
		this.user = user;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.addres = addres;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSurname() {
		return surname;
	}


	public void setSurname(String surname) {
		this.surname = surname;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public List<String> getAddres() {
		return addres;
	}


	public void setAddres(List<String> addres) {
		this.addres = addres;
	}


	@Override
	public String toString() {
		return "User [user=" + user + ", password=" + password + ", name=" + name + ", surname=" + surname + ", age="
				+ age + ", addres=" + addres + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(addres, age, name, password, surname, user);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(addres, other.addres) && age == other.age && Objects.equals(name, other.name)
				&& Objects.equals(password, other.password) && Objects.equals(surname, other.surname)
				&& Objects.equals(user, other.user);
	}
	
	

}
