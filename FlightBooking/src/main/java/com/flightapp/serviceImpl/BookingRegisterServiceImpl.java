package com.flightapp.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.flightapp.exception.UserDefinedException;
import com.flightapp.model.BookingRegister;
import com.flightapp.model.Flightapp;
import com.flightapp.model.SelectedSeats;
import com.flightapp.model.UserData;
import com.flightapp.repo.BookingRegisterRepo;
import com.flightapp.repo.FlightappRepo;
import com.flightapp.repo.SelectedSeatsRepo;
import com.flightapp.repo.UserRepository;
import com.flightapp.service.BookingRegisterService;
import com.flightapp.util.BookingUtility;

@Service
public class BookingRegisterServiceImpl implements BookingRegisterService {

	@Autowired
	BookingRegisterRepo bookRegisterRepo;

	@Autowired
	FlightappRepo flightappRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	SelectedSeatsRepo selectedSeatsRepo;

	//public final static SelectedSeats seats = new SelectedSeats();

	public ResponseEntity<Object> bookFlightTicket(BookingRegister register, Integer flightNumber , SelectedSeats seats) {

		List<String> validateBookingRegister = BookingUtility.validateBookingRegister(register);
		Optional<Flightapp> findById = flightappRepo.findById(flightNumber);
		if (!validateBookingRegister.isEmpty()) {
			return BookingUtility
					.prepareBadRequest(BookingUtility.prepareErrorMessage(validateBookingRegister).getMessage());

		} else if (findById.isPresent()) {

			Flightapp findByFlightNumber = flightappRepo.findByFlightNumber(flightNumber);

			if (register.getMealType().equalsIgnoreCase("veg") || register.getMealType().equalsIgnoreCase("Non-veg")
					|| register.getMealType().equalsIgnoreCase("none")) {

				Optional<UserData> findByEmailid = userRepo.findByEmailid(register.getEmailId());
				if (findByEmailid.isPresent()) {
					register.setFlightNumber(flightNumber);
					register.setFlightdetails(findByFlightNumber);
					String seatNumbers = register.getSeatNumbers();

					Optional<SelectedSeats> findBystartDateAndseatNumbers = selectedSeatsRepo
							.findByStartDateAndSeatNumbersAndFlightNumber(findByFlightNumber.getStartDate(), seatNumbers , findById.get().getFlightNumber());
					System.out.println(findBystartDateAndseatNumbers);

					if (findBystartDateAndseatNumbers.isEmpty()) {

						Random rnd = new Random();
						int number = rnd.nextInt(999999);
						String pnr = String.format("%06d", number);
						register.setPnr(pnr);
						register.setSeatNumbers(seatNumbers);
						seats.setFlightNumber(flightNumber);
						seats.setPnr(pnr);
						seats.setStartDate(findByFlightNumber.getStartDate());
						seats.setEmail(register.getEmailId());
						seats.setSeatNumbers(seatNumbers);
						

						if (register.getRoundTripStatus()) {
							register.setTotalBasePrice(
									findById.get().getRoundTripCost() * seatNumbers.replaceAll("\\D+", "").length());
						} else {
							register.setTotalBasePrice(
									findById.get().getTicketCost() * seatNumbers.replaceAll("\\D+", "").length());
						}
						System.out.println(seatNumbers);

						selectedSeatsRepo.save(seats);
						bookRegisterRepo.save(register);
						return new ResponseEntity<Object>(" PNR " + register.getPnr(), HttpStatus.OK);
					} else {
						return BookingUtility.prepareBadRequest(seatNumbers + " Already Booked");
					}

//					register.setSeatNumbers(seatNumbers);
//					
//					seats.setSeatNumbers(seatNumbers);
//					seats.setStartDate(findByFlightNumber.getStartDate());
//					
//					
//					if (register.getRoundTripStatus()) {
//						register.setTotalBasePrice(
//								findById.get().getRoundTripCost() * seatNumbers.replaceAll("\\D+", "").length());
//					} else {
//						register.setTotalBasePrice(
//								findById.get().getTicketCost() * seatNumbers.replaceAll("\\D+", "").length());
//					}
//					System.out.println(seatNumbers);
//					Random rnd = new Random();
//					int number = rnd.nextInt(999999);
//					String pnr = String.format("%06d", number);
//					register.setPnr(pnr);
//					seats.setPnr(pnr);
//					selectedSeatsRepo.save(seats);
//					bookRegisterRepo.save(register);
//					return new ResponseEntity<Object>(" PNR " + register.getPnr(), HttpStatus.OK);
				} else {
					return BookingUtility.prepareBadRequest(
							register.getEmailId() + " Do you don't have an Account .???   ...please register");
				}

			} else {
				return BookingUtility.prepareBadRequest("Meal type should be veg/non-veg/non");
			}
		} else {
			return BookingUtility
					.prepareBadRequest("Flight number is Not found... , Please enter correct details .. !!");
		}

	}

	@Override
	public Optional<BookingRegister> getBookingDetails(String pnr) {

		Optional<BookingRegister> findByPnr = bookRegisterRepo.findByPnr(pnr);
		if (findByPnr.isPresent()) {
			// register.setFlightdetails(findById);
			return findByPnr;
		} else {
			throw new UserDefinedException("Please enter correct PNR Number .. !!");
		}
	}

	@Override
	public List<BookingRegister> getBookingDetailsBasedOnEmail(String emailId) {
		List<BookingRegister> findByEmailId = bookRegisterRepo.findByEmailId(emailId);
		if (findByEmailId.isEmpty()) {
			throw new UserDefinedException("Please enter Correct Email id .. !!");
		}
		return findByEmailId;

	}

	@Override
	public Optional<BookingRegister> deleteBookingDetails(String pnr) {
		Optional<BookingRegister> findByPnr = bookRegisterRepo.findByPnr(pnr);
		if (findByPnr.isPresent()) {
			LocalDate startDate = findByPnr.get().getFlightdetails().getStartDate();
			System.out.println(startDate);
			LocalDate lt = LocalDate.now();
			LocalDate CurrentBookingDate = lt.plusDays(1);
			System.out.println(CurrentBookingDate);
			if (startDate.equals(CurrentBookingDate)) {
				throw new UserDefinedException("Before 24 hrs ticket cancel is not possible ");
			} else {
				selectedSeatsRepo.removeByPnr(pnr); 
				return bookRegisterRepo.removeByPnr(pnr);
			}
		} else {
			throw new UserDefinedException("Please enter correct PNR Number .. !!");
		}
	}
}