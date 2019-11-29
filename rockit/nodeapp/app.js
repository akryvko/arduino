const express = require("express");

const PORT = process.env.PORT || 5000;

var app = express();

var rockStatus = "stopped";

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

app.route("/kabachok/rock")
    .get((req, res) => {
        console.log(`Sending status: ${rockStatus}`);
        res.send(rockStatus);
    });

// for simplicity - gets
app.route("/kabachok/rock/start")
    .get((req, res) => {
        console.log(`Starting cradle...`);
        rockStatus = "started";
        res.send(rockStatus);
    });

app.route("/kabachok/rock/stop")
    .get((req, res) => {
        console.log(`Stopping cradle...`);
        rockStatus = "stopped";
        res.send(rockStatus);
    });
