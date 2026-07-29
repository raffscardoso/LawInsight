package com.raffs.LawInsight.repository.specification;

import com.raffs.LawInsight.domain.Contract;
import com.raffs.LawInsight.domain.enumeration.ContractStatus;
import com.raffs.LawInsight.domain.enumeration.FileType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ContractSpecification {

    private ContractSpecification() {
        // Private constructor for utility class
    }

    public static Specification<Contract> filterBy(
            String title,
            ContractStatus status,
            FileType fileType,
            Long clientId,
            Long uploadedById) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase().trim() + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (fileType != null) {
                predicates.add(cb.equal(root.get("fileType"), fileType));
            }
            if (clientId != null) {
                predicates.add(cb.equal(root.get("client").get("id"), clientId));
            }
            if (uploadedById != null) {
                predicates.add(cb.equal(root.get("uploadedBy").get("id"), uploadedById));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
