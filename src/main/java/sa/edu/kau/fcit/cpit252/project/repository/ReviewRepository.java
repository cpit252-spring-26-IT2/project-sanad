package sa.edu.kau.fcit.cpit252.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sa.edu.kau.fcit.cpit252.project.entity.ReviewEntity;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByTargetTypeAndTargetId(ReviewTargetType targetType, Long targetId);

    @Query("select r.targetId as targetId, avg(r.rating) as avgRating, count(r.id) as reviewCount " +
            "from ReviewEntity r where r.targetType = :targetType group by r.targetId")
    List<Object[]> summarizeByTargetType(@Param("targetType") ReviewTargetType targetType);
}
