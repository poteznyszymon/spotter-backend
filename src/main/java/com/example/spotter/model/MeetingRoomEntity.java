package com.example.spotter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
//@AllArgsConstructor
@NoArgsConstructor
//@Builder
@SuperBuilder
@Table(name = "meeting_rooms")
@EqualsAndHashCode(callSuper = true)
public class MeetingRoomEntity extends MapElement {
}
