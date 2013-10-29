/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.springframework.webflow.samples.booking;

import java.util.List;

/**
 * A service interface for retrieving hotels and bookings from a backing repository. Also supports the ability to cancel
 * a booking.
 */
public interface BookingService {

    /**
     * Find bookings made by the given user
     * @param username the user's name
     * @return their bookings
     */
    public List<Booking> findBookings(String username);

    /**
     * Find hotels available for booking by some criteria.
     * @param criteria the search criteria
     * @param firstResult the index of the first result to return
     * @param sortBy the field to sort by
     * @param ascending true if the sorting should be in ascending order, false for descending
     * @return a list of hotels meeting the criteria
     */
    public List<Hotel> findHotels(SearchCriteria criteria, int firstResult, String sortBy, boolean ascending);

    /**
     * Find hotels by their identifier.
     * @param id the hotel id
     * @return the hotel
     */
    public Hotel findHotelById(Long id);

    /**
     * Create a new, transient hotel booking instance for the given user.
     * @param hotelId the hotelId
     * @param userName the user name
     * @return the new transient booking instance
     */
    public Booking createBooking(Long hotelId, String userName);

    /**
     * Persist the booking to the database
     * @param booking the booking
     */
    public void persistBooking(Booking booking);

    /**
     * Cancel an existing booking.
     * @param id the booking id
     */
    public void cancelBooking(Booking booking);

    /**
     * Return the total number of hotels for the given criteria.
     * @param criteria the criteria to use
     * @return the number of matching hotels
     */
    int getNumberOfHotels(SearchCriteria criteria);

}
