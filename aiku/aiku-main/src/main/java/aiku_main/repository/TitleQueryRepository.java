package aiku_main.repository;

import common.domain.title.Title;
import common.domain.title.TitleCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TitleQueryRepository extends JpaRepository<Title, Long>, TitleQueryRepositoryCustom {

    Optional<Title> findByTitleCode(TitleCode titleCode);
}
