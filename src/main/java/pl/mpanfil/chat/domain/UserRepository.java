package pl.mpanfil.chat.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
