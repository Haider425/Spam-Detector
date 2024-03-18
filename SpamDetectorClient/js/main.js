const URL = "http://localhost:8080/spamDetector-1.0/api/spam";
const URLPrecision = "http://localhost:8080/spamDetector-1.0/api/spam/precision";
const URLAccuracy = "http://localhost:8080/spamDetector-1.0/api/spam/accuracy";

const addRecordToTable = (record) => {
  const row = document.createElement("tr");
  for (const value of Object.values(record)) {
    const cell = document.createElement("td");
    cell.innerHTML = value;
    row.appendChild(cell);
  }
  document.querySelector("#chart tbody").appendChild(row);
};

const getPrecision = () => {
  fetch(URLPrecision)
    .then(response => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then(data => {
      if (data !== undefined) {
        document.getElementById("precision").value = data;
      } else {
        console.error("Invalid precision data structure.");
      }
    })
    .catch(handleError);
};

const getAccuracy = () => {
  fetch(URLAccuracy)
    .then(response => {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response.json();
    })
    .then(data => {
      if (data !== undefined) {
        document.getElementById("accuracy").value = data;
      }else {
        console.error("Invalid accuracy data structure.");
      }
    })
    .catch(handleError);
};

const handleError = (error) => {
  console.error("Error:", error);
  // Add any additional error handling logic here
};

// Fetch data from the main URL and populate the table
fetch(URL)
  .then(response => {
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    return response.json();
  })
  .then(data => {
    if (data && Array.isArray(data)) {
      data.forEach(record => {
        addRecordToTable(record);
      });
    } else {
      console.error("Invalid data structure. Expected an array.");
    }
  })
  .catch(handleError);

// Fetch precision and accuracy separately and populate input elements
getPrecision();
getAccuracy();