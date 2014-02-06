package com.vedana.camelia.generator.components_2_2;

import nu.xom.Element;

public class Behavior {
	
	 private final String id;
	 
	 private final String behaviorClass;
	 
	 public Behavior(Element element) {
		 id = element.getAttributeValue("id");
		 behaviorClass =  element.getAttributeValue("class");
	}
	 
	 public String getBehaviorClass() {
		return behaviorClass;
	}
	 
	 public String getId() {
		return id;
	}

}
