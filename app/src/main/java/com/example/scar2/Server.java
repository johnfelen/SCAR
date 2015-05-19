package com.example.scar2;

/*
 * @authors: 
 * @description: class that contains the server information
 */
public class Server {

	int _id;
	String _hostname;
	int _port_num;
	String _username;
	String _password;
	int _status; // active/inactive states
	String _serverType;
	// Empty constructor
	public Server() {

	}

	// 2nd constructor
	public Server(int id, String server_type, String hostname, int port_num,String username,
			String password, int status) {
		this._id = id;
		this._serverType = server_type;
		this._hostname = hostname;
		this._port_num = port_num;
		this._username = username;
		this._password = password;
		this._status = status;
	}

	// 3rd constructor
	public Server(String server_type, String hostname, int port_num, String username,
			String password, int status) {
		this._serverType = server_type;
		this._hostname = hostname;
		this._port_num = port_num;
		this._username = username;
		this._password = password;
		this._status = status;
	}

	// Getters
	public String getHostname() {
		return this._hostname;
	}

	public String getUsername() {
		return this._username;
	}

	public String getPassword() {
		return this._password;
	}

	public int getStatus() {

		return this._status;
	}

	public int getPort() {
		return this._port_num;
	}

	public int getID() {
		return this._id;
	}
	public String getServer() {
		return this._serverType;
	}
	// Setters
	public void setHostname(String host) {
		 this._hostname =host;
	}

	public void setUsername(String user) {
		 this._username =user;
	}

	public void setPassword(String pass) {
		 this._password =pass;
	}

	public void setStatus(int status) {

		 this._status =status;
	}

	public void setPort(int port) {
		 this._port_num =port;
	}

	public void setID(int id) {
		 this._id =id;
	}
	public void setServer(String server) {
		 this._serverType =server;
	}
}
