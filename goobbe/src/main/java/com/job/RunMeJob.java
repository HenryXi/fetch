package com.job;

import com.util.Index;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class RunMeJob extends QuartzJobBean {
    private Index index;

    public void setIndex(Index index) {
        this.index = index;
    }

    protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

        index.createIndex();

	}
}