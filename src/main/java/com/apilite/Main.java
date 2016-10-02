package com.apilite;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.spi.ResteasyDeployment;

import com.apilite.server.RestEasyNettyServer;
import com.apilite.settings.ManagerSettings;

public class Main {
	public static void main(String[] args){
		ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
		Application apiRestApplication = new APIRestApplication();
		resteasyDeployment.setApplication(apiRestApplication);
		
		RestEasyNettyServer server = new RestEasyNettyServer(
				resteasyDeployment, ManagerSettings.SERVER_PORT
				, ManagerSettings.ROOT_RESOURCE_PATH);
		try {
			server.start();
			System.out.println("API Manager Starting ...");
			System.out.println("Root Path : " + ManagerSettings.ROOT_RESOURCE_PATH);
			System.out.println("Port : " + ManagerSettings.SERVER_PORT);
			System.out.println("API Manager Started ...");
		} catch (Exception e) {
			System.out.println("API Manager Starting Failure ...");
			e.printStackTrace();
		}
	}
}