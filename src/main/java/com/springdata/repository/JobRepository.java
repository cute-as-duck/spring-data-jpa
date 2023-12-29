package com.springdata.repository;

import com.springdata.entity.Company;
import com.springdata.entity.JobEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends ListCrudRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {

    List<JobEntity> findAllByTitleContainsIgnoreCaseAndCompanyOrderByMaxSalaryDesc(String title, Company company);

    @Query(nativeQuery = true,
            value = "select * from job where lower(title) like concat('%', lower(:p_title), '%') and company_name = :#{#p_company.name()}")
    List<JobEntity> findAllByFilter(@Param("p_company") Company company, @Param("p_title") String title, Pageable page);
}
