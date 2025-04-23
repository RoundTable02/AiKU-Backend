package aiku_main.repository;

import common.domain.title.Title;
import common.domain.title.TitleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Long>, TitleRepositoryCustom {

    Optional<Title> findByTitleCode(TitleCode titleCode);
}
