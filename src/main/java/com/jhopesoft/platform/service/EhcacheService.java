package com.jhopesoft.platform.service;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jhopesoft.framework.bean.ActionResult;

// import net.sf.ehcache.CacheManager;
// import net.sf.ehcache.Ehcache;

@Service
public class EhcacheService {

  // @Autowired
  // private CacheManager cacheManager;

  public ActionResult clean() {
    // String[] names = cacheManager.getCacheNames();
    // for (String name : names) {
    //   Ehcache cache = cacheManager.getEhcache(name);
    //   cache.removeAll();
    // }
    return new ActionResult();
  }
}
