const express = require("express");

const PORT = process.env.PORT || 5000;
const DEFAULT_DURATION = 5 * 60 * 1000; // ms - 5 minutes
const API_PATH = "/kabachok/rock";
const app = express();

let rockStatus = "stopped";

const sendStart = (res) => {
    console.log(`Starting cradle...`);
    rockStatus = "started";
    if (!!res) {
        res.send(rockStatus);
    }
};

const sendStop = (res) => {
    console.log(`${new Date().getTime()} - Stopping cradle...`);
    rockStatus = "stopped";
    if (!!res) {
        res.send(rockStatus);
    } else {
        console.log(`Cradle stopped - timed out`);
    }
};

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

app.route(API_PATH)
    .get((req, res) => {
        console.log(`Sending status: ${rockStatus}`);
        res.send(rockStatus);
    });

// for simplicity - gets
app.route(`${API_PATH}/start`)
    .get((req, res) => {
        sendStart(res);
        setTimeout(sendStop, DEFAULT_DURATION, null);
    });

app.route(`${API_PATH}/stop`)
    .get((req, res) => {
        sendStop(res);
    });
