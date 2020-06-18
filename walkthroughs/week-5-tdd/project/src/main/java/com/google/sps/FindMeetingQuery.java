// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to find optimal times for a meeting to be scheduled given a list of pre-existing events
 */
public final class FindMeetingQuery {
  /**
   * Check what times, if any exist, a meeting can be scheduled so all attendees can come Will first
   * find a time that accommodates optional attendees, then if none exist, find times that work when
   * optional attendees are ignored.
   *
   * @param events a collection of events that attendees have already committed to. Includes name,
   *     time range, and collection of attendees
   * @param request a request for a new meeting that must not conflict with any existing events.
   *     Includes name, duration, and collection of attendees
   * @return A collection of time ranges when the new meeting can occur without conflict
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Get the available times, first trying to accommodate the optional attendees
    List<TimeRange> availableTimes = queryToggleOptional(events, request, false);

    // If there were no times that worked, try to find times without accommodating the optional
    // attendees (assuming there are required attendees at all)
    if (availableTimes.size() == 0 && request.getAttendees().size() > 0) {
      availableTimes = queryToggleOptional(events, request, true);
    }

    return availableTimes;
  }

  /**
   * Check what times, if any exist, a meeting can be scheduled so all attendees can come.
   *
   * @param events a collection of events that attendees have already committed to. Includes name,
   *     time range, and collection of attendees
   * @param request a request for a new meeting that must not conflict with any existing events.
   *     Includes name, duration, and collection of attendees
   * @param ignoreOptional false if optional attendees should be accommodated for scheduling. True
   *     otherwise
   * @return A collection of time ranges when the new meeting can occur without conflict
   */
  public List<TimeRange> queryToggleOptional(
      Collection<Event> events, MeetingRequest request, boolean ignoreOptional) {

    // Get the current attendees, including/excluding the optional ones if specified
    // Use HashSet to prevent duplicates
    HashSet<String> attendees = new HashSet<>(request.getAttendees());
    if (!ignoreOptional) {
      attendees.addAll(request.getOptionalAttendees());
    }

    // Filter the data to remove events with no shared attendees with the meeting request
    // Sort the events from earliest start time to latest
    List<TimeRange> eventTimes = eventsToSortedTimes(events, attendees);

    // Merge the event times into a sorted list of non-overlapping events
    List<TimeRange> availableTimes = generateSuggestedTimes(eventTimes, request.getDuration());

    return availableTimes;
  }

  /**
   * Converts a list of events to a sorted list of time ranges for the events. Will remove events
   * that are irrelevant (i.e. the event attendees don't match the requestedAttendees, or the event
   * only contains optional attendees and ignoreOptional is true). Will sort by start time, from
   * earliest in the day to latest
   *
   * @param events list of Event objects that the meeting request must work around
   * @param requestedAttendees list of attendees (optional and required) requested for the new
   *     meeting
   * @return a list of time ranges of events that must be worked around
   */
  private List<TimeRange> eventsToSortedTimes(
      Collection<Event> events, Collection<String> requestedAttendees) {
    return events.stream()
        .filter(
            e -> {
              HashSet<String> sharedAttendees = new HashSet<>(e.getAttendees());
              sharedAttendees.retainAll(requestedAttendees);
              return sharedAttendees.size() != 0;
            })
        .map(Event::getWhen)
        .sorted(TimeRange.ORDER_BY_START)
        .collect(Collectors.toList());
  }

  /**
   * Takes in a list of all events and outputs a list of available time ranges that can accommodate
   * the requested meeting duration
   *
   * @param eventTimes a sorted list of event time ranges (from earliest start time to latest start
   *     time)
   * @param requestedDurationMinutes a length of time given in minutes for the requested meeting.
   *     Must be > 0.
   * @return a sorted list of non-overlapping time ranges (from earliest start time to latest start
   *     time)
   */
  private List<TimeRange> generateSuggestedTimes(List<TimeRange> eventTimes, long requestedDurationMinutes) {
    // Create empty list to store busy periods
    List<TimeRange> availableTimes = new ArrayList<>();

    // Store the startTime of the next availability window.
    // First window of time will start at the beginning of the day
    int availableStartTime = TimeRange.START_OF_DAY;

    // Iterate through all of the events
    // merge overlapping events into one larger event
    // And then create available time windows that work around the merged events
    int currentEventIndex = 0;
    while (currentEventIndex < eventTimes.size()) {
      // The shortest the merged unavailable window can be is the length of the current window.
      int startTime = eventTimes.get(currentEventIndex).start();
      int endTime = eventTimes.get(currentEventIndex).end();

      // Then, continue iterating through the list (if possible)
      while (currentEventIndex < eventTimes.size() - 1) {
        // This event is guaranteed to start later than the current startTime, since the
        // event list is sorted
        TimeRange compare = eventTimes.get(currentEventIndex + 1);

        // Three Cases:
        // Case 1: If the next event contains the end time of the previous,
        // or starts when the previous one ends, the events are overlapping
        // and the merged event should grow to the endTime of the second event
        //
        // Prev. Event(s) : |------|
        // Next Event     :       |---|
        // Merged Event   : |---------|
        //
        // Case 2: The next event is fully contained by the merged event
        // Continue iterating and looking for more merges, but don't change current interval
        // Prev. Event(s) : |-----------------|
        // Next Event     :      |--|
        // Merged Event   : |-----------------|
        //
        // Case 3: If the next event starts later than the previous one ends,
        // a gap of availability is identified. Merging can now stop
        //
        // Prev. Event(s) : |------|
        // Next Event     :          |-----|
        // No merge. Move on.
        //
        if (compare.start() < endTime && compare.end() > endTime) {
          endTime = eventTimes.get(currentEventIndex + 1).end();
          currentEventIndex++;
        } else if (compare.end() <= endTime) {
          currentEventIndex++;
        } else {
          break;
        }
      }

      // Using the start time of the next window that has been saved and
      // the start time of this merged event, check if a window of availability
      // that can fit the requested meeting duration is available. Then, update
      // the start time of the next window, and continue the loop.
      //
      // Previous Start Time  :     |
      // Current Merged Event :           |--------|
      // Day                  : |----------------------|
      // New Window           :     |-----|
      // Next start time      :                    |
      if (startTime - availableStartTime >= requestedDurationMinutes) {
        TimeRange availablePeriod =
            TimeRange.fromStartEnd(availableStartTime, startTime, false);
        availableTimes.add(availablePeriod);
      }

      currentEventIndex++;
      availableStartTime = endTime;
    }

    // This handles the last availability window, which will span to the end of the day
    // Add if it can fit the requested meeting length
    //
    // Previous Start Time :             |
    // Day                 : |------------------|
    // Last Window         :             |------|
    if (TimeRange.END_OF_DAY - availableStartTime >= requestedDurationMinutes) {
      TimeRange availablePeriod =
          TimeRange.fromStartEnd(availableStartTime, TimeRange.END_OF_DAY, true);
      availableTimes.add(availablePeriod);
    }

    // Return list of available windows
    return availableTimes;
  }
}
