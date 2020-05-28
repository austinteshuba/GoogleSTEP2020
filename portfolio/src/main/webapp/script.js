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

/**
 * Adds a random activity I might be doing to the page
 */

const activities = ['Coding 💻', 'Travelling 🗼', 'Brewing coffee ☕', 'Going on a nature walk 🌲'];

function addActivity() {
  const activityContainer = document.getElementById('activity-container');

  // Always show a new activity from the below list of activities
  // every time the button is clicked.
  const eligibleActivities = activities.filter(ele => ele !== activityContainer.innerText);

  // Pick a random greeting.
  const activity = eligibleActivities[Math.floor(Math.random() * eligibleActivities.length)];

  // Add it to the page
  activityContainer.innerText = activity;
}
