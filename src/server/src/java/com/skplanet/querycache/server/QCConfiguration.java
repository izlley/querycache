package com.skplanet.querycache.server;

import com.skplanet.querycache.conf.Configuration;

public class QCConfiguration extends Configuration {
  static {
    addDeprecatedKeys();
    // adds the default resources
    Configuration.addDefaultResource("qc-default.xml");
    Configuration.addDefaultResource("qc-site.xml");
  }
  
  public QCConfiguration() {
    super();
  }
  
  public QCConfiguration(boolean aLoadDefaults) {
    super(aLoadDefaults);
  }
  
  public QCConfiguration(Configuration aConf) {
    super(aConf);
  }
  
  private static void addDeprecatedKeys() {
      // add deprecated keys
  }
}
