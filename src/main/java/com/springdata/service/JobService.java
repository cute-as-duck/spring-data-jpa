package com.springdata.service;

import com.springdata.entity.Company;
import com.springdata.entity.JobEntity;
import com.springdata.entity.JobEntity_;
import com.springdata.repository.JobRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JdbcTemplate jdbcTemplate;
    private final JobRepository jobRepository;
    private final EntityManager entityManager;

    public List<JobEntity> getAllJobs() {
        return jdbcTemplate.query("""
    
            select id,
                    company_name as company,
                    experience,
                    title,
                    max_salary,
                    min_salary from job
    """, BeanPropertyRowMapper.newInstance(JobEntity.class));
    }

    public List<JobEntity> listAllJobs() {
        return jobRepository.findAll();
    }

    public List<JobEntity> findAllJobsWithoutCheckForNulls(String title, Company company) {
        return jobRepository
                .findAllByFilter(company, title, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "max_salary")));
    }

    /*
    Predicates are introduced with CriteriaBuilder. if parameter is null, filter isn't added.
     */
    public List<JobEntity> findAllJobsCheckForNulls(String title, Company company) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<JobEntity> query = criteriaBuilder.createQuery(JobEntity.class);
        final Root<JobEntity> job = query.from(JobEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (company != null) {
            /*
            constant was generated after 'gradle build', dependency:
                compileOnly 'org.hibernate.orm:hibernate-jpamodelgen:6.4.1.Final'
                annotationProcessor 'org.hibernate.orm:hibernate-jpamodelgen:6.4.1.Final'
            alternatively:
                predicates.add(criteriaBuilder.equal(job.get("company")));
             */
            predicates.add(criteriaBuilder.equal(job.get(JobEntity_.COMPANY), company));
        }
        if (StringUtils.hasText(title)) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.upper(job.get(JobEntity_.TITLE)), "%" + title.toUpperCase() + "%"));
        }
        return entityManager.createQuery(query.where(
                predicates.toArray(Predicate[]::new)
        )).getResultList();
    }

    /*
    Using 'JobRepository extends JpaSpecificationExecutor<JobEntity>'
     */
    public List<JobEntity> findAllJobsCheckForNullsUsingJpa(String title, Company company) {
        return jobRepository.findAll(
                Specification.allOf(
                        (root, query, criteriaBuilder) -> {
                            if (company == null) {
                                return null;
                            }
                            return criteriaBuilder.equal(root.get(JobEntity_.COMPANY), company);
                        },
                        (root, query, criteriaBuilder) -> {
                            if (!StringUtils.hasText(title)) {
                                return null;
                            }
                            return criteriaBuilder.like(criteriaBuilder.upper(root.get(JobEntity_.TITLE)), "%" + title.toUpperCase() + "%");
                        }
                )
        );
    }

}
