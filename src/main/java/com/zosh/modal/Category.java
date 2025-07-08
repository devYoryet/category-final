package com.zosh.modal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORY")
@SequenceGenerator( // ► Hibernate crea la secuencia si no existe
        name = "category_seq", sequenceName = "CATEGORY_SEQ", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String image;

    @Column(name = "SALON_ID", nullable = false)
    private Long salonId;
}
