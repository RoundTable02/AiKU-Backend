package aiku_main.repository;

import common.domain.title.Title;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleQueryRepository extends JpaRepository<Title, Long>, TitleQueryRepositoryCustom {
}
