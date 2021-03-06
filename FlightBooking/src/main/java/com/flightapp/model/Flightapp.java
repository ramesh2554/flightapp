package com.flightapp.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.FutureOrPresent;

import lombok.Data;

@Entity
@Data
public class Flightapp {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Integer flightNumber;

	private String airline;
	private String fromplace;
	private String toplace;

	@FutureOrPresent(message = "StartDate should be present or future only")
	private LocalDate startDate;

	private String depatureTime;
	
	@FutureOrPresent(message = "StartDate should be present or future only")
	private LocalDate endDate;
	
	private String arrivalTime;

	private String scheduledDays;

	private Integer totalBusinessClassSeats;

	private Integer totalNonBusinessClassSeats;

	private Double ticketCost;

	private Double roundTripCost;

	private Integer numberofRows;

	private String mealType;

	private Boolean flightStatus;

	private String seatNumbers;
}
