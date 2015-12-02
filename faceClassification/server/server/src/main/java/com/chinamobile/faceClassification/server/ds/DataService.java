package com.chinamobile.faceClassification.server.ds;

import com.chinamobile.faceClassification.server.Profile;
import com.linkedin.restli.server.CollectionResult;
import com.linkedin.restli.server.PagingContext;

public interface DataService {
    Profile getProfileByName(String name);
}
