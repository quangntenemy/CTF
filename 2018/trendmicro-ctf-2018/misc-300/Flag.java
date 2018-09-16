package com.trendmicro.jail;

import java.io.Serializable;

public class Flag implements Serializable {
  static final long serialVersionUID = 6119813099625710381L;
  
  public Flag() {}
  
  public static void getFlag() throws Exception { throw new Exception("<FLAG GOES HERE>"); }
}
