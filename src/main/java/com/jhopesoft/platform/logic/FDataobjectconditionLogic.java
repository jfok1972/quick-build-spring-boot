package com.jhopesoft.platform.logic;

import com.jhopesoft.framework.core.annotation.Module;
import com.jhopesoft.framework.core.annotation.Module.Type;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Module
public class FDataobjectconditionLogic {

  @Module(type = Type.newDataAfter)
  public void newDataAfter() {}

  @Module(type = Type.updateDataAfter)
  public void updateDataAfter() {}

  @Module(type = Type.deleteDataAfter)
  public void deleteDataAfter() {}

}
