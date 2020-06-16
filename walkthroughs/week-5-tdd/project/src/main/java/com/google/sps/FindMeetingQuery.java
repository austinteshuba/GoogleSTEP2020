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

import java.util.*;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  /**
   * Check what times, if any exist, a meeting can be scheduled so all attendees can come
   * @param events a collection of events that attendees have already committed to.
   *     Includes name, time range, and collection of attendees
   * @param request a request for a new meeting that must not conflict with any existing events.
   *     Includes name, duration, and collection of attendees
   * @return A collection of time ranges when the new meeting can occur without conflict
   */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // TODO: Optional Attendees

    // Get the relevant event time ranges and remove all events that don't share
    // attendees with the meeting request
    // And Sort the times from earliest start to latest
    // Optional: Add a filter to the stream to account for optional attendees
    List<TimeRange> eventTimes =
        events.stream().filter(e -> {
          HashSet<String> sharedAttendees = new HashSet<>(e.getAttendees());
          sharedAttendees.retainAll(request.getAttendees());
          return sharedAttendees.size() != 0;
        }).map(Event::getWhen).sorted(TimeRange.ORDER_BY_START)
            .collect(Collectors.toList());

    System.out.println(Arrays.toString(eventTimes.toArray()));
    List<TimeRange> busyTimes = new ArrayList<>();


    // Iterate through all of the events
    // merge overlapping events to its simplest form
    int currentEventIndex = 0;
    while (currentEventIndex < eventTimes.size()) {
      // The shortest the current unavailable window can be is the length of the current window.
      int startTime = eventTimes.get(currentEventIndex).start();
      int endTime = eventTimes.get(currentEventIndex).end();

      System.out.println("Here");
      while (currentEventIndex < eventTimes.size() - 1) {
        currentEventIndex++;

        TimeRange compare = eventTimes.get(currentEventIndex);
        if (compare.contains(endTime) || compare.start() == endTime) {
          // Extend the end time due to overlap
          endTime = eventTimes.get(currentEventIndex).end();

        } else if (compare.end() >= endTime) {
          // The next time range does not contain the current time range's end
          // and it is not overlapping.
          // Thus, there are no other events to merge together.
          currentEventIndex--;
          break;
        }
      }

      TimeRange busyPeriod = TimeRange.fromStartEnd(startTime, endTime, false);

      busyTimes.add(busyPeriod);
      currentEventIndex++;
    }

    System.out.println("Inputted Events: " + Arrays.toString(eventTimes.toArray()));
    System.out.println("Merged Events: " + Arrays.toString(busyTimes.toArray()));


    // Now that we have the non-overlapping version of busy periods
    // Get the non-busy periods
    List<TimeRange> availableTimes = new ArrayList<>();

    int startTime = TimeRange.START_OF_DAY;
    int endTime;

    for (TimeRange busyTime: busyTimes) {
      endTime = busyTime.start();
      if (endTime - startTime >= request.getDuration()) {
        availableTimes.add(TimeRange.fromStartEnd(startTime, endTime, false));
      }

      startTime = busyTime.end();
    }

    if (TimeRange.END_OF_DAY - startTime >= request.getDuration()) {
      availableTimes.add(TimeRange.fromStartEnd(startTime, TimeRange.END_OF_DAY, true));
    }

    return availableTimes;
  }
}
