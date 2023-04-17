package com.ead.course.repositories;

import com.ead.course.models.ModuleModel;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleModel, UUID>, JpaSpecificationExecutor<ModuleModel> {

    /*
    @EntityGraph(attributePaths = {"course"})//utilizado para alterar de LAZY para EAGER nessa consulta específica
    ModuleModel findByTitle(String title);
     */
    @Query(value= "select * from tb_modules where course_course_id = :courseId", nativeQuery = true)
    List<ModuleModel> findAllModulesIntoCourse(@Param("courseId") UUID courseId);

    @Query(value = "select * from tb_modules where course_course_id = :courseId and module_id = :moduleId", nativeQuery = true)
    Optional<ModuleModel> findModuleIntoCourse(@Param("courseId") UUID courseId, @Param("moduleId") UUID moduleId);

    /*
    @Modifying
    @Query
    Se usarmos essas duas anotações JPA, podemos realizar uma transação no banco (Delete, update e input)
    No Spring IO temos a documentação para ajudar
     */
}
