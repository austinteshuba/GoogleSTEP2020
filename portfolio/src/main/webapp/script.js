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
const passions = ['Front-End Development?', 'Machine Learning?',
    'Software Design?', 'Technology Consulting?', 'Data Science?',
    'Algorithms?', 'User Experience?'];
const typewriterLetterDelayMs = 100;
const typewriterWordDelayMs = 1000;
const typewriterLoadDelayMs = 1000;

window.onload = function() {
  // Initialize the business card form and retrieve blobkey references for the
  // business cards stores in blobstore
  fetchBlobstoreUrl();
  checkAuthentication();
  getBlobKeys();

  // Start typewriter effect
  const passionSelector = document.getElementById('passions');
  window.setTimeout(() => {
    startTypewriter(
        passionSelector,
        passions,
        typewriterLetterDelayMs,
        typewriterWordDelayMs);
  }, typewriterLoadDelayMs);
};

/**
 * Start a typewriter effect on an HTML element
 * where one letter of a word (in a list of words) appears at a time
 * @param {HTMLElement} textSelector - html selector of target element
 * @param {Array} words - list of words to cycle through in the effect.
 *     Min length is 1
 * @param {number} letterDelayMs - duration in milliseconds of delay between
 *     letter keypresses. Must be > 0.
 * @param {number} wordDelayMs - duration in milliseconds of delay between
 *     presenting a finished word and starting the next one. Must be >0
 */
function startTypewriter(textSelector, words, letterDelayMs, wordDelayMs) {
  typewriter(textSelector, words, letterDelayMs, wordDelayMs);
}

/**
 * Recursive function for typewriter effect
 * Do not call directly - use convenience function
 * @param {HTMLElement} textSelector - html selector of target element
 * @param {Array} words - list of words to cycle through in the effect.
 *     Min length is 1
 * @param {number} letterDelayMs - duration in milliseconds of delay between
 *     letter keypresses. Must be > 0.
 * @param {number} wordDelayMs - duration in milliseconds of delay between
 *     presenting a finished word and starting the next one. Must be >0
 * @param {number} wordIndex - current index of word in words.
 *     Start at first word by default
 * @param {number} letterIndex - current index of next letter in word.
 *     Start at first letter by default.
 */
function typewriter(textSelector, words, letterDelayMs,
    wordDelayMs, wordIndex = 0, letterIndex = 0) {
  textSelector.innerText =
      textSelector.innerText + words[wordIndex].charAt(letterIndex);

  // If the word is finished typing, go to the next word, wait a second, then
  // clear the text and start typing the next word.
  if (letterIndex + 1 === words[wordIndex].length) {
    window.setTimeout(() => {
      textSelector.innerText = '';
      typewriter(
          textSelector, words, letterDelayMs, wordDelayMs,
          (wordIndex + 1) % words.length, 0);
    }, wordDelayMs);
  } else {
    // Otherwise, start typing the next letter
    window.setTimeout(() => {
      typewriter(
          textSelector, words, letterDelayMs, wordDelayMs, wordIndex,
          letterIndex + 1);
    }, letterDelayMs);
  }
}

/**
 * Execute GET request on the /data URL.
 * Expected response is an array of comments previously inputted.
 * This function will display each comment on a new line in the
 * response-container div.
 */
function getData() {
  // Get the element
  const quantityElement = document.getElementById('display');

  // Get the stored value
  // Could be empty - this means display all comments.
  const display = quantityElement.value;

  // Create query string
  const queryString = '/data?display=' + display;

  fetch(queryString)
      .then((response) => response.json())
      .then((comments) => {
        // Create a string to contain all of the comments
        let commentString = '';

        // Build up the string with information from each comment.
        for (const comment of comments) {
          commentString += 'Comment:\n';
          commentString += 'First Name: ' + comment['firstName'] + '\n';
          commentString += 'Last Name: ' + comment['lastName'] + '\n';
          commentString += 'Email: ' + comment['email'] + '\n';
          commentString += 'Reason for Visit: ' + comment['visitReason'] + '\n';
          commentString += 'Comment Body: ' + comment['comment'] + '\n\n';
        }

        // Display the comment
        document.getElementById('response-container').innerText =
            commentString;
      });
}

/**
 * Delete all comments currently in the datastore.
 */
function deleteData() {
  // Create the request
  const request = new Request('/delete-data', {method: 'POST'});

  // Perform the request to delete all comments
  // and then perform a GET request to update the comments view.
  fetch(request)
      .then(() => getData());
}

/**
 * Fetch the blobstore URL for the business card form.
 */
function fetchBlobstoreUrl() {
  // Get the form element
  const bizCardForm = document.getElementById('bizCardForm');

  // Get the url
  fetch('/blobstore-upload-url')
      .then((response) => response.json())
      .then((urlObject) => {
        bizCardForm.action = urlObject['blobUrl'];
      });
}

/**
 * Fetch the blobKeys of all business cards in the blobstore.
 */
function getBlobKeys() {
  fetch('/biz-card')
      .then((response) => response.json())
      .then((blobKeys) => {
        // Create empty list in div to store blobKey links
        const container = document.getElementById('images-container');
        const blobKeyList = document.createElement('ol');
        container.appendChild(blobKeyList);

        // Iterate through blobKeys, make them links to serve the blob image
        // Enclose each link in a list element for formatting purposes
        blobKeys.forEach((blobKey) => {
          const listElement = document.createElement('li');
          const blobKeyLink = document.createElement('a');

          blobKeyLink.innerText = blobKey;
          blobKeyLink.href = '/serve-image?blobKey=' + blobKey;
          blobKeyLink.target = '_blank';
          blobKeyLink.className = 'blob-key-link';

          listElement.appendChild(blobKeyLink);
          blobKeyList.appendChild(listElement);
        });
      });
}

/**
 * Gets the user's authentication status from the auth-status servlet
 * and calls handleAuthenticationStatus function to change form visibility
 * accordingly
 */
function checkAuthentication() {
  fetch('/auth-status')
      .then((response) => response.json())
      .then((authInfo) => {
        // Get the authentication status as boolean and auth link as string
        const loggedIn = authInfo['loggedIn'];
        const authLink = authInfo['changeAuthenticationUrl'];

        // Begin changes to the DOM depending on the authentication status
        initializeFormState(loggedIn, authLink);
      });
}

/**
 * Updates the HTML elements to prompt the user to log in or log out,
 * with the correct link to do so below the text. Hides the comment
 * form if logged out, shows it if logged in.
 * @param {boolean} loggedIn true if user is logged in, false otherwise
 * @param {string} authLink link to either log in or log out, as needed.
 */
function initializeFormState(loggedIn, authLink) {
  const form = document.getElementById('comment-form');

  const logInContainer = document.getElementById('logIn');
  const logOutContainer = document.getElementById('logOut');
  const logInLink = document.getElementById('logInLink');
  const logOutLink = document.getElementById('logOutLink');

  // If logged in, prompt user with link to log out and show form
  // If logged out, prompt user to log in and hide form
  if (loggedIn) {
    logOutLink.href = authLink;
    logInLink.href = '#';
    logOutContainer.removeAttribute('hidden');
    logInContainer.setAttribute('hidden', '');

    form.style.display = 'block';
  } else {
    logInLink.href = authLink;
    logOutLink.href = '#';
    logInContainer.removeAttribute('hidden');
    logOutContainer.setAttribute('hidden', '');

    form.style.display = 'none';
  }
}

