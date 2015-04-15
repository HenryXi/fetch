package com.service.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class RunMeJob extends QuartzJobBean {
    private IndexService indexService;

    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

        indexService.createIndex();

	}
}