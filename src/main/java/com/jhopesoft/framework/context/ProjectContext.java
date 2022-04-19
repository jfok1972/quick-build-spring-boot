package com.jhopesoft.framework.context;

/**
 *
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

public class ProjectContext {
	/**
	 * 获取项目空间 
	 * @return
	 */
	private static ProjectSpace projectSpace;
	
	public static ProjectSpace getProjectSpace() {
		if(projectSpace == null){
			projectSpace = new ProjectSpace();
		}
		return projectSpace;
	}

	public void setProjectSpace(ProjectSpace projectSpace) {
		ProjectContext.projectSpace = projectSpace;
	}

}
