package com.chinamobile.faceClassification.server.db;


import com.chinamobile.faceClassification.server.Profile;

// interface for the Database access
public interface SocialDB {
	Profile getProfileByName(String name);
}