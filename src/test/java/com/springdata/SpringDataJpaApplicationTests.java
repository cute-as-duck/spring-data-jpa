package com.springdata;

import com.springdata.entity.Company;
import com.springdata.service.JobService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringDataJpaApplicationTests {

    @Autowired
    private JobService jobService;

    @Test
    void test_get_all_job_with_jdbc_success() {
        var result = jobService.getAllJobs();
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void test_get_all_job_with_repository_success() {
        var result = jobService.listAllJobs();
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void test_get_all_jobs_by_title_success() {
        var result = jobService.findAllJobsCheckForNulls("ager", Company.ORACLE);
        Assertions.assertTrue(result.isEmpty());
    }

}
