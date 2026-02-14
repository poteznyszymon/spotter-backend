package com.example.spotter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
//@NoArgsConstructor
@SuperBuilder
//@Builder
@Table(name = "desks")
@EqualsAndHashCode(callSuper = true)
public class DeskEntity extends MapElement{

}
