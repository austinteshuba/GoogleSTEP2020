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

// This is a list of passions to iterate through on the homepage
const passions = ["Front-End Development?", "Machine Learning?", "Software Design?", "Technology Consulting?" ,"Data Science?", "Algorithms?", "User Experience?"];

// Get the selector for the Passions type-writer function when the window loads.


window.onload = function () { 
    var passionSelector = document.getElementById("passions").childNodes[0]
    var wordIndex = 0;
    var letterIndex = 0;

    window.setTimeout(() => typewriter(passionSelector, passions, wordIndex, letterIndex), 1000);
};


function typewriter(textSelector, words, wordIndex, letterIndex) {
    textSelector.nodeValue = textSelector.nodeValue + words[wordIndex].charAt(letterIndex);

    // If the word is finished typing, go to the next word, wait a second, then clear
    // the text and start typing the next word. Must use a space character to clear the text
    // or the node won't render and the nodeValue will become null and unsettable. 
    if (letterIndex+1 === words[wordIndex].length) {

        (wordIndex + 1) % words.length;
        window.setTimeout(() => {
            textSelector.nodeValue = " ";
            typewriter(textSelector, words, (wordIndex + 1) % words.length, 0);
        }, 1000);
        return;
    }
    
    window.setTimeout(() => typewriter(textSelector, words, wordIndex, letterIndex+1), 100);
}
















// A list of activities that could appear when clicking activities button.
const activities = ['Coding 💻', 'Travelling 🗼', 'Brewing coffee ☕', 'Going on a nature walk 🌲'];


/**
 * Adds a random activity I might be doing to the page
 */
function addActivity() {
  const activityContainer = document.getElementById('activity-container');

  // Always show a new activity from the below list of activities
  // every time the button is clicked.
  const eligibleActivities = activities.filter(ele => ele !== activityContainer.innerText);

  // Pick a random greeting.
  const activity = eligibleActivities[Math.floor(Math.random() * eligibleActivities.length)];

  // Add it to the page
  activityContainer.innerText = activity;
  console.log(passionSelector.nodeValue);
}
