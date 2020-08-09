package tfg.luismiguel.ews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tfg.luismiguel.ews.entity.Worker;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
}
