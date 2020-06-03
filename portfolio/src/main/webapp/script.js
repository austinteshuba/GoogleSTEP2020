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
const typewriterLetterDelayMs = 100;
const typewriterWordDelayMs = 1000;
const typewriterLoadDelayMs = 1000;

// Run the Get request on load
// and start the typewriter effect in the passions element
window.onload = function () {
    getData();

    var passionSelector = document.getElementById("passions");
    window.setTimeout(() => startTypewriter(passionSelector, passions, typewriterLetterDelayMs, typewriterWordDelayMs), typewriterLoadDelayMs);
};

// Function to start a typewriter effect on an HTML element
// where one letter of a word (in a list of words) appears at a time
// @param textSelector - html selector of target element
// @param words - list of words to cycle through in the effect. min length is 1
// @param letterDelay - duration in milliseconds of delay between letter keypresses. Must be > 0. 
// @param wordDelay - duration in milliseconds of delay between presenting a finished word and starting the next one. Must be >0
function startTypewriter(textSelector, words, letterDelay, wordDelay) {
    typewriter(textSelector, words, letterDelay, wordDelay);
}

// Recursive function for typewriter effect
// Do not call directly - use convenience function
// @param textSelector - html selector of target element
// @param words - list of words to cycle through in the effect. min length is 1
// @param letterDelay - duration in milliseconds of delay between letter keypresses. Must be > 0. 
// @param wordDelay - duration in milliseconds of delay between presenting a finished word and starting the next one. Must be >0
// @param wordIndex - current index of word in words. Start at first word by default
// @param letterIndex - current index of next letter in word. Start at first letter by default.
function typewriter(textSelector, words, letterDelay = typewriterLetterDelay, wordDelay = typewriterWordDelay, wordIndex = 0, letterIndex = 0) {

    textSelector.innerText = textSelector.innerText + words[wordIndex].charAt(letterIndex);

    // If the word is finished typing, go to the next word, wait a second, then clear
    // the text and start typing the next word. Must use a space character to clear the text
    // or the node won't render and the nodeValue will become null and unsettable. 
    if (letterIndex+1 === words[wordIndex].length) {

        window.setTimeout(() => {
            textSelector.innerText = "";
            typewriter(textSelector, words, letterDelay, wordDelay, (wordIndex + 1) % words.length, 0);
        }, wordDelay);

    } else {
        // Otherwise, start typing the next letter
        window.setTimeout(() => typewriter(textSelector, words, letterDelay, wordDelay, wordIndex, letterIndex+1), letterDelay);
    }

}

/**
 * This function will execute GET request on the /data URL.
 * Expected response is an array of hardcoded comments.
 *
 * This function will display each comment on a new line in the
 * response-container div.
 */
function getData() {
  console.log("Here");
  fetch('/data')
      .then((response) => response.json())
      .then((commentsArr) => {
        console.log(commentsArr);
      });
}
