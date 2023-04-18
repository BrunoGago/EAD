package com.ead.course.models;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name= "TB_COURSES")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseModel implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID courseId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 250)
    private String description;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime creationDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUpdateDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseStatus courseStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseLevel courseLevel;

    @Column(nullable = false)
    private UUID userInstructor;

    //Um curso pode ter vários módulos (Ex: Java com Módulos I, II, III)
    //SET: não é ordenado e não permite duplicatas. Hibernate gerencia melhor com SET do que com LIST, pois ele gera várias querys com List

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//Define o tipo de acesso ao atributo na Serialização e Deserialização
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)//, cascade = CascadeType.ALL, orphanRemoval = true)//deleta todos os modulos em cascata com o curso vinculado e orphanRemoval garante que se tiver um modulo nao vinculado tb sera deletado
    @Fetch(FetchMode.SUBSELECT)//Vai utilizar o fetch lazy para obeter os dados como definido anteriormente. Caso não seja definido, o JPA utiliza como default o JOIN (EAGER)
    //@OnDelete(action = OnDeleteAction.CASCADE)//A deleção é feita pelo banco de dados, assim, o BD vai deletar os módules que estão associados com um curso
    private Set<ModuleModel> modules;

    //Definição de API Composition com relacionamento entre as classes
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<CourseUserModel> coursesUsers;

    public CourseUserModel convertToCourseUserModel(UUID userId){
        return new CourseUserModel(null, this, userId);
    }

}
