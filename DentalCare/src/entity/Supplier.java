package entity;

import java.util.Objects;

public class Supplier {
private String id;
private String name;
private String contactNumber;
private String email;
private String address;
public Supplier(String id, String name, String contactNumber, String email, String address) {
	super();
	this.id = id;
	this.name = name;
	this.contactNumber = contactNumber;
	this.email = email;
	this.address = address;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getContactNumber() {
	return contactNumber;
}
public void setContactNumber(String contactNumber) {
	this.contactNumber = contactNumber;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
@Override
public int hashCode() {
	return Objects.hash(address, contactNumber, email, id, name);
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Supplier other = (Supplier) obj;
	return Objects.equals(address, other.address) && Objects.equals(contactNumber, other.contactNumber)
			&& Objects.equals(email, other.email) && Objects.equals(id, other.id) && Objects.equals(name, other.name);
}
@Override
public String toString() {
	return "Supplier [id=" + id + ", name=" + name + ", contactNumber=" + contactNumber + ", email=" + email
			+ ", address=" + address + "]";
}
}
